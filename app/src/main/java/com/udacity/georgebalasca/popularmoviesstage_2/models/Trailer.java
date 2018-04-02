package com.udacity.georgebalasca.popularmoviesstage_2.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jorj on 02/28/2018.
 */

public class Trailer implements Parcelable {

    private String id;
    private String key;
    private String name;
    private String site;
    private String size;
    private String type;

    // empty constructor
    public Trailer(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getId());
        dest.writeString(this.getKey());
        dest.writeString(this.getName());
        dest.writeString(this.getSite());
        dest.writeString(this.getSize());
        dest.writeString(this.getType());
    }

    // Parcelling part
    private Trailer(Parcel in){
        this.setId(in.readString());
        this.setKey(in.readString());
        this.setName(in.readString());
        this.setSite(in.readString());
        this.setSize(in.readString());
        this.setType(in.readString());
    }

    public static final Creator CREATOR = new Creator() {
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
