package com.amir.stickergram.sticker.icon.user;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.sticker.icon.IconItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class IconAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener {
    private final BaseActivity activity;
    private final OnStickerClickListener listener;
    private final LayoutInflater inflater;
    public List<String> items;

    IconAdapter(BaseActivity activity, OnStickerClickListener listener) {
        this.activity = activity;
        this.listener = listener;
        this.inflater = activity.getLayoutInflater();
        try {
            items = getItems();
            if (items.size() == 0) listener.OnNoItemWereFoundListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getItems() throws IOException {
        File file = new File(BaseActivity.USER_STICKERS_DIRECTORY);
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
        for (File file1 : files) {
            if (!file1.isFile()) {
                directories.add(file1.getAbsolutePath());
            }
        }
        return directories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_icon_sticker, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = items.get(position);

        if (name != null) {
            int i = name.lastIndexOf("/") + 1;
            name = name.substring(i, name.length());
            holder.populate(new IconItem(activity, name, null));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() instanceof IconItem) {
            IconItem item = (IconItem) view.getTag();
            listener.OnIconClicked(item);
        }
    }

    interface OnStickerClickListener {
        void OnIconClicked(IconItem item);

        void OnNoItemWereFoundListener();
    }
}
