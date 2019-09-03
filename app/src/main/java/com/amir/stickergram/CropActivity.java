package com.amir.stickergram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.ViewGroup;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.backgroundRmover.BackgroundRemoverFragment;
import com.amir.stickergram.backgroundRmover.CropFragment;
import com.amir.stickergram.imagePadder.ImagePadderFragment;
import com.amir.stickergram.infrastructure.Constants;

public class CropActivity extends BaseActivity implements CropFragment.CropFragmentCallbacks, BackgroundRemoverFragment.BackgroundRemoverFragmentCallbacks {
    private boolean hasUsedAnEmptyImage = false;
    private boolean launchedToAddImage = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        setFont((ViewGroup) findViewById(R.id.nav_drawer));
        setFont((ViewGroup) findViewById(R.id.activity_crop_main_container));

        if (savedInstanceState != null) return;

        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        if (intent != null) {
            hasUsedAnEmptyImage = intent.getBooleanExtra(Constants.IS_USING_EMPTY_IMAGE, false);
            launchedToAddImage = intent.getBooleanExtra(Constants.LAUNCHED_TO_ADD_IMAGE, false);
            bundle.putParcelable(Constants.CROP_SOURCE, intent.getParcelableExtra(Constants.CROP_SOURCE));
            Uri destinyUri = intent.getParcelableExtra(Constants.CROP_DESTINY);
            bundle.putParcelable(Constants.CROP_DESTINY, destinyUri);

        }

        getSupportActionBar().setTitle(getString(R.string.crop));
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.crop_fragment_container, CropFragment.newInstance(bundle)).
                commit();


    }

    @Override
    public void cropFinished(Bundle bundle) {

//        int rotation = (int) Loader.capturedRotationFix(Loader.getRealPathFromURI(destinyUri, getContentResolver()));
//        Log.e(getClass().getSimpleName(), "rotation: " + rotation);

        if (hasUsedAnEmptyImage) {
            Intent intent = new Intent(this, EditImageActivity.class);
//            intent.putExtra(Constants.EDIT_IMAGE_URI, destinyUri);
            intent.putExtra(Constants.EDIT_IMAGE_URI, bundle.getParcelable(Constants.EDIT_IMAGE_URI));
            startActivity(intent);
            finish();
        } else {

            getSupportActionBar().setTitle(getString(R.string.background_remover));

            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.crop_fragment_container, BackgroundRemoverFragment.Companion.getInstance(bundle)).
                    commit();
        }
    }


    @Override
    public void backgroundRemoverFinished(Bitmap finishedBitmap) {

        getSupportActionBar().setTitle(getString(R.string.image_stroke));

        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.crop_fragment_container, ImagePadderFragment.getInstance(finishedBitmap, launchedToAddImage)).
                commit();
    }
}
