package com.amir.stickergram.sticker.icon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Constants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class IconItem {
    private static final String PNG = Constants.PNG;
    private static final String WEBP = Constants.WEBP;
    private final String folder;
    private final String enName;
    private final String baseStickerDir;

    public IconItem(String name, String enName, String baseStickerDir) {
        this.folder = name;
        this.enName = enName;
        this.baseStickerDir = baseStickerDir;
    }

    public String getName() {
        return folder;
    }

//    public Bitmap getBitmapIconFromAsset() {
//        Bitmap bitmap = null;
//        try {
//            InputStream inputStream = context.getAssets().open(Constants.STICKERS + folder + File.separator + "10" + WEBP);
//            bitmap = BitmapFactory.decodeStream(inputStream);
//            bitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3);
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bitmap;
//    }

    public Bitmap getBitmapFromExternalStorage() {
        String pathName = baseStickerDir + folder + "/0.png";
//        String pathName = BaseActivity.USER_STICKERS_DIRECTORY + folder + "/0" + WEBP;
        Bitmap bitmap = BitmapFactory.decodeFile(pathName);
        if (bitmap == null)
            Log.e(getClass().getSimpleName(),
                    "bitmap for the file in " + pathName + " was null");
        return bitmap;
    }

    public String getEnName() {
        return enName;
    }

    public String getFolder() {
        return folder;
    }

//    public String getFolderDirInExternalStorage() {
//        return BaseActivity.USER_STICKERS_DIRECTORY + folder + File.separator;
//    }
}
