package com.udacity.georgebalasca.popularmoviesstage_2.arrayadapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.udacity.georgebalasca.popularmoviesstage_2.R;
import com.udacity.georgebalasca.popularmoviesstage_2.data.MovieContract;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Movie;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Review;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Trailer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.udacity.georgebalasca.popularmoviesstage_2.utils.NetUtils.getFileNameFromURL;

public class MovieDetailsArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_DETAILS = 0;
    private final int VIEW_TYPE_TRAILERS = 1;
    private final int VIEW_TYPE_REVIEWS = 2;

    private Movie mMovie;
    private ArrayList<Trailer> trailers;
    private ArrayList<Review> reviews;
    private Context context;

    private  static  final String TAG = MovieDetailsArrayAdapter.class.getSimpleName();

    public MovieDetailsArrayAdapter(Movie movie, ArrayList<Trailer> trailers, ArrayList<Review> reviews, Context context) {
        this.mMovie = movie;
        this.trailers = trailers;
        this.reviews = reviews;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_DETAILS;

        if (position > 0 && position <= trailers.size())
            return VIEW_TYPE_TRAILERS;
        else
            return VIEW_TYPE_REVIEWS;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DETAILS) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_details, parent, false);
            return new ViewHolderDetails(itemView);
        } else if (viewType == VIEW_TYPE_TRAILERS) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_trailer_item, parent, false);
            return new ViewHolderTrailers(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_review_item, parent, false);
            return new ViewHolderReviews(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_DETAILS:
                ViewHolderDetails viewHolderDetails = (ViewHolderDetails) holder;
                viewHolderDetails.bindViews(context);
                break;
            case VIEW_TYPE_TRAILERS:
                ViewHolderTrailers viewHolderTrailers = (ViewHolderTrailers) holder;
                viewHolderTrailers.bindViews(context, position-1);
                break;
            case VIEW_TYPE_REVIEWS:
                ViewHolderReviews viewHolderReviews = (ViewHolderReviews) holder;
                int trailersSize = 0;
                if(trailers!= null)
                    trailersSize = trailers.size();
                viewHolderReviews.bindViews(context, position-1 - trailersSize);
        }
    }

    @Override
    public int getItemCount() {
        int total = 1;

        if(trailers != null)
            total += trailers.size();

        if(reviews != null)
            total += reviews.size();

        return total;
    }

    /**
     * Details ViewHolder
     */
    public class ViewHolderDetails extends RecyclerView.ViewHolder {
        ImageView posterIv;
        TextView movieTitleTv;
        TextView voteAverageTv;
        TextView movieDescriptionTv;
        TextView originalTitleTv;
        TextView releaseDateTv;
        ImageButton favoritesButton;

        // inflating the views into the holder
        public ViewHolderDetails(final View itemView) {
            super(itemView);

            posterIv = itemView.findViewById(R.id.movie_poster);
            movieTitleTv = itemView.findViewById(R.id.movie_title);
            voteAverageTv = itemView.findViewById(R.id.vote_average);
            movieDescriptionTv = itemView.findViewById(R.id.movie_description);
            originalTitleTv = itemView.findViewById(R.id.original_title);
            releaseDateTv = itemView.findViewById(R.id.release_date);
            favoritesButton = itemView.findViewById(R.id.favorites_button);
        }

        // binding the views
        public void bindViews(final Context context) {
            if(mMovie != null){
                movieTitleTv.setText(mMovie.getTitle());
                voteAverageTv.setText(String.valueOf(mMovie.getVoteAverage()));
                movieDescriptionTv.setText(mMovie.getOverview());
                originalTitleTv.setText(mMovie.getOriginalTitle());
                releaseDateTv.setText(mMovie.getReleaseDate());

                // check load the appropiate image(FROM URL OF FROM LOCAL FILE)
                if(mMovie != null)
                    if( URLUtil.isValidUrl( mMovie.getPosterURL() ))
                        Picasso.with(context)
                                .load( mMovie.getPosterLandURL() )
                                .into(posterIv);
                    else
                        Picasso.with(context)
                                .load( new File(mMovie.getPosterLandURL()) )
                                .into(posterIv);

                updateFavouriteMovieVisualIndicator(favoritesButton);
                favoritesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateDatabaseWithFavouriteMovie();
                        updateFavouriteMovieVisualIndicator(favoritesButton);
                    }
                });
            }
        }
    }

    /**
     * Update the visual star indicator
     * Favourite movie: star selected
     * Not a favourite movie: star unselected
     */
    private void updateFavouriteMovieVisualIndicator(ImageButton imageButton) {

        if(isMovieAddedToFavourites() != -1)
            imageButton.setImageResource(android.R.drawable.btn_star_big_on);
        else
            imageButton.setImageResource(android.R.drawable.btn_star_big_off);

    }

    /**
     *  Used to query the DB to see if a movie is added to Fav. (by movie's Identifier)
     * @return -1 if not in the DB, != -1 if added
     */
    private int isMovieAddedToFavourites() {
        final ContentResolver resolver = context.getContentResolver();
        final String[] projection = { MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_TITLE, MovieContract.MovieEntry.COLUMN_IDENTIFIER };

        Cursor cursor = resolver.query(MovieContract.MovieEntry.CONTENT_URI, projection, MovieContract.MovieEntry.COLUMN_IDENTIFIER + " == ?" ,
                new String[] { String.valueOf(mMovie.getId()) }, null);

        if(cursor.getCount() == 0)
            return -1;

        cursor.moveToFirst();

        // return the DB id if it's a match\

        return cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IDENTIFIER)) == mMovie.getId() ?
                cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID)) :
                -1;
    }


    /**
     * Used to update the database(insert/delete fav. movie)
     */
    private void updateDatabaseWithFavouriteMovie() {

        Uri mUri = MovieContract.MovieEntry.CONTENT_URI;

        if(isMovieAddedToFavourites() == -1){
            // Create new empty ContentValues object
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_IDENTIFIER, mMovie.getId());

            contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mMovie.getOriginalTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
            contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mMovie.getVoteAverage());
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, getImageLocalFile(mMovie.getPosterURL()).toString());
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_LAND, getImageLocalFile(mMovie.getPosterLandURL()).toString());

            // save locally the poster
            Picasso.with(context).load(mMovie.getPosterURL()).into(picassoImageTarget(context, "imageDir", getFileNameFromURL(mMovie.getPosterURL())));
            // save locally the poster landscape
            Picasso.with(context).load(mMovie.getPosterLandURL() ).into(picassoImageTarget(context, "imageDir", getFileNameFromURL(mMovie.getPosterLandURL())));



            // Insert the content values via a ContentResolver
            Uri uri = context.getContentResolver().insert(mUri, contentValues);

            // Display a success action toast
            if(uri != null) {
                Toast.makeText(context,
                        context.getResources().getString(R.string.successfully_saved_as_favourite_prefix)
                        + " " + mMovie.getTitle() + " "
                        + context.getResources().getString(R.string.successfully_saved_as_favourite_suffix),
                        Toast.LENGTH_LONG).show();
            }
        }else{
            mUri = mUri.buildUpon().appendPath( String.valueOf(isMovieAddedToFavourites()) ).build();

            int moviesDeleted = context.getContentResolver().delete(mUri, null, null);

            if(moviesDeleted > 0)
                Toast.makeText(context,
                        context.getResources().getString(R.string.successfully_deleted_from_favourite_prefix)
                                + " " + mMovie.getTitle() + " "
                                + context.getResources().getString(R.string.successfully_deleted_from_favourite_suffix)
                        , Toast.LENGTH_LONG).show();

            // delete saved images
            if (getImageLocalFile(mMovie.getPosterURL()).delete()
                    && getImageLocalFile(mMovie.getPosterLandURL()).delete()){
                //  Log.d(TAG, "Deleted successfully");
            }

        }
    }

    // returns the local file where the image is stored (for deleting, display etc)
    private File getImageLocalFile(String imageUrl){
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        return new File(directory, getFileNameFromURL(imageUrl));
    }

    /**
     * Trailers ViewHolder
     */
    public class ViewHolderTrailers extends RecyclerView.ViewHolder {
        TextView trailerName;

        // inflating the views into the holder
        public ViewHolderTrailers(final View itemView) {
            super(itemView);
            trailerName = itemView.findViewById(R.id.trailer_name);

        }

        // binding the views
        public void bindViews(final Context context, final int position) {
            trailerName.setText( trailers.get(position).getName() );

            if(trailers!= null && trailers.get(position).getSite().compareToIgnoreCase("YouTube") == 0)
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String video_path = "http://www.youtube.com/watch?v=" + trailers.get(position).getKey();
                        Uri uri = Uri.parse(video_path);

                        // With this line the Youtube application, if installed, will launch immediately.
                        // Without it you will be prompted with a list of the application to choose.
                        uri = Uri.parse("vnd.youtube:"  + uri.getQueryParameter("v"));

                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }
                });
            else
                trailerName.setText(trailers.get(position).getName() + " (n/a player)");
        }
    }

    /**
     * Trailers ViewHolder
     */
    public class ViewHolderReviews extends RecyclerView.ViewHolder {
        TextView authorName;
        TextView reviewContent;
        TextView reviewSource;

        // inflating the views into the holder
        public ViewHolderReviews(final View itemView) {
            super(itemView);
            authorName = itemView.findViewById(R.id.author_name);
            reviewContent = itemView.findViewById(R.id.review_content);
            reviewSource = itemView.findViewById(R.id.review_source);
        }

        // binding the views
        public void bindViews(Context context, int position) {
            if(reviews != null){
                authorName.setText( reviews.get(position).getAuthor() );
                reviewContent.setText( reviews.get(position).getContent() );
                reviewSource.setText( reviews.get(position).getUrl() );
            }
        }
    }


    public void updateDetails(Movie movie) {
        this.mMovie = movie;
        notifyDataSetChanged();
    }

    public void updateTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    public void updateReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }



    private Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir

//        final File directory = new File("file:///android_asset/");
//        final File directory = context.getFilesDir();
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName); // Create image file
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };
    }
}
