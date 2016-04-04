package com.amir.telegramstickerbuilder;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.AsyncTaskPhoneAdapter;
import com.amir.telegramstickerbuilder.infrastructure.Loader;
import com.amir.telegramstickerbuilder.navdrawer.MainNavDrawer;
import com.amir.telegramstickerbuilder.sticker.single.SingleStickersAdapter;
import com.amir.telegramstickerbuilder.sticker.single.StickerItem;

import java.io.File;

public class PhoneStickersActivity extends BaseActivity implements SingleStickersAdapter.OnStickerClickListener, SwipeRefreshLayout.OnRefreshListener, AsyncTaskPhoneAdapter.AsyncPhoneTaskListener{
    public static final String PHONE_STICKERS_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + "org.telegram.messenger" + File.separator + "cache" + File.separator;
    private static final String IS_REFRESHING = "IS_REFRESHING";
    private static final int THUMBNAIL_IMAGE_QUALITY = 85;

    TextView loadingTextPercentage;
    TextView loadingStickersCount;
    View loadingDialogView;
    AlertDialog dialog;
    RecyclerView recyclerView;
    SingleStickersAdapter adapter;
    SwipeRefreshLayout swipeRefresh;
    AsyncTaskPhoneAdapter task;

    boolean wasRefreshing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_stickers);
        setNavDrawer(new MainNavDrawer(this));

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.activity_phone_stickers_swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.activity_phone_stickers_list);

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this);
            swipeRefresh.setColorSchemeColors(
                    Color.parseColor("#FF00DDFF"),
                    Color.parseColor("#FF99CC00"),
                    Color.parseColor("#FFFFBB33"),
                    Color.parseColor("#FFFF4444"));
        }
        if (recyclerView != null) {
            adapter = new SingleStickersAdapter(this, this);
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

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (task != null)
            task.detach();
        return (task);
    }

    @Override
    public void OnStickerClicked(StickerItem item) {
        Loader.loadStickerDialog(item.getUri(), this);
    }

    @Override
    public void OnStickerLongClicked(StickerItem item) {

    }

    @Override
    public void onRefresh() {
        callAsyncTaskPhoneAdapter();
    }


    @Override
    public void onTaskStartListener() {
        if (!hasCashedPhoneStickersOnce()) //to not to show the loading dialog if user is doing a swipeRefresh
            instantiateLoadingDialog();
    }

    @Override
    public void onTaskDismissedListener() {
        //TODO: What happen if there are no stickers in the directory
    }

    @Override
    public void onTaskUpdateListener(int percent, int stickerCount) {
        if (loadingTextPercentage != null && loadingStickersCount != null) {
            loadingTextPercentage.setText(percent + "%");
            loadingStickersCount.setText(String.valueOf(stickerCount));
        }
    }

    @Override
    public void onTaskFinishedListener() {
        if (dialog != null)
            dialog.hide();
        if (swipeRefresh != null)
            swipeRefresh.setRefreshing(false);
        setPhoneStickerCashStatus(true);
        adapter.refreshPhoneSticker();

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_REFRESHING, swipeRefresh.isRefreshing());
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
        loadingDialogView = getLayoutInflater().inflate(R.layout.dialog_phone_stickers_loading, null);
        dialog = new AlertDialog.Builder(PhoneStickersActivity.this)
                .setView(loadingDialogView)
                .setCancelable(false)
                .create();
        dialog.show();

        loadingTextPercentage = (TextView) loadingDialogView.findViewById(R.id.phone_loading_dialog_text_percentage);
        loadingStickersCount = (TextView) loadingDialogView.findViewById(R.id.phone_loading_dialog_total_sticker);
    }

}
