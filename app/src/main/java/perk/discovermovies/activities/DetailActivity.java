package perk.discovermovies.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import perk.discovermovies.R;
import perk.discovermovies.models.Movie;

/**
 * Activity that displays more details about the Movie
 */
public class DetailActivity extends AppCompatActivity {

    private Movie movie;
    private TextView tvMovieTitle;
    private TextView tvReleaseDate;
    private TextView tvVotesAverage;
    private TextView tvPlotSynopsis;
    private String poster_thumbnail_url;
    private ImageView ivThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //retrieve movie object from DiscoverActivity
        Intent i = getIntent();
        movie = (Movie) i.getSerializableExtra("movie");
        setUp(movie);
    }

    /**
     * Initiate all textviews with movie details
     * Load movie thumbnail
     *
     * @param movie
     */
    protected void setUp(Movie movie){
        getSupportActionBar().setTitle(movie.title);
        tvMovieTitle = (TextView) findViewById(R.id.tvMovieTitle);
        tvMovieTitle.setText(movie.title);
        tvReleaseDate = (TextView) findViewById(R.id.tvReleaseDate);
        tvReleaseDate.setText(movie.release_date);
        tvVotesAverage = (TextView) findViewById(R.id.tvVoteAverage);
        tvVotesAverage.setText(String.valueOf(movie.vote_average) +"/10");
        tvPlotSynopsis = (TextView) findViewById(R.id.tvPlotSynopsis);
        tvPlotSynopsis.setText(movie.plot_synopsis);
        poster_thumbnail_url = movie.poster_url;
        ImageView ivThumbnail = (ImageView) findViewById(R.id.ivDetailThumbnail);
        ivThumbnail.setImageResource(0);
        Picasso.with(this).load(poster_thumbnail_url).into(ivThumbnail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }
}
