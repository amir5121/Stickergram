package com.amir.stickergram.sticker.icon.user;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amir.stickergram.R;
import com.amir.stickergram.sticker.icon.IconItem;

public class ViewHolder extends RecyclerView.ViewHolder {
    ImageView iconImageView;
    TextView iconNameTextView;


    public ViewHolder(View itemView) {
        super(itemView);
        iconImageView = (ImageView) itemView.findViewById(R.id.template_sticker_icon_item_stickerItem);
        iconNameTextView = (TextView) itemView.findViewById(R.id.template_sticker_icon_item_stickerName);
    }

    void populate(IconItem item) {
        Bitmap bitmap = item.getBitmapFromExternalStorage();
        itemView.setTag(item);
        iconNameTextView.setText(item.getName());
        if (bitmap == null) {
            Log.e(getClass().getSimpleName(), "bitmap was null");
            return;
        }
        iconImageView.setImageBitmap(bitmap);
    }
}
