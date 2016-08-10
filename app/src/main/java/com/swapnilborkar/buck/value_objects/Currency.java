package com.swapnilborkar.buck.value_objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SWAPNIL on 07-08-2016.
 */
public class Currency implements Parcelable {

    private double rate;
    private String date;
    private String base;
    private String name;
    private long id;

    protected Currency(Parcel in) {
        rate = in.readDouble();
        date = in.readString();
        base = in.readString();
        name = in.readString();
        id = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(rate);
        parcel.writeString(date);
        parcel.writeString(base);
        parcel.writeString(name);
        parcel.writeLong(id);
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}