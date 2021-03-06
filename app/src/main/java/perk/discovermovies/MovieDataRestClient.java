package perk.discovermovies;

import perk.discovermovies.models.Movie;
import perk.discovermovies.models.MovieVideos;
import perk.discovermovies.models.Review;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Interface for Retrofit Rest Client
 * Created by perk on 8/28/2015.
 */
public interface MovieDataRestClient {

    /**
     * Obtain list of movies based on preferences
     *
     * @param pref
     * @param api_key
     * @param reviewCallback
     */
    @Headers("Accept: application/json")
    @GET("/discover/movie")
    void listMovies(@Query("sort_by") String pref, @Query("api_key") String api_key, Callback<Movie> reviewCallback);

    /**
     * Obtain list of reviews for a movie with movie_id
     *
     * @param id
     * @param api_key
     * @param reviewCallback
     */
    @Headers("Accept: application/json")
    @GET("/movie/{movie_id}/reviews")
    void listReview(@Path("movie_id") String id, @Query("api_key") String api_key, Callback<Review> reviewCallback);

    /**
     * Obtain list of videos for a movie with movie_id
     *
     * @param id
     * @param api_key
     * @param reviewCallback
     */
    @Headers("Accept: application/json")
    @GET("/movie/{movie_id}/videos")
    void listVideos(@Path("movie_id") String id, @Query("api_key") String api_key, Callback<MovieVideos> reviewCallback);
}
