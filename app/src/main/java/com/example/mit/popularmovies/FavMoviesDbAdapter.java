//package com.example.mit.popularmovies;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//public class FavMoviesDbAdapter {
//
//    public static final String KEY_ROWID = "_id";
//    public static final String MOVIE_ID = "movie_id";
//    public static final String THUMBNAIL = "thumbnail";
//    public static final String NAME = "name";
//    public static final String RELEASE_DATE = "release_date";
//    public static final String OVERVIEW = "overview";
//    public static final String RATING = "rating";
//    public static final String LOCALPATH = "local_path";
//    public static final String[] FAVORITE_FIELDS = new String[]{
//            KEY_ROWID,
//            NAME,
//            MOVIE_ID,
//            THUMBNAIL,
//            RELEASE_DATE,
//            OVERVIEW,
//            RATING,
//            LOCALPATH
//    };
//    private static final String LOG_TAG = FavMoviesDbAdapter.class.getSimpleName();
//    private static final String DATABASE_NAME = "FAVORITE_MOVIES.db";
//    private static final String FAV_MOVIES_TABLE = "FAV_MOVIES_TABLE";
//    private static final String NO_DATA = "n/a";
//    private static final int DATABASE_VERSION = 991;
//    private static final String CREATE_TABLE_FAVORITE_MOVIES =
//            "create table " + FAV_MOVIES_TABLE + "("
//                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                    + NAME + " TEXT, "
//                    + MOVIE_ID + " INTEGER UNIQUE, "
//                    + THUMBNAIL + " TEXT, "
//                    + RELEASE_DATE + " TEXT, "
//                    + OVERVIEW + " TEXT, "
//                    + RATING + " TEXT, "
//                    + LOCALPATH + " TEXT"
//                    + ");";
//    private final Context context;
//    private DatabaseHelper databaseHelper;
//    private SQLiteDatabase sqLiteDatabase;
//
//    /**
//     * Open, close and update database
//     */
//    public FavMoviesDbAdapter(Context context) {
//        this.context = context;
//    }
//
//    public static Movie getMovieFromCursor(Cursor cursor) {
//        Movie movie = new Movie(0, NO_DATA, NO_DATA, NO_DATA, NO_DATA, NO_DATA, NO_DATA);
//        movie.setMovieID(cursor.getInt(cursor.getColumnIndex(MOVIE_ID)));
//        movie.setThumbnail(cursor.getString(cursor.getColumnIndex(THUMBNAIL)));
//        movie.setTitleMovie(cursor.getString(cursor.getColumnIndex(NAME)));
//        movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(RELEASE_DATE)));
//        movie.setMovieOverview(cursor.getString(cursor.getColumnIndex(OVERVIEW)));
//        movie.setRating(cursor.getString(cursor.getColumnIndex(RATING)));
//        movie.setImageLocalPath(cursor.getString(cursor.getColumnIndex(LOCALPATH)));
//        return movie;
//    }
//
//    public FavMoviesDbAdapter open() throws SQLException {
//        databaseHelper = new DatabaseHelper(context);
//        sqLiteDatabase = databaseHelper.getWritableDatabase();
//        return this;
//    }
//
//    public void close() {
//        if (databaseHelper != null) {
//            databaseHelper.close();
//        }
//    }
//
//    /**
//     * Input, update and remove specific data (movie)
//     */
//    public long insertMovie(ContentValues initialValues) {
//        return sqLiteDatabase.insert(FAV_MOVIES_TABLE,
//                null, initialValues);
//    }
//
//    public boolean updateMovie(int id, ContentValues newValues) {
//        String[] selectionArgs = {String.valueOf(id)};
//        return sqLiteDatabase.update(FAV_MOVIES_TABLE, newValues,
//                KEY_ROWID + "=?", selectionArgs) > 0;
//    }
//
//    public boolean removeMovie(long id) {
//        String[] selectionArgs = {String.valueOf(id)};
//        return sqLiteDatabase.delete(FAV_MOVIES_TABLE,
//                MOVIE_ID + "=?", selectionArgs) > 0;
//    }
//
//    /**
//     * load data with Cursor
//     */
//    public Cursor getMovies() {
//        return sqLiteDatabase.query(FAV_MOVIES_TABLE, FAVORITE_FIELDS,
//                null, null, null, null, null);
//    }
//
//    public Cursor getMovie(int movieID) {
//        return sqLiteDatabase.query(true, FAV_MOVIES_TABLE, FAVORITE_FIELDS,
//                MOVIE_ID + " =" + movieID,
//                null, null, null, null, null);
//    }
//
//    private static class DatabaseHelper extends SQLiteOpenHelper {
//        DatabaseHelper(Context context) {
//            super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        }
//
//        @Override
//        public void onCreate(SQLiteDatabase database) {
//            database.execSQL(CREATE_TABLE_FAVORITE_MOVIES);
//        }
//
//        @Override
//        public void onUpgrade(SQLiteDatabase database,
//                              int oldDbVersion,
//                              int newDbVersion) {
//            Log.w(LOG_TAG, "Updating database from old version: " +
//                    oldDbVersion + ", to new version: " + newDbVersion);
//            database.execSQL("DROP TABLE IF EXISTS " + FAV_MOVIES_TABLE);
//            onCreate(database);
//        }
//    }
//}
