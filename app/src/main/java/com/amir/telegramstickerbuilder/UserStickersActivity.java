package com.amir.telegramstickerbuilder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.StickerItem;
import com.amir.telegramstickerbuilder.views.MainNavDrawer;
import com.amir.telegramstickerbuilder.views.StickerAdapter;

public class UserStickersActivity extends BaseActivity implements StickerAdapter.OnStickerClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_stickers);

        setNavDrawer(new MainNavDrawer(this));

        StickerAdapter adapter = new StickerAdapter(this, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_users_stickers_list);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void OnStickerClicked(StickerItem item) {

    }

    @Override
    public void OnStickerLongClicked(StickerItem item) {

    }
}
