package perk.discovermovies.fragments;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import perk.discovermovies.App;
import perk.discovermovies.MovieDataRestClient;
import perk.discovermovies.R;
import perk.discovermovies.adapters.GridViewAdapter;
import perk.discovermovies.models.Movie;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Fragment that displays movies sorted by popularity or vote_average
 *
 * Created by perk on 8/24/2015.
 */
public class DiscoverMovieFragment extends Fragment {

    private GridView gvResults;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<Movie.Result> list_of_MovieResult;
    private OnListItemSelectedListener listener;
    private DownloadMovie dlMovie;
    private String current_pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_pref = getArguments().getString("current_pref");
        list_of_MovieResult = new ArrayList<Movie.Result>();
        gridViewAdapter = new GridViewAdapter(getActivity(), list_of_MovieResult);
        dlMovie = new DownloadMovie();
        setRetainInstance(true);
    }

    /**
     * Creates a new fragment and pass in a string representing the current pref
     *
     * @param current_pref
     * @return
     */
    public static DiscoverMovieFragment newInstance(String current_pref){
        DiscoverMovieFragment discoverMovieFragment = new DiscoverMovieFragment();
        Bundle args = new Bundle();
        args.putString("current_pref", current_pref);
        discoverMovieFragment.setArguments(args);
        return discoverMovieFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fg_discover_movie, container, false);
        gvResults = (GridView) v.findViewById(R.id.gvMovieThumbnails);
        gvResults.setAdapter(gridViewAdapter);
        // Set up onItemClickListener to go to DetailActivity once a movie is clicked
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie.Result movie = gridViewAdapter.getItem(position);
                listener.onItemSelected(movie);
            }
        });
        // If existing movie list do not exist, load new data. Default is popular
        if(list_of_MovieResult.size() <= 0) {
            if (current_pref.equals(getString(R.string.action_settings_favorite))){
                //load favs from local DB
                List<Movie.Result> fav_list = new Movie().getResultFromDB();
                gridViewAdapter.clear();
                gridViewAdapter.addAll(fav_list);
                gridViewAdapter.notifyDataSetChanged();
            }
            else{
                dlMovie.execute(current_pref);
            }
        }
        else{
            // Redraw according to new width
            gridViewAdapter.notifyDataSetChanged();
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putSerializable("movie_list", list_of_MovieResult);
    }

    /**
     * Retrieve movie based on preferences
     * If preferences are different, reload list
     *
     * @param pref
     */
    public void loadMovieResults(String pref){
        //if pref changed, reload movie results
        if(pref != current_pref){
            current_pref = pref;
            if(pref == getString(R.string.action_settings_favorite)) {
                //load favs from local DB
                gridViewAdapter.clear();
                gridViewAdapter.addAll(new Movie().getResultFromDB());
                gridViewAdapter.notifyDataSetChanged();
            }
            else{
                //load new movie list from the movie db
                dlMovie = new DownloadMovie();
                dlMovie.execute(current_pref);
            }
        }
    }

    public void updateMovieResultsFromDB(){
        gridViewAdapter.clear();
        gridViewAdapter.addAll(new Movie().getResultFromDB());
        gridViewAdapter.notifyDataSetChanged();
    }

    /**
     * Interface to handler fragment triggered events
     */
    public interface OnListItemSelectedListener{
        public void onItemSelected(Movie.Result movie);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if(activity instanceof OnListItemSelectedListener){
            listener = (OnListItemSelectedListener) activity;
        } else{
            throw new ClassCastException(activity.toString() + " implement listener");
        }
    }

    /**
     * AsyncTask to download movie reviews using Retrofit
     */
    private class DownloadMovie extends AsyncTask<String, Void, Void> {
        /**
         * Retrieve movie reviews
         * Inflate view for review
         * Add review to linearlayout
         *
         * @param params param[0] is the movie movie_id
         * @return list of reviews
         */
        @Override
        protected Void doInBackground(String... params) {
            App.getClient().listMovies(params[0], getString(R.string.themoviedb_api_key_only), new Callback<Movie>() {
                @Override
                public void success(Movie review, Response response) {
                    gridViewAdapter.clear();
                    gridViewAdapter.addAll(review.getResults());
                    gridViewAdapter.notifyDataSetChanged();
                }
                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getContext(), "Unable to load movie list. Check network connection.", Toast.LENGTH_SHORT).show();
                    Log.d("DEBUG DownloadMovie: ", error.toString());
                }
            });
            return null;
        }
    }
}
