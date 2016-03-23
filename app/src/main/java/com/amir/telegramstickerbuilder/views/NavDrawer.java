package com.amir.telegramstickerbuilder.views;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseActivity;

import java.util.ArrayList;

public class NavDrawer {
    private ArrayList<NavDrawerItem> items;
    private NavDrawerItem selectedItem;

    protected BaseActivity activity;
    protected DrawerLayout drawerLayout;
    protected ViewGroup navDrawerView;

    public NavDrawer(BaseActivity activity) {
        this.activity = activity;
        items = new ArrayList<>();
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        navDrawerView = (ViewGroup) activity.findViewById(R.id.nav_drawer);

        if (drawerLayout == null || navDrawerView == null)
            throw new RuntimeException("To use this class you must have views with ids of drawer_layout and nav_drawer");

        Toolbar toolbar = activity.getToolbar();
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOpen(!isOpen());
            }
        });


    }

    public void addItem(NavDrawerItem item) {
        items.add(item);
        item.navDrawer = this;
    }

    public boolean isOpen() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public void setOpen(boolean isOpen) {
        if (isOpen)
            drawerLayout.openDrawer(GravityCompat.START);
        else
            drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void setSelectedItem(NavDrawerItem item) {
        if (selectedItem != null)
            selectedItem.setSelected(false);

        selectedItem = item;
        selectedItem.setSelected(true);
    }

    public void create() {
        LayoutInflater inflater = activity.getLayoutInflater();
        for (NavDrawerItem item : items) {
            item.inflate(inflater, navDrawerView);
        }
    }

    public static abstract class NavDrawerItem {
        protected NavDrawer navDrawer;

        public abstract void inflate(LayoutInflater inflater, ViewGroup container);

        public abstract void setSelected(boolean isSelected);
    }

    public static class BaseNavDrawerItem extends NavDrawerItem implements View.OnClickListener {
        private String text;
        private int iconDrawable;
        private int containerId;

        private ImageView icon;
        private TextView textView;
        private View view;
        private int defaultTextColor;

        public BaseNavDrawerItem(String text, int iconDrawable, int containerId) {
            this.text = text;
            this.iconDrawable = iconDrawable;
            this.containerId = containerId; //Can be top_items or bottom_items
        }

        @Override
        public void inflate(LayoutInflater inflater, ViewGroup navDrawerView) {
            ViewGroup container = (ViewGroup) navDrawerView.findViewById(containerId);
            if (container == null)
                throw new RuntimeException("Nav Drawer Item " + text + " could not be attached to ViewGroup. View Not Found");

            view = inflater.inflate(R.layout.nav_drawer_item, container, false);
            container.addView(view);

            view.setOnClickListener(this);
            icon = (ImageView) view.findViewById(R.id.nav_drawer_item_icon);
            textView = (TextView) view.findViewById(R.id.nav_drawer_item_text);
            defaultTextColor = textView.getCurrentTextColor();

            icon.setImageResource(iconDrawable);
            textView.setText(text);
        }

        @Override
        public void setSelected(boolean isSelected) {
            if (isSelected) {
                view.setBackgroundResource(R.drawable.nav_drawer_item_slected_background);
                textView.setTextColor(ContextCompat.getColor(navDrawer.activity, R.color.list_item_nav_drawer_selcted_item_text_color));
            } else {
                view.setBackgroundResource(0);
                textView.setTextColor(defaultTextColor);
            }
        }

        public void setText(String text) {
            this.text = text;

            if (view != null)
                textView.setText(text);
        }

        public void setIconDrawable(int iconDrawable) {
            this.iconDrawable = iconDrawable;

            if (view == null)
                icon.setImageResource(iconDrawable);
        }

        @Override
        public void onClick(View v) {
            navDrawer.setSelectedItem(this);
        }
    }

    public static class ActivityNavDrawerItem extends BaseNavDrawerItem {
        public final Class targetActivity;

        public ActivityNavDrawerItem(Class targetActivity, String text, int iconDrawable, int containerId) {
            super(text, iconDrawable, containerId);
            this.targetActivity = targetActivity;
        }

        @Override
        public void inflate(LayoutInflater inflater, ViewGroup navDrawerView) {
            super.inflate(inflater, navDrawerView);

            if (navDrawer.activity.getClass() == targetActivity) {
                navDrawer.setSelectedItem(this);
            }
        }

        @Override
        public void onClick(View view) {
            navDrawer.setOpen(false);
            BaseActivity activity = this.navDrawer.activity;

            if (activity.getClass() == targetActivity)
                return;

            super.onClick(view);

            //TODO: ANIMATION

            activity.startActivity(new Intent(activity, targetActivity));
            activity.finish();
        }
    }

}
