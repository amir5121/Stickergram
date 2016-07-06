package com.amir.stickergram.sticker.single;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.DataSource;

import java.util.ArrayList;
import java.util.List;

public class SingleStickersAdapter extends RecyclerView.Adapter<SingleStickerViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private final LayoutInflater inflater;

    private List<StickerItem> items;
    private OnStickerClickListener listener;
    private DataSource dataSource;


    public SingleStickersAdapter(BaseActivity activity) {
        try {
            this.listener = (OnStickerClickListener) activity;
        } catch (ClassCastException e) {
            Log.e(getClass().getSimpleName(), "must implement OnStickerClickListener");
            e.printStackTrace();
        }
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
//        if (items.size() > 0)
        Log.e(getClass().getSimpleName(), "size: " + items.size());
        if (items.size() == 0)
        listener.OnNoItemExistedListener();
        notifyItemRangeChanged(0, items.size());
        notifyDataSetChanged();
    }

    public void refreshUserSticker() {
        items = dataSource.getAllUserStickers();
        if (items.size() > 0)
            notifyItemRangeChanged(0, items.size());
    }

    public List<StickerItem> getItems() {
        return items;
    }

    public interface OnStickerClickListener {
        void OnStickerClicked(StickerItem item);

        void OnStickerLongClicked(StickerItem item);

        void OnNoItemExistedListener();
    }

}