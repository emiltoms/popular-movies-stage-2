package com.example.mit.popularmovies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class Movie implements Parcelable {

    private int dbId; // id in SQLite Database
    private int movieID;
    private String thumbnail;
    private String titleMovie;
    private String releaseDate;
    private String movieOverview;
    private String rating;
    private String imageLocalPath;
    private boolean isAdult;
    private int runTime;
    private int voteCount;

    public Movie() {
    }

//    public Movie(int movieID, String thumbnail, String titleMovie,
//                 String releaseDate, String movieOverview, String rating) {
//        this.movieID = movieID;
//        this.thumbnail = thumbnail;
//        this.titleMovie = titleMovie;
//        this.releaseDate = releaseDate;
//        this.movieOverview = movieOverview;
//        this.rating = rating;
//    }

    public Movie(int movieID, String thumbnail, String titleMovie,
                 String releaseDate, String movieOverview, String rating,
                 String imageLocalPath) {
        this.movieID = movieID;
        this.thumbnail = thumbnail;
        this.titleMovie = titleMovie;
        this.releaseDate = releaseDate;
        this.movieOverview = movieOverview;
        this.rating = rating;
        this.imageLocalPath = imageLocalPath;
//        this.isAdult = isAdult;
//        this.runTime = runTime;
//        this.voteCount = voteCount;
    }

    public boolean getIsAdult(){
        return isAdult;
    }

    public int getRunTime() {
        return runTime;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setAdult(boolean isAdult) {
        this.isAdult = isAdult;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getImageLocalPath() {
        return imageLocalPath;
    }

    public void setImageLocalPath(String imageLocalPath) {
        this.imageLocalPath = imageLocalPath;
    }

    public int getMovieID() {
        return movieID;
    }

    public int getDbId() {
        return dbId;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public String getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getTitleMovie() {
        return titleMovie;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public void setMovieOverview(String movieOverview) {
        this.movieOverview = movieOverview;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setTitleMovie(String titleMovie) {
        this.titleMovie = titleMovie;
    }


    private Movie(Parcel in) {
        movieID = in.readInt();
        thumbnail = in.readString();
        titleMovie = in.readString();
        releaseDate = in.readString();
        movieOverview = in.readString();
        rating = in.readString();
        imageLocalPath = in.readString();

//        int contentBytesInt = in.readInt();
//        byte[] bytes = new byte[contentBytesInt];
//        in.readByteArray(bytes);
//        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

//        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
//        bitmap = Bitmap.CREATOR.createFromParcel(in);
//        bitmap = in.readParcelable(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieID);
        dest.writeString(thumbnail);
        dest.writeString(titleMovie);
        dest.writeString(releaseDate);
        dest.writeString(movieOverview);
        dest.writeString(rating);
        dest.writeString(imageLocalPath);

//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] bytes = stream.toByteArray();
//        dest.writeInt(bytes.length);
//        dest.writeByteArray(bytes);

//        bitmap.writeToParcel(dest, flags);
//        dest.writeValue(bitmap);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}