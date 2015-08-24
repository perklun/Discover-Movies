package perk.discovermovies.adapters;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import perk.discovermovies.R;
import perk.discovermovies.models.Movie;

/**
 * Adapter to arrange add movies thumbnails to a grid
 *
 * Created by perk on 8/23/2015.
 */
public class GridViewAdapter extends ArrayAdapter<Movie> {

    private int screenwidth;

    public GridViewAdapter(Context context, List<Movie> objects) {
        super(context, R.layout.movie_thumbnail, objects);
        screenwidth = getScreenWidth()/2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie m = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_thumbnail, parent, false);
        }
        ImageView ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
        // Resize imageview to half of screen size
        ivThumbnail.setMaxWidth(screenwidth);
        ivThumbnail.setImageResource(0);
        Picasso.with(getContext()).load(m.poster_url).into(ivThumbnail);
        return convertView;
    }

    /**
     * Get width size to make sure image is displayed properly
     *
     * @return width
     */
    private int getScreenWidth(){
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}

