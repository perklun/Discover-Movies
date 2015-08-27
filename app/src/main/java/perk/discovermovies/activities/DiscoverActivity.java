package perk.discovermovies.activities;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import perk.discovermovies.R;
import perk.discovermovies.fragments.DiscoverMovieFragment;
import perk.discovermovies.fragments.MovieDetailFragment;
import perk.discovermovies.models.Movie;

/**
 * Main activity that displays movies based on selection filter
 */
public class DiscoverActivity extends AppCompatActivity implements DiscoverMovieFragment.OnListItemSelectedListener{

    private DiscoverMovieFragment discoverMovieFragment;
    private int popular = 0;
    private int vote_average = 1;
    private int settings = popular;
    private boolean isTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        determinePaneLayout();
        // If fragment is saved, retrieve previous fragment
        FragmentManager fm = getSupportFragmentManager();
        discoverMovieFragment = (DiscoverMovieFragment) fm.findFragmentByTag("discover_movie");
        // If previous fragment does not exist, create a new fragment
        if(discoverMovieFragment == null) {
            discoverMovieFragment = new DiscoverMovieFragment();
            fm.beginTransaction().add(R.id.fl_movie_container, discoverMovieFragment, "discover_movie").commit();
        }
    }

    /**
     * Determines if the user has a tablet
     * If user has a tablet, set isTwoPane to true
     */
    private void determinePaneLayout() {
        FrameLayout fragmentMovieWithList = (FrameLayout) findViewById(R.id.fl_large_movie_details);
        // If there is a second panel for details
        if(fragmentMovieWithList != null){
            isTwoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_discover, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings_popular) {
            if(settings != popular){
                settings = popular;
                discoverMovieFragment.downloadMovies(popular);
            }
            return true;
        }
        if (id == R.id.action_settings_vote_average) {
            if(settings != vote_average){
                settings = vote_average;
                discoverMovieFragment.downloadMovies(vote_average);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie movie) {
        if(isTwoPane){
            MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(movie);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_large_movie_details, movieDetailFragment);
            ft.commit();
        } else{
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra("movie", movie);
            startActivity(i);
        }
    }
}
