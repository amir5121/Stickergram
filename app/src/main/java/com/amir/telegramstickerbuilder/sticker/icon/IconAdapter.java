package com.amir.telegramstickerbuilder.sticker.icon;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseActivity;

import java.io.IOException;

public class IconAdapter extends RecyclerView.Adapter<IconViewHolder> implements View.OnClickListener {
    private final BaseActivity activity;
    private final OnStickerClickListener listener;
    private final LayoutInflater inflater;

    public String items[];

    public IconAdapter(BaseActivity activity, OnStickerClickListener listener) {
        this.activity = activity;
        this.listener = listener;
        this.inflater = activity.getLayoutInflater();
        try {
            items = activity.getAssets().list("");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.icon_sticker_item, parent, false);
        view.setOnClickListener(this);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IconViewHolder holder, int position) {
        holder.populate(new IconItem(activity, items[position]));
    }

    @Override
    public int getItemCount() {
        return items.length - 3;
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
