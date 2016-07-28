package com.amir.stickergram.imageProcessing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.amir.stickergram.EditImageActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.infrastructure.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import app.minimize.com.seek_bar_compat.SeekBarCompat;

public class BackgroundRemoverFragment extends BaseFragment implements View.OnClickListener, RemoverView.RemoverViewCallbacks, SeekBar.OnSeekBarChangeListener {
    private static final String BITMAP_EXTRA = "BITMAP_EXTRA";
    private static final int REMOVE = 0;
    private static final int REPAIR = 1;
    private static final int ZOOM_ON = 2;
    private static final int ZOOM_OFF = 3;
    private static final int SHOW_FLOOD_POINTER = 4;
    private static final int APPLY_FLOOD = 5;
    private static final String BITMAP_WIDTH = "BITMAP_WIDTH";
    private static final String BITMAP_HEIGHT = "BITMAP_HEIGHT";
    private RemoverView removerView;
    private PointerViewBottom pointerViewBottom;
    private ImageButton modeButton;
    private SeekBarCompat radiusSeekBar;
    private SeekBarCompat offsetSeekBar;
    private SeekBarCompat toleranceSeekBar;
    private View radiusContainer;
    private View offsetContainer;
    private View toleranceContainer;
    private ImageButton zoomToggleButton;
    private View loadingDialog;
    private boolean applyFloodFillMode = false;
    private ImageButton floodFillerButton;

    public static Fragment getInstance(Bundle bundle) {
        BackgroundRemoverFragment fragment = new BackgroundRemoverFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_remove_background, container, false);
        modeButton = (ImageButton) view.findViewById(R.id.fragment_remove_background_repair_toggle_mode);
        modeButton.setOnClickListener(this);

        view.findViewById(R.id.fragment_remove_background_radius_button).setOnClickListener(this);
        radiusSeekBar = (SeekBarCompat) view.findViewById(R.id.fragment_remove_background_radius_seek_bar);
        radiusSeekBar.setOnSeekBarChangeListener(this);
        radiusContainer = view.findViewById(R.id.fragment_remove_background_radius_container);

        zoomToggleButton = (ImageButton) view.findViewById(R.id.fragment_remove_background_mode_zoom_toggle);
        zoomToggleButton.setOnClickListener(this);

        loadingDialog = view.findViewById(R.id.fragment_remove_background_loading_dialog);

        offsetSeekBar = (SeekBarCompat) view.findViewById(R.id.fragment_remove_background_offset_seek_bar);
        offsetSeekBar.setOnSeekBarChangeListener(this);
        offsetContainer = view.findViewById(R.id.fragment_remove_background_offset_container);

        view.findViewById(R.id.fragment_remove_background_mode_offset).setOnClickListener(this);

        floodFillerButton = (ImageButton) view.findViewById(R.id.fragment_remove_background_flood_filler);
        floodFillerButton.setOnClickListener(this);
        toleranceSeekBar = (SeekBarCompat) view.findViewById(R.id.fragment_remove_background_tolerance_seek_bar);
        toleranceSeekBar.setOnSeekBarChangeListener(this);
        toleranceContainer = view.findViewById(R.id.fragment_remove_background_tolerance_container);

        try {
//            Bitmap mBitmap = Constants.getWorkingBitmap();
//            if (mBitmap == null)
            Bitmap mBitmap = MediaStore.Images.Media.getBitmap(
                    getActivity().getContentResolver(), (Uri) getArguments().getParcelable(Constants.EDIT_IMAGE_URI));
//            Constants.setWorkingBitmap(mBitmap);
            if (mBitmap != null) {
                mBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
                if (savedInstanceState != null)
                    mBitmap =
                            (savedInstanceState.getParcelable(BITMAP_EXTRA) != null) ?
                                    (Bitmap) savedInstanceState.getParcelable(BITMAP_EXTRA) : mBitmap;
                removerView = new RemoverView((BaseActivity) getActivity(), this, mBitmap);
            } else {
                Log.e(getClass().getSimpleName(), "bitmap was null");
                Toast.makeText(getContext(), getString(R.string.there_was_a_problem_getting_the_picture), Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        pointerViewBottom = new PointerViewBottom(getContext());
        RelativeLayout surfaceContainer = (RelativeLayout) view.findViewById(R.id.fragment_remove_background_surface_container);
        surfaceContainer.addView(removerView);
        surfaceContainer.addView(pointerViewBottom);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BITMAP_EXTRA, removerView.getFinishedBitmap());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.crop_activity_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.crop_activity_menu_save) {
            File file = new File(BaseActivity.TEMP_CROP_CASH_DIR);
            createFolderStructure(file);
            try {
                removerView.getFinishedBitmap().compress(Bitmap.CompressFormat.PNG, 85, new FileOutputStream(file));
                Intent intent = new Intent(getContext(), EditImageActivity.class);
                intent.putExtra(Constants.EDIT_IMAGE_URI, Uri.fromFile(file));
                startActivity(intent);
                getActivity().finish();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.fragment_remove_background_repair_toggle_mode) {
            if (removerView.removeToggle()) {
                switchTo(REMOVE);
            } else {
                switchTo(REPAIR);
            }
        } else if (itemId == R.id.fragment_remove_background_mode_zoom_toggle) {
            if (removerView.changeZoomMode()) {
                //zoom mode enabled
                switchTo(ZOOM_ON);
            } else {
                switchTo(ZOOM_OFF);
            }
        } else if (itemId == R.id.fragment_remove_background_radius_button) {
            manageSeekBarsVisibility(radiusContainer);
            radiusSeekBar.setProgress(removerView.getRadius());
        } else if (itemId == R.id.fragment_remove_background_mode_offset) {
            manageSeekBarsVisibility(offsetContainer);
            offsetSeekBar.setProgress(removerView.getOffset());
        } else if (itemId == R.id.fragment_remove_background_flood_filler) {
            if (!applyFloodFillMode) {
                switchTo(SHOW_FLOOD_POINTER);
            } else {
                switchTo(APPLY_FLOOD);

            }
        }
    }

    private void switchTo(int mode) {
        manageSeekBarsVisibility(null);
//        removerView.setUsingFloodFillPointer(false);
//        floodFillerButton.setBackgroundColor(Color.parseColor("#1565c0"));
//        floodFillerButton.setImageResource(R.drawable.ic_flood_fill);
        switch (mode) {
            case REMOVE:
                removerView.setUsingFloodFillPointer(false);
                modeButton.setImageResource(R.drawable.ic_remove_blue);
                modeButton.setBackgroundColor(Color.parseColor("#1565c0"));
                dismissFloodPointer();
                break;
            case REPAIR:
                removerView.setUsingFloodFillPointer(false);
                modeButton.setImageResource(R.drawable.ic_repair);
                modeButton.setBackgroundColor(Color.WHITE);
                dismissFloodPointer();
                break;
            case ZOOM_ON:
                zoomToggleButton.setImageResource(R.drawable.ic_hand_blue);
                zoomToggleButton.setBackgroundColor(Color.WHITE);
                dismissFloodPointer();
                break;
            case ZOOM_OFF:
                zoomToggleButton.setImageResource(R.drawable.ic_hand_white);
                zoomToggleButton.setBackgroundColor(Color.parseColor("#1565c0"));
                dismissFloodPointer();
                break;
            case SHOW_FLOOD_POINTER:
                manageSeekBarsVisibility(toleranceContainer);
                toleranceSeekBar.setProgress(removerView.getTolerance());
                removerView.setUsingFloodFillPointer(true);
                floodFillerButton.setBackgroundColor(Color.WHITE);
                floodFillerButton.setImageResource(R.drawable.ic_done_blue);
                setApplyFloodFillMode(true);
                break;
            case APPLY_FLOOD:
                removerView.floodFill();
                removerView.setUsingFloodFillPointer(false);
                floodFillerButton.setBackgroundColor(Color.parseColor("#1565c0"));
                floodFillerButton.setImageResource(R.drawable.ic_flood_fill);
                break;

        }

    }

    private void dismissFloodPointer() {
        removerView.setUsingFloodFillPointer(false);
        floodFillerButton.setBackgroundColor(Color.parseColor("#1565c0"));
        floodFillerButton.setImageResource(R.drawable.ic_flood_fill);
        setApplyFloodFillMode(false);
    }

    private void manageSeekBarsVisibility(View view) {
        boolean isVisible = false;
        if (view != null)
            isVisible = view.getVisibility() == View.VISIBLE;
        radiusContainer.setVisibility(View.GONE);
        offsetContainer.setVisibility(View.GONE);
        toleranceContainer.setVisibility(View.GONE);

        if (!isVisible && view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private boolean createFolderStructure(File file) {
        try {
            file.mkdirs();
            if (file.exists())
                file.delete();
            file.createNewFile();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void updateBottomPointer(float top, float left, float scale) {
        pointerViewBottom.updatePointer(top, left);
    }

    @Override
    public void floodFillerFinished() {
        loadingDialogDismiss(true);
        setApplyFloodFillMode(false);
        manageSeekBarsVisibility(null);
//        applyFloodContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void floodFillerStarted() {
        loadingDialogDismiss(false);
    }

    @Override
    public void dismissSeekBarContainers() {
        manageSeekBarsVisibility(null);
    }

    private void loadingDialogDismiss(boolean dismiss) {
        if (dismiss)
            loadingDialog.setVisibility(View.GONE);
        else loadingDialog.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//        int seekBarId = seekBar.getId();
        if (seekBar == radiusSeekBar) {
            removerView.setRadius(seekBar.getProgress());
        } else if (seekBar == offsetSeekBar) {
            removerView.setOffset(seekBar.getProgress());
        } else if (seekBar == toleranceSeekBar) {
            removerView.setTolerance(seekBar.getProgress());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void setApplyFloodFillMode(boolean applyFloodFillMode) {
        this.applyFloodFillMode = applyFloodFillMode;
    }

}
