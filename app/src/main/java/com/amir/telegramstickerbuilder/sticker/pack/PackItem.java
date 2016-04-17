package com.amir.telegramstickerbuilder.sticker.pack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.amir.telegramstickerbuilder.base.BaseActivity;

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
        this.folder ="Stickers/" + folder;
        this.name = name + ".png";
        thumbnail = BitmapFactory.decodeFile(BaseActivity.BASE_THUMBNAIL_DIRECTORY + folder + "_" + this.name);
//        Log.e(getClass().getSimpleName(), "path: "+ context.getCacheDir() + File.separator + "thumb_" + folder + "_" + this.name);
        //the thumbnail was made in the first load
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getFolder() {
        return folder;
    }

    public InputStream getInputStream() throws IOException {
        return context.getAssets().open(( folder + File.separator + name).replace(".png", ".webp"));
    }

    public String getDirInAsset() {
        return (folder + File.separator + name).replace(".png", ".webp");
    }

    public Bitmap getBitmap() {
        InputStream inputStream = null;
        try {
            inputStream = getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(inputStream);
    }
}
