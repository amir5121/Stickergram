package com.amir.stickergram.sticker.icon.user;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.sticker.icon.IconItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private static final int VIEW_TYPE_FOOTER = 0;
    private static final int VIEW_TYPE_CELL = 1;
    private final BaseActivity activity;
    private final OnStickerClickListener listener;
    private final LayoutInflater inflater;
    public List<String> items;

    IconAdapter(BaseActivity activity, OnStickerClickListener listener) {
        this.activity = activity;
        this.listener = listener;
        this.inflater = activity.getLayoutInflater();
        items = getItems();
        if (items.size() == 0) listener.OnNoItemWereFoundListener();
    }

    public List<String> getItems() {
        return Loader.INSTANCE.getUserStickerDirectories(activity, activity);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_icon_sticker, parent, false);
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
//        if (position != items.size()) {
        String name = items.get(position);

        if (name != null) { //picking the name of the folder as the name of the stickers
            int i = name.lastIndexOf("/") + 1;
            name = name.substring(i, name.length());
            holder.populate(new IconItem(name, null, BaseActivity.USER_STICKERS_DIRECTORY));
//            }
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

    @Override
    public boolean onLongClick(View view) {
        if (view.getTag() instanceof IconItem) {
            IconItem item = (IconItem) view.getTag();
            listener.OnIconLongClicked(item);
            return true;
        }
        return false;
    }


    public interface OnStickerClickListener {
        void OnIconClicked(IconItem item);

        void OnIconLongClicked(IconItem item);

        void OnNoItemWereFoundListener();
    }
}
