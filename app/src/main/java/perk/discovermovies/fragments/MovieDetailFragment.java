package perk.discovermovies.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import perk.discovermovies.R;
import perk.discovermovies.models.Movie;

/**
 * Displays details about a particular selected movie
 *
 * Created by perk on 8/24/2015.
 */
public class MovieDetailFragment extends Fragment {

    private Movie movie;
    private TextView tvMovieTitle;
    private TextView tvReleaseDate;
    private TextView tvVotesAverage;
    private TextView tvPlotSynopsis;
    private String poster_thumbnail_url;

    /**
     * Creates a new fragment and pass in a movie item
     *
     * @param movie
     * @return MovieDetailFragment
     */
    public static MovieDetailFragment newInstance(Movie movie){
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("movie", movie);
        movieDetailFragment.setArguments(args);
        return movieDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movie = (Movie) getArguments().getSerializable("movie");
    }

    /**
     * Initiate all textviews with movie details
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
        tvPlotSynopsis.setText(movie.plot_synopsis);
        ImageView ivThumbnail = (ImageView) v.findViewById(R.id.ivDetailThumbnail);
        ivThumbnail.setImageResource(0);
        // Check if poster thumbnail url is null
        if(movie.poster_url != null){
            poster_thumbnail_url = movie.poster_url;
            Picasso.with(getActivity()).load(poster_thumbnail_url).into(ivThumbnail);
        }
        return v;
    }
}
