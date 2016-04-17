package com.amir.telegramstickerbuilder.fonts;

import android.graphics.Typeface;
import android.os.Environment;
import android.view.View;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.FontItem;

import java.io.File;

public class AddFontAdapter extends FontAdapter {
    public AddFontAdapter(BaseActivity activity, OnFontClickListener listener, View loadingFrame) {
        super(activity, listener, loadingFrame);

    }

    @Override
    public void setItems() {
        if (fontItems.size() > 0)
            fontItems.clear();
        String path = Environment.getExternalStorageDirectory().toString() + "/TSB/fonts";
        File f = new File(path);
        File files[] = f.listFiles();
        if (files != null) {
            for (File file : files) {
                Typeface font = Typeface.createFromFile(file.getPath());
                if (font != null)
                    fontItems.add(new FontItem(file.getName(), font));
            }
        }
        //todo: what if there are no fonts founded there
    }

}