package com.example.mit.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private static final Long ALREADY_IN_FAVORITES = (long) -3;
    private static final int BUTTON_DEACTIVATE = 0;
    private static final int BUTTON_ACTVIE = 1;
    private static final Long REMOVED_FROM_FAVORITES = (long) -4;
    private static final Long ERROR_FROM_DATABASE_OPERATION = (long) -5;
    private static final Long NOT_IN_FAVORITES = (long) -6;
    @BindView(R.id.thumbnail_image)
    ImageView thumbnailIV;
//    @BindView(R.id.title_tv)
//    TextView titleTV;
    @BindView(R.id.release_date_tv)
    TextView releaseDateTV;
    @BindView(R.id.movie_overview_tv)
    TextView movieOverviewTV;
    @BindView(R.id.rating_tv)
    TextView ratingTV;
    @BindString(R.string.msg_null_pointer_exception)
    String MSG_NULL_EXCEPTION;
    @BindString(R.string.intent_extra_name)
    String INTENT_EXTRA_NAME;
    @BindView(R.id.movie_detail_collapsing_image_view)
    ImageView collapsingImageView;
    @BindView(R.id.overview_read_more_tv)
    TextView readMoreTextView;
    @BindView(R.id.offline_recyclerView_alternative_textView)
            TextView recyclerViewOfflineTextView;
//
    ArrayList<String> trailersData;
//    TrailersAdapter trailersAdapter;

    //    Button favButton;

    FloatingActionButton favButton;
    FloatingActionButton favButtonBottom;

    private boolean isInFavorites;
    private Context context;
    private Movie selectedMovie;
    private boolean btnClicked;
    private Snackbar addSnackbar;
    private Snackbar removeSnackbar;
    private boolean isInternetConnection;

    NestedScrollView nestedScrollView;


    private RecyclerView recyclerView;
    private TrailersAdapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        context = this;
        try {
            selectedMovie = getIntent().getExtras().getParcelable(INTENT_EXTRA_NAME);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, MSG_NULL_EXCEPTION + e);
            Toast.makeText(this, MSG_NULL_EXCEPTION, Toast.LENGTH_SHORT).show();
            closeActivity();
        }
        nestedScrollView = findViewById(R.id.nested_scroll_view);
        nestedScrollView.scrollTo(0, 0);

        new ConnectToDatabase().execute(selectedMovie);

        favButton = findViewById(R.id.movie_floating_action_button);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favButton.setEnabled(false);
                btnClicked = true;
                new ConnectToDatabase().execute(selectedMovie);
            }
        });

        favButtonBottom = findViewById(R.id.movie_floating_action_button_bottom);
        favButtonBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favButton.callOnClick();
            }
        });

        if (!MainActivity.sortedBy.equals(context.getResources().getString(R.string.sorted_by_favorites))) {
            Picasso.with(context).load(selectedMovie.getThumbnail()).into(thumbnailIV);
            Picasso.with(context).load(selectedMovie.getThumbnail()).into(collapsingImageView);
        }   // else {Image will load in ConnectToDatabase class}


        releaseDateTV.setText(selectedMovie.getReleaseDate());
        ratingTV.setText(selectedMovie.getRating());
        final String fullOverview = selectedMovie.getMovieOverview();
        if (fullOverview.length() > 150) {
            movieOverviewTV.setText(fullOverview.substring(0, 100) + "...");
            readMoreTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    readMoreTextView.setVisibility(View.GONE);
                    movieOverviewTV.setText(fullOverview);
                }
            });
        } else {
            movieOverviewTV.setText(fullOverview);
            readMoreTextView.setVisibility(View.GONE);
        }

        Toolbar toolbar = findViewById(R.id.movie_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitleEnabled(true);
        collapsingToolbarLayout.setTitle(selectedMovie.getTitleMovie());

        AppBarLayout appBarLayout = findViewById(R.id.appBar);
        // https://stackoverflow.com/questions/31682310/android-collapsingtoolbarlayout-collapse-listener
        // https://stackoverflow.com/questions/38039574/set-fab-anchor-gravity-dynamically
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    favButtonBottom.show();
                } else {
                    favButtonBottom.hide();

                }
            }
        });

        View coordinateLayout = findViewById(R.id.coordinator_layout);
        addSnackbar = Snackbar.make(coordinateLayout, getResources().getString(R.string.msg_movie_added_to_favorite), Snackbar.LENGTH_LONG);
        removeSnackbar = Snackbar.make(coordinateLayout, getResources().getString(R.string.msg_movie_removed_from_favorite), Snackbar.LENGTH_LONG);

//        recyclerLayoutManager = new LinearLayoutManager(this);
//        trailersListView.setLayoutManager(recyclerLayoutManager);
//
//        recyclerAdapter = new TrailersAdapter(trailersList);
//        trailersListView.setAdapter(recyclerAdapter);

        recyclerView = (RecyclerView) findViewById(R.id.trailer_recyclerView);
//        recyclerView.setHasFixedSize(false);

        // If internetStatus = true, create list of trailers,
        // else show offline alternative.
        if (internetStatus()) {
            isInternetConnection = true;
            recyclerViewOfflineTextView.setVisibility(View.GONE);
            recyclerLayoutManager = new LinearLayoutManager(this);
            recyclerLayoutManager.setAutoMeasureEnabled(true);
            recyclerView.setLayoutManager(recyclerLayoutManager);
            recyclerView.setNestedScrollingEnabled(false);
        } else {
            isInternetConnection = false;
            recyclerViewOfflineTextView.setVisibility(View.VISIBLE);
        }

        trailersData = new ArrayList<>();
    }
    /**
     * This method check is device has connection to the internet.
     *
     * @return true if device has internet connection, false if not;
     */
    public boolean internetStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie, menu);

        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        setShareActionProvider();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
//            case R.id.menu_item_share :
//                Toast.makeText(context, "MenuItem SHARE clicked", Toast.LENGTH_SHORT).show();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setShareActionProvider() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, selectedMovie.getTitleMovie());
        intent.putExtra(Intent.EXTRA_TEXT, selectedMovie.getRating());
        intent.setType("text/plain");

        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(intent);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void closeActivity() {
        finish();
    }

    private void newAdnotation(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setButtonStatus(boolean newStatus) {
        if (newStatus) {
            favButton.setImageResource(R.drawable.ic_favorite_black_24dp);
            favButtonBottom.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            favButtonBottom.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }

    private byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }

    public void toTopButtonClicked(View view) {
        nestedScrollView.scrollTo(0, 0);
    }

    @SuppressLint("StaticFieldLeak")
    private class ConnectToDatabase extends AsyncTask<Movie, Integer, Long> {

        private ArrayList<String> loadTrailersData;
        private FavMoviesDbAdapter favMoviesDbAdapter;
        private Movie movie;

        @Override
        protected void onPreExecute() {
            favMoviesDbAdapter = new FavMoviesDbAdapter(context);
            try {
                favMoviesDbAdapter.open();
            } catch (SQLException e) {
                Log.e(LOG_TAG, "onPreExecute() : exception : e == " + e);
            }
        }

        @Override
        protected void onPostExecute(Long result) {
            favMoviesDbAdapter.close();

            if (isInternetConnection) {
                trailersData = loadTrailersData;
                recyclerAdapter = new TrailersAdapter(trailersData, context);
                recyclerView.setAdapter(recyclerAdapter);


            }

            Log.i(LOG_TAG, "onPostExecute; recyclerAdapter = " + recyclerAdapter);
            Log.i(LOG_TAG, "Aaand trailersData = " + trailersData);

            if (result > -1) {
                newAdnotation("Added to favorite. \n Movie db ID: " + result + "\n" + String.valueOf(this.movie.getMovieID()));
                setButtonStatus(true);
            } else if (result == -1) {
                newAdnotation("I CAN'T SAVE THIS MOVIE." + "\n" + result);
            } else if (result.equals(ALREADY_IN_FAVORITES)) {
                setButtonStatus(true);
            } else if (result.equals(ERROR_FROM_DATABASE_OPERATION)) {
                newAdnotation("Problem with database.");
                Log.w(LOG_TAG, "ERROR_FROM_DATABASE_OPERATION" + String.valueOf(ERROR_FROM_DATABASE_OPERATION));
            } else if (result.equals(NOT_IN_FAVORITES)) {
                setButtonStatus(false);
            } else if (result.equals(REMOVED_FROM_FAVORITES)) {
                newAdnotation("Removed from favorites.");
                setButtonStatus(false);
            }
            favButton.setEnabled(true);
        }

        @Override
        protected Long doInBackground(Movie... movie) {
            this.movie = movie[0];

            loadTrailersData = Utils.takeTrailers(this.movie.getMovieID());
//            trailersAdapter = new TrailersAdapter(MovieDetailActivity.this, trailersList);

            Cursor movieCursor = favMoviesDbAdapter.getMovie(this.movie.getMovieID());
            if (movieCursor.getCount() > 0) {
                // This movie is already in db.
                if (btnClicked) {
                    // If from user initiate
                    btnClicked = false;
                    if (favMoviesDbAdapter.removeMovie(this.movie.getMovieID())) {
                        return REMOVED_FROM_FAVORITES;
                    } else {
                        return ERROR_FROM_DATABASE_OPERATION;
                    }
                } else {
                    // If from onCreate initiate
                    loadLocalImage(this.movie.getImageLocalPath());
                    return ALREADY_IN_FAVORITES;
                }
            } else {
                // This movie is not in db.
                if (btnClicked) {
                    // If from User initiate
                    this.movie.setImageLocalPath(newLocalImagePath(this.movie.getMovieID(), this.movie.getThumbnail())); // Save image on hardware and get path to it.
                    Log.i(LOG_TAG, "imageLocalPath == " + this.movie.getImageLocalPath());
                    ContentValues values = new ContentValues();
                    values.put(FavMoviesDbAdapter.MOVIE_ID, this.movie.getMovieID());
                    values.put(FavMoviesDbAdapter.THUMBNAIL, this.movie.getThumbnail());
                    values.put(FavMoviesDbAdapter.NAME, this.movie.getTitleMovie());
                    values.put(FavMoviesDbAdapter.RELEASE_DATE, this.movie.getReleaseDate());
                    values.put(FavMoviesDbAdapter.OVERVIEW, this.movie.getMovieOverview());
                    values.put(FavMoviesDbAdapter.RATING, this.movie.getRating());
                    values.put(FavMoviesDbAdapter.LOCALPATH, this.movie.getImageLocalPath());

                    Log.i(LOG_TAG, "ConventValues == " + values);

                    return favMoviesDbAdapter.insertMovie(values);

                } else {
                    // This movie is not favorites (so it is not in database neither).
                    return NOT_IN_FAVORITES;
                }
            }
        }

        private void loadLocalImage(String localImagePath) {
            try {
                File f = new File(localImagePath, String.valueOf(this.movie.getMovieID()));
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                thumbnailIV.setImageBitmap(bitmap);
                collapsingImageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.w(LOG_TAG, "loadLocalImage, FileNotFoundException, e == " + e);
            }
        }

        private String newLocalImagePath(int movieID, String imageURL) {
            Bitmap bitmap = streamImageToBitmap(imageURL);
            String imageName = String.valueOf(movieID);


            // try nr 3. https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
            File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
            File newPath = new File(directory, imageName);

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(newPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            } catch (Exception e) {
                Log.w(LOG_TAG, "FileOutputStrem Exception e == " + e);
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.w(LOG_TAG, "FileOutputStrem, fileOutputStream.close(), Exception e == " + e);
                }
            }

            return directory.getAbsolutePath();


//            FileOutputStream outputStream = null;
//            File file = new File(context.getExternalFilesDir(
//                    Environment.DIRECTORY_PICTURES), "thumbnails");
//            if (file.mkdirs()) {
//                Log.i(LOG_TAG, "newLocalImagePath, new File, file.mkdirs == true");
//            }
//            String extStorageDirectory = file.toString();
//            File f = new File(extStorageDirectory, imageName);
//            String localImagePath = f.getAbsolutePath() + ".jpg";
//
//            try {
//                outputStream = new FileOutputStream(f);
//                assert bitmap != null;
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
//                newAdnotation("Image saved at "+file.getAbsolutePath());
//            } catch (FileNotFoundException e) {
//                Log.e(LOG_TAG, "newLocalImagePath, FileNotFoundException, e = "+e);
//                return "n/a";
//            }
//            try {
//                outputStream.flush();
//                outputStream.close();
//                return localImagePath;
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "newLocalImagePath, IOException, e :" +e);
//                return "n/a";
//            }


//            FileOutputStream outputStream = null;
//            try {
//                outputStream = openFileOutput(imageName, Context.MODE_PRIVATE);
//                outputStream.write(bitmap.getByteCount());
//                outputStream.close();
//            } catch (Exception e) {
//                Log.e(LOG_TAG, "saveImage : exception e = "+e);
//            }

//            File imageFile = getOutputMediaFile();
//            if (imageFile == null) {
//                Log.e(LOG_TAG, "Media Storage Permission.");
//                return null;
//            }
//            try {
//                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//                fileOutputStream.close();
//            } catch (FileNotFoundException e) {
//                Log.e(LOG_TAG, "saveImage : FileNotFoundExcetpion : e = " + e);
//                return "n/a";
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "saveImage : IOE : e = " + e);
//                return "n/a";
//            }

        }

//        private File getOutputMediaFile() {
//            File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
//            + "/Android/data"
//            + getApplicationContext().getPackageName()
//            + "/Files");
//
//            if (! mediaStorageDir.exists()) {
//                if (! mediaStorageDir.mkdirs()) {
//                    return null;
//                }
//            }
//
//            File mediaFile;
//            String imageName = String.valueOf(this.movie.getMovieID());
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator + imageName);
//
//            return mediaFile;
//        }

        private Bitmap streamImageToBitmap(String imageURL) {
            try {
                URL url = new URL(imageURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                return BitmapFactory.decodeStream(stream);
            } catch (Exception e) {
                Log.e(LOG_TAG, "streamImageToBitmap Exception e : " + e);
                return null;
            }
        }

    } // innerClass end here;

}
