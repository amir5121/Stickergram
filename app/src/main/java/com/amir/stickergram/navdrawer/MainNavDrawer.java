package com.amir.stickergram.navdrawer;

import android.content.Intent;
import android.view.View;

import com.amir.stickergram.HowToActivity;
import com.amir.stickergram.MainActivity;
import com.amir.stickergram.PhoneStickersActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.TemplateStickersActivity;
import com.amir.stickergram.UserStickersActivity;
import com.amir.stickergram.base.BaseActivity;

public class MainNavDrawer extends NavDrawer {
    public MainNavDrawer(final BaseActivity activity) {
        super(activity);

        addItem(new ActivityNavDrawerItem(MainActivity.class, activity.getString(R.string.home), R.drawable.ic_home, R.id.include_nav_drawer_top_items));
        addItem(new ActivityNavDrawerItem(UserStickersActivity.class, activity.getString(R.string.your_stickers), R.drawable.ic_your_stickers, R.id.include_nav_drawer_top_items));
        addItem(new ActivityNavDrawerItem(PhoneStickersActivity.class, activity.getString(R.string.phone_stickers), R.drawable.ic_phone, R.id.include_nav_drawer_top_items));
        addItem(new ActivityNavDrawerItem(TemplateStickersActivity.class, activity.getString(R.string.template_stickers), R.drawable.ic_template, R.id.include_nav_drawer_top_items));

        addItem(new ActivityNavDrawerItem(HowToActivity.class, activity.getString(R.string.help), R.drawable.ic_info, R.id.include_nav_drawer_bottom_items));
        addItem(new BaseNavDrawerItem(activity.getString(R.string.exit), R.drawable.ic_exit, R.id.include_nav_drawer_bottom_items) {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                activity.finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });
    }
}
