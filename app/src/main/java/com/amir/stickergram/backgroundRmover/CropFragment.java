package com.amir.stickergram.backgroundRmover;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amir.stickergram.EditImageActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.infrastructure.Constants;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CropFragment extends BaseFragment {
    private static final String TAG = EditImageActivity.class.getSimpleName();
    private CropImageView mCropView;
    private View progressContainer;
    private Uri sourceUri;
    private CropFragmentCallbacks listener;
//    private int rotation;

    public static CropFragment newInstance(Bundle args) {
        CropFragment f = new CropFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (CropFragmentCallbacks) context;
        } catch (ClassCastException e) {
            throw new RuntimeException("Must implement CropFragmentCallback");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crop, container, false);
        setFont((ViewGroup) view);
        setHasOptionsMenu(true);

        progressContainer = view.findViewById(R.id.activity_crop_progress_bar_container);

        view.findViewById(R.id.button1_1).setOnClickListener(btnListener);
        view.findViewById(R.id.button3_4).setOnClickListener(btnListener);
        view.findViewById(R.id.button4_3).setOnClickListener(btnListener);
        view.findViewById(R.id.button9_16).setOnClickListener(btnListener);
        view.findViewById(R.id.button16_9).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonFree).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonRotateLeft).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonRotateRight).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonCustom).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonCircle).setOnClickListener(btnListener);

        mCropView = (CropImageView) view.findViewById(R.id.activity_crop_crop_image_view);
//        mCropView.setDebug(BuildConfig.DEBUG);
        mCropView.setCropMode(CropImageView.CropMode.FREE);

//        rotation = (int) Loader.capturedRotationFix(Loader.getRealPathFromURI((Uri) getArguments().getParcelable(Constants.CROP_SOURCE)
//                , getActivity().getContentResolver()));
//        Log.e(TAG, "rotation: " + rotation);

        setCropViewUri((Uri) getArguments().getParcelable(Constants.CROP_SOURCE), savedInstanceState != null);

        return view;
    }

    public void setCropViewUri(Uri uri, boolean isComingFromASavedState) {
        if (sourceUri == null)//from screen orientation change
//            sourceUri = getIntent().getParcelableExtra(Constants.CROP_SOURCE);
            sourceUri = uri;

        if (isComingFromASavedState)
            showLoadingDialog(false);

        if (sourceUri != null && !isComingFromASavedState) {
//            Log.e(TAG, "startLoad");
            mCropView.startLoad(
                    sourceUri,
                    new LoadCallback() {
                        @Override
                        public void onSuccess() {
                            showLoadingDialog(false);
//                            Log.e("amir", "loadCallBack onSuccess");
                        }

                        @Override
                        public void onError() {
                            showLoadingDialog(false);
                            Log.e("amir", "loadCallBack onError");
                        }
                    });
        } else {
            Log.e(TAG, "sourceUri was null");
        }
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
            showLoadingDialog(true);
//            File outPutFile = Loader.generateEmptyBitmapFile((BaseActivity) getActivity(),true);
            Uri destiny = getArguments().getParcelable(Constants.CROP_DESTINY);
//            Uri destiny = Uri.fromFile(outPutFile);
            if (destiny != null) {
                Log.e(TAG, "destiny: " + destiny.toString());
            }
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
//                    if (BaseActivity.isPaid)
                    mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
//                    else
//                        Toast.makeText(getContext(), getString(R.string.circular_crop_is_only), Toast.LENGTH_LONG).show();
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
//            Log.e("amir", "saveCallback onSuccess");
        }

        @Override
        public void onError() {
            Log.e("amir", "saveCallBack error");
            showLoadingDialog(false);
            Toast.makeText(getContext(), getString(R.string.there_was_a_problem_getting_the_picture), Toast.LENGTH_SHORT).show();
            getActivity().finish();
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

            File file = new File(BaseActivity.Companion.getTEMP_CROP_CASH_DIR());
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
                if (resBitmap != null) {
//                    if (rotation == 90 || rotation == 90.0)
//                        resBitmap = Loader.rotateImage(resBitmap, rotation);
                    resBitmap.compress(Bitmap.CompressFormat.PNG, 90, new FileOutputStream(file));
                } else Log.e(TAG, "resBitmap was null");
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bundle bundle = new Bundle();
//            bundle.putInt(Constants.IMAGE_ROTATION, rotation);
            bundle.putParcelable(Constants.EDIT_IMAGE_URI, Uri.fromFile(file));
            Log.e(TAG, "-----width: " + resBitmap.getWidth() + " height: " + resBitmap.getHeight());
            listener.cropFinished(bundle);


//            Intent intent = new Intent(getContext(), EditImageActivity.class);
//            intent.putExtra(Constants.EDIT_IMAGE_URI, Uri.fromFile(file));
//            startActivity(intent);
//            getActivity().finish();

//            Log.e("amir", "cropCallBack onSuccess");
        }

        @Override
        public void onError() {
            getActivity().finish();
            Toast.makeText(getContext(), getString(R.string.there_was_a_problem_getting_the_picture), Toast.LENGTH_SHORT).show();
            Log.e("amir", "cropCallBack onError");
        }
    };


    public interface CropFragmentCallbacks {
        void cropFinished(Bundle bundle);
    }

}
