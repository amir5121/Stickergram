package com.amir.stickergram.sticker.single;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.DataSource;
import com.amir.stickergram.phoneStickers.unorganized.PhoneStickersUnorganizedFragment;

import java.util.ArrayList;
import java.util.List;

public class SingleStickersAdapter extends RecyclerView.Adapter<SingleStickerViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private final LayoutInflater inflater;

    private List<StickerItem> items;
    private OnStickerClickListener listener;
    private DataSource dataSource;

    public SingleStickersAdapter(BaseActivity activity, OnStickerClickListener listener) {
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
        holder.populate(items.get(position), PhoneStickersUnorganizedFragment.isInCropMode);
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

    public void refreshPhoneSticker() {
        items = dataSource.getAllVisiblePhoneStickers();
//        Arrays.sort(items.toArray());

        if (items.size() == 0)
            listener.OnNoItemExistedListener();
        notifyItemRangeChanged(0, items.size());
        notifyDataSetChanged();
    }

    public void hideItems(List<StickerItem> selectedItems) {
        dataSource.hideItems(selectedItems);
    }

    public void updateItems(ArrayList<StickerItem> selectedItems) {
//        String mString;
//        String toMatch;
        int size = items.size();
        if (selectedItems != null)
            for (StickerItem selectedItem : selectedItems) {
                for (int i = 0; i < size; i++) {
                    if (items.get(i).getName().equals(selectedItem.getName())) {
                        items.set(i, selectedItem);
                        break;
                    }
                }
            }

    }


    public interface OnStickerClickListener {
        void OnStickerClicked(StickerItem item);

        void OnStickerLongClicked(StickerItem item);

        void OnNoItemExistedListener();
    }

}
