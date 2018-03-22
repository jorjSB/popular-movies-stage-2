package com.udacity.georgebalasca.popularmoviesstage_1.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jorj on 02/28/2018.
 */

public class Movie implements Parcelable {

    private String posterURL;
    private String posterLandURL;
    private String title;
    private int id;
    private String originalTitle;
    private String overview;
    private String releaseDate;
    private Float voteAverage;


    // empty constructor
    public Movie(){
    }


    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public String getPosterLandURL() {
        return posterLandURL;
    }

    public void setPosterLandURL(String posterLandURL) {
        this.posterLandURL = posterLandURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public void setvoteAverage(Float voteAverage) {
        this.voteAverage = voteAverage;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getPosterURL());
        dest.writeString(this.getPosterLandURL());
        dest.writeString(this.getTitle());
        dest.writeInt(this.getId());
        dest.writeString(this.getOriginalTitle());
        dest.writeString(this.getOverview());
        dest.writeString(this.getReleaseDate());
        dest.writeFloat(this.getVoteAverage());

    }

    // Parcelling part
    private Movie(Parcel in){
        this.setPosterURL(in.readString());
        this.setPosterLandURL(in.readString());
        this.setTitle(in.readString());
        this.setId(in.readInt());
        this.setOriginalTitle(in.readString());
        this.setOverview(in.readString());
        this.setReleaseDate(in.readString());
        this.setvoteAverage(in.readFloat());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
