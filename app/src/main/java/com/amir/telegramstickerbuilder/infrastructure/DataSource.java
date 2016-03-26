package com.amir.telegramstickerbuilder.infrastructure;

import android.content.Context;
import android.content.SharedPreferences;

import com.amir.telegramstickerbuilder.Single.StickerItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataSource  {
    private static final String DIRECTORIES = "DIRECTORIES";
    private static final String STRING = "STRING";
    private static final String BOOLEAN = "BOOLEAN";
    private static final String INT = "INT";
    SharedPreferences preferences;

    public DataSource(Context context) {
        preferences = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
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
        removeDirectoryFromSet(item.getStickerDirectory());

        if (preferences.contains(item.getStickerDirectory())) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(item.getStickerDirectory());

            editor.apply();
            return true;
        }
        return false;
    }


    //** Returns all of the stickers that are saved in telegram directory (basically all of the user's stickers that telegram has cashed*//
    public List<StickerItem> getAllItems() {
        List<StickerItem> items = new ArrayList<>();
        Set<String> newSet = preferences.getStringSet(DIRECTORIES, new HashSet<String>());

        for (String directory : newSet)
            items.add(getItem(directory));
        return items;
    }

    public StickerItem getItem(String directory) {

        String thumbDirectory = preferences.getString(directory + STRING, null);
        Boolean isVisible = preferences.getBoolean(directory + BOOLEAN, true);
        int type = preferences.getInt(directory + INT, 1);

        return new StickerItem(directory, thumbDirectory, type, false, isVisible);
    }

    private boolean removeDirectoryFromSet(String stickerDirectory) {
        Set<String> newSet = preferences.getStringSet(DIRECTORIES, null);
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
        Set<String> newSet = preferences.getStringSet(DIRECTORIES, new HashSet<String>());
        SharedPreferences.Editor editor = preferences.edit();
        newSet.add(stickerDirectory);

        editor.putStringSet(DIRECTORIES, newSet);
        editor.apply();
    }

}
