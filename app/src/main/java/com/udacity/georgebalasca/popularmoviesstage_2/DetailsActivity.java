package com.udacity.georgebalasca.popularmoviesstage_2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Movie;

public class DetailsActivity extends AppCompatActivity {

    private ImageView posterIv;
    private TextView movieTitleTv;
    private TextView voteAverageTv;
    private TextView movieDescriptionTv;
    private TextView originalTitleTv;
    private TextView releaseDateTv;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        posterIv = findViewById(R.id.movie_poster);
        movieTitleTv = findViewById(R.id.movie_title);
        voteAverageTv = findViewById(R.id.vote_average);
        movieDescriptionTv = findViewById(R.id.movie_description);
        originalTitleTv = findViewById(R.id.original_title);
        releaseDateTv = findViewById(R.id.release_date);

        Bundle data = getIntent().getExtras();
        movie =  data != null ? (Movie) data.getParcelable("movie") : null ;

        if(movie!=null)
            inflateView();

    }

    /**
     * Inflate the graphic views
     */
    private void inflateView() {
        Picasso.with(this)
                .load(movie.getPosterLandURL())
                .into(posterIv);

        movieTitleTv.setText(movie.getTitle());
        voteAverageTv.setText( String.valueOf(movie.getVoteAverage()));
        movieDescriptionTv.setText(movie.getOverview());
        originalTitleTv.setText(movie.getOriginalTitle());
        releaseDateTv.setText(movie.getReleaseDate());
    }
}
