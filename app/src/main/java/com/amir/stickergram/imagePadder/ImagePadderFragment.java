package com.amir.stickergram.imagePadder;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.amir.stickergram.EditImageActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import app.minimize.com.seek_bar_compat.SeekBarCompat;

public class ImagePadderFragment extends BaseFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, ImagePadder.ImagePadderCallBacks {

    private static final String EXTRA_IMAGE = "EXTRA_IMAGE";
    private ImageView mainImageView;
    private Bitmap mainImage;
    SeekBarCompat seekBar;
    int color = Color.WHITE;
    int width = 5;
    private View loadingView;
    private Bitmap lastRes;
    private boolean imageHasChanged = false;
    private boolean launchedToAddImage = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_padder, container, false);
        setFont((ViewGroup) view);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        if (bundle == null) {
            Toast.makeText(getContext(), getString(R.string.somthing_went_wrong), Toast.LENGTH_LONG).show();
            getActivity().finish();
            return view;
        }

        view.findViewById(R.id.fragment_image_padder_width_button).setOnClickListener(this);
        view.findViewById(R.id.fragment_image_padder_color_button).setOnClickListener(this);
        view.findViewById(R.id.fragment_image_padder_apply).setOnClickListener(this);

        loadingView = view.findViewById(R.id.fragment_image_padder_loading);

        seekBar = (SeekBarCompat) view.findViewById(R.id.fragment_image_padder_width_seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(15);

        launchedToAddImage = bundle.getBoolean(Constants.LAUNCHED_TO_ADD_IMAGE, false);
        mainImage = bundle.getParcelable(EXTRA_IMAGE);
        if (mainImage == null) {
            Toast.makeText(getContext(), getString(R.string.somthing_went_wrong), Toast.LENGTH_LONG).show();
            getActivity().finish();
            return view;
        }
        mainImageView = ((ImageView) view.findViewById(R.id.fragment_image_padder_main_image));
        mainImageView.setImageBitmap(mainImage);

        width = 5;

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.image_padder, menu);
        if (imageHasChanged) menu.getItem(0).setVisible(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.image_padder_save) {
            saveImage();
//            return true;
        } else if (itemId == R.id.image_padder_discard) {
            lastRes = null;
            saveImage();
        }
        return true;
    }

    private void saveImage() {
        File file = new File(BaseActivity.Companion.getTEMP_CROP_CASH_DIR());
        Loader.createFolderStructure(file);
        try {
            if (lastRes == null)
                mainImage.compress(Bitmap.CompressFormat.PNG, 85, new FileOutputStream(file));
            else lastRes.compress(Bitmap.CompressFormat.PNG, 85, new FileOutputStream(file));

            if (launchedToAddImage) {
                Intent intent = new Intent();
                intent.putExtra(EditImageActivity.ADDED_IMAGE_ADDRESS, file.getAbsolutePath());
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            } else {
                Intent intent = new Intent(getContext(), EditImageActivity.class);
                intent.putExtra(Constants.EDIT_IMAGE_URI, Uri.fromFile(file));
                startActivity(intent);
                getActivity().finish();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Fragment getInstance(Bitmap finishedBitmap, boolean launchedToAddImage) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_IMAGE, finishedBitmap);
        bundle.putBoolean(Constants.LAUNCHED_TO_ADD_IMAGE, launchedToAddImage);
        ImagePadderFragment imagePadderFragment = new ImagePadderFragment();
        imagePadderFragment.setArguments(bundle);
        return imagePadderFragment;
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.fragment_image_padder_color_button) {

            seekBar.setVisibility(View.GONE);

            ColorPickerDialogBuilder
                    .with(getContext())
                    .setTitle(getString(R.string.choose_color))
                    .initialColor(Color.WHITE)
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(int selectedColor) {
                            Log.e(getClass().getSimpleName(), "++++++++selectedColor: " + selectedColor + " oldColor: " + color);
                        }
                    })
                    .setPositiveButton(getString(R.string.ok), new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                            Log.e(getClass().getSimpleName(), " selectedColor: " + selectedColor + " oldColor: " + color);
                            color = selectedColor;
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e(getClass().getSimpleName(), "-------oldColor: " + color);
                        }
                    })
                    .build()
                    .show();

        } else if (itemId == R.id.fragment_image_padder_width_button) {
            seekBar.setProgress(width);
            seekBar.setVisibility(View.VISIBLE);
        } else if (itemId == R.id.fragment_image_padder_apply) {
            loadingView.setVisibility(View.VISIBLE);
            for (int i = 0; i <= mainImage.getWidth() / 100; i++) {
                new ImagePadder(color, width, mainImage, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i * 100);
            }
//            Log.e(getClass().getSimpleName(), " width: " + width);
//            new ImagePadder(color, width, mainImage, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar.getId() == R.id.fragment_image_padder_width_seek_bar) {
            width = i;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void paddingFinished(Bitmap finishedBitmap) {
        loadingView.setVisibility(View.GONE);
        lastRes = finishedBitmap;
        mainImageView.setImageBitmap(finishedBitmap);
        imageHasChanged = true;
        getActivity().invalidateOptionsMenu();
    }

}
