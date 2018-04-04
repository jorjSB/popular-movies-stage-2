package com.udacity.georgebalasca.popularmoviesstage_2.arrayadapters;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.georgebalasca.popularmoviesstage_2.DetailsActivity;
import com.udacity.georgebalasca.popularmoviesstage_2.R;
import com.udacity.georgebalasca.popularmoviesstage_2.data.MovieContract;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Movie;

import java.util.ArrayList;

/**
 * Created by Jorj on 03/02/2018.
 */

public class MoviesListArrayAdapter extends ArrayAdapter<Movie> {

    private ArrayList<Movie> mMovies;
    private Context mContext;
    /**
     * Custom constructor for our adapter
     *
     * @param ctx: context
     * @param movies: movies obj. list
     */
    public MoviesListArrayAdapter(Context ctx, ArrayList<Movie> movies){
        super(ctx, 0, movies);
        mMovies = movies;
        mContext = ctx;
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

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);

        ImageView moviePosterView = convertView.findViewById(R.id.movie_poster);
        ImageView movieFavIndicator = convertView.findViewById(R.id.favourite_movie);

        moviePosterView.setAdjustViewBounds(true);
        moviePosterView.setPadding(0, 0, 0, 0);

//        if(mMovies != null)
//            return convertView;

            // Gets the Movie object from the ArrayAdapter at the appropriate position
            final Movie mMovie = mMovies.get(position);

            Picasso.with(mContext)
                    .load(mMovie != null ? mMovie.getPosterURL() : null)
                    .into(moviePosterView);

            showStarIfFavourite(movieFavIndicator, mMovie);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Class destinationClass = DetailsActivity.class;
                    Intent intentToStartDetailActivity = new Intent(mContext, destinationClass);
                    intentToStartDetailActivity.putExtra("movie", mMovie);
                    mContext.startActivity(intentToStartDetailActivity);
                }
            });

        return convertView;
    }

    private void showStarIfFavourite(ImageView movieFavIndicator, Movie mMovie) {
        final ContentResolver resolver = mContext.getContentResolver();
        final String[] projection = { MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_TITLE, MovieContract.MovieEntry.COLUMN_IDENTIFIER };

        Cursor cursor = resolver.query(MovieContract.MovieEntry.CONTENT_URI, projection, MovieContract.MovieEntry.COLUMN_IDENTIFIER + " == ?" ,
                new String[] { String.valueOf(mMovie.getId()) }, null);

        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            if(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IDENTIFIER)) == mMovie.getId())
                movieFavIndicator.setVisibility(View.VISIBLE);
        }else
                movieFavIndicator.setVisibility(View.GONE);

    }

    public void updateAdapter(ArrayList<Movie> movies){
        this.mMovies.clear();
        this.mMovies.addAll(movies);

        this.notifyDataSetChanged();
        Log.d("inflateGridView()", "data: " + mMovies.size());

    }
}
