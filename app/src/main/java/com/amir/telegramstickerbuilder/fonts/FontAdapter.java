package com.amir.telegramstickerbuilder.fonts;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.FontItem;

import java.util.ArrayList;
import java.util.List;

public abstract class FontAdapter extends RecyclerView.Adapter<FontViewHolder> implements View.OnClickListener {
    private final OnFontClickListener listener;
    private final LayoutInflater inflater;

    protected List<FontItem> fontItems;

    BaseActivity activity;
    View loadingFrame;

    public FontAdapter(BaseActivity activity, OnFontClickListener listener, View loadingFrame) {
        this.listener = listener;
        this.inflater = activity.getLayoutInflater();
        this.activity = activity;
        this.loadingFrame = loadingFrame;

        fontItems = new ArrayList<>();

        new AsyncLoadFonts().execute();
    }

    @Override
    public FontViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_font, parent, false);
        view.setOnClickListener(this);
        return new FontViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FontViewHolder holder, int position) {
        holder.populate(fontItems.get(position));
    }

    @Override
    public int getItemCount() {
        return fontItems.size();
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() instanceof FontItem) {
            listener.onFontClicked((FontItem) view.getTag());
        }
    }

    public interface OnFontClickListener {

        void onFontClicked(FontItem fontItem);
    }

    private class AsyncLoadFonts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (loadingFrame != null)
                loadingFrame.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            setItems();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyItemRangeChanged(0, fontItems.size());
            if (loadingFrame != null)
                loadingFrame.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }

    }

    public abstract void setItems();
}
