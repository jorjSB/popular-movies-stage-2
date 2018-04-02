package com.udacity.georgebalasca.popularmoviesstage_2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.georgebalasca.popularmoviesstage_2.arrayadapters.MovieDetailsArrayAdapter;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Movie;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Review;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Trailer;
import com.udacity.georgebalasca.popularmoviesstage_2.utils.JsonUtils;
import com.udacity.georgebalasca.popularmoviesstage_2.utils.NetUtils;

import java.net.URL;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieDetailsArrayAdapter movieDetailsArrayAdapter;
    private Movie movie;
    private ArrayList<Trailer> trailerArrayList;
    private ArrayList<Review> reviewArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // get the rv
        recyclerView = findViewById(R.id.recycler_view);


        Bundle data = getIntent().getExtras();
        movie =  data != null ? (Movie) data.getParcelable("movie") : null ;

        if(movie!=null)
            inflateView();

        // Get movie's extra details: trailers, reviews or show a toast if no internet.
        if(NetUtils.isOnline(this)){
            loadMovieTrailers();
            loadMovieReviews();
        }else
            Toast.makeText(this, getResources().getString(R.string.no_interet_movie_details), Toast.LENGTH_LONG).show();

    }

    /**
     * Inflate the array Adapter without the trailers or reviews (temporary)
     */
    private void inflateView() {
        movieDetailsArrayAdapter = new MovieDetailsArrayAdapter(movie, null, null, this);
        recyclerView.setAdapter(movieDetailsArrayAdapter);
    }

    /**
     * Loads the movie's extras
     */
    private void loadMovieTrailers() {
        // Build my URL for fetching movie's extra videos
        URL movieExtraVideos =  NetUtils.getMovieVideosURL(getResources().getString(R.string.api_key_v3), movie.getId() );

        new AsyncFetchData().execute(movieExtraVideos);
    }

    /**
     * Loads the movie's extras
     */
    private void loadMovieReviews() {
        // Build my URL for fetching movie's extra comments
        URL movieExtraReviews =  NetUtils.getMovieReviewsURL(getResources().getString(R.string.api_key_v3), movie.getId());

        new AsyncFetchData().execute(movieExtraReviews);
    }

    /**
     * AsyncTask to fetch data from the internet (avoid networking in main tread) add inflate the views
     */
    public class AsyncFetchData extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... params) {
            try {
                Log.d("Url: ", " " + params[0]);
                // use netUtils to fetch data from url param provided
                return NetUtils.getResponseFromHttpUrl(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String data) {
            if (data != null) {

                String typeOfData = JsonUtils.getMovieExtrasType(data);
                // Q: is there a better way to determine the result of the asynk task? (maybe a way to aknowledge which method called it?)
                if(typeOfData != null && typeOfData.compareTo("trailers") == 0)
                    movieDetailsArrayAdapter.updateVideos(JsonUtils.getTrailerArray(data));
                else if (typeOfData != null && typeOfData.compareTo("reviews") == 0)
                    movieDetailsArrayAdapter.updateReviews(JsonUtils.getReviewArray(data));
            }
        }
    }

}
