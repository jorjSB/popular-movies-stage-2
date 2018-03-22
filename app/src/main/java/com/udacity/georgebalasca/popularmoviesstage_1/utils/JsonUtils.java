package com.udacity.georgebalasca.popularmoviesstage_1.utils;

import com.udacity.georgebalasca.popularmoviesstage_1.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Jorj on 02/28/2018.
 *
 * Will be used to parse the response to json
 */

public class JsonUtils {

    /**
     * Returns a list with all movie objects after parsing the response to json
     *
     * @param data string fetched from url
     * @param api_key - needed to get the poster URL
     */
    public static ArrayList<Movie> getMoviesArray(String api_key, String data){

        // check if data
        if(data != null && data.isEmpty())
            return null;

        // try to convert string result to Json Object
        JSONObject movies;
        try {
            movies = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        // get the movie json array
        JSONArray moviesJsonArray = movies.optJSONArray("results");

        // create the array containing movie objects
        ArrayList<Movie> moviesArray = new ArrayList<>();

        // add movie objects in the array created
        for(int i=0; i<moviesJsonArray.length(); i++){
            moviesArray.add( getMovieObjectFromJsonObject( api_key, moviesJsonArray.optJSONObject(i) ) );
        }

        return moviesArray;
    }

    /**
     * Parses a jsonObject into a Movie object
     *
     * @param data - data fetched from url
     * @param api_key - needed to get the poster URL
     */
    private static Movie getMovieObjectFromJsonObject(String api_key, JSONObject data){

        Movie movie = new Movie();

        String posterUrl = NetUtils.getMoviePosterURL( api_key ,data.optString("poster_path").replaceAll("/","")).toString();
        String posterLandUrl = NetUtils.getMoviePosterURL( api_key ,data.optString("backdrop_path").replaceAll("/","")).toString();

        movie.setId(data.optInt("id"));
        movie.setvoteAverage( BigDecimal.valueOf(data.optDouble("vote_average")).floatValue() );
        movie.setTitle(data.optString("title"));
        movie.setOriginalTitle(data.optString("title"));
        movie.setOverview(data.optString("overview"));
        movie.setReleaseDate(data.optString("release_date"));
        movie.setPosterURL( posterUrl );
        movie.setPosterLandURL( posterLandUrl );

        return movie;
    }
}
