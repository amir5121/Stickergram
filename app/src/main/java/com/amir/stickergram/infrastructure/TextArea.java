package com.amir.stickergram.infrastructure;

import android.os.Parcel;
import android.os.Parcelable;

public class TextArea implements Parcelable {
    private Position startPosition;
    private float width;
    private float height;

    public TextArea(Position startPosition, float width, float height) {
        this.height = height;
        this.startPosition = new Position(startPosition.getTop(), startPosition.getLeft());
        this.width = width;
    }

    public TextArea(Parcel parcel) {
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


    public static final Creator<TextArea> CREATOR = new Creator<TextArea>() {
        @Override
        public TextArea createFromParcel(Parcel parcel) {
            return new TextArea(parcel);
        }

        @Override
        public TextArea[] newArray(int i) {
            return new TextArea[0];
        }
    };
}
