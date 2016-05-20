package com.amir.stickergram;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.amir.stickergram.sticker.icon.IconItem;
import com.amir.stickergram.sticker.icon.AssetIconListFragment;
import com.amir.stickergram.sticker.pack.TemplateIconPackDetailedFragment;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.navdrawer.MainNavDrawer;

public class TemplateStickersActivity extends BaseActivity implements AssetIconListFragment.OnIconSelectedListener {
    public static final String ICON_STICKER_ITEM_FOLDER = "ICON_FOLDER";
    private static final String DETAILED_ICON_FRAGMENT = "DETAILED_ICON_FRAGMENT";
    private static final String ICONS_FRAGMENT = "ICONS_FRAGMENT";

    private String folder;

    //todo: add explosm to the stickers
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_stickers);
        setNavDrawer(new MainNavDrawer(this));

        if (savedInstanceState != null) {
            saveState(savedInstanceState.getString(ICON_STICKER_ITEM_FOLDER, null));
            return;
        }

        if (findViewById(R.id.activity_template_sticker_fragment_container) != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_template_sticker_fragment_container, new AssetIconListFragment(), ICONS_FRAGMENT)
                    .commit();
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
            if (findViewById(R.id.activity_template_sticker_fragment_container) != null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_template_sticker_fragment_container, new AssetIconListFragment(), ICONS_FRAGMENT)
                        .commit();
            return;
        }
        instantiateFragment(folder);
    }

    private void instantiateFragment(String folder) {
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

            templateIconPackDetailedFragment.refresh(folder);
        } else {
            templateIconPackDetailedFragment =
                    (TemplateIconPackDetailedFragment) getSupportFragmentManager().findFragmentById(R.id.activity_template_stickers_detailed_fragment);

            templateIconPackDetailedFragment.refresh(folder);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ICON_STICKER_ITEM_FOLDER, folder);
    }
}
