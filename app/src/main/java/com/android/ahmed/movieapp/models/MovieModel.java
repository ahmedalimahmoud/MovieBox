
package com.android.ahmed.movieapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahmed on 2/18/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class MovieModel implements Parcelable{
    public MovieModel(Parcel in) {
        id = in.readInt();
        poster_path = in.readString();
        back_cover_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        original_title = in.readString();
        vote_average = in.readFloat();
    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    public MovieModel() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getBack_cover_path() {
        return back_cover_path;
    }

    public void setBack_cover_path(String back_cover_path) {
        this.back_cover_path = back_cover_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    private int id;
    private String poster_path;
    private String back_cover_path;
    private String overview;
    private String release_date;
    private String original_title;
    private float vote_average;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(poster_path);
        dest.writeString(back_cover_path);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(original_title);
        dest.writeFloat(vote_average);
    }
}
