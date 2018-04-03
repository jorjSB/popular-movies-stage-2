package com.udacity.georgebalasca.popularmoviesstage_2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.udacity.georgebalasca.popularmoviesstage_2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";


    /**
     * Movies Contract - define the content of the movies table
     */
    public static final class MovieEntry implements BaseColumns {

        // MovieEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        // table name
        public static final String TABLE_NAME = "movies";

        // columns
        public static final String COLUMN_IDENTIFIER = "identifier";
        public static final String COLUMN_TITLE = "title";

    }
}
