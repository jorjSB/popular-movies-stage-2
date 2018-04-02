package com.udacity.georgebalasca.popularmoviesstage_2.arrayadapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.georgebalasca.popularmoviesstage_2.R;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Movie;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Review;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Trailer;

import java.util.ArrayList;

public class MovieDetailsArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_DETAILS = 0;
    private final int VIEW_TYPE_TRAILERS = 1;
    private final int VIEW_TYPE_REVIEWS = 2;

    private Movie mMovie;
    private ArrayList<Trailer> trailers;
    private ArrayList<Review> reviews;
    private Context context;

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

        // inflating the views into the holder
        public ViewHolderDetails(final View itemView) {
            super(itemView);

            posterIv = itemView.findViewById(R.id.movie_poster);
            movieTitleTv = itemView.findViewById(R.id.movie_title);
            voteAverageTv = itemView.findViewById(R.id.vote_average);
            movieDescriptionTv = itemView.findViewById(R.id.movie_description);
            originalTitleTv = itemView.findViewById(R.id.original_title);
            releaseDateTv = itemView.findViewById(R.id.release_date);
        }

        // binding the views
        public void bindViews(Context context) {
            Picasso.with(context).load(mMovie.getPosterLandURL()).into(posterIv);
            movieTitleTv.setText(mMovie.getTitle());
            voteAverageTv.setText(String.valueOf(mMovie.getVoteAverage()));
            movieDescriptionTv.setText(mMovie.getOverview());
            originalTitleTv.setText(mMovie.getOriginalTitle());
            releaseDateTv.setText(mMovie.getReleaseDate());
        }
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
        public void bindViews(Context context, int position) {
            trailerName.setText( trailers.get(position).getName() );
        }
    }

    /**
     * Trailers ViewHolder
     */
    public class ViewHolderReviews extends RecyclerView.ViewHolder {
        TextView authorName;

        // inflating the views into the holder
        public ViewHolderReviews(final View itemView) {
            super(itemView);
            authorName = itemView.findViewById(R.id.author_name);
        }

        // binding the views
        public void bindViews(Context context, int position) {
            authorName.setText( reviews.get(position).getAuthor() );
        }
    }


    public void updateVideos(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    public void updateReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }
}
