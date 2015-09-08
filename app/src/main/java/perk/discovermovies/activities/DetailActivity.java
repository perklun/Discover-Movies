package perk.discovermovies.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import perk.discovermovies.R;
import perk.discovermovies.fragments.DiscoverMovieFragment;
import perk.discovermovies.fragments.MovieDetailFragment;
import perk.discovermovies.models.Movie;

/**
 * Activity that displays more details about the Movie
 */
public class DetailActivity extends AppCompatActivity implements MovieDetailFragment.onItemUnFavorite{

    private Movie.Result movie;
    MovieDetailFragment movieDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //retrieve movie object from DiscoverActivity
        movie = (Movie.Result) getIntent().getSerializableExtra("movie");
        if(savedInstanceState == null){
            movieDetailFragment = MovieDetailFragment.newInstance(movie);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_movie_detail, movieDetailFragment);
            ft.commit();
        }
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

    @Override
     public void onItemUnFav() {
        FragmentManager fm = getSupportFragmentManager();
        DiscoverMovieFragment discoverMovieFragment = (DiscoverMovieFragment) fm.findFragmentByTag("discover_movie");
        discoverMovieFragment.loadMovieResults(getString(R.string.action_settings_favorite));
    }
}
