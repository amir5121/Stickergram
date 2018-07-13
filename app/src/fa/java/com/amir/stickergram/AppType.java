package com.amir.stickergram;

import android.os.Environment;

import com.amir.stickergram.infrastructure.Constants;

public class AppType {
    public static final String LINK_TO_CHANNEL = "https://telegram.me/stickergram_fa";
    public static final int DEFAULT_LANGUAGE = Constants.PERSIAN_LANGUAGE;
    public static final int FLAVOR = 2;
    public static String STICKERGRAM = "/Stickergram";
    public static String WEBP_CASH_DIR = Environment.getExternalStorageDirectory() + STICKERGRAM + STICKERGRAM + "/.صفحهه ی چت را دوباره باز کنید.webp";
    public static String WEBP_CASH_DIR_OLD = Environment.getExternalStorageDirectory() + STICKERGRAM + "/.صفحهه ی چت را دوباره باز کنید.webp";
}
