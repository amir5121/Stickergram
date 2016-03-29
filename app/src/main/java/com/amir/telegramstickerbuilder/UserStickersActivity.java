package com.amir.telegramstickerbuilder;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.AsyncTaskPhoneAdapter;
import com.amir.telegramstickerbuilder.infrastructure.AsyncTaskUserAdapter;
import com.amir.telegramstickerbuilder.navdrawer.MainNavDrawer;
import com.amir.telegramstickerbuilder.sticker.single.SingleStickersAdapter;
import com.amir.telegramstickerbuilder.sticker.single.StickerItem;

public class UserStickersActivity extends BaseActivity implements SingleStickersAdapter.OnStickerClickListener, AsyncTaskUserAdapter.AsyncUserTaskListener {
    public static final String USER_STICKERS_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TSB/User/";
    //ToDo: put a dot in the directory so the user don't see the stickers in the gallery

    RecyclerView recyclerView;
    View noStickerFrame;
    SingleStickersAdapter adapter;
    AsyncTaskUserAdapter task;
    View loadingDialogView;
    AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stickers);
        setNavDrawer(new MainNavDrawer(this));

        recyclerView = (RecyclerView) findViewById(R.id.activity_user_stickers_list);

        if (recyclerView == null)
            Log.e(getClass().getSimpleName(), "RecyclerView is null");

        if (recyclerView != null) {
            adapter = new SingleStickersAdapter(this, this);
            adapter.refreshPhoneSticker();

            if (isTablet)
                recyclerView.setLayoutManager(new GridLayoutManager(UserStickersActivity.this, 5));
            else
                recyclerView.setLayoutManager(new GridLayoutManager(UserStickersActivity.this, 3));

            recyclerView.setAdapter(adapter);
        }

        task = (AsyncTaskUserAdapter) getLastCustomNonConfigurationInstance();
        if (task == null) {
            task = new AsyncTaskUserAdapter(UserStickersActivity.this);
            task.execute(adapter);
        } else {
            instantiateLoadingDialog();
            task.attach(this);
        }

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (task != null)
            task.detach();
        return (task);
    }

    private void instantiateLoadingDialog() {
        loadingDialogView = getLayoutInflater().inflate(R.layout.dialog_simple_loading, null, false);
        dialog = new AlertDialog.Builder(UserStickersActivity.this)
                .setView(loadingDialogView)
                .setCancelable(false)
                .create();
        dialog.show();
    }

    @Override
    public void OnStickerClicked(StickerItem item) {

    }

    @Override
    public void OnStickerLongClicked(StickerItem item) {

    }

    @Override
    public void onTaskStartListener() {
        instantiateLoadingDialog();
    }

    @Override
    public void onTaskDismissedListener() {
        //TODO: What happen if there are no sticker in the directory
        Log.e(getClass().getSimpleName(), "onTaskDismissedListener was called");

        if (recyclerView != null)
            recyclerView.setVisibility(View.GONE);

        noStickerFrame = findViewById(R.id.no_sticker_found_frame);
        if (noStickerFrame != null)
            noStickerFrame.setVisibility(View.VISIBLE);

    }

    @Override
    public void onTaskFinishedListener() {

        if (dialog != null)
            dialog.hide();
        if (adapter != null)
            adapter.refreshUserSticker();
        else
            Log.e(getClass().getSimpleName(), "adapter is null");
    }
}
