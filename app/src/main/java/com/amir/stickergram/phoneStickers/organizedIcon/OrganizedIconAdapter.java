package com.amir.stickergram.phoneStickers.organizedIcon;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.sticker.icon.IconItem;
//import com.amir.stickergram.sticker.icon.user.ViewHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class OrganizedIconAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private static final int VIEW_TYPE_FOOTER = 0;
    private static final int VIEW_TYPE_CELL = 1;
    private final BaseActivity activity;
    private final OnStickerClickListener listener;
    private final LayoutInflater inflater;
    public List<String> items;
    private boolean isImagePicker;

    OrganizedIconAdapter(BaseActivity activity, OnStickerClickListener listener, boolean isImagePicker) {
        this.activity = activity;
        this.listener = listener;
        this.inflater = activity.getLayoutInflater();
        this.isImagePicker = isImagePicker;
        try {
            items = getItems();
            if (items.size() == 0) listener.OnNoItemWereFoundListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getItems() throws IOException {
        File file = new File(BaseActivity.Companion.getBASE_PHONE_ORGANIZED_STICKERS_DIRECTORY());
        if (!file.exists())
            if (Loader.checkPermission(activity))
                file.mkdirs();
            else {
                Loader.gainPermission(activity, 0);
                activity.finish();
            }
        File[] files = file.listFiles();
        if (files == null) {
            Log.e(getClass().getSimpleName(), "files were null");
            activity.finish();
            return null;
        }
        List<String> directories = new ArrayList<>();
//        int i = 0;
        for (File file1 : files) {
            if (file1.isDirectory()) {
                directories.add(file1.getName());
//                Log.e(getClass().getSimpleName(), "i: " + i++);
            }
        }
//        Log.e(getClass().getSimpleName(), "i: " + i + "items.size(): " + directories.size());
        return directories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_CELL) {
            view = inflater.inflate(R.layout.item_icon_sticker_organized, parent, false);
        } else {
            view = inflater.inflate(R.layout.new_folder_button, parent, false);
            if (isImagePicker)
                view.findViewById(R.id.new_folder_button_image).setVisibility(View.GONE);
//            view.setOnClickListener(this);
//            view.setOnLongClickListener(this);
//            return (ViewHolder) new ViewHolder(view);
        }
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new ViewHolder(view, activity.getAssets());
    }

    @Override
    public int getItemViewType(int position) {
        return (position == items.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        if (holder instanceof ViewHolder) {
        if (position != items.size()) {
            String name = items.get(position);

            if (name != null) { //picking the name of the folder as the name of the stickers
                int i = name.lastIndexOf("/") + 1;
                name = name.substring(i, name.length());
                holder.populate(new IconItem(name, null, BaseActivity.Companion.getBASE_PHONE_ORGANIZED_STICKERS_DIRECTORY()));
            }
        }
    }

    @Override
    public int getItemCount() {
//        Log.e(getClass().getSimpleName(), "size: " + items.size() + 1);
        return items.size() + 1;
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() instanceof IconItem) {
            IconItem item = (IconItem) view.getTag();
            listener.OnIconClicked(item);
        } else if (view.getId() == R.id.new_folder_button_container) {
            listener.OnCreateNewFolderSelected();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getTag() instanceof IconItem) {
            IconItem item = (IconItem) view.getTag();
            listener.OnIconLongClicked(item);
            return true;
        }
        return false;
    }

    public void refresh(String folder, boolean delete) {
        if (delete) {
            int i = items.indexOf(folder);
            items.remove(i);
            Loader.removeDirectory(new File(BaseActivity.Companion.getBASE_PHONE_ORGANIZED_STICKERS_DIRECTORY() + folder + File.separator));
            removeThumbs(folder);
            notifyItemRemoved(i);
        } else {
            int i = items.size();
            items.add(i, folder);
            notifyItemInserted(i);
        }
    }

    private void removeThumbs(String folder) {
        File organizedThumbs = new File(BaseActivity.Companion.getBASE_PHONE_ORGANIZED_THUMBNAIL_DIRECTORY());
        if (organizedThumbs.exists()) {
            File[] files = organizedThumbs.listFiles();
            for (File file : files) {
                if (file.getName().contains(folder)) file.delete();
            }
        }
    }
}
