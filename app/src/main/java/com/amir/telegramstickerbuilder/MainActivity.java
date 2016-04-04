package com.amir.telegramstickerbuilder;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.AsyncFirstLoad;
import com.amir.telegramstickerbuilder.infrastructure.Loader;
import com.amir.telegramstickerbuilder.navdrawer.MainNavDrawer;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, AsyncFirstLoad.AsyncFirstTaskListener {
    private static final int REQUEST_SELECT_IMAGE = 100;
    private static final String TEMP_OUTPUT_DIRECTORY = Environment.getExternalStorageDirectory() + "/TSB/.cash/";
    View userStickersButton;
    View phoneStickersButton;
    View templateStickerButton;
    View scratchButton;
    View topImage;
    AlertDialog dialog;
    TextView firstLoadTextPercentage;
    AsyncFirstLoad task;
    File tempOutPutFile; // this is where the captured image would be saved

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavDrawer(new MainNavDrawer(this));

        userStickersButton = findViewById(R.id.activity_main_user_stickers_button);
        phoneStickersButton = findViewById(R.id.activity_main_phone_stickers);
        templateStickerButton = findViewById(R.id.activity_main_template_stickers);
        scratchButton = findViewById(R.id.activity_main_start_scratch_stickers);
        topImage = findViewById(R.id.activity_main_image);

        if (!hasCashedPackStickers()) {
            Log.e(getClass().getSimpleName(), "Loading");
            callAsyncTaskPhoneAdapter();
        }

        if (userStickersButton != null && phoneStickersButton != null && templateStickerButton != null && scratchButton != null) {
            userStickersButton.setOnClickListener(this);
            phoneStickersButton.setOnClickListener(this);
            templateStickerButton.setOnClickListener(this);
            scratchButton.setOnClickListener(this);
        }

        if (isInLandscape)
            topImage.setVisibility(View.GONE);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (task != null)
            task.detach();
        return (task);
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        //TODO: Animation

        if (itemId == R.id.activity_main_user_stickers_button) {
            //todo: Gain permission
            startActivity(new Intent(this, UserStickersActivity.class));
        } else if (itemId == R.id.activity_main_phone_stickers) {
            Loader.gainPermission(this);
            if (Loader.checkPermission(this)) {
                startActivity(new Intent(this, PhoneStickersActivity.class));
            } else {
                Toast.makeText(this, getResources().getString(R.string.need_permission), Toast.LENGTH_LONG).show();
            }
        } else if (itemId == R.id.activity_main_template_stickers) {
            Loader.gainPermission(this);
            if (Loader.checkPermission(this)) {
                    startActivity(new Intent(this, TemplateStickersActivity.class));
            } else {
                Toast.makeText(this, getResources().getString(R.string.need_permission), Toast.LENGTH_LONG).show();
            }
        } else if (itemId == R.id.activity_main_start_scratch_stickers) {
            instantiateChooser();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            tempOutPutFile.delete();
            return;
        }
        if (requestCode == REQUEST_SELECT_IMAGE) {
            Uri outputFile;
            Uri tempFileUri = Uri.fromFile(tempOutPutFile);

            Log.e(getClass().getSimpleName(), "it's a picture");
            if (data != null && (data.getAction() == null || !data.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE))) {
                outputFile = data.getData();
            } else {
                outputFile = tempFileUri;
            }

            Crop.of(outputFile, tempFileUri).start(this);
        } else if (requestCode == Crop.REQUEST_CROP) {
            Intent intent = new Intent(this, EditImageActivity.class);
            intent.putExtra(BaseActivity.EDIT_IMAGE_URI, Uri.fromFile(tempOutPutFile));
            startActivity(intent);
        }
    }

    @Override
    public void onTaskStartListener() {
        instantiateLoadingDialog();
    }

    @Override
    public void onTaskUpdateListener(int percent) {
        if (firstLoadTextPercentage != null)
            firstLoadTextPercentage.setText(percent + "%");
    }

    @Override
    public void onTaskFinishedListener() {
        dialog.hide();
        setPackCashStatus(true);
    }

    private void instantiateChooser() {
        tempOutPutFile = new File(TEMP_OUTPUT_DIRECTORY, "temp_file.jpg");
        if (!tempOutPutFile.mkdirs())
            Log.e(getClass().getSimpleName(), "could not make directory");
        try {
            if (tempOutPutFile.exists())
                tempOutPutFile.delete();
            if (!tempOutPutFile.createNewFile()) {
                Toast.makeText(this, getResources().getString(R.string.could_not_make_file), Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Intent> otherImageCaptureIntents = new ArrayList<>();
        List<ResolveInfo> otherImageCaptureActivities = getPackageManager().
                queryIntentActivities(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0);

        for (ResolveInfo info : otherImageCaptureActivities) {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempOutPutFile));
            otherImageCaptureIntents.add(captureIntent);
        }

        Intent selectImageIntent = new Intent(Intent.ACTION_PICK);
        selectImageIntent.setType("image/*");

        Intent chooser = Intent.createChooser(selectImageIntent, getResources().getString(R.string.choose_an_image));
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, otherImageCaptureIntents.toArray(new Parcelable[otherImageCaptureActivities.size()]));

        startActivityForResult(chooser, REQUEST_SELECT_IMAGE);
    }


    private void callAsyncTaskPhoneAdapter() {
        task = (AsyncFirstLoad) getLastCustomNonConfigurationInstance();
        if (task == null) {
            task = new AsyncFirstLoad(this);
            task.execute(this);
        } else {
            instantiateLoadingDialog();
            task.attach(this);
        }
    }

    private void instantiateLoadingDialog() {
        View firstLoadView = getLayoutInflater().inflate(R.layout.dialog_first_load, null);
        dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(firstLoadView)
                .setCancelable(false)
                .create();
        dialog.show();

        firstLoadTextPercentage = (TextView) firstLoadView.findViewById(R.id.first_load_dialog_text_percentage);
        if (firstLoadTextPercentage != null) {
            firstLoadTextPercentage.setText(0 + "%");
        } else Log.e(getClass().getSimpleName(), "was null in preExecute");

    }
}