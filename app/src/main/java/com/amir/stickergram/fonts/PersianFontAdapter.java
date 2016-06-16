package com.amir.stickergram.fonts;

import android.view.View;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.FontItem;

import java.util.List;


import android.graphics.Typeface;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PersianFontAdapter extends FontAdapter {
    public PersianFontAdapter(BaseActivity activity, OnFontClickListener listener, View loadingFrame) {
        super(activity, listener, loadingFrame);
    }

    @Override
    public void setItems() {
        try {
            fontItems.addAll(readPersianFont());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<FontItem> readPersianFont() throws IOException {
        String[] fonts;
        String folder = "Fonts/per";
        fonts = activity.getAssets().list(folder);
        folder += "/";
        List<FontItem> fontItems = new ArrayList<>();
        for (String font : fonts) {
            InputStream inputStream = activity.getAssets().open(folder + font);

            File f = new File(BaseActivity.CACHE_DIR);
            if (!f.exists()) {
                if (!f.mkdirs())
                    return null;
            }
            String outPath = BaseActivity.CACHE_DIR + font;
            try {
                byte[] buffer = new byte[inputStream.available()];
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outPath));

                int l = 0;
                while ((l = inputStream.read(buffer)) > 0) {
                    bos.write(buffer, 0, l);
                }
                bos.close();
                fontItems.add(new FontItem(font, Typeface.createFromFile(outPath)));

                File f2 = new File(outPath);
                f2.delete();
            } catch (IOException e) {
                return null;
            }
        }

        return fontItems;
    }
}
