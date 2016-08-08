package com.amir.stickergram.sticker.pack.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PackItem {
    public static final String PNG = Constants.PNG;

    private final String folder;
    private final String name;
    private String baseThumbDir;
    private String baseStickerDir;

    public PackItem(String folder, String name, String thumbDir, String baseStickerDir) {
        this.folder = folder;
        this.name = name;
        this.baseThumbDir = thumbDir;
        this.baseStickerDir = baseStickerDir;
    }

    public Bitmap getThumbnail() {
        Bitmap bitmap = BitmapFactory.decodeFile(baseThumbDir + File.separator + folder + "_" + name + PNG);
        if (bitmap == null) {
            Bitmap tempBitmap;
            try {
                tempBitmap = BitmapFactory.decodeFile(getDir());
                File thumbFile = new File((baseThumbDir + File.separator + folder + "_" + name + PNG));
                if (!thumbFile.getParentFile().exists())
                    if (!thumbFile.getParentFile().mkdirs())
                        Log.e(getClass().getSimpleName(), "failed");

                if (!thumbFile.createNewFile())
                    if (!thumbFile.exists())
                        Log.e(getClass().getSimpleName(), "File creation was failed");

                OutputStream outputStream = new FileOutputStream(thumbFile);
                if (tempBitmap != null)
                    ThumbnailUtils.extractThumbnail(tempBitmap, tempBitmap.getWidth() / 3, tempBitmap.getHeight() / 3).compress(Bitmap.CompressFormat.PNG, 85, outputStream);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public String getFolder() {
        return folder;
    }

    public String getDir() {
//        return BaseActivity.USER_STICKERS_DIRECTORY + folder + File.separator + name + PNG;
        return baseStickerDir+ folder + File.separator + name + PNG;
    }

    public String getWebpDir() {
        Bitmap tempBitmap = BitmapFactory.decodeFile(baseStickerDir + folder + File.separator + name + PNG);
        if (tempBitmap == null) {
            Log.e(getClass().getSimpleName(), "tempBitmap was null");
            return null;
        }
        String dir = Constants.WEBP_CASH_DIR;
        try {
            File file = new File(dir);
            if (file.exists())
                file.delete();
            file.createNewFile();
            tempBitmap.compress(Bitmap.CompressFormat.WEBP, 80, new FileOutputStream(dir));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(getClass().getSimpleName(), dir);
        return dir;
    }

    public void removeThumb() {
        File file = new File(BaseActivity.BASE_USER_THUMBNAIL_DIRECTORY + File.separator + folder + "_" + name + PNG);
        if (file.exists())
            file.delete();
    }
}
