package perk.discovermovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class GridViewAdapter extends ArrayAdapter<Movie.Result> {

    public GridViewAdapter(Context context, List<Movie.Result> objects) {
        super(context, R.layout.movie_thumbnail, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie.Result m = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_thumbnail, parent, false);
        }
        ImageView ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
        // Resize imageview to half of screen size
        ivThumbnail.setImageResource(0);
        //Check if poster url is null
        if(m.getPosterURL() != null){
            String poster_url = null;
            StringBuilder poster_url_builder = new StringBuilder();
            poster_url_builder.append(getContext().getResources().getString(R.string.themoviedb_api_image_url));
            poster_url_builder.append(getContext().getResources().getString(R.string.themoviedb_api_image_size));
            poster_url_builder.append(m.getPosterURL());
            poster_url = poster_url_builder.toString();
            Picasso.with(getContext()).load(poster_url).into(ivThumbnail);
        }
        return convertView;
    }
}

