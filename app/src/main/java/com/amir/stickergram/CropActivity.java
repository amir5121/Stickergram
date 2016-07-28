package com.amir.stickergram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.imageProcessing.BackgroundRemoverFragment;
import com.amir.stickergram.imageProcessing.CropFragment;
import com.amir.stickergram.infrastructure.Constants;

import static java.security.AccessController.getContext;

public class CropActivity extends BaseActivity implements CropFragment.CropFragmentCallbacks {
    private boolean hasUsedAnEmptyImage = false;
    private Uri destinyUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        if (savedInstanceState != null) return;

        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        if (intent != null) {
            hasUsedAnEmptyImage = intent.getBooleanExtra(Constants.IS_USING_EMPTY_IMAGE, false);
            bundle.putParcelable(Constants.CROP_SOURCE, intent.getParcelableExtra(Constants.CROP_SOURCE));
            destinyUri = intent.getParcelableExtra(Constants.CROP_DESTINY);
            bundle.putParcelable(Constants.CROP_DESTINY, destinyUri);
        }

        getSupportFragmentManager().
                beginTransaction().
                add(R.id.crop_fragment_container, CropFragment.newInstance(bundle)).
                commit();

    }

    @Override
    public void cropFinished(Bundle bundle) {
        Log.e(getClass().getSimpleName(), "cropFinished");

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
