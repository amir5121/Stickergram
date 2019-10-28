package com.amir.stickergram;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.navdrawer.MainNavDrawer;
import com.amir.stickergram.sticker.icon.IconItem;
import com.amir.stickergram.sticker.icon.OnIconSelectedListener;
import com.amir.stickergram.sticker.icon.user.UserIconListFragment;
import com.amir.stickergram.sticker.pack.user.UserIconPackDetailedFragment;

public class UserStickersActivity extends BaseActivity
        implements OnIconSelectedListener//this is used for the user sticker click event
{
    public static final String ICON_FOLDER = "ICON_FOLDER";
    private static final String DETAILED_ICON_FRAGMENT = "DETAILED_ICON_FRAGMENT";
    private static final String ICONS_FRAGMENT = "ICONS_FRAGMENT";
    private static final String PUBLISH_NOTE_STATUS = "PUBLISH_NOTE_STATUS";

    private String folder;
    private boolean noItemWereFoundFlag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Loader.INSTANCE.checkPermission(this)) {
            Loader.INSTANCE.gainPermission(this, Constants.USER_STICKER_GAIN_PERMISSION);
            return;
        }

        setContentView(R.layout.activity_user_stickers);
        setNavDrawer(new MainNavDrawer(this));

        setUpView();

        if (savedInstanceState != null) {
            saveState(savedInstanceState.getString(ICON_FOLDER, null));
            return;
        }

        if (findViewById(R.id.activity_user_stickers_fragment_container) != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_user_stickers_fragment_container, new UserIconListFragment(), ICONS_FRAGMENT)
                    .commit();
        }

        Intent intent = getIntent();//when we are coming from the savingStickerActivity
        if (intent != null) {
            folder = intent.getStringExtra(SaveStickerActivity.EXTRA_FOLDER);
            if (folder != null) {
                getSupportFragmentManager().popBackStackImmediate();
                instantiateFragment(folder);
            }
        }
    }

    private void setUpView() {
        View noItemFoundText = findViewById(R.id.activity_user_stickers_no_sticker);
        if (noItemFoundText != null && noItemWereFoundFlag)
            noItemFoundText.setVisibility(View.VISIBLE);

        setFont((ViewGroup) findViewById(R.id.nav_drawer));
        setFont((ViewGroup) findViewById(R.id.activity_phone_stickers_main_container));

    }

    @Override
    public void OnIconSelected(IconItem item) {
        folder = item.getName(); //folder is used to hold the state
        //what happening here is the same as saveState
        instantiateFragment(folder);
    }

    @Override
    public void OnNoStickerWereFoundListener() {
        noItemWereFoundFlag = true;
        setUpView();
        // there is topMarginAnimation scenario where this method will be called before the call to OnCreate so i'm calling the setUpView

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
        outState.putString(ICON_FOLDER, folder);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.USER_STICKER_GAIN_PERMISSION) {
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


    @Override
    public void onBackPressed() {
        if (findViewById(R.id.activity_user_stickers_fragment_container) == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            super.onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

}
