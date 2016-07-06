package com.amir.stickergram;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.AsyncTaskPhoneAdapter;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.navdrawer.MainNavDrawer;
import com.amir.stickergram.sticker.single.SingleStickersAdapter;
import com.amir.stickergram.sticker.single.StickerItem;

import java.io.File;

public class PhoneStickersActivity extends BaseActivity implements SingleStickersAdapter.OnStickerClickListener, SwipeRefreshLayout.OnRefreshListener, AsyncTaskPhoneAdapter.AsyncPhoneTaskListener {
    private static final String IS_REFRESHING = "IS_REFRESHING";
    private static final String STICKER_COUNT = "STICKER_COUNT";
    private static final String PERCENT = "PERCENT";

    TextView loadingTextPercentage;
    TextView loadingStickersCount;
    TextView noStickerText;
    View loadingDialogView;
    AlertDialog dialog;
    RecyclerView recyclerView;
    SingleStickersAdapter adapter;
    SwipeRefreshLayout swipeRefresh;
    AsyncTaskPhoneAdapter task;

    int stickerCount = 0;
    int percent = 0;
    Bundle savedInstanceState;
    boolean wasRefreshing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_stickers);
        setNavDrawer(new MainNavDrawer(this));
        this.savedInstanceState = savedInstanceState;

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.activity_phone_stickers_swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.activity_phone_stickers_list);
        noStickerText = (TextView) findViewById(R.id.activity_phone_stickers_no_cached_text);

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this);
            swipeRefresh.setColorSchemeColors(
                    Color.parseColor("#FF00DDFF"),
                    Color.parseColor("#FF99CC00"),
                    Color.parseColor("#FFFFBB33"),
                    Color.parseColor("#FFFF4444"));
        }
        if (recyclerView != null) {
            adapter = new SingleStickersAdapter(this);
            adapter.refreshPhoneSticker();
            if (isTablet || isInLandscape)
                recyclerView.setLayoutManager(new GridLayoutManager(PhoneStickersActivity.this, 5));
            else
                recyclerView.setLayoutManager(new GridLayoutManager(PhoneStickersActivity.this, 3));
            recyclerView.setAdapter(adapter);
        }

        if (savedInstanceState != null) {
            wasRefreshing = savedInstanceState.getBoolean(IS_REFRESHING, false);
            TypedValue typed_value = new TypedValue();
            this.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
            swipeRefresh.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
            swipeRefresh.setRefreshing(wasRefreshing);
        }

        if (!hasCashedPhoneStickersOnce())
            callAsyncTaskPhoneAdapter();

        if (savedInstanceState != null && loadingTextPercentage != null && loadingStickersCount != null) {
            percent = savedInstanceState.getInt(PERCENT);
            stickerCount = savedInstanceState.getInt(STICKER_COUNT);
            loadingTextPercentage.setText(percent + "%");
            loadingStickersCount.setText(String.valueOf(stickerCount));
        }

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (task != null)
            task.detach();
        return (task);
    }

    @Override
    public void OnStickerClicked(StickerItem item) {
        if (item.getBitmap() != null)
            Loader.loadStickerDialog(item.getUri(), this);
        else {
            setPhoneStickerCashStatus(false);
            callAsyncTaskPhoneAdapter();
        }
    }

    @Override
    public void OnStickerLongClicked(StickerItem item) {

    }

    @Override
    public void OnNoItemExistedListener() {
        if (noStickerText != null) noStickerText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        callAsyncTaskPhoneAdapter();
    }


    @Override
    public void onTaskStartListener() {
        if (!hasCashedPhoneStickersOnce()) //to not to show the loading firstLoadingDialog if user is doing a swipeRefresh
            instantiateLoadingDialog();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTaskUpdateListener(int percent, int stickerCount) {
        this.percent = percent;
        this.stickerCount = stickerCount;

        if (loadingTextPercentage != null && loadingStickersCount != null) {
            String percentTemp = String.valueOf(percent);
            String stickerCountTemp = String.valueOf(stickerCount);
            if (Loader.deviceLanguageIsPersian()) {
                percentTemp = Loader.convertToPersianNumber(percentTemp);
                loadingTextPercentage.setText("% " + percentTemp);
                stickerCountTemp = Loader.convertToPersianNumber(stickerCountTemp);
            } else loadingTextPercentage.setText(percentTemp + " %");

            loadingStickersCount.setText(stickerCountTemp);

        }

    }

    @Override
    public void onTaskFinishedListener() {
        if (noStickerText != null) noStickerText.setVisibility(View.GONE);
        manageView();
    }

    private void manageView() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        if (swipeRefresh != null)
            swipeRefresh.setRefreshing(false);
        setPhoneStickerCashStatus(true);
        adapter.refreshPhoneSticker();
    }

    @Override
    public void onNoCashDirectoryListener() {
        Toast.makeText(this, getString(R.string.couldn_t_find_telegram_cash_directory), Toast.LENGTH_LONG).show();
        if (!BaseActivity.isTelegramInstalled)
            Toast.makeText(this, getString(R.string.telegram_is_not_installed), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onNoStickerWereFoundListener() {
        if (noStickerText != null)
            noStickerText.setVisibility(View.VISIBLE);
        manageView();
    }

    @Override
    public void onRequestReadWritePermission() {
        Loader.gainPermission(this, Loader.PHONE_STICKERS_GAIN_PERMISSION);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_REFRESHING, swipeRefresh.isRefreshing());
        outState.putInt(STICKER_COUNT, stickerCount);
        outState.putInt(PERCENT, percent);
        super.onSaveInstanceState(outState);
    }

    private void callAsyncTaskPhoneAdapter() {
        task = (AsyncTaskPhoneAdapter) getLastCustomNonConfigurationInstance();

        if (task == null) {
            task = new AsyncTaskPhoneAdapter(this);
            task.execute(adapter);
        } else {
            instantiateLoadingDialog();
            task.attach(this);
        }
    }

    private void instantiateLoadingDialog() {
        loadingDialogView = getLayoutInflater().inflate(R.layout.dialog_phone_stickers_loading, null, false);
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
            Log.e(getClass().getSimpleName(), "instantiateDialog firstLoadingDialog was set to null");
        }
        dialog = new AlertDialog.Builder(PhoneStickersActivity.this)
                .setView(loadingDialogView)
                .setCancelable(false)
                .create();
        dialog.show();

        loadingTextPercentage = (TextView) loadingDialogView.findViewById(R.id.phone_loading_dialog_text_percentage);
        loadingStickersCount = (TextView) loadingDialogView.findViewById(R.id.phone_loading_dialog_total_sticker);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Loader.PHONE_STICKERS_GAIN_PERMISSION) {
//            Log.e(getClass().getSimpleName(), "------here");
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finish();
                startActivity(new Intent(this, PhoneStickersActivity.class));
                this.overridePendingTransition(0, 0);

                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                Log.e(getClass().getSimpleName(), "permission denied");
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
    public void onDestroy() {
        if (dialog != null) {
            Log.e(getClass().getSimpleName(), "onDestroy firstLoadingDialog was set to null");
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.phone_sticker_activity_menu_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.phone_sticker_activity_menu_item_refresh) {
            onRefresh();
            Toast.makeText(this, getString(R.string.sweep_refresh_is_also_available), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
