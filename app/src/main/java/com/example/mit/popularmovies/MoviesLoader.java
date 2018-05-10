package com.example.mit.popularmovies;

import android.annotation.SuppressLint;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class MoviesLoader extends AsyncTaskLoader<List<Movie>> {

    private String sortedBy;
    private static final String BY_FAVORITES = "favorites";
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
//          return Utils.takeMovies(sortedBy);
        if (sortedBy.equals(BY_FAVORITES)) {
            List<Movie> movies = new ArrayList<>();
            FavMoviesDbAdapter favMoviesDbAdapter = new FavMoviesDbAdapter(context);
            favMoviesDbAdapter.open();

            Cursor cursor = favMoviesDbAdapter.getMovies();
            if (cursor.moveToFirst()) {
                do {
                    Movie movieFromCursor = FavMoviesDbAdapter.getMovieFromCursor(cursor);
                    movies.add(movieFromCursor);
                } while (cursor.moveToNext());
            }

            favMoviesDbAdapter.close();
            cursor.close();
            return movies;
        } else {
            List<Movie> list = Utils.takeMovies(sortedBy);
            Log.i(MoviesLoader.class.getSimpleName(), " List<Movie> list = Utils.takeMovies(sortedBy) = "+list);
            return list;
        }
    }
}
