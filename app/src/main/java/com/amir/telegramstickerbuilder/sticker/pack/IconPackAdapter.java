package com.amir.telegramstickerbuilder.sticker.pack;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.telegramstickerbuilder.sticker.icon.IconItem;
import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseFragment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class IconPackAdapter extends RecyclerView.Adapter<IconPackViewHolder> implements View.OnClickListener {
    private final BaseFragment fragment;
    private final OnStickerClickListener listener;
    private final LayoutInflater inflater;
    private String folder;

    public List<String> items;

    public IconPackAdapter(BaseFragment fragment, OnStickerClickListener listener, String folder) {
        this.fragment = fragment;
        this.listener = listener;
        this.folder = folder;
        this.inflater = fragment.getLayoutInflater(null);
        items = new ArrayList<>();
    }


    @Override
    public IconPackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_pack_sticker, parent, false);
        view.setOnClickListener(this);
        return new IconPackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IconPackViewHolder holder, int position) {
        holder.populate(new PackItem(fragment.getContext(), folder, String.valueOf(position)));
    }

    @Override
    public int getItemCount() {
        return items.size() - 1;
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() instanceof IconItem) {
            listener.OnIconClicked((IconItem) view.getTag());
        }

    }

    public interface OnStickerClickListener {
        void OnIconClicked(IconItem item);
    }

    public void refresh() {
//        InputStream inputStream;
        try {
            AssetManager assets = fragment.getActivity().getAssets();
            String files[] = assets.list(folder);
            for (String file : files) {
//                inputStream = assets.open(folder + File.separator + file);
//                items.add(Drawable.createFromStream(inputStream, null));
                items.add(folder + File.separator + file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyItemRange() {
        notifyItemRangeChanged(0, items.size());
    }
}

