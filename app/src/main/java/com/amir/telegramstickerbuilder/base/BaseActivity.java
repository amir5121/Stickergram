package com.amir.telegramstickerbuilder.base;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.navdrawer.NavDrawer;

import java.io.File;

public abstract class BaseActivity extends BaseAuthenticatedActivity {
    private static final String SETTING = "SETTING";
    private static final String HAS_CASHED_PACK_STICKERS = "HAS_CASHED_PACK_STICKERS";
    private static final String HAS_CASHED_PHONE_STICKERS = "HAS_CASHED_PHONE_STICKERS";
    public static final String EDIT_IMAGE_URI = "EDIT_IMAGE_URI";
    public static final String EDIT_IMAGE_DIR_IN_ASSET = "EDIT_IMAGE_DIR_IN_ASSET";
    public static final String TELEGRAM_PACKAGE = "org.telegram.messenger";
    public static final String PNG = ".png";
    public static final String NEED_ROTATION = "NEED_ROTATION";
    public static final String STICKERS = "Stickers/";
    public static String TEMP_OUTPUT_DIRECTORY;
    public static String STICKER_CASH_DIR;
    public static String FONT_DIRECTORY;
    public static String BASE_THUMBNAIL_DIRECTORY;
    public static String USER_STICKERS_DIRECTORY;
    public static String STICKERGRAM = "/Stickergram";
    public static boolean isPaid;

    private SharedPreferences preferences;

    protected Toolbar toolbar;
    protected NavDrawer navDrawer;

    public static boolean isTablet;
    public static boolean isInLandscape;

    //TODO: i think you might need to change the minimum sdk to a higher version find a device to test

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        isTablet = (metrics.widthPixels / metrics.density) >= 600;
        isInLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        BASE_THUMBNAIL_DIRECTORY = getExternalCacheDir() + File.separator + "thumb_Stickers";
        USER_STICKERS_DIRECTORY = Environment.getExternalStorageDirectory() + STICKERGRAM + "/.user/";
        FONT_DIRECTORY = Environment.getExternalStorageDirectory() + STICKERGRAM + "/font/";
        STICKER_CASH_DIR = getExternalCacheDir() + File.separator + "temp_sticker.png";
        TEMP_OUTPUT_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
        preferences = getSharedPreferences(SETTING, MODE_PRIVATE);
        isPaid  = false;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        toolbar = (Toolbar) findViewById(R.id.include_toolbar);
        if (toolbar != null)
            setSupportActionBar(toolbar);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setNavDrawer(NavDrawer navDrawer) {
        this.navDrawer = navDrawer;
        navDrawer.create();
    }

    public boolean hasCashedPhoneStickersOnce() {
        return preferences.getBoolean(HAS_CASHED_PHONE_STICKERS, false);
    }

    public void setPhoneStickerCashStatus(boolean status) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(HAS_CASHED_PHONE_STICKERS, status);
        editor.apply();
    }

    public boolean hasCashedPackStickers() {
        return preferences.getBoolean(HAS_CASHED_PACK_STICKERS, false);
    }

    public void setPackCashStatus(boolean status) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(HAS_CASHED_PACK_STICKERS, status);
        editor.apply();
    }

}
