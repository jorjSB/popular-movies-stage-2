package com.udacity.georgebalasca.popularmoviesstage_2.utils;

import com.udacity.georgebalasca.popularmoviesstage_2.models.Movie;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Review;
import com.udacity.georgebalasca.popularmoviesstage_2.models.Trailer;

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
     * Returns a list with all the "trailer" objects after parsing the response to json
     *
     * @param data string fetched from url(to be converted to Json)
     */
    public static ArrayList<Trailer> getTrailerArray(String data){

        // check if data
        if(data != null && data.isEmpty())
            return null;

        // try to convert string result to Json Object
        JSONObject tarilerJsonObj;
        try {
            tarilerJsonObj = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        // get the trailer json array
        JSONArray trailersJsonArray = tarilerJsonObj.optJSONArray("results");

        // create the array containing movie objects
        ArrayList<Trailer> trailerArrayList = new ArrayList<>();

        // add movie objects in the array created
        for(int i=0; i<trailersJsonArray.length(); i++){
            trailerArrayList.add( getTrailerObjectFromJsonObject( trailersJsonArray.optJSONObject(i) ) );
        }

        return trailerArrayList;
    }

    /**
     * Returns a list with all the "review" objects after parsing the response to json
     *
     * @param data string fetched from url(to be converted to Json)
     */
    public static ArrayList<Review> getReviewArray(String data){

        // check if data
        if(data != null && data.isEmpty())
            return null;

        // try to convert string result to Json Object
        JSONObject reviewJsonObj;
        try {
            reviewJsonObj = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        // get the trailer json array
        JSONArray reviewJsonArray = reviewJsonObj.optJSONArray("results");

        // create the array containing movie objects
        ArrayList<Review> reviewArrayList = new ArrayList<>();

        // add movie objects in the array created
        for(int i=0; i<reviewJsonArray.length(); i++){
            reviewArrayList.add( getReviewObjectFromJsonObject( reviewJsonArray.optJSONObject(i) ) );
        }

        return reviewArrayList;
    }


    /**
     * Used to determine the result of a Asynk Task
     *
     * @param data
     * @return
     */
    public static String getMovieExtrasType(String data) {

        // check if data
        if (data != null && data.isEmpty())
            return null;

        // try to convert string result to Json Object
        JSONObject reviewJsonObj;
        try {
            reviewJsonObj = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        // get the trailer json array
        JSONArray dataJsonArray = reviewJsonObj.optJSONArray("results");

        if(dataJsonArray != null && dataJsonArray.length() > 0)
            return dataJsonArray.optJSONObject(0).optString("site").isEmpty() ? "reviews" : "trailers";
        else
            return null;
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


    /**
     * Parses a jsonObject into a Trailer object
     *
     * @param data
     * @return
     */
    private static Trailer getTrailerObjectFromJsonObject(JSONObject data){

        Trailer trailer = new Trailer();

        trailer.setId(data.optString("id"));
        trailer.setKey(data.optString("key"));
        trailer.setName(data.optString("name"));
        trailer.setSite(data.optString("site"));
        trailer.setSize(data.optString("size"));
        trailer.setType(data.optString("type"));

        return trailer;
    }

    /**
     * Parses a jsonObject into a Review object
     *
     * @param data
     * @return
     */
    private static Review getReviewObjectFromJsonObject(JSONObject data){

        Review review = new Review();

        review.setId(data.optString("id"));
        review.setAuthor(data.optString("author"));
        review.setContent(data.optString("content"));
        review.setUrl(data.optString("url"));

        return review;
    }
}
