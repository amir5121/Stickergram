package com.amir.telegramstickerbuilder.fonts;

import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.FontItem;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EnglishFontAdapter extends FontAdapter {
    public EnglishFontAdapter(BaseActivity activity, OnFontClickListener listener, View lodingFrame) {
        super(activity, listener, lodingFrame);
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
        String[] fonts;
        String folder = "Fonts/eng";
        fonts = activity.getAssets().list(folder);
        folder += "/";

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TSB/.cash/";

        List<FontItem> fontItems = new ArrayList<>();
        Log.e(getClass().getSimpleName(), "folder " + folder);
        Log.e(getClass().getSimpleName(), fonts.length + " length");
        for (String font : fonts) {
            InputStream inputStream = activity.getAssets().open(folder + font);

            File f = new File(path);
            if (!f.exists()) {
                if (!f.mkdirs())
                    return null;
            }
            String outPath = path + font;
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
