package com.amir.stickergram.phoneStickers.organizedIcon;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amir.stickergram.R;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.sticker.icon.IconItem;

public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView iconImageView;
    private TextView iconNameTextView;


    public ViewHolder(View itemView, AssetManager assets) {
        super(itemView);
        iconImageView = (ImageView) itemView.findViewById(R.id.template_sticker_icon_item_stickerItem);
        iconNameTextView = (TextView) itemView.findViewById(R.id.template_sticker_icon_item_stickerName);

        if (iconImageView != null && iconNameTextView != null)
            if (Loader.deviceLanguageIsPersian())
                iconNameTextView.setTypeface(Typeface.createFromAsset(assets, Constants.APPLICATION_PERSIAN_FONT_ADDRESS_IN_ASSET));
            else
                iconNameTextView.setTypeface(Typeface.createFromAsset(assets, Constants.APPLICATION_ENGLISH_FONT_ADDRESS_IN_ASSET));

    }

    void populate(IconItem item) {
        Bitmap bitmap = item.getBitmapFromExternalStorage();
        itemView.setTag(item);
        iconNameTextView.setText(item.getName());
//        if (bitmap == null) {
//            Log.e(getClass().getSimpleName(), "bitmap was null");
//            return;
//        }
        iconImageView.setImageBitmap(bitmap);
    }
}
