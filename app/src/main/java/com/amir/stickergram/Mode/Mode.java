package com.amir.stickergram.mode;

import android.os.Environment;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;

import java.io.File;

public class Mode {
    private static final String BASE_MODE_CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator;
    private static final String END_CACHE_DIR = File.separator + "cache" + File.separator;
    private String pack;
    String name;
    public boolean isAvailable = false;

    public Mode(String pack, BaseActivity activity) {
        isAvailable = Loader.isAppInstalled(activity, pack);
        this.pack = pack;

        if (pack != null)
            switch (pack) {
                case Loader.TELEGRAM_PACKAGE:
                    name = activity.getString(R.string.telegram);
                    break;
                case Loader.TELEGRAPH_PACKAGE:
                    name = activity.getString(R.string.telegraph);
                    break;
                case Loader.TELEGRAM_PLUS_PACKAGE:
                    name = activity.getString(R.string.telegram_plus);
                    break;
                case Loader.PERSIAN_TELEGRAM:
                    name = activity.getString(R.string.persian_telegram);
                    break;
                case Loader.MOBOGRAM_PACKAGE:
                    name = activity.getString(R.string.mobogram);
                    break;
                case Loader.ORANGE_TELEGRAM:
                    name = activity.getString(R.string.telegram_narenji);
                    break;
                case Loader.PERSIAN_VOICE_TELEGRAM:
                    name = activity.getString(R.string.voice_telegram);
                    break;
                case Loader.MY_TELEGRAM:
                    name = activity.getString(R.string.my_telegram);
                    break;
                case Loader.ANIWAYS:
                    name = activity.getString(R.string.aniways);
                    break;
                case Loader.LAGATGRAM:
                    name = activity.getString(R.string.la_telegram);
                    break;

            }
    }

    public String getCacheDir() {
//        if (isAvailable)
        return BASE_MODE_CACHE_DIR + pack + END_CACHE_DIR;
//        return null;
    }

    public String getPack() {
        if (isAvailable)
            return pack;
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
}
