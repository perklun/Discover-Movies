package perk.discovermovies.models;

import java.io.Serializable;
import java.util.List;

/**
 * Class to store each review list for movie as an object
 * POJO for GSON
 *
 * Created by perk on 8/27/2015.
 */
public class Review implements Serializable{

    public String id;
    public int page;
    public int total_pages;
    public int total_results;
    public List<Results> results;

    /**
     * Getter for reviews
     * @return list of reviews
     */
    public List<Results> getResults() {
        return results;
    }

    /**
     * Inner class for POJO
     */
    public class Results implements Serializable{
        public String id;
        public String author;
        public String content;
        public String url;

        /**
         * Returns review author
         *
         * @return author
         */
        public String getAuthor() {
            return author;
        }

        /**
         * Returns review content
         * @return
         */
        public String getContent() {
            return content;
        }
    }


}
