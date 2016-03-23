package com.amir.telegramstickerbuilder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.Loader;
import com.amir.telegramstickerbuilder.infrastructure.StickerItem;
import com.amir.telegramstickerbuilder.views.MainNavDrawer;
import com.amir.telegramstickerbuilder.views.StickerAdapter;

public class PhoneStickersActivity extends BaseActivity implements StickerAdapter.OnStickerClickListener {
    View progressBar;
    RecyclerView recyclerView;
    StickerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_stickers);

        setNavDrawer(new MainNavDrawer(this));
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.activity_users_stickers_list);

        adapter = new StickerAdapter(this, this);
        recyclerView.setLayoutManager(new GridLayoutManager(PhoneStickersActivity.this, 3));
        recyclerView.setAdapter(adapter);

        new AsyncAdapter().execute();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void OnStickerClicked(StickerItem item) {

    }

    @Override
    public void OnStickerLongClicked(StickerItem item) {

    }

    public class AsyncAdapter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Loader.loadPhoneStickers(adapter.getDataSource());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.refresh();
        }
    }
}
