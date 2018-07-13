package com.amir.stickergram.image;

import android.os.Parcel;
import android.os.Parcelable;

public class DrawableArea implements Parcelable {
    private Position startPosition;
    private float width;
    private float height;

    public DrawableArea(Position startPosition, float width, float height) {
        this.height = height;
        this.startPosition = new Position(startPosition.getTop(), startPosition.getLeft());
        this.width = width;
    }

    public DrawableArea(Parcel parcel) {
        startPosition = parcel.readParcelable(Position.class.getClassLoader());
        width = parcel.readFloat();
        height = parcel.readFloat();
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(startPosition, 0);
        parcel.writeFloat(width);
        parcel.writeFloat(height);
    }

    public float getHeight() {
        return height;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public float getWidth() {
        return width;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<DrawableArea> CREATOR = new Creator<DrawableArea>() {
        @Override
        public DrawableArea createFromParcel(Parcel parcel) {
            return new DrawableArea(parcel);
        }

        @Override
        public DrawableArea[] newArray(int i) {
            return new DrawableArea[0];
        }
    };
}
