package com.amir.telegramstickerbuilder.infrastructure;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class StickerItem {
    private final String stickerDirectory;
    private final String thumbDirectory;
    private final int type;
    private boolean isSelected;
    private boolean visible;

    public static final int TYPE_USER_MADE = 1;
    public static final int TYPE_IN_PHONE = 2;
    public static final int TYPE_TEMPLATE = 3;
    public static final int TYPE_FROM_SCRATCH = 4;

    public StickerItem(String stickerDirectory, String thumbDirectory, int type, boolean isSelected, boolean visibale) {
        this.stickerDirectory = stickerDirectory;
        this.thumbDirectory = thumbDirectory;
        this.type = type;
        this.isSelected = isSelected;
        this.visible = visibale;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getStickerDirectory() {
        return stickerDirectory;
    }

    public String getThumbDirectory() {
        return thumbDirectory;
    }

    public int getType() {
        return type;
    }

    public Bitmap getBitmap() {
        return BitmapFactory.decodeFile(stickerDirectory);
    }

    public Bitmap getThumbBitmap() {
        return BitmapFactory.decodeFile(thumbDirectory);
    }
}
