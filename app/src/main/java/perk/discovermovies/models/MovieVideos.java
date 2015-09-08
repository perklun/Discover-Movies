package perk.discovermovies.models;

import com.activeandroid.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to store movie's videos
 * POJO for GSON
 * Created by perk on 8/28/2015.
 */

public class MovieVideos implements Serializable{

    public int id;
    public List<Video> results;

    /**
     * Getter for results
     * Remove results that are not youtube links and not trailer
     * Must have a key before inserting into list
     *
     * @return list of results
     */
    public List<Video> getResults() {
        List<Video> filtered_list = new ArrayList<Video>();
        for(Video v : results){
            if(v.site.toLowerCase().equals("youtube") && v.type.toLowerCase().equals("trailer")){
                if(v.key != null){
                    filtered_list.add(v);
                }
            }
        }
        return filtered_list;
    }

    public class Video implements Serializable{
        public String id;
        public String iso_639_1;
        public String key;
        public String name;
        public String site;
        public int size;
        public String type;
    }
}
