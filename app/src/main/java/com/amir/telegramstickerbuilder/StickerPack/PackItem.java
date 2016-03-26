package com.amir.telegramstickerbuilder.StickerPack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PackItem {
    private final Context context;
    private final String folder;
    private final String name;
    private final Bitmap thumbnail;


    public PackItem(Context context, String folder, String name) {
        this.context = context;
        this.folder = folder;
        this.name = name + ".webp";
        thumbnail = getItemThumbnail();
    }

    public Bitmap getThumbnail() {
        Log.e(getClass().getSimpleName(), folder + name);
        return thumbnail;
    }

    public Bitmap getItemThumbnail() {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = context.getAssets().open(folder + File.separator + name);
            Log.e(getClass().getCanonicalName(), folder + File.separator + name);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int width = 100;
        int height = 100;
        if (bitmap != null) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }
        return ThumbnailUtils.extractThumbnail(bitmap, width, height);
    }

    public String getFolder() {
        return folder;
    }
}
