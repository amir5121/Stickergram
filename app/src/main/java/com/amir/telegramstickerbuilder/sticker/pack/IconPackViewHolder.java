package com.amir.telegramstickerbuilder.sticker.pack;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.amir.telegramstickerbuilder.R;

public class IconPackViewHolder extends RecyclerView.ViewHolder {
    ImageView iconImageView;


    public IconPackViewHolder(View itemView) {
        super(itemView);
        iconImageView = (ImageView) itemView.findViewById(R.id.item_pack_sticker_image);
    }

    public void populate(PackItem item) {
        itemView.setTag(item);
        iconImageView.setImageBitmap(item.getThumbnail());
    }
}
