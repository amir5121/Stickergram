package com.amir.telegramstickerbuilder.navdrawer;

import android.view.View;
import android.widget.Toast;

import com.amir.telegramstickerbuilder.MainActivity;
import com.amir.telegramstickerbuilder.PhoneStickersActivity;
import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.TemplateStickersActivity;
import com.amir.telegramstickerbuilder.UserStickersActivity;
import com.amir.telegramstickerbuilder.base.BaseActivity;

public class MainNavDrawer extends NavDrawer {
    public MainNavDrawer(final BaseActivity activity) {
        super(activity);

        addItem(new ActivityNavDrawerItem(MainActivity.class, activity.getResources().getString(R.string.home), R.drawable.ic_home, R.id.include_nav_drawer_top_items));
        addItem(new ActivityNavDrawerItem(UserStickersActivity.class, activity.getResources().getString(R.string.your_stickers), R.drawable.ic_your_stickers, R.id.include_nav_drawer_top_items));
        addItem(new ActivityNavDrawerItem(PhoneStickersActivity.class, activity.getResources().getString(R.string.phone_stickers), R.drawable.ic_phone, R.id.include_nav_drawer_top_items));
        addItem(new ActivityNavDrawerItem(TemplateStickersActivity.class, activity.getResources().getString(R.string.template_stickers), R.drawable.ic_template, R.id.include_nav_drawer_top_items));

        addItem(new BaseNavDrawerItem(activity.getResources().getString(R.string.exit), R.drawable.ic_exit, R.id.include_nav_drawer_bottom_items) {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                activity.finish();
            }
        });
    }
}
