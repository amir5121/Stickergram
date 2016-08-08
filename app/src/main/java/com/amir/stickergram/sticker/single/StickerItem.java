package com.amir.stickergram.sticker.single;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class StickerItem implements Parcelable {
    private String stickerDirectory;
    private String thumbDirectory;
    private boolean isVisible;
    private boolean isSelected;

    public StickerItem(String stickerDirectory, String thumbDirectory, boolean isSelected, boolean visible) {
        this.stickerDirectory = stickerDirectory;
        this.thumbDirectory = thumbDirectory;
        this.isSelected = isSelected;
        this.isVisible = visible;
    }

    private StickerItem(Parcel parcel) {
        stickerDirectory = parcel.readString();
        thumbDirectory = parcel.readString();
        isVisible = parcel.readByte() == 1;
        isSelected = parcel.readByte() == 1;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(stickerDirectory);
        parcel.writeString(thumbDirectory);
        parcel.writeByte((byte) (isVisible ? 1 : 0));
        parcel.writeByte((byte) (isSelected ? 1 : 0));
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setThumbDirectory(String thumbDirectory) {
        this.thumbDirectory = thumbDirectory;
    }

    public String getStickerDirectory() {
        return stickerDirectory;
    }

    public String getThumbDirectory() {
        return thumbDirectory;
    }

//    }

    public Bitmap getBitmap() {
        return BitmapFactory.decodeFile(stickerDirectory);
    }

    public Bitmap getThumbBitmap() {
        return BitmapFactory.decodeFile(thumbDirectory);
    }

    public Uri getUri() {
        return Uri.fromFile(new File(getStickerDirectory()));
    }

    public static final Creator<StickerItem> CREATOR = new Creator<StickerItem>() {
        @Override
        public StickerItem createFromParcel(Parcel parcel) {
            return new StickerItem(parcel);
        }

        @Override
        public StickerItem[] newArray(int i) {
            return new StickerItem[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return stickerDirectory.substring(stickerDirectory.lastIndexOf("/") + 1);
    }
}
