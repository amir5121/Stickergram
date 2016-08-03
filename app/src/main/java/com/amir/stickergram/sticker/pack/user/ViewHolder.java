package com.amir.stickergram.sticker.pack.user;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.amir.stickergram.R;
import com.amir.stickergram.sticker.pack.user.PackItem;

public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView iconImageView;

    public ViewHolder(View itemView) {
        super(itemView);
        iconImageView = (ImageView) itemView.findViewById(R.id.item_pack_sticker_image);
    }

    public void populate(PackItem item) {
        itemView.setTag(item);
        iconImageView.setImageBitmap(item.getThumbnail());
    }
}
