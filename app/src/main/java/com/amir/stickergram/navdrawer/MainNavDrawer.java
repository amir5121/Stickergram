package com.amir.stickergram.navdrawer;

import android.view.View;

import com.amir.stickergram.ContactActivity;
import com.amir.stickergram.HelpActivity;
import com.amir.stickergram.MainActivity;
import com.amir.stickergram.PhoneStickersActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.SettingActivity;
import com.amir.stickergram.TemplateStickersActivity;
import com.amir.stickergram.UserStickersActivity;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;

public class MainNavDrawer extends NavDrawer {
    public MainNavDrawer(final BaseActivity activity) {
        super(activity);

        addItem(new ActivityNavDrawerItem(MainActivity.class, activity.getString(R.string.home), R.drawable.ic_home, R.id.include_nav_drawer_top_items));
        addItem(new ActivityNavDrawerItem(UserStickersActivity.class, activity.getString(R.string.my_stickers), R.drawable.ic_your_stickers, R.id.include_nav_drawer_top_items));
        addItem(new ActivityNavDrawerItem(PhoneStickersActivity.class, activity.getString(R.string.telegram_sticker), R.drawable.ic_phone, R.id.include_nav_drawer_top_items));
        addItem(new ActivityNavDrawerItem(TemplateStickersActivity.class, activity.getString(R.string.template_stickers), R.drawable.ic_template, R.id.include_nav_drawer_top_items));

        if (!BaseActivity.isPaid) {
            addItem(new BaseNavDrawerItem(activity.getString(R.string.buy_pro), R.drawable.ic_buy_pro, R.id.include_nav_drawer_bottom_items) {
                @Override
                public void onClick(View view) {
                    super.onClick(view);
                    activity.requestProVersion();
                }
            });
        }
        addItem(new ActivityNavDrawerItem(HelpActivity.class, activity.getString(R.string.help), R.drawable.ic_question, R.id.include_nav_drawer_bottom_items));
        addItem(new ActivityNavDrawerItem(ContactActivity.class, activity.getString(R.string.contact), R.drawable.ic_contact, R.id.include_nav_drawer_bottom_items));
        addItem(new BaseNavDrawerItem(activity.getString(R.string.rate_us), R.drawable.ic_rate, R.id.include_nav_drawer_bottom_items) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                Loader.rate(activity);
            }
        });
        addItem(new ActivityNavDrawerItem(SettingActivity.class, activity.getString(R.string.setting), R.drawable.ic_settings, R.id.include_nav_drawer_bottom_items));
        addItem(new BaseNavDrawerItem(activity.getString(R.string.exit), R.drawable.ic_exit, R.id.include_nav_drawer_bottom_items) {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                Loader.exit(activity);
            }
        });

    }
}
