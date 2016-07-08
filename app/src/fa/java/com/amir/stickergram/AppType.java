package com.amir.stickergram;

import android.os.Environment;

import com.amir.stickergram.infrastructure.Loader;

public class AppType {
    public static final String LINK_TO_CHANNEL = "https://telegram.me/joinchat/Ap1t9T8jpiK4UY7SXbfwqQ";
    public static final int DEFAULT_LANGUAGE = Loader.PERSIAN_LANGUAGE;
    public static String STICKERGRAM = "/Stickergram";
    public static String WEBP_CASH_DIR = Environment.getExternalStorageDirectory() + STICKERGRAM + "/.صفحهه ی چت را دوباره باز کنید.webp";
}
