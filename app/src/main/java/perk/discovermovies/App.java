package perk.discovermovies;

import android.app.Application;
import android.content.Context;

import retrofit.RestAdapter;

/**
 * Created by perk on 8/23/2015.
 */
public class App extends com.activeandroid.app.Application {

    private static App DiscoverMovieApp = null;
    // client for app
    private static MovieDataRestClient client;

    @Override
    public void onCreate() {
        super.onCreate();
        setUpMovieDataRestClient();
        DiscoverMovieApp = this;
    }

    /**
     * Return application context
     *
     * @return context
     */
    public static Context getContext(){
        return DiscoverMovieApp.getApplicationContext();
    }

    /**
     * Return Retrofit client
     *
     * @return client
     */
    public static MovieDataRestClient getClient(){
        return client;
    }

    /**
     * Set up Retrofit Client
     */
    public void setUpMovieDataRestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(getString(R.string.themoviedb_api_url)).build();
        client = restAdapter.create(MovieDataRestClient.class);
    }


}
