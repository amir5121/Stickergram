package com.amir.stickergram.image;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;

public class FontItem implements Parcelable {
    public static final int DEFAULTS = 1;
    public static final int ASSET_en = 2;
    public static final int ASSET_fa = 3;
    public static final int STORAGE = 4;
    public static final String SANS_SERIF = "SANS_SERIF";
    public static final String SERIF = "SERIF";

    private Typeface typeface;

    private final int type;
    private final String directory;
    private final String name;

    public FontItem(String name, Typeface typeface, int type, String directory) {
        this.name = name;
        this.typeface = typeface;
        this.type = type;
        this.directory = directory;
    }

    public FontItem(Parcel parcel) {
        name = parcel.readString();
        type = parcel.readInt();
        directory = parcel.readString();
    }

    public String getDirectory() {
        return directory;
    }

    public int getType() {
        return type;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(type);
        parcel.writeString(directory);
    }

    public String getName() {
        return name;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FontItem> CREATOR = new Creator<FontItem>() {
        @Override
        public FontItem createFromParcel(Parcel parcel) {
            return new FontItem(parcel);
        }

        @Override
        public FontItem[] newArray(int i) {
            return new FontItem[i];
        }
    };
}
