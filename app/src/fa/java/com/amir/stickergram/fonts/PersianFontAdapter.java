package com.amir.stickergram.fonts;

import android.content.res.AssetManager;
import android.util.Log;
import android.view.View;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.image.FontItem;
import com.amir.stickergram.infrastructure.Loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;


import android.graphics.Typeface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PersianFontAdapter extends FontAdapter {
    PersianFontAdapter(BaseActivity activity, OnFontClickListener listener, View loadingFrame) {
        super(activity, listener, loadingFrame);
    }

    @Override
    public void setItems() {
        try {
            List<FontItem> list = readPersianFont();
            if (list != null)
                fontItems.addAll(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<FontItem> readPersianFont() throws IOException {

        String folder = Constants.PERSIAN_FONT_DIRECTORY;
        AssetManager assetManager = activity.getAssets();
        String[] fonts = assetManager.list(folder);
        folder += "/";
        List<FontItem> fontItems = new ArrayList<>();
        Arrays.sort(fonts);
        List<String> fontNames = null;
        try {
            InputStream in = assetManager.open(Constants.FONT_DIRECTORY_IN_ASSET + Constants.PERSIAN_FONT_NAME);
            String persian_font_names = BaseActivity.Companion.getCACHE_DIR() + Constants.PERSIAN_FONT_NAME;
            FileOutputStream fo = new FileOutputStream(persian_font_names);
            Loader.INSTANCE.copyFile(in, fo);
            in.close();
            fo.close();

//Get the text file
            File file = new File(persian_font_names);
            if (!file.exists()) {
                Log.e(getClass().getSimpleName(), "File didn't existed");
                return null;
            }
//Read text from file
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            fontNames = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                fontNames.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            //You'll need to add proper error handling here
        }

        if (fontNames != null) {
            if (fonts != null && fonts.length != fontNames.size()) {
                Log.e(getClass().getSimpleName(), "not enough name for persian fonts");
                Log.e(getClass().getSimpleName(), fonts.length + " " + fontNames.size());
                return null;
            }
        } else return null;
        for (int i = 0; i < fonts.length; i++) {
            fontItems.add(
                    new FontItem(
                            fontNames.get(i),
                            Typeface.createFromAsset(activity.getAssets(), folder + fonts[i]),
                            FontItem.ASSET_fa,
                            fonts[i]));
        }
        return fontItems;
    }
}
