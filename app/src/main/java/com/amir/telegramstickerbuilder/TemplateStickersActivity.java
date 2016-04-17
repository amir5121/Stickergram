package com.amir.telegramstickerbuilder;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.amir.telegramstickerbuilder.sticker.icon.IconItem;
import com.amir.telegramstickerbuilder.sticker.icon.IconListFragment;
import com.amir.telegramstickerbuilder.sticker.pack.IconPackDetailedFragment;
import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.navdrawer.MainNavDrawer;

public class TemplateStickersActivity extends BaseActivity implements IconListFragment.OnIconSelectedListener {
    public static final String ICON_STICKER_ITEM_FOLDER = "ICON_STICKER_ITEM_FOLDER";

    private String folder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_stickers);
        setNavDrawer(new MainNavDrawer(this));

        if (savedInstanceState != null) {
            saveState(savedInstanceState.getString(ICON_STICKER_ITEM_FOLDER, "Bunny"));
            return;
        }

        if (findViewById(R.id.activity_template_sticker_fragment_container) != null) {

            IconListFragment iconListFragment = new IconListFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_template_sticker_fragment_container, iconListFragment)
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
        instantiateFragment(folder);
    }

    private void instantiateFragment(String folder) {
        IconPackDetailedFragment iconPackDetailedFragment;
        if (findViewById(R.id.activity_template_sticker_fragment_container) != null) {
            iconPackDetailedFragment = new IconPackDetailedFragment();

            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.activity_template_sticker_fragment_container, iconPackDetailedFragment).
                    addToBackStack(null).
                    commit();

            iconPackDetailedFragment.refresh(folder);
        } else {
            iconPackDetailedFragment =
                    (IconPackDetailedFragment) getSupportFragmentManager().findFragmentById(R.id.activity_template_stickers_detailed_fragment);

            iconPackDetailedFragment.refresh(folder);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ICON_STICKER_ITEM_FOLDER, folder);
    }
}
