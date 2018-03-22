package com.udacity.georgebalasca.popularmoviesstage_1.arrayadapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.georgebalasca.popularmoviesstage_1.DetailsActivity;
import com.udacity.georgebalasca.popularmoviesstage_1.R;
import com.udacity.georgebalasca.popularmoviesstage_1.models.Movie;

import java.util.ArrayList;

/**
 * Created by Jorj on 03/02/2018.
 */

public class MoviesListArrayAdapter extends ArrayAdapter<Movie> {

    /**
     * Custom constructor for our adapter
     *
     * @param ctx: context
     * @param movies: movies obj. list
     */
    public MoviesListArrayAdapter(Context ctx, ArrayList<Movie> movies){
        super(ctx, 0, movies);

    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position = position of the list
     * @param convertView = the view (Grid View item)
     * @param parent = the Grid
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Gets the Movie object from the ArrayAdapter at the appropriate position
        final Movie movie = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);

        ImageView moviePosterView = convertView.findViewById(R.id.movie_poster);

        moviePosterView.setAdjustViewBounds(true);
        moviePosterView.setPadding(0, 0, 0, 0);

        Picasso.with(getContext())
                .load(movie != null ? movie.getPosterURL() : null)
                .into(moviePosterView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class destinationClass = DetailsActivity.class;
                Intent intentToStartDetailActivity = new Intent(getContext(), destinationClass);
                intentToStartDetailActivity.putExtra("movie", movie);
                getContext().startActivity(intentToStartDetailActivity);
            }
        });

        return convertView;
    }
}
