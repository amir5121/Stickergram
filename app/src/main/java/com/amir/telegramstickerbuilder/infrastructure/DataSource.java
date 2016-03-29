package com.amir.telegramstickerbuilder.infrastructure;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.amir.telegramstickerbuilder.sticker.single.StickerItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataSource {
    private static final String DIRECTORIES = "DIRECTORIES";
    private static final String STRING = "STRING";
    private static final String BOOLEAN = "BOOLEAN";
    private static final String INT = "INT";
    SharedPreferences preferences;
    Set<String> newSet;

    public DataSource(Context context) {
        preferences = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
        newSet = preferences.getStringSet(DIRECTORIES, new HashSet<String>());

    }

    public void update(StickerItem item) {
        addDirectoryToSet(item.getStickerDirectory());
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(item.getStickerDirectory() + STRING, item.getThumbDirectory());
        editor.putBoolean(item.getStickerDirectory() + BOOLEAN, item.isVisible());
        editor.putInt(item.getStickerDirectory() + INT, item.getType());

        editor.apply();
    }

    public boolean remove(StickerItem item) {

        String stickerDirectory = item.getStickerDirectory();

        if (newSet.contains(stickerDirectory)) {
            removeDirectoryFromSet(stickerDirectory);
            SharedPreferences.Editor editor = preferences.edit();
//            editor.remove(item.getStickerDirectory());
            editor.remove(stickerDirectory + STRING);
            editor.remove(stickerDirectory + BOOLEAN);
            editor.remove(stickerDirectory + INT);

            editor.apply();
            return true;
        }
        return false;
    }


    //** Returns all of the stickers that are saved in telegram directory (basically all of the user's stickers that telegram has cashed*//
    public List<StickerItem> getAllPhoneStickers() {
        StickerItem item;
        List<StickerItem> items = new ArrayList<>();
//        newSet = preferences.getStringSet(DIRECTORIES, new HashSet<String>());

        for (String directory : newSet) {
            item = getItem(directory);
            if (item.getType() == StickerItem.IN_PHONE)
                items.add(item);
        }
        return items;
    }

    public List<StickerItem> getAllUserStickers() {
        StickerItem item;
        List<StickerItem> items = new ArrayList<>();
//        newSet = preferences.getStringSet(DIRECTORIES, new HashSet<String>());

        for (String directory : newSet) {
            item = getItem(directory);
            if (item.getType() == StickerItem.USER_STICKER)
                items.add(item);
        }
        return items;
    }

    public boolean contain(String directory) {
        return newSet.contains(directory);
    }

    public StickerItem getItem(String directory) {

        String thumbDirectory = preferences.getString(directory + STRING, null);
        Boolean isVisible = preferences.getBoolean(directory + BOOLEAN, true);
        int type = preferences.getInt(directory + INT, 1);

        return new StickerItem(directory, thumbDirectory, type, false, isVisible);
    }

    private boolean removeDirectoryFromSet(String stickerDirectory) {
        newSet = preferences.getStringSet(DIRECTORIES, null);
        if (newSet != null) {
            if (newSet.contains(stickerDirectory)) {
                newSet.remove(stickerDirectory);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putStringSet(DIRECTORIES, newSet);
                editor.apply();
                return true;
            }
        }
        return false;
    }

    private void addDirectoryToSet(String stickerDirectory) {
        newSet = preferences.getStringSet(DIRECTORIES, new HashSet<String>());
        SharedPreferences.Editor editor = preferences.edit();
        newSet.add(stickerDirectory);

        editor.putStringSet(DIRECTORIES, newSet);
        editor.apply();
    }

}
