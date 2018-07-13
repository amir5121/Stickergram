package com.amir.stickergram.phoneStickers.unorganized;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amir.stickergram.EditImageActivity;
import com.amir.stickergram.MainActivity;
import com.amir.stickergram.PhoneStickersActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.image.ImageReceiverCallBack;
import com.amir.stickergram.infrastructure.AsyncTaskPhoneAdapter;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.sticker.single.SingleStickersAdapter;
import com.amir.stickergram.sticker.single.StickerItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhoneStickersUnorganizedFragment extends BaseFragment
        implements
        SingleStickersAdapter.OnStickerClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        AsyncTaskPhoneAdapter.AsyncPhoneTaskListener {

    private static final String IS_REFRESHING = "IS_REFRESHING";
    private static final String STICKER_COUNT = "STICKER_COUNT";
    private static final String PERCENT = "PERCENT";
    private static final String IS_ENABLE = "IS_ENABLE";
    private static final String SELECTED_ITEMS = "SELECTED_ITEMS";
    private static final String IS_IN_CROP_MODE = "IS_IN_CROP_MODE";

    public static boolean isInCropMode = false;

    private TextView loadingTextPercentage;
    private TextView loadingStickersCount;
    private TextView noStickerText;
    private AlertDialog dialog;
    private SingleStickersAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;

    private ArrayList<StickerItem> selectedItems;
    private int stickerCount = 0;
    private int percent = 0;

    private boolean onRefreshWasCalled = false;
    private UnorganizedFragmentCallbacks listener;
    private boolean enable = true;
    private View enableView;
    private boolean isAnImagePicker;
    private ImageReceiverCallBack imageListener;

    public static BaseFragment newInstance(boolean isAnImagePicker) {

        Bundle args = new Bundle();
        args.putBoolean(Constants.IS_AN_IMAGE_PICKER, isAnImagePicker);
        PhoneStickersUnorganizedFragment fragment = new PhoneStickersUnorganizedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.listener = (UnorganizedFragmentCallbacks) context;
        } catch (ClassCastException e) {
            throw new RuntimeException(context.getPackageName() + " must implement UnorganizedFragmentCallbacks");
        }
        try {
            this.imageListener = (ImageReceiverCallBack) context;
        } catch (ClassCastException e) {
            //do Nothing
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);

        Bundle args = getArguments();
        if (args != null) {
            isAnImagePicker = args.getBoolean(Constants.IS_AN_IMAGE_PICKER, false);
        }

        View view = inflater.inflate(R.layout.fragment_phone_stickers_unorganized, container, false);

        setFont((ViewGroup) view);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.activity_phone_stickers_unorganized_swipeRefresh);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.activity_phone_stickers_unorganized_list);
        noStickerText = (TextView) view.findViewById(R.id.activity_phone_stickers_no_cached_text);

        enableView = view.findViewById(R.id.fragment_phone_stickers_unorganized_enable_view);

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this);
            swipeRefresh.setColorSchemeColors(
                    Color.parseColor("#FF00DDFF"),
                    Color.parseColor("#FF99CC00"),
                    Color.parseColor("#FFFFBB33"),
                    Color.parseColor("#FFFF4444"));
        }
        if (recyclerView != null) {
            adapter = new SingleStickersAdapter((BaseActivity) getActivity(), this);
            adapter.refreshPhoneSticker();
            if (BaseActivity.isTablet || BaseActivity.isInLandscape) {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            }
            recyclerView.setAdapter(adapter);
        }

        isInCropMode = false;
        enable = true;
        if (savedInstanceState != null) {
            TypedValue typed_value = new TypedValue();
            getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
            swipeRefresh.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
            swipeRefresh.setRefreshing(savedInstanceState.getBoolean(IS_REFRESHING, false));
            setEnable(savedInstanceState.getBoolean(IS_ENABLE, true));
            isInCropMode = savedInstanceState.getBoolean(IS_IN_CROP_MODE, false);
            swipeRefresh.setEnabled(!isInCropMode);
            selectedItems = savedInstanceState.getParcelableArrayList(SELECTED_ITEMS);
            adapter.updateItems(selectedItems);
            adapter.notifyDataSetChanged();
        }

        if (!((BaseActivity) getActivity()).hasCashedPhoneStickersOnce()) {
            callAsyncTaskPhoneAdapter();
        }

        if (savedInstanceState != null && loadingTextPercentage != null && loadingStickersCount != null) {
            percent = savedInstanceState.getInt(PERCENT);
            stickerCount = savedInstanceState.getInt(STICKER_COUNT);
            loadingTextPercentage.setText(percent + "%");
            loadingStickersCount.setText(String.valueOf(stickerCount));
        }


        return view;
    }

    @Override
    public void OnStickerClicked(StickerItem item) {
        if (!isAnImagePicker) {
            if (item.getBitmap() == null) {
                ((BaseActivity) getActivity()).setPhoneStickerCashStatus(false);
                callAsyncTaskPhoneAdapter();
            } else if (!isInCropMode) {
                Loader.loadStickerDialog(item.getUri(), (BaseActivity) getActivity());
            } else {
                manageSelectedItems(item);
            }
        } else {
            imageListener.receivedImage(item.getBitmap());
        }
    }

    private void manageSelectedItems(StickerItem item) {
        if (selectedItems != null) {
            if (item.isSelected()) {
                item.setSelected(false);
//                adapter.updateItem(item);
                selectedItems.remove(item);
                if (selectedItems.size() == 0) {
                    selectedItems = null;
                    toggleCutMode();
                }
            } else {
                item.setSelected(true);
//                adapter.updateItem(item);
                selectedItems.add(item);
            }
            adapter.notifyDataSetChanged();

        } else {
            Log.e(getClass().getSimpleName(), "selectedItems was null");
        }
    }

    @Override
    public void OnStickerLongClicked(StickerItem item) {
        if (!isAnImagePicker) {
            if (!isInCropMode) {
                selectedItems = new ArrayList<>();
                toggleCutMode();
            }
            manageSelectedItems(item);
        }
    }

    public void toggleCutMode() {
        if (isInCropMode && selectedItems != null) {
            for (StickerItem item : selectedItems)
                item.setSelected(false);
        }
        isInCropMode = !isInCropMode;
        listener.cutModeToggled(isInCropMode);
        swipeRefresh.setEnabled(!isInCropMode);
        adapter.notifyDataSetChanged();
    }

    public List<StickerItem> getSelectedItems() {
        return selectedItems;
    }

    public void hideSelectedList() {
        if (selectedItems != null) {
            adapter.hideItems(selectedItems);

            adapter.refreshPhoneSticker();
        } else Log.e(getClass().getSimpleName(), "SelectedItems was null");
    }


    @Override
    public void OnNoItemExistedListener() {
        if (noStickerText != null) noStickerText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        onRefreshWasCalled = true;
        Log.e(getClass().getSimpleName(), "OnRefresh was called");
        callAsyncTaskPhoneAdapter();
    }

    @Override
    public void onTaskStartListener() {
        if (!((BaseActivity) getActivity()).hasCashedPhoneStickersOnce()) //to not to show the loading firstLoadingDialog if user is doing a swipeRefresh
            instantiateLoadingDialog();
        else swipeRefresh.setRefreshing(true);
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
        if (getActivity() != null)
            ((BaseActivity) getActivity()).setPhoneStickerCashStatus(true);
        adapter.refreshPhoneSticker();
    }

    @Override
    public void onNoCashDirectoryListener() {
        if (!BaseActivity.chosenMode.isAvailable)
            Toast.makeText(getActivity(), getString(R.string.telegram_is_not_installed), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getActivity(), getString(R.string.couldn_t_find_telegram_cash_directory), Toast.LENGTH_LONG).show();
        if (!isAnImagePicker) {
            getActivity().finish();
        } else
            dialog.dismiss();
    }

    @Override
    public void onNoStickerWereFoundListener() {
        if (noStickerText != null)
            noStickerText.setVisibility(View.VISIBLE);
        manageView();
    }

    @Override
    public void onRequestReadWritePermission() {
        Loader.gainPermission((BaseActivity) getActivity(), Constants.PHONE_STICKERS_GAIN_PERMISSION);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_REFRESHING, swipeRefresh.isRefreshing());
        outState.putInt(STICKER_COUNT, stickerCount);
        outState.putInt(PERCENT, percent);
        outState.putBoolean(IS_ENABLE, enable);
        outState.putBoolean(IS_IN_CROP_MODE, isInCropMode);
        if (selectedItems != null) outState.putParcelableArrayList(SELECTED_ITEMS, selectedItems);
        super.onSaveInstanceState(outState);
    }

    private void callAsyncTaskPhoneAdapter() {
        File file = new File(BaseActivity.STICKERGRAM_ROOT + ".nomedia");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!swipeRefresh.isRefreshing() || onRefreshWasCalled) {
            new AsyncTaskPhoneAdapter((BaseActivity) getActivity(), this).execute(adapter);
            onRefreshWasCalled = false;
        }
    }

    private void instantiateLoadingDialog() {
        View loadingDialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_phone_stickers_loading, null, false);
        setFont((ViewGroup) loadingDialogView);
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
            Log.e(getClass().getSimpleName(), "instantiateDialog firstLoadingDialog was set to null");
        }
        dialog = new AlertDialog.Builder(getContext())
                .setView(loadingDialogView)
                .setCancelable(false)
                .create();

        dialog.show();

        loadingTextPercentage = (TextView) loadingDialogView.findViewById(R.id.phone_loading_dialog_text_percentage);
        loadingStickersCount = (TextView) loadingDialogView.findViewById(R.id.phone_loading_dialog_total_sticker);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.PHONE_STICKERS_GAIN_PERMISSION) {
//            Log.e(getClass().getSimpleName(), "------here");
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getActivity().finish();
                startActivity(new Intent(getContext(), PhoneStickersActivity.class));
                getActivity().overridePendingTransition(0, 0);

                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                Log.e(getClass().getSimpleName(), "permission denied");
                getActivity().finish();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().overridePendingTransition(0, 0);
                Toast.makeText(getActivity(), getResources().getString(R.string.need_permission_to_look_for_your_stickers), Toast.LENGTH_LONG).show();
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

//    public View getRecyclerView() {
//        return recyclerView;
//    }

    public void setEnable(boolean enable) {
        this.enable = enable;
//        Log.e(getClass().getSimpleName(), "enable: " + enable);
        enableView.setClickable(!enable);
        if (enable) {
            enableView.animate()
                    .alpha(0)
                    .setDuration(300)
                    .start();
        } else {
            enableView.animate()
                    .alpha(1)
                    .setDuration(300)
                    .start();
        }
    }

    public boolean isEnable() {
        return enable;
    }

    public void setSwipeRefreshEnable(boolean enabled) {
        swipeRefresh.setEnabled(enabled);
    }


    public interface UnorganizedFragmentCallbacks {
        void cutModeToggled(boolean enabled);
    }
}
