package com.amir.telegramstickerbuilder;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.navdrawer.MainNavDrawer;

public class HowToActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setNavDrawer(new MainNavDrawer(this));
    }
}
