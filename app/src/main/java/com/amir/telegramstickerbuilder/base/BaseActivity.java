package com.amir.telegramstickerbuilder.base;

import android.app.Application;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.views.NavDrawer;

public abstract class BaseActivity extends BaseAuthenticatedActivity {
    protected Toolbar toolbar;
    protected NavDrawer navDrawer;
    protected static boolean payed = false;

    public static boolean isTablet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        isTablet = (metrics.widthPixels / metrics.density) >= 600;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        toolbar = (Toolbar) findViewById(R.id.include_toolbar);
        if (toolbar != null)
            setSupportActionBar(toolbar);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setNavDrawer(NavDrawer navDrawer) {
        this.navDrawer = navDrawer;
        navDrawer.create();
    }

    public static boolean getPaymentStatus() {
        return payed;
    }
}
