package com.amir.stickergram.mode;

import android.os.Environment;
import android.util.Log;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;

import java.io.File;

public class Mode {
    private static final String BASE_MODE_CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator;
    private static final String END_CACHE_DIR = File.separator + "cache" + File.separator;
    private static final String TAG = "Mode";
    private String pack;
    private String name;
    public boolean isAvailable;

    public Mode(String pack, BaseActivity activity) {
        isAvailable = Loader.INSTANCE.isAppInstalled(activity, pack);
        Log.wtf("ASDWWWWWWWWWWWWWWWWWWw", String.valueOf(isAvailable));
        this.pack = pack;

        if (pack != null)
            switch (pack) {
                case Constants.TELEGRAM_PACKAGE:
                    name = activity.getString(R.string.telegram);
                    break;
                case Constants.TELEGRAM_X:
                    name = activity.getString(R.string.telegram_x);
                    break;
                case Constants.TELEGRAPH_PACKAGE:
                    name = activity.getString(R.string.telegraph);
                    break;
                case Constants.TELEGRAM_PLUS_PACKAGE:
                    name = activity.getString(R.string.telegram_plus);
                    break;
                case Constants.PERSIAN_TELEGRAM:
                    name = activity.getString(R.string.persian_telegram);
                    break;
                case Constants.MOBOGRAM_PACKAGE:
                    name = activity.getString(R.string.mobogram);
                    break;
                case Constants.MOBOGRAM_TWO:
                    name = activity.getString(R.string.mobogram_two);
                    break;
                case Constants.GOLDEN_TELEGRAM:
                    name = activity.getString(R.string.golden_telegram);
                    break;
                case Constants.ORANGE_TELEGRAM:
                    name = activity.getString(R.string.telegram_narenji);
                    break;
                case Constants.PERSIAN_VOICE_TELEGRAM:
                    name = activity.getString(R.string.voice_telegram);
                    break;
                case Constants.MY_TELEGRAM:
                    name = activity.getString(R.string.my_telegram);
                    break;
                case Constants.ANIWAYS:
                    name = activity.getString(R.string.aniways);
                    break;
                case Constants.LAGATGRAM:
                    name = activity.getString(R.string.la_telegram);
                    break;
                case Constants.TELEGRAM_PACKAGE_BETA:
                    name = activity.getString(R.string.telegram_beta);
                    break;
                case Constants.INFOGRAM_PACKAGE:
                    name = activity.getString(R.string.info_gram_telegram);
                    break;
                case Constants.NITRO_TELEGRAM_PACKAGE:
                    name = activity.getString(R.string.nitrogram_telegram);
                    break;
                case Constants.BGRAM_PACKAGE:
                    name = activity.getString(R.string.bgram_telegram);
                    break;
                case Constants.BLACKGRAM_PACKAGE:
                    name = activity.getString(R.string.black_telegram);
                    break;
                case Constants.TELEPLUS:
                    name = activity.getString(R.string.teleplus_telegram);
                    break;
                case Constants.TELEDR:
                    name = activity.getString(R.string.teledr_telegram);
                    break;
                case Constants.ROYAL_TELEGRAM:
                    name = activity.getString(R.string.royal_telegram);
                    break;
            }


    }

    public String getCacheDir() {
        return BASE_MODE_CACHE_DIR + pack + END_CACHE_DIR;
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
