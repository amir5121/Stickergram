package com.amir.stickergram;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.navdrawer.MainNavDrawer;
import com.amir.stickergram.sticker.icon.AssetIconListFragment;
import com.amir.stickergram.sticker.icon.IconItem;
import com.amir.stickergram.sticker.icon.UserIconListFragment;
import com.amir.stickergram.sticker.pack.UserIconPackDetailedFragment;

public class UserStickersActivity extends BaseActivity implements AssetIconListFragment.OnIconSelectedListener, View.OnClickListener {
    public static final String ICON_FOLDER = "ICON_FOLDER";
    private static final String DETAILED_ICON_FRAGMENT = "DETAILED_ICON_FRAGMENT";
    private static final String ICONS_FRAGMENT = "ICONS_FRAGMENT";
    private static final String PUBLISH_NOTE_STATUS = "PUBLISH_NOTE_STATUS";

    private String folder;
    private boolean publishNoteIsHidden;
    View publishNoteContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Loader.checkPermission(this)) {
            Loader.gainPermission(this, Loader.USER_STICKER_GAIN_PERMISSION);
            return;
        }

        setContentView(R.layout.activity_user_stickers);
        setNavDrawer(new MainNavDrawer(this));

        setUpView();


        if (savedInstanceState != null) {
            if (publishNoteContainer != null) {
                publishNoteIsHidden = savedInstanceState.getBoolean(PUBLISH_NOTE_STATUS, false);
                publishNoteContainer.setVisibility(publishNoteIsHidden ? View.GONE : View.VISIBLE);
            }
            saveState(savedInstanceState.getString(ICON_FOLDER, null));
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

    private void setUpView() {
        View publishNoteCloseButton = findViewById(R.id.include_detailed_note_close);
        View publishNoteText = findViewById(R.id.include_detailed_note_text);
        View publishIcon = findViewById(R.id.include_detailed_note_info_icon);
        publishNoteContainer = findViewById(R.id.include_detailed_note);
        if (publishNoteCloseButton != null &&
                publishIcon != null &&
                publishNoteText != null) {
            publishNoteText.setOnClickListener(this);
            publishNoteText.setOnClickListener(this);
            publishNoteCloseButton.setOnClickListener(this);

        }

    }

    @Override
    public void OnIconSelected(IconItem item) {
        folder = item.getFolder(); //folder is used to hold the state
        //what happening here is the same as saveState
        instantiateFragment(folder);
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
        outState.putBoolean(PUBLISH_NOTE_STATUS, publishNoteIsHidden);
        outState.putString(ICON_FOLDER, folder);
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.include_detailed_note_close) {
            if (publishNoteContainer != null) {
                publishNoteContainer.setVisibility(View.GONE);
                publishNoteIsHidden = true;
                Log.e(getClass().getSimpleName(), "on click: " + publishNoteIsHidden);
            }
        } else if (itemId == R.id.include_detailed_note_text || itemId == R.id.include_detailed_note_info_icon) {
            finish();
            startActivity(new Intent(this, HowToActivity.class));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Loader.USER_STICKER_GAIN_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //restarting the activity
                finish();
                startActivity(new Intent(this, UserStickersActivity.class));
                this.overridePendingTransition(0, 0);

                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                finish();
                startActivity(new Intent(this, MainActivity.class));
                this.overridePendingTransition(0, 0);
                Toast.makeText(this, getResources().getString(R.string.need_permission_to_look_for_your_stickers), Toast.LENGTH_LONG).show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }

            return;
        }
    }
}
