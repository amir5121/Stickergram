package com.amir.telegramstickerbuilder.infrastructure;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.amir.telegramstickerbuilder.sticker.single.StickerItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataSource {
    private static final String DIRECTORIES = "DIRECTORIES";
    private static final String THUMB_DIR_STRING = "THUMB_DIR_STRING";
    private static final String IS_VISIBLE_BOOLEAN = "IS_VISIBLE_BOOLEAN";
    private static final String TYPE_INT = "TYPE_INT";
    private Set<String> stickerDirectories;
    SharedPreferences preferences;

    public DataSource(Context context) {
        preferences = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
        stickerDirectories = preferences.getStringSet(DIRECTORIES, new HashSet<String>());
    }

    public void update(StickerItem item) {
        addDirectoryToSet(item.getStickerDirectory());
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(item.getStickerDirectory() + THUMB_DIR_STRING, item.getThumbDirectory());
        editor.putBoolean(item.getStickerDirectory() + IS_VISIBLE_BOOLEAN, item.isVisible());
        editor.putInt(item.getStickerDirectory() + TYPE_INT, item.getType());

        editor.apply();
    }

    public boolean remove(StickerItem item) {

        String stickerDirectory = item.getStickerDirectory();

        if (stickerDirectories.contains(stickerDirectory)) {
            removeDirectoryFromSet(stickerDirectory);
            SharedPreferences.Editor editor = preferences.edit();
//            editor.removeThumb(item.getStickerDirectory());
            editor.remove(stickerDirectory + THUMB_DIR_STRING);
            editor.remove(stickerDirectory + IS_VISIBLE_BOOLEAN);
            editor.remove(stickerDirectory + TYPE_INT);

            editor.apply();
            return true;
        }
        return false;
    }

    /**
     * Returns all of the stickers that are save in telegram directory
     * (basically all of the user's stickers that telegram has cashed)
     */
    public List<StickerItem> getAllPhoneStickers() {
        stickerDirectories = preferences.getStringSet(DIRECTORIES, null);
        StickerItem item;
        List<StickerItem> items = new ArrayList<>();
        if (stickerDirectories != null)
            for (String directory : stickerDirectories) {
                item = getItem(directory);
                if (item.getType() == StickerItem.IN_PHONE)
                    items.add(item);
            }
        return items;
    }

    public List<StickerItem> getAllUserStickers() {
        StickerItem item;
        List<StickerItem> items = new ArrayList<>();

        for (String directory : stickerDirectories) {
            item = getItem(directory);
            if (item.getType() == StickerItem.USER_STICKER)
                items.add(item);
        }
        return items;
    }

    public boolean contain(String directory) {
        if (stickerDirectories != null)
            return stickerDirectories.contains(directory);
        return false;
    }

    public StickerItem getItem(String directory) {

        String thumbDirectory = preferences.getString(directory + THUMB_DIR_STRING, null);
        Boolean isVisible = preferences.getBoolean(directory + IS_VISIBLE_BOOLEAN, true);
        int type = preferences.getInt(directory + TYPE_INT, StickerItem.IN_PHONE);

        return new StickerItem(directory, thumbDirectory, type, false, isVisible);
    }

    private void addDirectoryToSet(String stickerDirectory) {
        stickerDirectories = preferences.getStringSet(DIRECTORIES, new HashSet<String>());
        SharedPreferences.Editor editor = preferences.edit();
        stickerDirectories.add(stickerDirectory);

        editor.putStringSet(DIRECTORIES, stickerDirectories);
        editor.apply();
    }

    private boolean removeDirectoryFromSet(String stickerDirectory) {
//        stickerDirectories = preferences.getStringSet(DIRECTORIES, null);
//        if (stickerDirectories != null) {
//        Log.e(getClass().getSimpleName(), "made it all the way here");
        if (stickerDirectories.contains(stickerDirectory)) {
            stickerDirectories.remove(stickerDirectory);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet(DIRECTORIES, stickerDirectories);
            editor.apply();
            return true;
        }
//        }
        return false;
    }

    public boolean remove(String dir) {
        if (stickerDirectories.contains(dir)) {
//            Log.e(getClass().getSimpleName(), "removeThumb: " + dir);
            SharedPreferences.Editor editor = preferences.edit();
//            editor.removeThumb(item.getStickerDirectory());
            String thumbDir = preferences.getString(dir + THUMB_DIR_STRING, null);
            if (thumbDir != null) {
                File thumbFile = new File(thumbDir);
                if (thumbFile.exists())
                    if (thumbFile.isFile())
                        thumbFile.delete();
            }
            editor.remove(dir + THUMB_DIR_STRING);
            editor.remove(dir + IS_VISIBLE_BOOLEAN);
            editor.remove(dir + TYPE_INT);

            editor.apply();
            return true;
        }
        return false;
    }

    public void updateSet(Set<String> updateSet) {
        Log.e(getClass().getSimpleName(), "update set was called");
//        Log.e(getClass().getSimpleName(), "stickerDirectories size: " + stickerDirectories.size());
        Set<String> temp = new HashSet<>(stickerDirectories);
//        temp.addAll(stickerDirectories);
//        Log.e(getClass().getSimpleName(), String.valueOf(stickerDirectories.size()));

        for (String existence : temp) {
            if (!updateSet.contains(existence)) {
//                Log.e(getClass().getSimpleName(), "before removeThumb: " + updateSet.contains(existence) + " " + existence);
//                stickerDirectories.removeThumb(existence);
                remove(existence);
//                Log.e(getClass().getSimpleName(), "after removeThumb: " + updateSet.contains(existence) + " " + existence);
            }
        }
//        Log.v(getClass().getSimpleName(), "temp size" + temp.size());
//        stickerDirectories = updateSet;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(DIRECTORIES, updateSet);
        editor.apply();

//        Log.e(getClass().getSimpleName(), String.valueOf(stickerDirectories.size()));
//        i = 0;
//        for (String existence : updateSet) {
//            Log.e(getClass().getSimpleName(), i++ + existence);
//        }
//        SharedPreferences.Editor editor = preferences.edit();
//        stickerDirectories = updateSet;
//        editor.putStringSet(DIRECTORIES, stickerDirectories);
//        editor.apply();
//        Log.e(getClass().getSimpleName(), "updateSet size: " + stickerDirectories.size());
    }
}
