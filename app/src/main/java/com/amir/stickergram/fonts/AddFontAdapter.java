package com.amir.stickergram.fonts;

import android.graphics.Typeface;
import android.view.View;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.image.FontItem;

import java.io.File;

class AddFontAdapter extends FontAdapter {
    AddFontAdapter(BaseActivity activity, OnFontClickListener listener, View loadingFrame) {
        super(activity, listener, loadingFrame);

    }

    @Override
    public void setItems() {
        if (fontItems.size() > 0)
            fontItems.clear();
        File f = new File(BaseActivity.FONT_DIRECTORY);
        File files[] = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.exists()) {
                    Typeface font = Typeface.createFromFile(file.getPath());
                    if (font != null)
                        fontItems.add(new FontItem(file.getName(), font, FontItem.STORAGE, file.getAbsolutePath()));
                }
            }
        }
    }

}