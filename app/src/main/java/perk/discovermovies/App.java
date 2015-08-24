package perk.discovermovies;

import android.app.Application;
import android.content.Context;

/**
 * Created by perk on 8/23/2015.
 */
public class App extends Application {
    private static App DiscoverMovieApp = null;

    @Override
    public void onCreate() {
        super.onCreate();
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
}
