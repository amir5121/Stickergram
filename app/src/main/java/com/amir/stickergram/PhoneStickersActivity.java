package com.amir.stickergram;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.navdrawer.MainNavDrawer;
import com.amir.stickergram.phoneStickers.AsyncStickersCut;
import com.amir.stickergram.phoneStickers.CustomRecyclerView;
import com.amir.stickergram.phoneStickers.organizedDetailed.OrganizedStickersDetailedDialogFragment;
import com.amir.stickergram.phoneStickers.organizedIcon.OnStickerClickListener;
import com.amir.stickergram.phoneStickers.organizedIcon.OrganizedStickersIconFragment;
import com.amir.stickergram.phoneStickers.unorganized.PhoneStickersUnorganizedFragment;
import com.amir.stickergram.sticker.icon.IconItem;

public class PhoneStickersActivity extends BaseActivity
        implements OnStickerClickListener,
        AsyncStickersCut.AsyncCutCallbacks,
        CustomRecyclerView.RecyclerViewMovementCallbacks,
        PhoneStickersUnorganizedFragment.UnorganizedFragmentCallbacks {

    private static final long ANIMATION_DURATION = 500;

    PhoneStickersUnorganizedFragment unorganizedFragment;
    View loadingFrame;
    View organizedFragmentView;
    View unorganizedFragmentView;
    private int organizedFragmentHeight = 0;
    public boolean isOrganizedFragmentHidden = false;
    private String lastClickedIcon = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_stickers);
        setNavDrawer(new MainNavDrawer(this));

        setFont((ViewGroup) findViewById(R.id.nav_drawer));
        setFont((ViewGroup) findViewById(R.id.activity_phone_stickers_main_container));

        unorganizedFragment = (PhoneStickersUnorganizedFragment) getSupportFragmentManager().findFragmentById(R.id.activity_phone_stickers_phone_stickers_unorganized_fragment);

        unorganizedFragmentView = findViewById(R.id.activity_phone_stickers_organized_fragment_container);
        organizedFragmentView = findViewById(R.id.activity_phone_stickers_phone_stickers_organized_fragment);

        loadingFrame = findViewById(R.id.activity_phone_stickers_loading_frame);

        if (PhoneStickersUnorganizedFragment.isInCropMode) onSlideUpCallback();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.phone_sticker_unoraganized_fragment_menu, menu);
        menu.getItem(1).setVisible(PhoneStickersUnorganizedFragment.isInCropMode);
        menu.getItem(0).setVisible(!PhoneStickersUnorganizedFragment.isInCropMode);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.phone_sticker_activity_menu_item_refresh) {
            unorganizedFragment.onRefresh();
            Toast.makeText(this, getString(R.string.sweep_refresh_is_also_available), Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.phone_sticker_activity_menu_item_cut) {
            unorganizedFragment.setEnable(false);
            onSlideDownCallback();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (PhoneStickersUnorganizedFragment.isInCropMode || !unorganizedFragment.isEnable()) {
            if (!unorganizedFragment.isEnable()) {
                unorganizedFragment.setEnable(true);
                onSlideUpCallback();
                return;
            }
            if (PhoneStickersUnorganizedFragment.isInCropMode) {
                unorganizedFragment.toggleCutMode();
            }
        } else {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    public void OnIconClicked(IconItem item) {
        lastClickedIcon = item.getFolder();

        if (PhoneStickersUnorganizedFragment.isInCropMode) {
            new AsyncStickersCut(this, unorganizedFragment.getSelectedItems(), item.getFolder(), this).execute();

        } else {
            OrganizedStickersDetailedDialogFragment
                    .newInstance(item.getFolder())
                    .show(getSupportFragmentManager(), "dialog");
        }
    }

    @Override
    public void OnIconLongClicked(IconItem item) {

    }

    @Override
    public void OnNoItemWereFoundListener() {
        Log.e(getClass().getSimpleName(), "OnNoItemWereFound");
    }

    @Override
    public void OnCreateNewFolderSelected() {
        //is handled in the Fragment
        //intentionally empty
    }

    @Override
    public void onCutStarted() {
        loadingFrame.setClickable(true);
        loadingFrame.animate()
                .alpha(1)
                .setDuration(300)
                .start();
        unorganizedFragment.setEnable(true);

        unorganizedFragment.hideSelectedList();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        organizedFragmentHeight = organizedFragmentView.getHeight();
    }

    @Override
    public void onCutFinished() {
        loadingFrame.setClickable(false);
        loadingFrame.animate().alpha(0).setDuration(300).start();

        if (lastClickedIcon != null)
            OrganizedStickersDetailedDialogFragment
                    .newInstance(lastClickedIcon)
                    .show(getSupportFragmentManager(), "dialog");


        onSlideDownCallback();

        OrganizedStickersIconFragment fragment =
                (OrganizedStickersIconFragment) getSupportFragmentManager().findFragmentById(R.id.activity_phone_stickers_phone_stickers_organized_fragment);

        unorganizedFragment.toggleCutMode();
        if (fragment != null) fragment.refreshItems();

    }


    @Override
    public void onSlideUpCallback() {
        if (!isOrganizedFragmentHidden) {
            isOrganizedFragmentHidden = true;
            organizedFragmentView
                    .animate()
                    .translationY(-organizedFragmentHeight)
                    .setDuration(ANIMATION_DURATION)
                    .alpha(0)
                    .start();
        }
    }

    @Override
    public void onSlideDownCallback() {
        if (isOrganizedFragmentHidden && !PhoneStickersUnorganizedFragment.isInCropMode || !unorganizedFragment.isEnable()) {
            slideDown();
        }
    }

    private void slideDown() {
        isOrganizedFragmentHidden = false;
        organizedFragmentView
                .animate()
                .translationY(0)
                .alpha(1)
                .setDuration(ANIMATION_DURATION)
                .start();
    }

    @Override
    public void cutModeToggled(boolean enabled) {
        invalidateOptionsMenu();
        unorganizedFragment.setSwipeRefreshEnable(!enabled);
        if (enabled) {
            onSlideUpCallback();
        } else {
            onSlideDownCallback();
        }
    }
}
