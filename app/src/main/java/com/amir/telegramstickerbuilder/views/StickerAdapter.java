package com.amir.telegramstickerbuilder.views;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.telegramstickerbuilder.PhoneStickersActivity;
import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.UserStickersActivity;
import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.DataSource;
import com.amir.telegramstickerbuilder.infrastructure.StickerItem;

import java.util.ArrayList;
import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter<StickerViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private final BaseActivity activity;
    private final OnStickerClickListener listener;
    private final LayoutInflater inflater;

    public List<StickerItem> items;
    private DataSource dataSource;

    private int stickerType;

    public StickerAdapter(BaseActivity activity, OnStickerClickListener listener) {
        this.activity = activity;
        this.listener = listener;
        this.inflater = activity.getLayoutInflater();
        stickerType = determineType(activity);

//        items = stickersDataSource.getAllItems();
        dataSource = new DataSource(activity);
        items = new ArrayList<>();
//        new AsyncAdapter().execute();
        Log.e("StickerAdapter", "Called");

    }

    public void setItems(List<StickerItem> items) {
        this.items = items;
    }

    @Override
    public StickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_view_item, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new StickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StickerViewHolder holder, int position) {

        holder.populate(activity, items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public DataSource getDataSource() {
//        return stickersDataSource;
        return dataSource;
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() instanceof StickerItem) {
            StickerItem note = (StickerItem) view.getTag();
            listener.OnStickerClicked(note);
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

    public void add(StickerItem item) {
//        stickersDataSource.update(item);
        dataSource.update(item);
        items.add(item);
        notifyItemInserted(items.indexOf(item));
    }

    public void refresh() {
//        items = stickersDataSource.getAllItems();
        items = dataSource.getAllItems();
        Log.e("StickerAdapter", Integer.toString(items.size()));
        notifyItemRangeChanged(0, items.size());
    }

    private int determineType(BaseActivity activity) {
        //TODO: rest of the if else statement
        Class activityClass = activity.getClass();
        if (activityClass == UserStickersActivity.class)
            return StickerItem.TYPE_USER_MADE;
        else if (activityClass == PhoneStickersActivity.class)
            return StickerItem.TYPE_IN_PHONE;
        return 0;
    }

    public interface OnStickerClickListener {
        void OnStickerClicked(StickerItem item);

        void OnStickerLongClicked(StickerItem item);
    }

    public class AsyncAdapter extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            items = dataSource.getAllItems();
            return null;
        }
    }
}
