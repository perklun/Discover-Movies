package perk.discovermovies.models;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

import perk.discovermovies.R;

/**
 * Created by perk on 8/31/2015.
 */
public class Movie2 implements Serializable {

    public int page;
    public List<Result> results;

    /**
     * Get list of movies
     *
     * @return
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     * Representation of each movie result
     */
    public class Result implements Serializable{
        public boolean adult;
        public String backdrop_path;
        public int[] genre_ids;
        public int id;
        public String original_language;
        public String original_title;
        public String overview;
        public String release_date;
        public String poster_path;
        public double popularity;
        public String title;
        public boolean video;
        public double vote_average;
        public int vote_count;
        public int total_pages;
        public int total_result;

        public String getPosterURL(){
            return poster_path;
        }

        public String getId() {
            return String.valueOf(id);
        }

}
}
