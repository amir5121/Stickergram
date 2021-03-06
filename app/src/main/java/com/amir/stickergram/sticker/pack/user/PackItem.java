package com.amir.stickergram.sticker.pack.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.util.Log;

import com.amir.stickergram.AppType;
import com.amir.stickergram.BuildConfig;
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
    public String getName() {
        return name;
    }

    public String getDir() {
//        return BaseActivity.USER_STICKERS_DIRECTORY + folder + File.separator + name + PNG;
        return baseStickerDir + folder + File.separator + name + PNG;
    }

    public Bitmap getBitmap() {
        return BitmapFactory.decodeFile(getDir());
    }

    public String getWebpDir() {
        Bitmap tempBitmap = BitmapFactory.decodeFile(baseStickerDir + folder + File.separator + name + PNG);
        if (tempBitmap == null) {
            Log.e(getClass().getSimpleName(), "tempBitmap was null");
            return null;
        }
        String dir = Constants.WEBP_CASH_DIR;
        try {
            //this was a bug from the old versions
            File fileOld = new File(AppType.WEBP_CASH_DIR_OLD);
            if (fileOld.exists())
                fileOld.delete();


            File file = new File(dir);

            if (file.exists())
                file.delete();

            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();

            file.createNewFile();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                tempBitmap.compress(Bitmap.CompressFormat.WEBP, 80, new FileOutputStream(dir));
            } else tempBitmap.compress(Bitmap.CompressFormat.PNG, 80, new FileOutputStream(dir));

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(getClass().getSimpleName(), dir);
        return dir;
    }

    public void removeThumb() {
        File file = new File(BaseActivity.Companion.getBASE_USER_THUMBNAIL_DIRECTORY() + File.separator + folder + "_" + name + PNG);
        if (file.exists())
            file.delete();
    }
}
