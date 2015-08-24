package perk.discovermovies.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
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

import perk.discovermovies.R;
import perk.discovermovies.adapters.GridViewAdapter;
import perk.discovermovies.models.Movie;

/**
 * Main activity that displays movies based on selection filter
 */
public class DiscoverActivity extends AppCompatActivity {

    private GridView gvResults;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<Movie> list_of_movieResults;
    private DownloadMoviesJSON dlMovieJSON;
    //Strings use to determine setting (whether movies are popular or highly rated)
    private int popular = 0;
    private int vote_average = 1;
    private int setting = popular; //default is popular movies

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        setUp();
    }

    protected void setUp(){
        list_of_movieResults = new ArrayList<Movie>();
        gridViewAdapter = new GridViewAdapter(this, list_of_movieResults);
        gvResults = (GridView) findViewById(R.id.gvMovieThumbnails);
        gvResults.setAdapter(gridViewAdapter);
        // Set up onItemClickListener to go to DetailActivity once a movie is clicked
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                             @Override
                                             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                 Intent i = new Intent(DiscoverActivity.this, DetailActivity.class);
                                                 i.putExtra("movie", list_of_movieResults.get(position));
                                                 startActivity(i);
                                             }
                                         }
        );
        // Retrieve movie information, default is popular;

        downloadMovies(setting);
    }

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
            gridViewAdapter.notifyDataSetChanged();
        } else{
            //Let user know there is no network available
            Toast.makeText(this, "No network connection!", Toast.LENGTH_SHORT).show();
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
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

    /**
     * AsyncTask to download movie data from themoviedb
     */
    private class DownloadMoviesJSON extends AsyncTask<String, Void, JSONObject>{

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
                } catch (JSONException e) {
                    Log.d("DEBUG: ", "JSONException");
                }
            }
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
            if(setting != popular){
                setting = popular;
                downloadMovies(setting);
            }
            return true;
        }
        if (id == R.id.action_settings_vote_average) {
            if(setting != vote_average){
                setting = vote_average;
                downloadMovies(setting);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
