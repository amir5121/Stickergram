package com.amir.stickergram.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.amir.stickergram.PhoneStickersActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.UserStickersActivity;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.navdrawer.NavDrawer;

import java.io.File;

public abstract class BaseActivity extends BaseAuthenticatedActivity {
    private static final String HAS_CASHED_PACK_STICKERS = "HAS_CASHED_PACK_STICKERS";
    private static final String HAS_CASHED_PHONE_STICKERS = "HAS_CASHED_PHONE_STICKERS";
    public static final String SETTING = "SETTING";
    public static final String EDIT_IMAGE_URI = "EDIT_IMAGE_URI";
    public static final String EDIT_IMAGE_DIR_IN_ASSET = "EDIT_IMAGE_DIR_IN_ASSET";
    public static final String TELEGRAM_PACKAGE = "org.telegram.messenger";
    public static final String PNG = ".png";
    public static final String NEED_ROTATION = "NEED_ROTATION";
    public static final String STICKERS = "Stickers/";
    public static final String EMAIL = "StickergramApp@gmail.com";
    public static String CACHE_DIR;
    public static String TEMP_OUTPUT_DIRECTORY;
    public static String TEMP_STICKER_CASH_DIR;
    public static String FONT_DIRECTORY;
    public static String BASE_THUMBNAIL_DIRECTORY;
    public static String USER_STICKERS_DIRECTORY;
    public static String STICKERGRAM = "/Stickergram";
    public static final int LIGHT_BLUE = Color.parseColor("#2196f3");
    public static final int DARK_BLUE = Color.parseColor("#1565c0");
    public static final int TRANSPARENT_DARK_BLUE = Color.parseColor("#882196f3");
    public static float density;

    private SharedPreferences preferences;

    protected Toolbar toolbar;
    protected NavDrawer navDrawer;

    public static boolean isTablet;
    public static boolean isInLandscape;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        isTablet = (metrics.widthPixels / metrics.density) >= 600;
        isInLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        density = getResources().getDisplayMetrics().density;

        BASE_THUMBNAIL_DIRECTORY = getExternalCacheDir() + File.separator + "thumb_Stickers";
        USER_STICKERS_DIRECTORY = Environment.getExternalStorageDirectory() + STICKERGRAM + "/.user/";
        FONT_DIRECTORY = Environment.getExternalStorageDirectory() + STICKERGRAM + "/font/";
        TEMP_STICKER_CASH_DIR = getExternalCacheDir() + File.separator + ".temp_sticker.png";
        TEMP_OUTPUT_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
        CACHE_DIR = getCacheDir().getAbsolutePath() + "/";

        preferences = getSharedPreferences(SETTING, MODE_PRIVATE);


        if (Loader.freeMemory() < 50 && !hasCashedPhoneStickersOnce()) {
            Toast.makeText(this, getString(R.string.low_storage_finish), Toast.LENGTH_LONG).show();
            Toast.makeText(this, getString(R.string.low_storage_finish), Toast.LENGTH_LONG).show();
            finish();
        } else if (Loader.freeMemory() < 5) {
            Toast.makeText(this, getString(R.string.low_storage), Toast.LENGTH_LONG).show();
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Loader.USER_STICKER_GAIN_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, UserStickersActivity.class));
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                Toast.makeText(this, getResources().getString(R.string.need_permission_to_look_for_your_stickers), Toast.LENGTH_LONG).show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }

            return;
        }
    }
}
