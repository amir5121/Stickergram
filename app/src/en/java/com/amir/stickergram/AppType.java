package com.amir.stickergram;

import android.os.Environment;

import com.amir.stickergram.infrastructure.Constants;

public class AppType {
    public static final String LINK_TO_CHANNEL = "https://telegram.me/stickergramApp";
    public static final int DEFAULT_LANGUAGE = Constants.SYSTEM_LANGUAGE;
    public static final int FLAVOR = 1;

    private static String STICKERGRAM = "/Stickergram";
    public static String WEBP_CASH_DIR = Environment.getExternalStorageDirectory() + STICKERGRAM + STICKERGRAM + "/.reopen the chat screen.webp";
    public static String WEBP_CASH_DIR_OLD = Environment.getExternalStorageDirectory() + STICKERGRAM + "/.reopen the chat screen.webp";
}
