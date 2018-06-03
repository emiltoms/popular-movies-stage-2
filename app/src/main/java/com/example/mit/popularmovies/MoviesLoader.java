package com.example.mit.popularmovies;

import android.annotation.SuppressLint;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.mit.popularmovies.data.FavoriteMovieContract;

import java.util.ArrayList;
import java.util.List;

import static com.example.mit.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry.LOCALPATH;
import static com.example.mit.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID;
import static com.example.mit.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry.NAME;
import static com.example.mit.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry.OVERVIEW;
import static com.example.mit.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry.RATING;
import static com.example.mit.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry.RELEASE_DATE;
import static com.example.mit.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry.THUMBNAIL;

class MoviesLoader extends AsyncTaskLoader<List<Movie>> {

    private static final String NO_DATA = "n/a";
    private static final String BY_FAVORITES = "favorites";
    private String sortedBy;
    @SuppressLint("StaticFieldLeak")
    private Context context;

    MoviesLoader(Context context, String sortedBy) {
        super(context);
        this.context = context;
        this.sortedBy = sortedBy;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if (sortedBy.equals(BY_FAVORITES)) {
            List<Movie> movies = new ArrayList<>();

            Cursor cursor = context.getContentResolver().query(
                    FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie(0, NO_DATA, NO_DATA, NO_DATA, NO_DATA, NO_DATA, NO_DATA);
                    movie.setMovieID(cursor.getInt(cursor.getColumnIndex(MOVIE_ID)));
                    movie.setThumbnail(cursor.getString(cursor.getColumnIndex(THUMBNAIL)));
                    movie.setTitleMovie(cursor.getString(cursor.getColumnIndex(NAME)));
                    movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(RELEASE_DATE)));
                    movie.setMovieOverview(cursor.getString(cursor.getColumnIndex(OVERVIEW)));
                    movie.setRating(cursor.getString(cursor.getColumnIndex(RATING)));
                    movie.setImageLocalPath(cursor.getString(cursor.getColumnIndex(LOCALPATH)));

                    movies.add(movie);
                } while (cursor.moveToNext());
            }

            if (cursor != null) {
                cursor.close();
            }

            return movies;
        } else {
            List<Movie> list = Utils.takeMovies(sortedBy);
            Log.i(MoviesLoader.class.getSimpleName(), " List<Movie> list = Utils.takeMovies(sortedBy) = " + list);
            return list;
        }
    }
}
