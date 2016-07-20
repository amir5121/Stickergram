package com.amir.stickergram.sticker.pack.user;

public interface OnStickerClickListener {
    void OnIconClicked(PackItem item);

    void OnLongClicked(PackItem item);

    void folderDeleted();
}
