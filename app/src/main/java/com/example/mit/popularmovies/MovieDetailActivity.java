package com.example.mit.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mit.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry;
import com.squareup.picasso.Picasso;

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
    private static final Long REMOVED_FROM_FAVORITES = (long) -4;
    private static final Long ERROR_FROM_DATABASE_OPERATION = (long) -5;
    private static final Long NOT_IN_FAVORITES = (long) -6;
    @BindView(R.id.thumbnail_image)
    ImageView thumbnailIV;
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
    @BindView(R.id.movie_review_tv)
    TextView movieReviewTV;
    @BindView(R.id.review_read_more_tv)
    TextView reviewReadMoreTv;

    ArrayList<String> trailersData;
    ArrayList<String> reviewsData;
    FloatingActionButton favButton;
    FloatingActionButton favButtonBottom;
    NestedScrollView nestedScrollView;
    ShareActionProvider shareActionProvider;
    private Context context;
    private Movie selectedMovie;
    private boolean btnClicked;
    private Snackbar addSnackbar;
    private Snackbar removeSnackbar;
    private boolean isInternetConnection;
    private RecyclerView recyclerView;
    private TrailersAdapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

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

        recyclerView = findViewById(R.id.trailer_recyclerView);

        // If internetStatus = true, create list of trailers,
        // else show offline alternative.
        if (internetStatus()) {
            isInternetConnection = true;
            recyclerViewOfflineTextView.setVisibility(View.GONE);
            recyclerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
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
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setShareActionProvider() {
        String subject = "Check out this movie: " + selectedMovie.getTitleMovie();
        String text = "Check out this movie!\n" +
                "It's from " + selectedMovie.getReleaseDate() +
                " and has rating " + selectedMovie.getRating() +
                " in Themoviedb.org.\n" +
                "See by yourself: \n" +
                "https://www.themoviedb.org/movie/" + selectedMovie.getMovieID();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");

        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(intent);
        }
    }

//    public void setReviews()

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

    public void toTopButtonClicked(View view) {
        nestedScrollView.scrollTo(0, 0);
    }

    @SuppressLint("StaticFieldLeak")
    private class ConnectToDatabase extends AsyncTask<Movie, Integer, Long> {

        Cursor cursor;
        private ArrayList<String> loadTrailersData;
        private Movie movie;

        @Override
        protected void onPreExecute() {
            Log.i(LOG_TAG, "AsyncTasks Started.");
        }

        @Override
        protected void onPostExecute(Long result) {

            if (isInternetConnection) {
                if (!loadTrailersData.isEmpty()) {
                    trailersData = loadTrailersData;
                    recyclerAdapter = new TrailersAdapter(trailersData, context);
                    recyclerView.setAdapter(recyclerAdapter);
                }

                if (!reviewsData.isEmpty()) {
                    final StringBuilder rev = new StringBuilder();
                    for (int i = 0; i < reviewsData.size(); i++) {
                        rev.append(reviewsData.get(i));
                        rev.append("\n");
                    }
                    if (rev.length() > 150) {
                        movieReviewTV.setText(rev.toString().substring(0, 100) + "...");
                        reviewReadMoreTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                reviewReadMoreTv.setVisibility(View.GONE);
                                movieReviewTV.setText(rev.toString());
                            }
                        });
                    } else {
                        reviewReadMoreTv.setVisibility(View.GONE);
                        movieReviewTV.setText(rev.toString());
                    }
                }
            }


            if (result > -1) {
                // movie saved correctly
                newAdnotation("Saved. \n" + result);
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
                // movie deleted correctly
                newAdnotation("Removed. \n" + result);
                setButtonStatus(false);
            }

            if (cursor != null) {
                cursor.close();
            }

            favButton.setEnabled(true);
        }

        @Override
        protected Long doInBackground(Movie... movie) {
            this.movie = movie[0];

            // load reviews
            reviewsData = Utils.takeReviews(this.movie.getMovieID());

            // load trailers
            loadTrailersData = Utils.takeTrailers(this.movie.getMovieID());

            // work with db
            Uri baseUri = FavoriteMovieEntry.CONTENT_URI;

            cursor = context.getContentResolver().query(baseUri, null,
                    FavoriteMovieEntry.MOVIE_ID + "=" + selectedMovie.getMovieID(),
                    null, null);

            Log.i(LOG_TAG, "return CURSOR: " + cursor);

            if (cursor.getCount() > 0) {
                // this movie is in db

                cursor.moveToFirst();

                String idInDb = String.valueOf(cursor.getLong(cursor.getColumnIndex("_id")));

                Uri movieUri = baseUri.buildUpon().appendPath(idInDb).build();

                if (btnClicked) {
                    // If from user initiate
                    btnClicked = false;
                    if (getContentResolver().delete(movieUri, null, null) > 0) {
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
                // this movie is not in db
                if (btnClicked) {
                    // If from User initiate
                    // INSERT MOVIE WITH URI
                    this.movie.setImageLocalPath(newLocalImagePath(this.movie.getMovieID(), this.movie.getThumbnail())); // Save image on hardware and get path to it.
                    Log.i(LOG_TAG, "imageLocalPath == " + this.movie.getImageLocalPath());

                    ContentValues values = new ContentValues();
                    values.put(FavoriteMovieEntry.MOVIE_ID, this.movie.getMovieID());
                    values.put(FavoriteMovieEntry.THUMBNAIL, this.movie.getThumbnail());
                    values.put(FavoriteMovieEntry.NAME, this.movie.getTitleMovie());
                    values.put(FavoriteMovieEntry.RELEASE_DATE, this.movie.getReleaseDate());
                    values.put(FavoriteMovieEntry.OVERVIEW, this.movie.getMovieOverview());
                    values.put(FavoriteMovieEntry.RATING, this.movie.getRating());
                    values.put(FavoriteMovieEntry.LOCALPATH, this.movie.getImageLocalPath());

                    Log.i(LOG_TAG, "ConventValues == " + values);

                    // TODO: contentResolver
                    Uri newUri = getContentResolver().insert(
                            baseUri, values);

                    long ret = ERROR_FROM_DATABASE_OPERATION;

                    if (newUri != null) {
                        ret = Long.parseLong(newUri.getPathSegments().get(1));
                        Log.i(LOG_TAG, "ContentResolver.insert() = uri = " + newUri +
                                "\n long ret = " + ret);
                    }

                    return ret;

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
        }

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
