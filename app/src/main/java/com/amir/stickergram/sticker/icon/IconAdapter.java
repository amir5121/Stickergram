package com.amir.stickergram.sticker.icon;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;

import java.io.IOException;
import java.util.List;

public abstract class IconAdapter extends RecyclerView.Adapter<IconViewHolder> implements View.OnClickListener {
    private final BaseActivity activity;
    private final OnStickerClickListener listener;
    private final LayoutInflater inflater;
    private final int type;
    public List<String> items;

    public IconAdapter(BaseActivity activity, OnStickerClickListener listener, int type) {
        this.activity = activity;
        this.listener = listener;
        this.inflater = activity.getLayoutInflater();
        this.type = type;
        try {
            items = getItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract List<String> getItems() throws IOException;

    @Override
    public IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_icon_sticker, parent, false);
        view.setOnClickListener(this);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IconViewHolder holder, int position) {
        if (type == IconItem.TYPE_USER) {
            String name = items.get(position);
            if (name != null) {
                int i = name.lastIndexOf("/") + 1;
                name = name.substring(i, name.length());
                holder.populate(new IconItem(activity, name), type);
            }
        } else if (type == IconItem.TYPE_ASSET)
            holder.populate(new IconItem(activity, items.get(position)), type);
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

    public interface OnStickerClickListener {
        void OnIconClicked(IconItem item);
    }
}
