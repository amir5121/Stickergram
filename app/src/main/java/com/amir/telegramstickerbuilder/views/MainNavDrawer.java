package com.amir.telegramstickerbuilder.views;

import android.view.View;
import android.widget.Toast;

import com.amir.telegramstickerbuilder.MainActivity;
import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseActivity;

public class MainNavDrawer extends NavDrawer{
    public MainNavDrawer(final BaseActivity activity) {
        super(activity);

        addItem(new ActivityNavDrawerItem(MainActivity.class, activity.getResources().getString(R.string.home), R.mipmap.ic_launcher, R.id.include_nav_drawer_top_items));

        addItem(new BaseNavDrawerItem(activity.getResources().getString(R.string.exit), R.mipmap.ic_launcher, R.id.include_nav_drawer_bottom_items){
            @Override
            public void onClick(View view) {
                super.onClick(view);
                Toast.makeText(activity, "You have pressed the Exit button", Toast.LENGTH_LONG).show();
            }
        });
    }
}
