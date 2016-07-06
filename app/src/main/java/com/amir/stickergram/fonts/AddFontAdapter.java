package com.amir.stickergram.fonts;

import android.graphics.Typeface;
import android.view.View;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.FontItem;

import java.io.File;

public class AddFontAdapter extends FontAdapter {
    public AddFontAdapter(BaseActivity activity, OnFontClickListener listener, View loadingFrame) {
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
                Typeface font = Typeface.createFromFile(file.getPath());
                if (font != null)
                    fontItems.add(new FontItem(file.getName(), font));
            }
        }
    }

}