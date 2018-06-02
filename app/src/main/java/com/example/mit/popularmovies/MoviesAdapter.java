package com.example.mit.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.mit.popularmovies.data.FavoriteMovieContract;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

class MoviesAdapter extends ArrayAdapter<Movie> {

    private Context context;

    MoviesAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View converterView, @NonNull ViewGroup parent) {
        View listItemView = converterView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_grid, parent, false);
        }

        Movie currentMovie = getItem(position);

        ImageView imageView = listItemView.findViewById(R.id.image_view_item_grid);
        assert currentMovie != null : context.getString(R.string.adapter_error_currentMovie_is_null);

        if (MainActivity.sortedBy.equals(context.getResources().getString(R.string.sorted_by_favorites))) {
            loadLocalImage(currentMovie, imageView);
        } else {
            Picasso.with(context).load(currentMovie.getThumbnail()).into(imageView);
        }

        ImageView favIcon = listItemView.findViewById(R.id.image_view_item_grid_favorite);

//        FavMoviesDbAdapter favMoviesDbAdapter = new FavMoviesDbAdapter(context);
//        try {
//            favMoviesDbAdapter.open();
//        } catch (SQLException e) {
//            Log.e(MoviesAdapter.class.getSimpleName(), "MoviesAdapter; onPreExecute() : exception : e == " + e);
//        }
//        Cursor cursor = favMoviesDbAdapter.getMovie(currentMovie.getMovieID());
//        if (cursor.getCount() > 0) {
//            favIcon.setVisibility(View.VISIBLE);
//        } else {
//            favIcon.setVisibility(View.GONE);
//        }

//        String[] selectionArgs = new String[]{String.valueOf(currentMovie.getMovieID())};

        Cursor cursor = context.getContentResolver().query(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI, null,
                FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID
                        + "="
                        + currentMovie.getMovieID(),
                null, null);

        if (cursor != null && cursor.getCount() > 0) {
            favIcon.setVisibility(View.VISIBLE);
        } else {
            favIcon.setVisibility(View.INVISIBLE);
        }

        if (cursor != null) {
            cursor.close();
        }

        return listItemView;
    }

    private void loadLocalImage(Movie movie, ImageView imageView) {
        String localImagePath = movie.getImageLocalPath();
        String imageName = String.valueOf(movie.getMovieID());
        try {
            File f = new File(localImagePath, imageName);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.w(MoviesAdapter.class.getSimpleName(), "loadLocalImage, FileNotFoundException, e == " + e);
        }
    }

}
