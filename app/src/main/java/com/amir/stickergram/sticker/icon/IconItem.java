package com.amir.stickergram.sticker.icon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.amir.stickergram.base.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class IconItem {
    private final String folder;
    private final Context context;

    public static final int TYPE_ASSET = 1;
    public static final int TYPE_USER = 2;

    public IconItem(Context context, String name) {
        this.folder = name;
        this.context = context;
    }

    public String getFolder() {
        return folder;
    }

    public Bitmap getBitmapIconFromAsset() {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = context.getAssets().open("Stickers/" + folder + File.separator + "8.webp");
            bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap getBitmapFromExternalStorage() {
        String pathName = BaseActivity.USER_STICKERS_DIRECTORY + folder + "/0.png";
        Bitmap bitmap = BitmapFactory.decodeFile(pathName);
        if (bitmap == null)
            Log.e(getClass().getSimpleName(),
                    "bitmap for the file in " + pathName + " was null");
        return bitmap;
    }

    public String getFolderDirInExternalStorage() {
        return BaseActivity.USER_STICKERS_DIRECTORY + folder + File.separator;
    }
}
