package com.amir.telegramstickerbuilder.Icon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
            InputStream inputStream = context.getAssets().open(folder + File.separator + "thumb.png");
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

//    public List<Drawable> getAllDrawable() {
//        List<Drawable> drawableList = new ArrayList<>();
//        InputStream inputStream;
//        try {
//            String files[] = context.getAssets().list(folder);
//            for (String file : files) {
//                inputStream = context.getAssets().open(folder + File.separator + file);
//                drawableList.add(Drawable.createFromStream(inputStream, null));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return drawableList;
//    }


//    public int getId() {
//        return id;
//    }
}
