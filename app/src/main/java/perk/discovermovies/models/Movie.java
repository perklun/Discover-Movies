package perk.discovermovies.models;

import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import perk.discovermovies.App;
import perk.discovermovies.R;

/**
 * Representation of each Movie
 *
 * Created by perk on 8/23/2015.
 */
public class Movie implements Serializable{

    //Movie attributes
    public String title;
    public String release_date;
    public String poster_url;
    public Double vote_average;
    public String plot_synopsis;

    /**
     * Create a movie object from each json object
     *
     * @param jsonObject
     */
    public Movie(JSONObject jsonObject) {
        try{
            title = jsonObject.getString("title");
            release_date = jsonObject.getString("release_date");
            StringBuilder poster_url_builder = new StringBuilder();
            // Formats the image url properly with the right URL
            Resources appResource = App.getContext().getResources();
            //check if poster url is available
            if(!jsonObject.isNull("poster_path")){
                poster_url_builder.append(appResource.getString(R.string.themoviedb_api_image_url));
                poster_url_builder.append(appResource.getString(R.string.themoviedb_api_image_size));
                poster_url_builder.append(jsonObject.getString("poster_path"));
                poster_url = poster_url_builder.toString();
            }
            else{
                poster_url = null;
            }
            vote_average = jsonObject.getDouble("vote_average");
            // Handle case when there is no plot synopsis
            if(!jsonObject.isNull("overview")) {
                plot_synopsis = jsonObject.getString("overview");
            }
            else{
                plot_synopsis = "No plot synopsis available.";
            }
        }
        catch (JSONException error){
            Log.d("DEBUG: ", error.toString());
        }
    }

    /**
     * Create a array list of movie objects from a JSON Array
     *
     * @param movie_database_json
     * @return list_of_movies ArrayList of movie objects
     */
    public static ArrayList<Movie> parseJSON(JSONArray movie_database_json){
        ArrayList<Movie> list_of_movies = new ArrayList<Movie>();
        try{
            for(int i = 0; i < movie_database_json.length(); i++){
                Movie m = new Movie(movie_database_json.getJSONObject(i));
                list_of_movies.add(m);
            }
        }
        catch (JSONException error){
            Log.d("DEBUG: ", error.toString());
        }
        return list_of_movies;
    }
}
