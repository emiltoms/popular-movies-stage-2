package com.example.mit.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.mit.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry.*;

public class FavoriteMovieContentProvider extends ContentProvider {
    private static final String LOG_TAG = FavoriteMovieContentProvider.class.getSimpleName();

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = createUriMatcher();
    private FavoriteMovieSQLDbHelper favoriteMovieSQLDbHelper;

    public static UriMatcher createUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY,
                FavoriteMovieContract.PATH_TO_FAVORITE_MOVIES,
                MOVIES);
        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY,
                FavoriteMovieContract.PATH_TO_FAVORITE_MOVIES + "/#",
                MOVIE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        favoriteMovieSQLDbHelper = new FavoriteMovieSQLDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase database = favoriteMovieSQLDbHelper.getWritableDatabase();

        int matchUri = sUriMatcher.match(uri);

        Cursor cursor;

        switch (matchUri) {
            case MOVIES :
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case MOVIE_WITH_ID :
                // uri : content://<authority/directory/#
                //---------------------------/----0----/1
                String id = uri.getPathSegments().get(1);

                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                cursor = database.query(TABLE_NAME, projection, mSelection, mSelectionArgs,
                        null, null, sortOrder);

                break;
            default :
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase database = favoriteMovieSQLDbHelper.getWritableDatabase();

        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case MOVIE_WITH_ID:
                long id = database.insert(
                        TABLE_NAME,
                        null, contentValues);

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(
                            CONTENT_URI, id);
                } else {
                    throw new SQLException("Can't insert() uri: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("UNKNOWN URI! Uri = " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase database = favoriteMovieSQLDbHelper.getWritableDatabase();

        int delete;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_WITH_ID :
                // uri content://<authority>/directory/#
                //--------------------------/----0----/1
                String id = uri.getPathSegments().get(1);

                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                delete = database.delete(TABLE_NAME, mSelection, mSelectionArgs);

                Log.i(LOG_TAG, "Deleted row: "+delete);

                break;
            default :
                // This application will not be able to remove more than one row.
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return delete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
