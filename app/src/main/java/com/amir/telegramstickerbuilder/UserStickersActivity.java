package com.amir.telegramstickerbuilder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.navdrawer.MainNavDrawer;
import com.amir.telegramstickerbuilder.sticker.icon.AssetIconListFragment;
import com.amir.telegramstickerbuilder.sticker.icon.IconItem;
import com.amir.telegramstickerbuilder.sticker.icon.UserIconListFragment;
import com.amir.telegramstickerbuilder.sticker.pack.UserIconPackDetailedFragment;

public class UserStickersActivity extends BaseActivity implements AssetIconListFragment.OnIconSelectedListener {
    public static final String ICON_STICKER_ITEM_FOLDER = "ICON_STICKER_ITEM_FOLDER";
    private static final String DETAILED_ICON_FRAGMENT = "DETAILED_ICON_FRAGMENT";
    private static final String ICONS_FRAGMENT = "ICONS_FRAGMENT";

    private String folder;

    //todo: add explosm to the stickers
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stickers);
        setNavDrawer(new MainNavDrawer(this));

        if (savedInstanceState != null) {
            saveState(savedInstanceState.getString(ICON_STICKER_ITEM_FOLDER, null));
            return;
        }

        if (findViewById(R.id.activity_user_stickers_fragment_container) != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_user_stickers_fragment_container, new UserIconListFragment(), ICONS_FRAGMENT)
                    .commit();
        }

        Intent intent = getIntent();
        if (intent != null) {
            folder = intent.getStringExtra(SavingStickerActivity.EXTRA_FOLDER);
            if (folder != null) {
                getSupportFragmentManager().popBackStackImmediate();
                instantiateFragment(folder);
            }
        }
    }

    @Override
    public void OnIconSelected(IconItem item) {
        folder = item.getFolder(); //is used to hold the state
        //what happening here is the same as saveState
        instantiateFragment(item.getFolder());
    }

    private void saveState(String folder) {
        this.folder = folder;
        if (folder == null) {
            if (findViewById(R.id.activity_user_stickers_fragment_container) != null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_user_stickers_fragment_container, new UserIconListFragment(), ICONS_FRAGMENT)
                        .commit();
            return;
        }
        instantiateFragment(folder);
    }

    private void instantiateFragment(String folder) {
        UserIconPackDetailedFragment userIconPackDetailedFragment;
        if (findViewById(R.id.activity_user_stickers_fragment_container) != null) {
            userIconPackDetailedFragment = new UserIconPackDetailedFragment();

            while (getSupportFragmentManager().getBackStackEntryCount() >= 1)
                getSupportFragmentManager().popBackStackImmediate();

            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.activity_user_stickers_fragment_container, userIconPackDetailedFragment, DETAILED_ICON_FRAGMENT).
                    addToBackStack(null).
                    commit();

            userIconPackDetailedFragment.refresh(folder);
        } else {
            userIconPackDetailedFragment =
                    (UserIconPackDetailedFragment) getSupportFragmentManager().findFragmentById(R.id.activity_template_stickers_detailed_fragment);

            userIconPackDetailedFragment.refresh(folder);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ICON_STICKER_ITEM_FOLDER, folder);
    }
}
