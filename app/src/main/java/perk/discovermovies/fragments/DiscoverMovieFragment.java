package perk.discovermovies.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import perk.discovermovies.MovieDataRestClient;
import perk.discovermovies.R;
import perk.discovermovies.activities.DetailActivity;
import perk.discovermovies.adapters.GridViewAdapter;
import perk.discovermovies.adapters.GridViewAdapter2;
import perk.discovermovies.models.Movie;
import perk.discovermovies.models.Movie2;
import perk.discovermovies.models.MovieVideos;
import perk.discovermovies.models.Review;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Fragment that displays movies sorted by popularity or vote_average
 *
 * Created by perk on 8/24/2015.
 */
public class DiscoverMovieFragment extends Fragment {

    private GridView gvResults;
    private GridViewAdapter gridViewAdapter;
    private GridViewAdapter2 gridViewAdapter2;
    private ArrayList<Movie> list_of_movieResults;
    private ArrayList<Movie2.Result> list_of_movieResults2;
    private DownloadMoviesJSON dlMovieJSON;
    // Determine setting (whether movies are popular or highly rated)
    private int popular = 0;
    private OnListItemSelectedListener listener;
    private MovieDataRestClient client;
    private DownloadMovie dlMovie;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list_of_movieResults = new ArrayList<Movie>();
        list_of_movieResults2 = new ArrayList<Movie2.Result>();
        //gridViewAdapter = new GridViewAdapter(getActivity(), list_of_movieResults);
        gridViewAdapter2 = new GridViewAdapter2(getActivity(), list_of_movieResults2);
        setUpMovieDataRestClient();
        dlMovie = new DownloadMovie();
        setRetainInstance(true);
    }

    public void setUpMovieDataRestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint((getString(R.string.themoviedb_api_url))).build();
        client = restAdapter.create(MovieDataRestClient.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fg_discover_movie, container, false);
        gvResults = (GridView) v.findViewById(R.id.gvMovieThumbnails);
        gvResults.setAdapter(gridViewAdapter2);
        // Set up onItemClickListener to go to DetailActivity once a movie is clicked
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie2.Result movie = gridViewAdapter2.getItem(position);
                listener.onItemSelected(movie);
            }
        });
        // If existing movie list do not exist, load new data. Default is popular
        if(list_of_movieResults.size() <= 0){
            //downloadMovies(popular);
            dlMovie.execute(getString(R.string.themoviedb_api_sort_by_popular));
        }
        else{
            // Redraw according to new width
            gridViewAdapter2.notifyDataSetChanged();
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("movie_list", list_of_movieResults);
    }

    /**
     * Builds URL string based on preferences and initiates URL request
     *
     * @param settings
     */
    public void downloadMovies(int settings){
        //format default to load movies
        dlMovieJSON = new DownloadMoviesJSON();
        StringBuilder default_url = new StringBuilder();
        default_url.append(getResources().getString(R.string.themoviedb_api_url));
        default_url.append(getResources().getString(R.string.themoviedb_api_url_discover_movie_default));
        default_url.append("?");
        if(settings == popular){
            default_url.append(getResources().getString(R.string.themoviedb_api_sort_by_popular));
        }
        else {
            default_url.append(getResources().getString(R.string.themoviedb_api_sort_by_vote_average));
        }
        default_url.append("&");
        default_url.append(getResources().getString(R.string.themoviedb_api_key));
        // If network is available, make request to retrieve movie data
        if(checkNetwork()) {
            Log.d("DEBUG: ", default_url.toString());
            dlMovieJSON.execute(default_url.toString());
        } else{
            //Let user know there is no network available
            Toast.makeText(getActivity(), "No network connection!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to make API request to themoviedb
     *
     * @param movie_url
     * @return null or JSONObject containing movie object
     * @throws IOException
     */
    private JSONObject downloadMovieData(String movie_url) throws IOException {
        InputStream is = null;
        try{
            URL url = new URL(movie_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000); //milliseconds
            conn.setConnectTimeout(15000); //milliseconds
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoInput(true);
            // Starts query
            conn.connect();
            int response = conn.getResponseCode();
            if(response == 200){
                is = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                String inputString;
                while ((inputString = bufferedReader.readLine()) != null){
                    stringBuilder.append(inputString);
                }
                return new JSONObject(stringBuilder.toString());
            } else{
                Log.d("DEBUG: ", response + " code");
            }
        } catch (MalformedURLException e) {
            Log.d("DEBUG: ", "MalformedURLException");
        } catch (ProtocolException e) {
            Log.d("DEBUG: ", "ProtocolException");
        } catch (IOException e) {
            Log.d("DEBUG: ", "IOException");
        } catch (JSONException e) {
            Log.d("DEBUG: ", "JSONException");
        } finally {
            if(is != null){
                is.close();
            }
        }
        return null;
    }

    /**
     * Check if network is available
     *
     * @return true is network is connected and online
     */
    protected boolean checkNetwork(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

    /**
     * AsyncTask to download movie data from themoviedb
     */
    private class DownloadMoviesJSON extends AsyncTask<String, Void, JSONObject> {
        /**
         * Retrieve movie data
         *
         * @param params
         * @return JSONObject containing movie data
         */
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                return downloadMovieData(params[0]);
            } catch (IOException e) {
                Log.d("DEBUG: ", "IOException");
            }
            return null;
        }
        /**
         * Create list of movies from JSON response if response isn't null
         *
         * @param jsonObject
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject != null) {
                try {
                    gridViewAdapter.clear();
                    gridViewAdapter.addAll(Movie.parseJSON(jsonObject.getJSONArray("results")));
                    gridViewAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d("DEBUG: ", "JSONException");
                }
            }
        }
    }

    /**
     * Interface to handler fragment triggered events
     */
    public interface OnListItemSelectedListener{
        public void onItemSelected(Movie2.Result movie);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnListItemSelectedListener){
            listener = (OnListItemSelectedListener) activity;
        } else{
            throw new ClassCastException( activity.toString() + " implement listener");
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
         * @param params param[0] is the movie id
         * @return list of reviews
         */
        @Override
        protected Void doInBackground(String... params) {
            client.listMovies(params[0], getString(R.string.themoviedb_api_key_only), new Callback<Movie2>() {
                @Override
                public void success(Movie2 review, Response response) {
                    gridViewAdapter2.clear();
                    gridViewAdapter2.addAll(review.getResults());
                    gridViewAdapter2.notifyDataSetChanged();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("DEBUG DownloadMovie: ", error.toString());
                }
            });
            return null;
        }
    }
}
