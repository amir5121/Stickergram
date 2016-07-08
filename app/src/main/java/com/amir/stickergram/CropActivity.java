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
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

public class CropActivity extends BaseActivity {
    CropImageView mCropView;
    View progressContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        setUpView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(getClass().getSimpleName(), "onCreateOptionsMenu was called");
        getMenuInflater().inflate(R.menu.crop_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.crop_activity_menu_save) {
            final Uri destiny = getIntent().getParcelableExtra(BaseActivity.CROP_DESTINY);
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

    private void setUpView() {
        progressContainer = findViewById(R.id.activity_crop_progress_bar_container);
//        findViewById(R.id.buttonDone).setOnClickListener(btnListener);
//        findViewById(R.id.buttonFitImage).setOnClickListener(btnListener);
        findViewById(R.id.button1_1).setOnClickListener(btnListener);
        findViewById(R.id.button3_4).setOnClickListener(btnListener);
        findViewById(R.id.button4_3).setOnClickListener(btnListener);
        findViewById(R.id.button9_16).setOnClickListener(btnListener);
        findViewById(R.id.button16_9).setOnClickListener(btnListener);
        findViewById(R.id.buttonFree).setOnClickListener(btnListener);
//        findViewById(R.id.buttonPickImage).setOnClickListener(btnListener);
        findViewById(R.id.buttonRotateLeft).setOnClickListener(btnListener);
        findViewById(R.id.buttonRotateRight).setOnClickListener(btnListener);
        findViewById(R.id.buttonCustom).setOnClickListener(btnListener);
        findViewById(R.id.buttonCircle).setOnClickListener(btnListener);
//        findViewById(R.id.buttonShowCircleButCropAsSquare).setOnClickListener(btnListener);
//        mRootLayout = (LinearLayout) view.findViewById(R.id.layout_root);
        mCropView = (CropImageView) findViewById(R.id.activity_crop_crop_image_view);
        Uri sourceUri = getIntent().getParcelableExtra(BaseActivity.CROP_SOURCE);
//        Bitmap bitmap = BitmapFactory.decodeFile(new File(sourceUri.getPath()).getAbsolutePath());
//        mCropView.getCircularBitmap(bitmap);

        if (sourceUri != null) {
            mCropView.startLoad(
                    sourceUri,
                    new LoadCallback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });
        } else {
            Log.e(getClass().getSimpleName(), "sourceUri was null");
        }
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

    SaveCallback saveCallback = new SaveCallback() {
        @Override
        public void onSuccess(Uri outputUri) {
            showLoadingDialog(false);
            Intent intent = new Intent(CropActivity.this, EditImageActivity.class);
            intent.putExtra(BaseActivity.EDIT_IMAGE_URI, outputUri);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError() {

        }
    };

    CropCallback cropCallback = new CropCallback() {
        @Override
        public void onSuccess(Bitmap cropped) {
        }

        @Override
        public void onError() {

        }
    };
}
