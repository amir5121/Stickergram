package com.amir.stickergram.fonts;

import android.graphics.Typeface;
import android.view.View;

import com.amir.stickergram.base.BaseActivity;
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
        String folder = BaseActivity.ENGLISH_FONT_DIRECTORY;
        String[] fonts = activity.getAssets().list(folder);
        folder += "/";

        for (String font : fonts) {
//            Log.e(getClass().getSimpleName(), font);
//            Typeface.createFromAsset(activity.getAssets(), folder+font);
            fontItems.add(new FontItem(font, Typeface.createFromAsset(activity.getAssets(), folder + font)));
//            InputStream inputStream = activity.getAssets().open(folder + font);
//
//            File f = new File(BaseActivity.CACHE_DIR);
//            if (!f.exists()) {
//                if (!f.mkdirs())
//                    return null;
//            }
//            String outPath = BaseActivity.CACHE_DIR + font;
//            try {
//                byte[] buffer = new byte[inputStream.available()];
//                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outPath));
//
//                int l;
//                while ((l = inputStream.read(buffer)) > 0) {
//                    bos.write(buffer, 0, l);
//                }
//                bos.close();
//                fontItems.add(new FontItem(font, Typeface.createFromFile(outPath)));
//
//                File f2 = new File(outPath);
//                f2.delete();
//            } catch (IOException e) {
//                return null;
//            }
        }

        return fontItems;
    }
}
