package com.udacity.georgebalasca.popularmoviesstage_2;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.georgebalasca.popularmoviesstage_2.arrayadapters.MoviesListArrayAdapter;
import com.udacity.georgebalasca.popularmoviesstage_2.data.MovieContract;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Movie;
import com.udacity.georgebalasca.popularmoviesstage_2.utils.NetUtils;

import java.net.URL;
import java.util.ArrayList;

import static com.udacity.georgebalasca.popularmoviesstage_2.utils.JsonUtils.getMoviesArray;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private TextView noInternetTV;
    private TextView noFavouritesTV;
    private GridView gridView;

    private int lastListPosition = -1;
    private final String LIST_POSITION_STATE_KEY = "list_position_state_key";
    private String order_by = NetUtils.SORT_BY_POPULAR;
    private final String ORDER_BY_STATE_KEY = "order_by_state_key";

    private Menu optionsMenu;
    private ArrayList<Movie> moviesArray;
    private final String MOVIES_ARRAY_STATE_KEY = "movies_array_state_key";
    private final String SHOW_FAVOURITES = "show_favourites";
    // db's loader id
    private final int MOVIES_LOADER_ID = 0;
    private Cursor moviesCursor;

    private  MoviesListArrayAdapter adapter;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noInternetTV = findViewById(R.id.no_internet);
        noFavouritesTV = findViewById(R.id.no_favourites_movies_tv);
        gridView = findViewById(R.id.movies_grid);

        moviesArray = new ArrayList<>();

        adapter = new MoviesListArrayAdapter(this,
                moviesArray);

        // attach the adapter to the GridView
        gridView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: CHECK IF ANY MORE SAVED MOVIES INTO THE DB
        // TODO: after adding the first movie, it shows the results from DB

        // if already have data, inflate view, else fetch data
        if(moviesArray != null && moviesArray.size() > 0 && !order_by.equals(SHOW_FAVOURITES))
            updateGridView();
        else
            loadMoviesData();


    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if(gridView != null)
            lastListPosition = gridView.getFirstVisiblePosition();

        // Save the state of item position
        outState.putInt(LIST_POSITION_STATE_KEY, lastListPosition);
        outState.putString(ORDER_BY_STATE_KEY, order_by);
        outState.putParcelableArrayList(MOVIES_ARRAY_STATE_KEY, moviesArray);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Read the state of item position
        lastListPosition = savedInstanceState.getInt(LIST_POSITION_STATE_KEY);
        order_by = savedInstanceState.getString(ORDER_BY_STATE_KEY);
        moviesArray = savedInstanceState.getParcelableArrayList(MOVIES_ARRAY_STATE_KEY);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        optionsMenu = menu;

        // sets the menu item state
        setMenuItemstate();

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // sets the global variable: order_by
        if (id == R.id.most_popular)
            order_by = NetUtils.SORT_BY_POPULAR;
        else if (id == R.id.top_rated)
            order_by = NetUtils.SORT_BY_TOP_RATED;
        else if (id == R.id.favourites)
            order_by = SHOW_FAVOURITES;

        // fetch new data data
        loadMoviesData();
        // sets the menu item state
        setMenuItemstate();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the menu item clicked as disabled so the user can click only on the other options(s)
     *
     */
    private void setMenuItemstate() {

        // get the item that has to be disabled
        MenuItem selectedMenuItem = optionsMenu.findItem(R.id.most_popular);
        if (order_by.equals(NetUtils.SORT_BY_POPULAR))
            selectedMenuItem  = optionsMenu.findItem(R.id.most_popular);
        else if(order_by.equals(NetUtils.SORT_BY_TOP_RATED))
            selectedMenuItem  = optionsMenu.findItem(R.id.top_rated);
        else if(order_by.equals(SHOW_FAVOURITES))
            selectedMenuItem  = optionsMenu.findItem(R.id.favourites);

        // set the item as disabled, enable the rest



        for(int i=0; i<optionsMenu.size(); i++)
            if (optionsMenu.getItem(i).equals(selectedMenuItem ))
                optionsMenu.getItem(i).setEnabled(false);
            else
                optionsMenu.getItem(i).setEnabled(true);

    }

    /**
     * Creates the initial url and loads the movie data
     *
     */
    private void loadMoviesData() {
        // if showing movies by order - load them from web
        if(order_by.compareTo(SHOW_FAVOURITES) != 0){
            // Build my URL for fetching data based on my private key, and the type of data needed!
            URL initialMoviesListURL =  NetUtils.getMoviesListSortedUrl(getResources().getString(R.string.api_key_v3), order_by);
            // fetch result
            if(NetUtils.isOnline(this)){
                new AsyncFetchData().execute(initialMoviesListURL);
                noInternetTV.setVisibility(View.GONE);
            } else {
                noInternetTV.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.GONE);
                noFavouritesTV.setVisibility(View.GONE);
            }
        // if showing saved movies - load them from DB
        }else if(order_by.equals(SHOW_FAVOURITES)){
            if(moviesCursor == null)
                getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
            else
                getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
        }
    }

    /**
     * AsyncTask to fetch data from the internet (avoid networking in main tread) add inflate the views
     */
    public class AsyncFetchData extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... params) {
            try {
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
                moviesArray = getMoviesArray(getResources().getString(R.string.api_key_v3), data);
                updateGridView();
            }
        }
    }

    /**
     * Create adapter and inflate the grid view
     */
    private void updateGridView() {
        // reset views
        noFavouritesTV.setVisibility(View.GONE);
        gridView.setVisibility(View.VISIBLE);

        // notify adapter, update data
        adapter.updateAdapter(moviesArray);

        // scroll to position
        if(lastListPosition != -1)
            gridView.setSelection(lastListPosition);

    }

    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mMoviesData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mMoviesData != null && !order_by.equals(SHOW_FAVOURITES)) {
                    // Use cached data
                    deliverResult(mMoviesData);
                } else {
                    // We have no data, so kick off loading it
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Query and load all movie data in the background
                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mMoviesData = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        updateCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        updateCursor(null);
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public void updateCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (moviesCursor == c)
            return; // bc nothing has changed

        moviesCursor = c; // new cursor value assigned
        if(moviesCursor != null && moviesCursor.getCount() > 0) {
            parseCursorToMovies();
            noFavouritesTV.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        } else {
            noFavouritesTV.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            noInternetTV.setVisibility(View.GONE);
            moviesArray.clear();
        }


    }

    // parse the cursor(DB results) into the movies array
    private void parseCursorToMovies() {

        // Indices for identifier and title
        int movieIdentifierIndex = moviesCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IDENTIFIER);
        int movieTitleIndex = moviesCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int movieOriginalTitleIndex = moviesCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        int movieOverviewIndex = moviesCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        int movieReleaseDateIndex = moviesCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        int movieVoteAverageIndex = moviesCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int moviePosterIndex = moviesCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
        int moviePosterLandscapeIndex = moviesCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_LAND);

        moviesCursor.moveToFirst();
        ArrayList<Movie> tempMoviesArray = new ArrayList<>();

        for (int i = 0; i<moviesCursor.getCount(); i++){
            Movie tempMovie = new Movie();
            tempMovie.setId(moviesCursor.getInt(movieIdentifierIndex));
            tempMovie.setTitle(moviesCursor.getString(movieTitleIndex));
            tempMovie.setOriginalTitle(moviesCursor.getString(movieOriginalTitleIndex));
            tempMovie.setVoteAverage(moviesCursor.getFloat(movieVoteAverageIndex));
            tempMovie.setOverview(moviesCursor.getString(movieOverviewIndex));
            tempMovie.setReleaseDate(moviesCursor.getString(movieReleaseDateIndex));
            tempMovie.setPosterURL(moviesCursor.getString(moviePosterIndex));
            tempMovie.setPosterLandURL(moviesCursor.getString(moviePosterLandscapeIndex));

            tempMoviesArray.add(tempMovie);
            moviesCursor.moveToNext();
        }

        // recreate the list only in case we have fav. movies
        if(!tempMoviesArray.isEmpty()) {
            // handle visibility
            gridView.setVisibility(View.VISIBLE);
            noInternetTV.setVisibility(View.GONE);
            noFavouritesTV.setVisibility(View.GONE);

            // update adapter
            moviesArray.clear();
            moviesArray = new ArrayList<>(tempMoviesArray);

            // moviesArray.addAll(tempMoviesArray);
            adapter.updateAdapter(moviesArray);
        }
    }
}
