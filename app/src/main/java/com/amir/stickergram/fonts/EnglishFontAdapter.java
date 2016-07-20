package com.amir.stickergram.fonts;

import android.graphics.Typeface;
import android.view.View;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.FontItem;

import java.io.IOException;
import java.util.List;

public class EnglishFontAdapter extends FontAdapter {
    public EnglishFontAdapter(BaseActivity activity, OnFontClickListener listener, View loadingFrame) {
        super(activity, listener, loadingFrame);
    }

    @Override
    public void setItems() {
        try {
            fontItems.addAll(readEnglishFonts());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<FontItem> readEnglishFonts() throws IOException {
        String folder = Constants.ENGLISH_FONT_DIRECTORY;
        String[] fonts = activity.getAssets().list(folder);
        folder += "/";

        for (String font : fonts)
            fontItems.add(new FontItem(font, Typeface.createFromAsset(activity.getAssets(), folder + font), FontItem.ASSET_en, font));

        return fontItems;
    }
}
