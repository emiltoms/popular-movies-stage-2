package com.example.mit.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteMovieContract {

    public static final String AUTHORITY = "com.example.mit.popularmovies";

    public static final Uri BASE_CONTENT_PATH = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TO_FAVORITE_MOVIES = "movies";


    public static final class FavoriteMovieEntry implements BaseColumns {

        // Name of table in db
        public static final String TABLE_NAME = "FAVORITE_MOVIE_TABLE";

        // Names of each column in table
        public static final String KEY_ROWID = "_id";
        public static final String MOVIE_ID = "movie_id";
        public static final String THUMBNAIL = "thumbnail";
        public static final String NAME = "name";
        public static final String RELEASE_DATE = "release_date";
        public static final String OVERVIEW = "overview";
        public static final String RATING = "rating";
        public static final String LOCALPATH = "local_path";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_PATH.buildUpon().appendPath(PATH_TO_FAVORITE_MOVIES).build();

    }
}
