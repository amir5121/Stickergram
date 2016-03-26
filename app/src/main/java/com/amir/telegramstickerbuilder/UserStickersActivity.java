package com.amir.telegramstickerbuilder;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.views.MainNavDrawer;

public class UserStickersActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_stickers);
        setNavDrawer(new MainNavDrawer(this));

    }
}
