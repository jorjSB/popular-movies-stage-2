package com.udacity.georgebalasca.popularmoviesstage_2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.georgebalasca.popularmoviesstage_2.arrayadapters.MoviesListArrayAdapter;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Movie;
import com.udacity.georgebalasca.popularmoviesstage_2.utils.NetUtils;

import java.net.URL;
import java.util.ArrayList;

import static com.udacity.georgebalasca.popularmoviesstage_2.utils.JsonUtils.getMoviesArray;

public class MainActivity extends AppCompatActivity {

    private TextView noInternetTV;
    private GridView gridView;

    private int lastListPosition = -1;
    private final String LIST_POSITION_STATE_KEY = "list_position_state_key";
    private String order_by = NetUtils.SORT_BY_POPULAR;
    private final String ORDER_BY_STATE_KEY = "order_by_state_key";

    private Menu optionsMenu;
    private ArrayList<Movie> moviesArray;
    private final String MOVIES_ARRAY_STATE_KEY = "movies_array_state_key";
    private final String SHOW_FAVOURITES = "show_favourites";

    private  MoviesListArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noInternetTV = findViewById(R.id.no_internet);
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

        // if already have data, inflate view, else fetch data
        if(moviesArray != null && moviesArray.size() > 0)
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
        if(order_by.compareTo(SHOW_FAVOURITES) != 0){
            // Build my URL for fetching data based on my private key, and the type of data needed!
            URL initialMoviesListURL =  NetUtils.getMoviesListSortedUrl(getResources().getString(R.string.api_key_v3), order_by);

            // fetch result
            if(NetUtils.isOnline(this))
                new AsyncFetchData().execute(initialMoviesListURL);
            else
                noInternetTV.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(this, "Show saved movies from DB", Toast.LENGTH_LONG).show();
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

        adapter.updateAdapter(moviesArray);

        if(lastListPosition != -1)
            gridView.setSelection(lastListPosition);

    }
}
