package com.amir.stickergram.sticker.pack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PackItem {
    public static final int TYPE_TEMPLATE = 1;
    public static final int TYPE_USER = 2;
    public static final String WEBP = BaseActivity.WEBP;
    public static final String PNG = BaseActivity.PNG;

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
//        Bitmap bitmap = BitmapFactory.decodeFile(BaseActivity.BASE_THUMBNAIL_DIRECTORY + File.separator + folder + "_" + name + WEBP);
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
//                File thumbFile = new File((BaseActivity.BASE_THUMBNAIL_DIRECTORY + File.separator + folder + "_" + name + WEBP));
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
//        return BaseActivity.USER_STICKERS_DIRECTORY + folder + File.separator + name + WEBP;
    }

    public String getWebpDir() {
        Bitmap tempBitmap = BitmapFactory.decodeFile(BaseActivity.USER_STICKERS_DIRECTORY + folder + File.separator + name + PNG);
        if (tempBitmap == null) {
            Log.e(getClass().getSimpleName(), "tempBitmap was null");
            return null;
        }
        String dir = BaseActivity.WEBP_CASH_DIR;
        try {
            File file = new File(dir);
            if (file.exists())
                file.delete();
            file.createNewFile();
            tempBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(dir));
//            int i = 0;
//            OutputStream outputStream;
//            InputStream inputStream;
//            do {
//                Bitmap temp = Loader.reduceImageSize(tempBitmap, i);
//                outputStream = new FileOutputStream(file);
//                temp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//                inputStream = new FileInputStream(file);
////                Log.e(getClass().getSimpleName(), "fileSize: " + String.valueOf(inputStream.available()));
//                i++;
//            } while (inputStream.available() >= 357376);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(getClass().getSimpleName(), dir);
        return dir;
    }

    public String getDirInAsset() {
        return ("Stickers/" + folder + File.separator + name + WEBP);
    }

    public void removeThumb() {
        if (type == TYPE_USER) {
            File file = new File(BaseActivity.BASE_THUMBNAIL_DIRECTORY + File.separator + folder + "_" + name + PNG);
//            File file = new File(BaseActivity.BASE_THUMBNAIL_DIRECTORY + File.separator + folder + "_" + name + WEBP);
            if (file.exists())
                file.delete();
        }
    }
//
//    public String convertToPng() {
//        Bitmap tempBitmap = BitmapFactory.decodeFile(BaseActivity.USER_STICKERS_DIRECTORY + folder + File.separator + name + WEBP);
//        if (tempBitmap == null) {
//            Log.e(getClass().getSimpleName(), "tempBitmap was null");
//            return null;
//        }
//        String dir = BaseActivity.TEMP_OUTPUT_DIRECTORY + File.separator + folder + " " + name + PNG;
//        try {
//            File file = new File(dir);
//            if (file.exists())
//                file.delete();
//            file.createNewFile();
//            tempBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(dir));
//            int i = 0;
//            OutputStream outputStream;
//            InputStream inputStream;
//            do {
//                Bitmap temp = Loader.reduceImageSize(tempBitmap, i);
//                outputStream = new FileOutputStream(file);
//                temp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//                inputStream = new FileInputStream(file);
////                Log.e(getClass().getSimpleName(), "fileSize: " + String.valueOf(inputStream.available()));
//                i++;
//            } while (inputStream.available() >= 357376);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Log.e(getClass().getSimpleName(), "tempFile: " + dir);
//        return dir;
//    }
//
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
