package com.amir.stickergram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Constants;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CropActivity extends BaseActivity {
    private static final String IMAGE_URT = "IMAGE_URI";
    private CropImageView mCropView;
    private View progressContainer;
    private Uri sourceUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        if (getIntent() != null) {
            sourceUri = getIntent().getParcelableExtra(IMAGE_URT);
        }
        setUpView(savedInstanceState != null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crop_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.crop_activity_menu_save) {
            final Uri destiny = getIntent().getParcelableExtra(Constants.CROP_DESTINY);
            showLoadingDialog(true);
            mCropView.startCrop(destiny, cropCallback, saveCallback);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLoadingDialog(boolean flag) {
        if (progressContainer != null)
            if (flag) {
                progressContainer.setVisibility(View.VISIBLE);
            } else progressContainer.setVisibility(View.GONE);


    }

    private void setUpView(boolean isComingFromASavedState) {
        Log.e(getClass().getSimpleName(), "setupView");
        progressContainer = findViewById(R.id.activity_crop_progress_bar_container);

        findViewById(R.id.button1_1).setOnClickListener(btnListener);
        findViewById(R.id.button3_4).setOnClickListener(btnListener);
        findViewById(R.id.button4_3).setOnClickListener(btnListener);
        findViewById(R.id.button9_16).setOnClickListener(btnListener);
        findViewById(R.id.button16_9).setOnClickListener(btnListener);
        findViewById(R.id.buttonFree).setOnClickListener(btnListener);
        findViewById(R.id.buttonRotateLeft).setOnClickListener(btnListener);
        findViewById(R.id.buttonRotateRight).setOnClickListener(btnListener);
        findViewById(R.id.buttonCustom).setOnClickListener(btnListener);
        findViewById(R.id.buttonCircle).setOnClickListener(btnListener);

        mCropView = (CropImageView) findViewById(R.id.activity_crop_crop_image_view);
        mCropView.setCropMode(CropImageView.CropMode.FREE);
        if (sourceUri == null)//from screen orientation change
            sourceUri = getIntent().getParcelableExtra(Constants.CROP_SOURCE);

        if (isComingFromASavedState)
            showLoadingDialog(false);

        if (sourceUri != null && !isComingFromASavedState) {
            Log.e(getClass().getSimpleName(), "startLoad");
            mCropView.startLoad(
                    sourceUri,
                    new LoadCallback() {
                        @Override
                        public void onSuccess() {
                            showLoadingDialog(false);
                            Log.e("amir", "loadCallBack onSuccess");
                        }

                        @Override
                        public void onError() {
                            showLoadingDialog(false);
                            Log.e("amir", "loadCallBack onError");
                        }
                    });
        } else {
            Log.e(getClass().getSimpleName(), "sourceUri was null");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(IMAGE_URT, sourceUri);
        mCropView.destroyDrawingCache();

    }

    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button1_1:
                    mCropView.setCropMode(CropImageView.CropMode.SQUARE);
                    break;
                case R.id.button3_4:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                    break;
                case R.id.button4_3:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
                    break;
                case R.id.button9_16:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);
                    break;
                case R.id.button16_9:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                    break;
                case R.id.buttonCustom:
                    mCropView.setCustomRatio(7, 5);
                    break;
                case R.id.buttonFree:
                    mCropView.setCropMode(CropImageView.CropMode.FREE);
                    break;
                case R.id.buttonCircle:
                    if (isPaid)
                        mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
                    else
                        Toast.makeText(CropActivity.this, getString(R.string.circular_crop_is_only), Toast.LENGTH_LONG).show();
                    break;
                case R.id.buttonRotateLeft:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                    break;
                case R.id.buttonRotateRight:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                    break;
            }
        }
    };
    private SaveCallback saveCallback = new SaveCallback() {
        @Override
        public void onSuccess(Uri outputUri) {
            Log.e("amir", "saveCallback onSuccess");
        }

        @Override
        public void onError() {
            Log.e("amir", "saveCallBack error");
            showLoadingDialog(false);
            Toast.makeText(CropActivity.this, getString(R.string.there_was_a_problem_getting_the_picture), Toast.LENGTH_SHORT).show();
            finish();
        }
    };


    private CropCallback cropCallback = new CropCallback() {
        @Override
        public void onSuccess(Bitmap cropped) {
            showLoadingDialog(false);
            int mainWidth = cropped.getWidth();
            int mainHeight = cropped.getHeight();
            Bitmap resBitmap;
            if (mainWidth != 512 && mainHeight != 512) {
                float scale;
                if (mainHeight > mainWidth) {
                    scale = 512f / mainHeight;
                    resBitmap = Bitmap.createScaledBitmap(cropped, (int) (mainWidth * scale), 512, false);
                } else {
                    scale = 512f / mainWidth;

                    resBitmap = Bitmap.createScaledBitmap(cropped, 512, (int) (mainHeight * scale), false);
                }
            } else {
                resBitmap = cropped;
            }

            File file = new File(BaseActivity.TEMP_CROP_CASH_DIR);
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
                if (resBitmap != null) {
                    resBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
                } else Log.e(getClass().getSimpleName(), "resBitmap was null");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(CropActivity.this, EditImageActivity.class);
            intent.putExtra(Constants.EDIT_IMAGE_URI, Uri.fromFile(file));
            startActivity(intent);
            finish();

            Log.e("amir", "cropCallBack onSuccess");
        }

        @Override
        public void onError() {
            Log.e("amir", "cropCallBack onError");
        }
    };

}
