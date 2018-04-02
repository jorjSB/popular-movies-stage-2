package com.udacity.georgebalasca.popularmoviesstage_2.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jorj on 02/28/2018.
 */

public class Review implements Parcelable {

    private String author;
    private String content;
    private String url;
    private String id;

    // empty constructor
    public Review(){
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getAuthor());
        dest.writeString(this.getContent());
        dest.writeString(this.getUrl());
        dest.writeString(this.getId());

    }

    // Parcelling part
    private Review(Parcel in){
        this.setAuthor(in.readString());
        this.setContent(in.readString());
        this.setUrl(in.readString());
        this.setId(in.readString());
    }

    public static final Creator CREATOR = new Creator() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
