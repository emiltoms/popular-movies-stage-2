package com.example.mit.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mit.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry;

public class FavoriteMovieSQLDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = FavoriteMovieSQLDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "FAVORITE_MOVIES_DB.db";
    private static final int DATABASE_VERSION = 6;


    FavoriteMovieSQLDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_FAVORITE_MOVIES = "CREATE TABLE "
                + FavoriteMovieEntry.TABLE_NAME + " ("
                + FavoriteMovieEntry.KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FavoriteMovieEntry.NAME + " TEXT, "
                + FavoriteMovieEntry.MOVIE_ID + " INTEGER UNIQUE, "
                + FavoriteMovieEntry.THUMBNAIL + " TEXT, "
                + FavoriteMovieEntry.RELEASE_DATE + " TEXT, "
                + FavoriteMovieEntry.OVERVIEW + " TEXT, "
                + FavoriteMovieEntry.RATING + " TEXT, "
                + FavoriteMovieEntry.LOCALPATH + " TEXT"
                + ");";
        sqLiteDatabase.execSQL(CREATE_TABLE_FAVORITE_MOVIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(LOG_TAG, "Updating database from old version: " +
                i + ", to new version: " + i1);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
