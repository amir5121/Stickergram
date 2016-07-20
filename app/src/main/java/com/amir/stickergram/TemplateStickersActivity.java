package com.amir.stickergram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amir.stickergram.sticker.icon.IconItem;
import com.amir.stickergram.sticker.icon.OnIconSelectedListener;
import com.amir.stickergram.sticker.icon.template.TemplateIconListFragment;
import com.amir.stickergram.sticker.pack.template.TemplateIconPackDetailedFragment;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.navdrawer.MainNavDrawer;

public class TemplateStickersActivity extends BaseActivity implements OnIconSelectedListener {
    public static final String ICON_STICKER_ITEM_NAME = "ICON_FOLDER";
    private static final String DETAILED_ICON_FRAGMENT = "DETAILED_ICON_FRAGMENT";
    private static final String ICONS_FRAGMENT = "ICONS_FRAGMENT";
    private static final String ICON_STICKER_ITEM_EN_NAME = "ICON_STICKER_ITEM_EN_NAME";

    private String name;
    private String enName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_stickers);
        setNavDrawer(new MainNavDrawer(this));

        if (savedInstanceState != null) {
            saveState(
                    savedInstanceState.getString(ICON_STICKER_ITEM_NAME, null),
                    savedInstanceState.getString(ICON_STICKER_ITEM_EN_NAME, null));
            return;
        }

        if (findViewById(R.id.activity_template_sticker_fragment_container) != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_template_sticker_fragment_container, new TemplateIconListFragment(), ICONS_FRAGMENT)
                    .commit();
        }

    }

    @Override
    public void OnIconSelected(IconItem item) {
        name = item.getName(); //is used to hold the state
        enName = item.getEnName();
        Log.e(getClass().getSimpleName(), "Name: " + name);
        //what happening here is the same as saveState
        instantiateFragment(item.getName(), item.getEnName());
    }

    @Override
    public void OnNoStickerWereFoundListener() {
//        Log.e(getClass().getSimpleName(), "no sticker was found");
    }

    private void saveState(String name, String enName) {
        this.name = name;
        this.enName = enName;
        if (name == null) {
            if (findViewById(R.id.activity_template_sticker_fragment_container) != null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_template_sticker_fragment_container, new TemplateIconListFragment(), ICONS_FRAGMENT)
                        .commit();
            return;
        }
        instantiateFragment(name, enName);
    }

    private void instantiateFragment(String name, String enName) {
//        Log.e(getClass().getSimpleName(), "name: " + name);
        TemplateIconPackDetailedFragment templateIconPackDetailedFragment;
        if (findViewById(R.id.activity_template_sticker_fragment_container) != null) {
            templateIconPackDetailedFragment = new TemplateIconPackDetailedFragment();

            while (getSupportFragmentManager().getBackStackEntryCount() >= 1)
                getSupportFragmentManager().popBackStackImmediate();

            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.activity_template_sticker_fragment_container, templateIconPackDetailedFragment, DETAILED_ICON_FRAGMENT).
                    addToBackStack(null).
                    commit();

            templateIconPackDetailedFragment.refresh(name, enName);
        } else {
            templateIconPackDetailedFragment =
                    (TemplateIconPackDetailedFragment) getSupportFragmentManager().findFragmentById(R.id.activity_template_stickers_detailed_fragment);

            templateIconPackDetailedFragment.refresh(name, enName);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ICON_STICKER_ITEM_NAME, name);
        outState.putString(ICON_STICKER_ITEM_EN_NAME, enName);
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.activity_template_sticker_fragment_container) == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        } else
            super.onBackPressed();
    }
}
