package com.amir.stickergram.phoneStickers.organizedIcon;

import com.amir.stickergram.sticker.icon.IconItem;

public interface OnStickerClickListener {
    void OnIconClicked(IconItem item);

    void OnIconLongClicked(IconItem item);

    void OnNoItemWereFoundListener();

    void OnCreateNewFolderSelected();
}
