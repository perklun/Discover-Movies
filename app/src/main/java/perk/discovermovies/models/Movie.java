package perk.discovermovies.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

/**
 * Class to store list of movies from discover
 * POJO for GSON
 * Created by perk on 8/31/2015.
 */
public class Movie extends Model implements Serializable{

    public Movie(){
        super();
    }

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

    public List<Result> getResultFromDB(){
        return new Select().from(Result.class).execute();
    }

    /**
     * Representation of each movie result
     */
    @Table(name = "Result")
    public static class Result extends Model implements Serializable {

        /**
         * Default constructor for ActiveAndroid
         */
        public Result(){
            super();
        }

        public boolean adult;
        public String backdrop_path;
        public int[] genre_ids;
        @Column(name = "movie_id")
        public int id;
        public String original_language;
        public String original_title;
        @Column(name = "overview")
        public String overview;
        @Column(name = "release_date")
        public String release_date;
        @Column(name = "poster_path")
        public String poster_path;
        public double popularity;
        @Column(name = "title")
        public String title;
        public boolean video;
        @Column(name = "vote_average")
        public double vote_average;
        public int vote_count;
        public int total_pages;
        public int total_result;

        /**
         * Get poster uri
         *
         * @return
         */
        public String getPosterURL(){
            return poster_path;
        }

        /**
         * Get Id of movie as a String
         *
         * @return
         */
        public String getStringId() {
            return String.valueOf(id);
        }
    }
}
