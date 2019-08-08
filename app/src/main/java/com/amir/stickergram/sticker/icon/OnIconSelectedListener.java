package com.amir.stickergram.sticker.icon;

public interface OnIconSelectedListener {// communicating with the activity

    void OnIconSelected(IconItem item);

    void OnNoStickerWereFoundListener();
}