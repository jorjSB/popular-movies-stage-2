package com.udacity.georgebalasca.popularmoviesstage_2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

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
    private Movie movieDetails;
    private ArrayList<Trailer> trailerArrayList;
    private ArrayList<Review> reviewArrayList;

    // private final keys used for bundle passing (save/restore)
    private static  final String MOVIE_DETAILS_KEY = "MOVIE_DETAILS_KEY";
    private static  final String MOVIE_TRAILERS_ARRAY_KEY = "MOVIE_TRAILERS_ARRAY_KEY";
    private static  final String MOVIE_REVIEWS_ARRAY_KEY = "MOVIE_REVIEWS_ARRAY_KEY";
    private static  final String BUNDLE_RECYCLER_LAYOUT = "BUNDLE_RECYCLER_LAYOUT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // get the view ref.
        recyclerView = findViewById(R.id.recycler_view);

        movieDetailsArrayAdapter = new MovieDetailsArrayAdapter(null, null, null, this);
        recyclerView.setAdapter(movieDetailsArrayAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // If not all data loaded(1's access - load data from intent's extras and fetch details from the internet)
        if(trailerArrayList == null && reviewArrayList == null ){
            Bundle data = getIntent().getExtras();
            movieDetails =  data != null ? (Movie) data.getParcelable("movie") : null ;
            if(movieDetails != null)
                inflateMovieDetailsAndFetchExtras();
        }
        // if data is available(eg: orientation changed) notify adapter
        else {
            if(movieDetails != null)
                movieDetailsArrayAdapter.updateDetails(movieDetails);
            if(trailerArrayList != null)
                movieDetailsArrayAdapter.updateTrailers(trailerArrayList);
            if(reviewArrayList != null)
                movieDetailsArrayAdapter.updateReviews(reviewArrayList);
        }

    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(MOVIE_DETAILS_KEY, movieDetails);
        outState.putParcelableArrayList(MOVIE_TRAILERS_ARRAY_KEY, trailerArrayList);
        outState.putParcelableArrayList(MOVIE_REVIEWS_ARRAY_KEY, reviewArrayList);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());

    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null)
        {
            if(movieDetails == null)
                movieDetails = savedInstanceState.getParcelable(MOVIE_DETAILS_KEY);

            trailerArrayList = savedInstanceState.getParcelableArrayList(MOVIE_TRAILERS_ARRAY_KEY);
            reviewArrayList = savedInstanceState.getParcelableArrayList(MOVIE_REVIEWS_ARRAY_KEY);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT));
        }

    }

    /**
     * Inflate the array Adapter without the trailers or reviews (temporary)
     */
    private void inflateMovieDetailsAndFetchExtras() {
        movieDetailsArrayAdapter.updateDetails(movieDetails);

        if(NetUtils.isOnline(this)){
            loadMovieTrailers();
            loadMovieReviews();
        }else
            Toast.makeText(this, getResources().getString(R.string.no_interet_movie_details), Toast.LENGTH_SHORT).show();
    }

    /**
     * Loads the movieDetails's extras
     */
    private void loadMovieTrailers() {
        // Build my URL for fetching movieDetails's extra videos
        URL movieExtraVideos =  NetUtils.getMovieVideosURL(getResources().getString(R.string.api_key_v3), movieDetails.getId() );
        new AsyncFetchData().execute(movieExtraVideos);
    }

    /**
     * Loads the movieDetails's extras
     */
    private void loadMovieReviews() {
        // Build my URL for fetching movieDetails's extra comments
        URL movieExtraReviews =  NetUtils.getMovieReviewsURL(getResources().getString(R.string.api_key_v3), movieDetails.getId());
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
                if(typeOfData != null && typeOfData.compareTo("trailers") == 0){
                    trailerArrayList = JsonUtils.getTrailerArray(data);
                    movieDetailsArrayAdapter.updateTrailers(trailerArrayList);
                } else if (typeOfData != null && typeOfData.compareTo("reviews") == 0){
                    reviewArrayList = JsonUtils.getReviewArray(data);
                    movieDetailsArrayAdapter.updateReviews(reviewArrayList);
                }
            }
        }
    }

}
