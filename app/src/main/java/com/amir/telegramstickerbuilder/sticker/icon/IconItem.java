package com.amir.telegramstickerbuilder.sticker.icon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class IconItem {
    private final String folder;
    private final Context context;

    public IconItem(Context context, String folder) {
        this.folder = folder;
        this.context = context;
    }

    public String getFolder() {
        return folder;
    }

    public Bitmap getBitmapIcon() {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = context.getAssets().open(folder + File.separator + "8.webp");
            bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
