package com.amir.telegramstickerbuilder.sticker.icon;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amir.telegramstickerbuilder.R;

public class IconViewHolder extends RecyclerView.ViewHolder {
    ImageView iconImageView;
    TextView iconNameTextView;


    public IconViewHolder(View itemView) {
        super(itemView);
        iconImageView = (ImageView) itemView.findViewById(R.id.template_sticker_icon_item_stickerItem);
        iconNameTextView = (TextView) itemView.findViewById(R.id.template_sticker_icon_item_stickerName);
    }

    public void populate(IconItem item) {
        Bitmap bitmap = item.getBitmapIcon();
        itemView.setTag(item);
        if (bitmap == null) {
            return;
        }
        iconImageView.setImageBitmap(bitmap);
        iconNameTextView.setText(item.getFolder());

    }
}
