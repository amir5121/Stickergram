package com.amir.telegramstickerbuilder.sticker.single;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.DataSource;

import java.util.ArrayList;
import java.util.List;

public class SingleStickersAdapter extends RecyclerView.Adapter<SingleStickerViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private final BaseActivity activity;
    private final OnStickerClickListener listener;
    private final LayoutInflater inflater;

    public List<StickerItem> items;
    private DataSource dataSource;


    public SingleStickersAdapter(BaseActivity activity, OnStickerClickListener listener) {
        this.activity = activity;
        this.listener = listener;
        this.inflater = activity.getLayoutInflater();
        dataSource = new DataSource(activity);
        items = new ArrayList<>(); // things actually happen in refresh method
    }

    @Override
    public SingleStickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_simple_sticker, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new SingleStickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SingleStickerViewHolder holder, int position) {
        holder.populate(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() instanceof StickerItem) {
            StickerItem item = (StickerItem) view.getTag();
            listener.OnStickerClicked(item);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getTag() instanceof StickerItem) {
            StickerItem note = (StickerItem) view.getTag();
            listener.OnStickerLongClicked(note);
        }
        return true;
    }

//    public void add(StickerItem item) {
//        dataSource.update(item);
//        items.add(item);
//        notifyItemInserted(items.indexOf(item));
//    }

    public void refreshPhoneSticker() {
        items = dataSource.getAllPhoneStickers();
        notifyItemRangeChanged(0, items.size());
    }

    public void refreshUserSticker() {
        items = dataSource.getAllUserStickers();
        notifyItemRangeChanged(0, items.size());
    }

    public interface OnStickerClickListener {
        void OnStickerClicked(StickerItem item);

        void OnStickerLongClicked(StickerItem item);
    }

}
