package perk.discovermovies.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
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
public class DiscoverActivity extends AppCompatActivity implements DiscoverMovieFragment.OnListItemSelectedListener, MovieDetailFragment.onItemUnFavorite{

    private DiscoverMovieFragment discoverMovieFragment;
    private String popular;
    private String vote_average;
    private String favorites;
    private String current_pref;
    private boolean isTwoPane = false;
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        determinePaneLayout();
        //Set up preferences
        popular = getString(R.string.themoviedb_api_sort_by_popular);
        vote_average = getString(R.string.themoviedb_api_sort_by_vote_average);
        favorites = getString(R.string.action_settings_favorite);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        edit = pref.edit();
        current_pref = pref.getString("current_pref", popular);
        // If fragment is saved, retrieve previous fragment
        FragmentManager fm = getSupportFragmentManager();
        discoverMovieFragment = (DiscoverMovieFragment) fm.findFragmentByTag("discover_movie");
        // If previous fragment does not exist, create a new fragment
        if(discoverMovieFragment == null) {
            discoverMovieFragment = DiscoverMovieFragment.newInstance(current_pref);
            fm.beginTransaction().add(R.id.fl_movie_container, discoverMovieFragment, "discover_movie").commit();
        }
    }

    /**
     * Determines if the user has a tablet
     * If user has a tablet, set isTwoPane to true
     */
    private void determinePaneLayout() {
        FrameLayout fragmentMovieWithList = (FrameLayout) findViewById(R.id.fl_movie_detail);
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
            current_pref = popular;

        }
        else if (id == R.id.action_settings_vote_average) {
            current_pref = vote_average;
        }
        else{
            current_pref = favorites;
        }
        discoverMovieFragment.loadMovieResults(current_pref);
        edit.remove("current_pref");
        edit.putString("current_pref", current_pref);
        edit.commit();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie.Result movie) {
        if(isTwoPane){
            MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(movie);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_movie_detail, movieDetailFragment);
            ft.commit();
        } else{
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra("movie", movie);
            startActivity(i);
        }
    }

    @Override
    public void onItemUnFav() {
        FragmentManager fm = getSupportFragmentManager();
        discoverMovieFragment = (DiscoverMovieFragment) fm.findFragmentByTag("discover_movie");
        discoverMovieFragment.updateMovieResultsFromDB();
    }
}
