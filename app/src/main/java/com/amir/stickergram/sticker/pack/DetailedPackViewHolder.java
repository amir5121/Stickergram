package com.amir.stickergram.sticker.pack;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.amir.stickergram.R;

public class DetailedPackViewHolder extends RecyclerView.ViewHolder {
    ImageView iconImageView;

    public DetailedPackViewHolder(View itemView) {
        super(itemView);
        iconImageView = (ImageView) itemView.findViewById(R.id.item_pack_sticker_image);
    }

    public void populate(PackItem item) {
        itemView.setTag(item);
        iconImageView.setImageBitmap(item.getThumbnail());
    }
}
