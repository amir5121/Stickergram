package com.amir.stickergram.infrastructure;

import android.os.Parcel;
import android.os.Parcelable;

public class Position implements Parcelable {
    private float top;
    private float left;

    public Position(float top, float left) {
        this.top = top;
        this.left = left;
    }

    public Position(Parcel parcel) {
        top = parcel.readFloat();
        left = parcel.readFloat();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(top);
        parcel.writeFloat(left);
    }


    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public void set(float left, float top) {
        this.left = left;
        this.top = top;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Position> CREATOR = new Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel parcel) {
            return new Position(parcel);
        }

        @Override
        public Position[] newArray(int i) {
            return new Position[0];
        }
    };
}
