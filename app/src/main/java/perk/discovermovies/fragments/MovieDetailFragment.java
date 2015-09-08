package perk.discovermovies.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;

import perk.discovermovies.App;
import perk.discovermovies.R;
import perk.discovermovies.models.Movie;
import perk.discovermovies.models.MovieVideos;
import perk.discovermovies.models.Review;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Displays details about a particular selected movie
 *
 * Created by perk on 8/24/2015.
 */
public class MovieDetailFragment extends Fragment {

    private Movie.Result movie;
    private TextView tvMovieTitle;
    private TextView tvReleaseDate;
    private TextView tvVotesAverage;
    private TextView tvPlotSynopsis;
    private ImageView ivThumbnail;
    private ImageView ib_favorite;
    private boolean is_favorite;
    // Listview for reviews
    private LinearLayout llMovieReviews;
    // LinearLayout for trailers/videos
    private LinearLayout llMovieVideos;
    private DownloadReviews dlReviews;
    private DownloadVideos dlVideos;
    private LinearLayout.LayoutParams lp;
    private Set<String> fav_movies;
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;
    private Review r;
    private MovieVideos mv;
    private onItemUnFavorite listener;

    /**
     * Creates a new fragment and pass in a movie item
     *
     * @param movie
     * @return MovieDetailFragment
     */
    public static MovieDetailFragment newInstance(Movie.Result movie){
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("movie", movie);
        movieDetailFragment.setArguments(args);
        return movieDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mv = (MovieVideos) savedInstanceState.getSerializable("mv");
            r = (Review) savedInstanceState.getSerializable("r");
        }

        movie = (Movie.Result) getArguments().getSerializable("movie");
        // Set up layout params for later view inflation
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,Math.round(getActivity().getResources().getDimension(R.dimen.views_margin)),0,0);
        // Determine if movie is a fav in shared pref
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        edit = pref.edit();
        fav_movies = pref.getStringSet("fav", new HashSet<String>());
        if(fav_movies.contains(movie.getStringId())){
            is_favorite = true;
        }
        else{
            is_favorite = false;
        }
        setRetainInstance(true);
    }

    /**
     * Initiate all views with movie details
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return v
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fg_movie_detail, container, false);
        // Set up views with movie details
        tvMovieTitle = (TextView) v.findViewById(R.id.tvMovieTitle);
        tvMovieTitle.setText(movie.title);
        tvReleaseDate = (TextView) v.findViewById(R.id.tvReleaseDate);
        tvReleaseDate.setText(movie.release_date);
        tvVotesAverage = (TextView) v.findViewById(R.id.tvVoteAverage);
        tvVotesAverage.setText(String.valueOf(movie.vote_average) +"/10");
        tvPlotSynopsis = (TextView) v.findViewById(R.id.tvPlotSynopsis);
        tvPlotSynopsis.setText(movie.overview);
        ivThumbnail = (ImageView) v.findViewById(R.id.ivDetailThumbnail);
        ivThumbnail.setImageResource(0);
        //Set up favorite button
        ib_favorite = (ImageView) v.findViewById(R.id.ib_favorite);
        // Star will be empty if the movie is not a favorite
        if(is_favorite){
            ib_favorite.setImageResource(R.mipmap.ic_star_full);
        }
        else{
            ib_favorite.setImageResource(R.mipmap.ic_star_empty);
        }
        ib_favorite.setOnClickListener(new View.OnClickListener() {
            // Update image and shared pref when star is pressed
            @Override
            public void onClick(View v) {
                if(is_favorite){
                    ib_favorite.setImageResource(R.mipmap.ic_star_empty);
                    fav_movies.remove(movie.getStringId());
                    movie.delete();
                    listener.onItemUnFav();
                }
                else{
                    ib_favorite.setImageResource(R.mipmap.ic_star_full);
                    fav_movies.add(movie.getStringId());
                    movie.save();
                }
                edit.remove("fav");
                edit.putStringSet("fav", fav_movies);
                boolean status = edit.commit();
                Log.d("DEBUG: commit ", String.valueOf(status));
                is_favorite = !is_favorite;
            }
        });
        // Check if poster thumbnail url is null
        if(movie.getPosterURL() != null) {
            String poster_url = null;
            StringBuilder poster_url_builder = new StringBuilder();
            poster_url_builder.append(getContext().getResources().getString(R.string.themoviedb_api_image_url));
            poster_url_builder.append(getContext().getResources().getString(R.string.themoviedb_api_image_size));
            poster_url_builder.append(movie.getPosterURL());
            poster_url = poster_url_builder.toString();
            Picasso.with(getContext()).load(poster_url).into(ivThumbnail);
        }
            // Set up video views linearlayout
        llMovieVideos = (LinearLayout) v.findViewById(R.id.llMovieVideos);
        // Retrieve movie videos
        if(mv != null){
            createTrailerButtons(mv);
        }
        else{
            dlVideos = new DownloadVideos();
            dlVideos.execute(movie.getStringId());
        }// Set up movie reviews views
        llMovieReviews = (LinearLayout) v.findViewById(R.id.llMovieReviews);
        // Retrieve movie reviews
        if(r != null) {
            createReviewTextView(r);
        }
        else{
            dlReviews = new DownloadReviews();
            dlReviews.execute(movie.getStringId());
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(r != null){
            outState.putSerializable("review",r);
        }
        if(mv != null){
            outState.putSerializable("mv", mv);
        }
    }

    /**
     * AsyncTask to download movie reviews using Retrofit
     */
    private class DownloadReviews extends AsyncTask<String, Void, Void> {
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
            App.getClient().listReview(params[0], getString(R.string.themoviedb_api_key_only), new Callback<Review>() {
                @Override
                public void success(Review review, Response response) {
                    createReviewTextView(review);
                }
                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getContext(), "Unable to load movie details. Check network connection.", Toast.LENGTH_SHORT).show();
                    Log.d("DEBUG DownloadReviews: ", error.toString());
                }
            });
            return null;
        }
    }

    protected void createReviewTextView(Review review){
        r = review;
        for (Review.Results r : review.getResults()) {
            if (r.getContent() != null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.item_review, null);
                TextView tvReview = (TextView) v.findViewById(R.id.tvReview);
                TextView tvReviewAuthor = (TextView) v.findViewById(R.id.tvReviewAuthor);
                tvReview.setText(r.getContent());
                // Only display author of there is a review text
                if (r.getAuthor() != null) {
                    tvReviewAuthor.setText("-" + r.getAuthor());
                } else {
                    tvReviewAuthor.setText("unknown");
                }
                llMovieReviews.addView(v, lp);
            }
        }
    }

    /**
     * AsyncTask to download movie reviews using Retrofit
     */
    private class DownloadVideos extends AsyncTask<String, Void, Void> {
        /**
         * Retrieve movie reviews
         * Create a button for each youtube trailer
         * Assign onClick to each button to open youtube trailer in new activity
         *
         * @param params param[0] is the movie movie_id
         * @return list of reviews
         */
        @Override
        protected Void doInBackground(String... params) {
            App.getClient().listVideos(params[0], getString(R.string.themoviedb_api_key_only), new Callback<MovieVideos>() {
                @Override
                public void success(MovieVideos movieVideos, Response response) {
                    createTrailerButtons(movieVideos);
                }
                @Override
                public void failure(RetrofitError error) {
                    Log.d("DEBUG DownloadVideos: ", error.toString());
                }
            });
            return null;
        }
    }

    protected void createTrailerButtons(MovieVideos movieVideos){
        mv = movieVideos;
        for (MovieVideos.Video video : movieVideos.getResults()) {
            Button btnTrailer = new Button(getActivity());
            btnTrailer.setText(video.name);
            llMovieVideos.addView(btnTrailer, lp);
            final String key = video.key;
            btnTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open youtube video in new activity
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key)));
                }
            });
        }
    }

    public interface onItemUnFavorite{
        void onItemUnFav();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof onItemUnFavorite){
            listener = (onItemUnFavorite) context;
        } else{
            throw new ClassCastException(context.toString() + " implement onItemUnFavorite");
        }
    }
}
