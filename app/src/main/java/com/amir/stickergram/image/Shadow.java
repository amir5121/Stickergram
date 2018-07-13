package com.amir.stickergram.image;

import android.os.Parcel;
import android.os.Parcelable;

public class Shadow implements Parcelable {
    private int color;
    private int radius;
    private int dY;
    private int dX;

    public Shadow(int color, int dX, int dY, int radius) {
        this.color = color;
        this.dX = dX;
        this.dY = dY;
        this.radius = radius;
    }

    public Shadow(Parcel parcel) {
        color = parcel.readInt();
        radius = parcel.readInt();
        dY = parcel.readInt();
        dX = parcel.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setDx(int dX) {
        this.dX = dX;
    }

    public void setDy(int dY) {
        this.dY = dY;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getColor() {
        return color;
    }

    public int getDx() {
        return dX;
    }

    public int getDy() {
        return dY;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<Shadow> CREATOR = new Creator<Shadow>() {
        @Override
        public Shadow createFromParcel(Parcel parcel) {
            return new Shadow(parcel);
        }

        @Override
        public Shadow[] newArray(int i) {
            return new Shadow[0];
        }
    };
}
