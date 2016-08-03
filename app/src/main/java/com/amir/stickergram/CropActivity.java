package com.amir.stickergram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ViewGroup;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.backgroundRmover.BackgroundRemoverFragment;
import com.amir.stickergram.backgroundRmover.CropFragment;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;

public class CropActivity extends BaseActivity implements CropFragment.CropFragmentCallbacks {
    private boolean hasUsedAnEmptyImage = false;
    private Uri destinyUri;

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
            bundle.putParcelable(Constants.CROP_SOURCE, intent.getParcelableExtra(Constants.CROP_SOURCE));
            destinyUri = intent.getParcelableExtra(Constants.CROP_DESTINY);
            if (destinyUri != null) {
                Log.e(getClass().getSimpleName(), "destiny: " + destinyUri.toString());
            }
            bundle.putParcelable(Constants.CROP_DESTINY, destinyUri);
        }

        getSupportFragmentManager().
                beginTransaction().
                add(R.id.crop_fragment_container, CropFragment.newInstance(bundle)).
                commit();


    }

    @Override
    public void cropFinished(Bundle bundle) {

        int rotation = (int) Loader.capturedRotationFix(Loader.getRealPathFromURI(destinyUri, getContentResolver()));
        Log.e(getClass().getSimpleName(), "rotation: " + rotation);

        if (hasUsedAnEmptyImage) {
            Intent intent = new Intent(this, EditImageActivity.class);
            intent.putExtra(Constants.EDIT_IMAGE_URI, destinyUri);
            startActivity(intent);
            finish();
        } else {
            Fragment f = BackgroundRemoverFragment.getInstance(bundle);
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.crop_fragment_container, f).
                    commit();
        }
    }
}
