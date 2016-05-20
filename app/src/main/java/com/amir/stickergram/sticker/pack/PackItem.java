package com.amir.stickergram.sticker.pack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.amir.stickergram.base.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PackItem {
    public static final int TYPE_TEMPLATE = 1;
    public static final int TYPE_USER = 2;
    public static final String WEBP = ".webp";
    public static final String PNG = ".png";

    private final Context context;
    private final String folder;
    private final String name;
    private final int type;

    public PackItem(Context context, String folder, String name, int type) {
        this.context = context;
        this.folder = folder;
        this.name = name;
        this.type = type;
//        thumbnail = BitmapFactory.decodeFile(BaseActivity.BASE_THUMBNAIL_DIRECTORY + folder + "_" + this.name);
//        Log.e(getClass().getSimpleName(), "path: "+ context.getCacheDir() + File.separator + "thumb_" + folder + "_" + this.name);
        //the thumbnail was made in the first load
    }

    public Bitmap getThumbnail() {
        Bitmap bitmap = BitmapFactory.decodeFile(BaseActivity.BASE_THUMBNAIL_DIRECTORY + File.separator + folder + "_" + name + PNG);
        if (bitmap == null) {
            //this part should only very rarely get called (it will only get called when the cached thumb has been removed)
            Bitmap tempBitmap;
            try {
                if (type == TYPE_TEMPLATE) {
                    InputStream inputStream = getInputStream();
                    tempBitmap = BitmapFactory.decodeStream(inputStream);
                    if (inputStream != null) inputStream.close();
                } else if (type == TYPE_USER) {
                    tempBitmap = BitmapFactory.decodeFile(getDir());
                } else throw new RuntimeException("Undefined Type for the pack item");

                File thumbFile = new File((BaseActivity.BASE_THUMBNAIL_DIRECTORY + File.separator + folder + "_" + name + PNG));
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

    public InputStream getInputStream() throws IOException {
//        Log.e(getClass().getSimpleName(), "folder: " + folder);
        if (type == PackItem.TYPE_USER) {
//            Log.e(getClass().getSimpleName(),
//                    "getInputStream: " + BaseActivity.USER_STICKERS_DIRECTORY + folder + File.separator + name);
//            return new FileInputStream(new File(BaseActivity.USER_STICKERS_DIRECTORY + folder + File.separator + name));
            return null;
        } else if (type == PackItem.TYPE_TEMPLATE) {
            return context.getAssets().open("Stickers/" + folder + File.separator + name + WEBP);
        } else {
            Log.e(getClass().getSimpleName(), "Undefined type in get inputStream method");
            return null;
        }
    }

    public String getDir() {
        return BaseActivity.USER_STICKERS_DIRECTORY + folder + File.separator + name + PNG;
    }

    public String getDirInAsset() {
        return ("Stickers/" + folder + File.separator + name + WEBP);
    }

    public void removeThumb() {
        if (type == TYPE_USER){
            File file = new File(BaseActivity.BASE_THUMBNAIL_DIRECTORY + File.separator + folder + "_" + name + PNG);
            if (file.exists())
                file.delete();
        }
    }

//    public Bitmap getBitmap() {
//        InputStream inputStream = null;
//        try {
//            inputStream = getInputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return BitmapFactory.decodeStream(inputStream);
//    }
}
