package com.amir.stickergram.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amir.stickergram.AppType;
import com.amir.stickergram.R;
import com.amir.stickergram.UserStickersActivity;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.mode.Mode;
import com.amir.stickergram.navdrawer.NavDrawer;

import java.io.File;
import java.util.ArrayList;

public abstract class BaseActivity extends BaseAuthenticatedActivity {
    private static final String TAG = "BaseActivity";
    public static float density;
    public static String CACHE_DIR;
    public static String TEMP_STICKER_CASH_DIR;
    public static String TEMP_CROP_CASH_DIR;
    public static String FONT_DIRECTORY;
    public static String BASE_USER_THUMBNAIL_DIRECTORY;
    public static String BASE_PHONE_ORGANIZED_THUMBNAIL_DIRECTORY;
    public static String USER_STICKERS_DIRECTORY;
    public static String BASE_PHONE_ORGANIZED_STICKERS_DIRECTORY;
    public static String STICKERGRAM_ROOT;
    public static String STICKERGRAM = "/Stickergram";

    private SharedPreferences preferences;

    protected Toolbar toolbar;
    protected NavDrawer navDrawer;

    public static boolean isTablet;
    public static boolean isInLandscape;

    public static Mode chosenMode;

    public int language = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(Constants.SETTING, MODE_PRIVATE);

        setLanguage(preferences.getInt(Constants.LANGUAGE, AppType.DEFAULT_LANGUAGE));

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        isTablet = (metrics.widthPixels / metrics.density) >= 600;
        isInLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        density = getResources().getDisplayMetrics().density;
        BASE_USER_THUMBNAIL_DIRECTORY = getExternalCacheDir() + File.separator + "thumb_Stickers";
        BASE_PHONE_ORGANIZED_THUMBNAIL_DIRECTORY = getExternalCacheDir() + File.separator + "thumb_phone_organized_Stickers";
        USER_STICKERS_DIRECTORY = Environment.getExternalStorageDirectory() + STICKERGRAM + "/.user/";
        BASE_PHONE_ORGANIZED_STICKERS_DIRECTORY = Environment.getExternalStorageDirectory() + STICKERGRAM + "/.phone_organized/";
        STICKERGRAM_ROOT = Environment.getExternalStorageDirectory() + STICKERGRAM + File.separator;
        FONT_DIRECTORY = Environment.getExternalStorageDirectory() + STICKERGRAM + "/font/";
        TEMP_STICKER_CASH_DIR = getExternalCacheDir() + File.separator + "temp_sticker.png";
        TEMP_CROP_CASH_DIR = getExternalCacheDir() + File.separator + "temp_crop.png";
        CACHE_DIR = getCacheDir().getAbsolutePath() + "/";

        chosenMode = new Mode(preferences.getString(Constants.ACTIVE_PACK, null), this);
        if (chosenMode.getPack() == null) {
            ArrayList<Mode> modes = Loader.getAllAvailableModes(this);
            if (modes.size() > 0)//when one of the supported modes is installed
                setDefaultMode(modes.get(0));
            if (chosenMode.getPack() == null) {// if none of the supported modes is installed
                setDefaultMode(new Mode(Constants.TELEGRAM_PACKAGE, this));
                Log.e(getClass().getSimpleName(), "no type of supported telegram was found and chosenMode was defaulted to original telegram");
            }
        }

//        if (Loader.freeMemory() < 50 && !hasCashedPhoneStickersOnce()) {
//            Toast.makeText(this, getString(R.string.low_storage_finish), Toast.LENGTH_LONG).show();
//            Toast.makeText(this, getString(R.string.low_storage_finish), Toast.LENGTH_LONG).show();
////            finish();
//        } else


    }


    public void setFont(TextView textView) {
        if (textView != null)
            if (Loader.deviceLanguageIsPersian())
                textView.setTypeface(Typeface.createFromAsset(getAssets(), Constants.APPLICATION_PERSIAN_FONT_ADDRESS_IN_ASSET));
            else
                textView.setTypeface(Typeface.createFromAsset(getAssets(), Constants.APPLICATION_ENGLISH_FONT_ADDRESS_IN_ASSET));
    }


    public void setFont(ViewGroup group) {
        if (group != null) {
            int count = group.getChildCount();
            View v;
            for (int i = 0; i < count; i++) {
                v = group.getChildAt(i);
                if (v instanceof TextView) {
                    setFont((TextView) v);
                } else if (v instanceof ViewGroup)
                    setFont((ViewGroup) v);
            }
        } else {
            Log.e(getClass().getSimpleName(), "viewGroup was null");
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        toolbar = (Toolbar) findViewById(R.id.include_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar ab = getSupportActionBar();
            if (ab != null)
                ab.setTitle(getString(R.string.app_name));
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setNavDrawer(NavDrawer navDrawer) {
        this.navDrawer = navDrawer;
        navDrawer.create();
    }

    public boolean hasCashedPhoneStickersOnce() {
        return preferences.getBoolean(Constants.HAS_CASHED_PHONE_STICKERS, false);
    }

    public void setPhoneStickerCashStatus(boolean status) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.HAS_CASHED_PHONE_STICKERS, status);
        editor.apply();
    }

    public boolean hasCashedPackStickers() {
        return preferences.getBoolean(Constants.HAS_CASHED_PACK_STICKERS, false);
    }

    public void setPackCashStatus(boolean status) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.HAS_CASHED_PACK_STICKERS, status);
        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.USER_STICKER_GAIN_PERMISSION) {
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

    public void setDefaultMode(Mode mode) {

        chosenMode = mode;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.ACTIVE_PACK, mode.getPack());
        editor.apply();
    }

    public void setLanguage(int language) {
        if (this.language != language) {
            this.language = language;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(Constants.LANGUAGE, language);
            editor.apply();

            Loader.setLocale(language, this);
//            restartActivity();
        }
    }

    public void cacheJsonResponse(String response) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.CACHED_JSON, response);
        editor.apply();
    }

    public String getCachedJson() {
        return preferences.getString(Constants.CACHED_JSON, null);
    }

    public void restartActivity() {
        Intent refresh = new Intent(this, this.getClass());
        startActivity(refresh);
        finish();
    }

}
