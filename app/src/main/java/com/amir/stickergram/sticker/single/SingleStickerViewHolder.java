package com.amir.stickergram.sticker.single;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.amir.stickergram.R;

public class SingleStickerViewHolder extends RecyclerView.ViewHolder {
    //    public static final int STICKER_ITEM = 0;
//    public static final int STICKER_POSITION = 1;
    private ImageView stickerImageView;
    private View overlay;
    private CheckBox checkBox;

    SingleStickerViewHolder(View itemView) {
        super(itemView);
        stickerImageView = (ImageView) itemView.findViewById(R.id.simple_sticker_item_image);
        overlay = itemView.findViewById(R.id.simple_sticker_item_image_overlay);
        checkBox = (CheckBox) itemView.findViewById(R.id.item_simple_sticker_checkbox);
    }

    public void populate(StickerItem item, boolean isInCropMode) {
//    public void populate(StickerItem item) {
        itemView.setTag(item);
        stickerImageView.setImageBitmap(item.getThumbBitmap());

        if (isInCropMode) {
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }

        if (item.isSelected()) {
            overlay.setVisibility(View.VISIBLE);
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
            overlay.setVisibility(View.GONE);
        }
    }
}
