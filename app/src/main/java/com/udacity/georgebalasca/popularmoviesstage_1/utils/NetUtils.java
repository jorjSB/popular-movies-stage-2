package com.udacity.georgebalasca.popularmoviesstage_1.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


/**
 * Created by george.balasca on 18/02/2018.
 */

public class NetUtils{

    // params needed
    private static final String API_KEY_KEY = "api_key";
    private static final String LANGUAGE_KEY =  "language";
    private static final String LANGUAGE_VAL =  "en-US";

    // https://api.themoviedb.org/3/movie/popular?api_key=****************&language=en-US&page=1
    private static final String SCHEME =  "https";
    private static final String API_BASE_URL =  "api.themoviedb.org";
    private static final String API_VERSION =  "3";
    private static final String TYPE_MOVIE =  "movie";
    public static final String SORT_BY_POPULAR =  "popular";
    public static final String SORT_BY_TOP_RATED =  "top_rated";

    // https://image.tmdb.org/t/p/w185/kqjL17yufvn9OVLyXYpvtyrFfak.jpg
    private static final String IMAGES_BASE_URL =  "image.tmdb.org";
    private static final String PATH_T =  "t";
    private static final String PATH_P =  "p";
    private static final String MOVIE_POSTER_SIZE = "w185"; //"w185"


    /**
     * Method to build URL for the movies list(with sorting). Chose to add to avoid confusion about params in the buildUrl(..) method.
     * @param api_key_value
     * @param sort_by
     * @return
     */
    public static URL getMoviesListSortedUrl(String api_key_value, String sort_by) {
        return buildUrl(api_key_value, sort_by, null);
    }

    /**
     * Method to build URL for the movies posters. Chose to add to avoid confusion about params in the buildUrl(..) method.
     * @param api_key_value
     * @param poster_path
     * @return
     */
    public static URL getMoviePosterURL(String api_key_value, String poster_path) {
        return buildUrl(api_key_value, null, poster_path);
    }

    /**
     * Build my URL using URI Parse. Based on params received decide what URL should build!
     *
     * @param api_key_value
     * @param sort_by
     * @param poster_path
     * @return
     */
    private static URL buildUrl(String api_key_value, String sort_by, String poster_path) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME);

        // build URL for sorted movies list
        if(sort_by != null && poster_path == null){
            builder.authority(API_BASE_URL)
                   .appendPath(API_VERSION)
                   .appendPath(TYPE_MOVIE)
                   .appendPath(sort_by)
                   .appendQueryParameter(API_KEY_KEY, api_key_value)
                   .appendQueryParameter(LANGUAGE_KEY, LANGUAGE_VAL);
        // build URL for movie poster
        } else if(poster_path != null){
            builder.authority(IMAGES_BASE_URL)
                   .appendPath(PATH_T)
                   .appendPath(PATH_P)
                   .appendPath(MOVIE_POSTER_SIZE)
                   .appendPath(poster_path);
        }

        URL url = null;
        try {
            url = new URL(builder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Call a httpUrl and return the response as a string
     *
     * @param url
     * @return
     * @throws IOException
     *
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     *  Check if connection to internet is available
     * @return
     */
    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
