package com.example.mit.popularmovies;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Movie>>,
        SwipeRefreshLayout.OnChildScrollUpCallback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_ID = 1;
    static String sortedBy;
    final LoaderManager loaderManager = getLoaderManager();

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.movies_grid)
    GridView moviesGrid;
    @BindString(R.string.instance_key_string)
    String SAVE_INSTANCE_KEY;
    @BindString(R.string.msg_no_data_to_load)
    String TOAST_MSG_LOADER_HAS_EMPTY_LIST;
    @BindString(R.string.msg_refreshed)
    String MSG_REFRESHED;
    @BindString(R.string.msg_no_internet_connection)
    String MSG_NO_INTERNET_CONNECTION;
    @BindString(R.string.intent_extra_name)
    String INTENT_EXTRA_NAME;
    @BindString(R.string.sorted_by_favorites)
    String sortedByFavoritesString;
    @BindString(R.string.sorted_by_popular)
    String sortedByPopularString;
    @BindString(R.string.sorted_by_top_rated)
    String sortedByTopRatedString;

    private MoviesAdapter adapter;
    private ArrayList<Movie> moviesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        refreshGridView(this, moviesGrid);

        if (savedInstanceState == null) {
            sortedBy = getResources().getString(R.string.sorted_by_popular);
            moviesData = new ArrayList<>();
            initiateLoader();
        } else {
            moviesData = savedInstanceState.getParcelableArrayList(SAVE_INSTANCE_KEY);
        }

        adapter = new MoviesAdapter(this, moviesData);
        moviesGrid.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateLoader();
                Log.i(LOG_TAG, MSG_REFRESHED);
            }
        });

        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Movie selectedMovie = moviesData.get(position);
                openMovieDetail(selectedMovie);
            }
        });

    }

    @Override
    protected void onResume() {
        moviesGrid.deferNotifyDataSetChanged();
        super.onResume();
    }

    /**
     * This method take User to new activity with detail of selected movie.
     * Intent use '.putExtra' method to transfer movie parcel to next activity.
     *
     * @param selectedMovie selected by User
     */
    private void openMovieDetail(Movie selectedMovie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(INTENT_EXTRA_NAME, selectedMovie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (internetStatus() && sortedBy.equals(getResources().getString(R.string.sorted_by_favorites))) {
            sortedBy = getResources().getString(R.string.sorted_by_popular);
            loaderManager.restartLoader(LOADER_ID, null, this);
            Log.i(LOG_TAG, sortedBy);
        } else {
            Toast.makeText(this, "See You Later!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * This simple method operate the menu where User can change movies list.
     *
     * @param item selected item by User;
     * @return return true if selected @param is included in switch statement.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                if (internetStatus()) {
                    sortedBy = sortedByTopRatedString;
                    loaderManager.restartLoader(LOADER_ID, null, this);
                    Log.i(LOG_TAG, sortedBy);
                    Toast.makeText(this, sortedBy, Toast.LENGTH_SHORT).show();
                } else {
                    showOfflineSnackbar();
                }
                return true;
            case R.id.item2:
                if (internetStatus()) {
                    sortedBy = sortedByPopularString;
                    loaderManager.restartLoader(LOADER_ID, null, this);
                    Log.i(LOG_TAG, sortedBy);
                    Toast.makeText(this, sortedBy, Toast.LENGTH_SHORT).show();
                } else {
                    showOfflineSnackbar();
                }
                return true;
            case R.id.item3:
                sortedBy = sortedByFavoritesString;
                loaderManager.restartLoader(LOADER_ID, null, this);
                Log.i(LOG_TAG, sortedBy);
                Toast.makeText(this, sortedBy, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showOfflineSnackbar() {
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.swipe_refresh),
                getString(R.string.msg_storage_info_snackbar_offline),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(SAVE_INSTANCE_KEY, moviesData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        return new MoviesLoader(this, sortedBy);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (movies != null) {
            moviesData.clear();
            adapter.clear();
            moviesData.addAll(movies);
            adapter.notifyDataSetChanged();

        } else {
            showOfflineSnackbar();
            Toast.makeText(this, TOAST_MSG_LOADER_HAS_EMPTY_LIST, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        adapter.clear();
        moviesData.clear();
    }

    /**
     * This method prevent of start Loader if device have not an internet connection.
     * If not, inform User about it by message in Toast.
     */
    private void initiateLoader() {
        if (internetStatus()) {
            loaderManager.initLoader(LOADER_ID, null, this);
            Toast.makeText(this, MSG_REFRESHED, Toast.LENGTH_SHORT).show();
        } else {
            showOfflineSnackbar();
            swipeRefreshLayout.setRefreshing(false);
            Log.i(LOG_TAG, MSG_NO_INTERNET_CONNECTION);
        }
        Log.i(LOG_TAG, "Internet status is = " + internetStatus());
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
    public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
        return false;
    }

    /**
     * This method set optimal numbers of columns in grid view.
     *
     * @param activity this;
     * @param gridView main view with movie posters which will be refreshed
     */
    public void refreshGridView(Activity activity, GridView gridView) {
        int gridViewEntrySize = activity.getResources().getDimensionPixelSize(R.dimen.grip_view_entry_size);

        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        assert windowManager != null;
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;

        int numColumns = width / gridViewEntrySize;
        Log.i(LOG_TAG, "refreshGridView > numClomuns: " + numColumns);
        gridView.setNumColumns(numColumns);
    }

}

