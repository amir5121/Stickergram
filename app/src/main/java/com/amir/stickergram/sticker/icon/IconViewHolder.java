package com.amir.stickergram.sticker.icon;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amir.stickergram.R;
import com.amir.stickergram.infrastructure.Loader;

public class IconViewHolder extends RecyclerView.ViewHolder {
    ImageView iconImageView;
    TextView iconNameTextView;


    public IconViewHolder(View itemView) {
        super(itemView);
        iconImageView = (ImageView) itemView.findViewById(R.id.template_sticker_icon_item_stickerItem);
        iconNameTextView = (TextView) itemView.findViewById(R.id.template_sticker_icon_item_stickerName);
    }

    public void populate(IconItem item, int type) {
        Bitmap bitmap = null;
        if (type == IconItem.TYPE_ASSET)
            bitmap = item.getBitmapIconFromAsset();
        else if (type == IconItem.TYPE_USER)
            bitmap = item.getBitmapFromExternalStorage();
        itemView.setTag(item);
        if (Loader.deviceLanguageIsPersian()) {
            switch (item.getFolder()) {
                case "Board":
                    iconNameTextView.setText("برد");
                    break;
                case "Bunny":
                    iconNameTextView.setText("خرگوش");
                    break;
                case "Celebrities":
                    iconNameTextView.setText("نامدار");
                    break;
                case "Explosm":
                    iconNameTextView.setText("اکسپلوسم");
                    break;
                case "Greg":
                    iconNameTextView.setText("امیر");
                    break;
                case "Troll":
                    iconNameTextView.setText("ترول");
                    break;
                default:
                    iconNameTextView.setText(item.getFolder());

            }
        } else
            iconNameTextView.setText(item.getFolder());
        if (bitmap == null) {
            Log.e(getClass().getSimpleName(), "bitmap was null");
            return;
        }
        iconImageView.setImageBitmap(bitmap);
//        Log.e(getClass().getSimpleName(), "??????????item name: " + item.getFolder());
    }
}
