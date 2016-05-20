package com.amir.stickergram;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.AsyncFirstLoad;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.navdrawer.MainNavDrawer;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, AsyncFirstLoad.AsyncFirstTaskListener {
    private static final int REQUEST_SELECT_IMAGE = 100;

    View userStickersButton;
    View phoneStickersButton;
    View templateStickerButton;
    View scratchButton;
    View topImage;
    AlertDialog firstLoadingDialog;
    TextView firstLoadTextPercentage;
    AsyncFirstLoad task;
    File tempOutPutFile; // this is where the captured image would be saved
    int rotation = 0; // if the image is captured it would probably need to be rotated

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavDrawer(new MainNavDrawer(this));

        //todo: let the user to choose an empty bitmap choosing the aspect ratio

        userStickersButton = findViewById(R.id.activity_main_user_stickers_button);
        phoneStickersButton = findViewById(R.id.activity_main_phone_stickers);
        templateStickerButton = findViewById(R.id.activity_main_template_stickers);
        scratchButton = findViewById(R.id.activity_main_start_scratch_stickers);
        topImage = findViewById(R.id.activity_main_text);

        if (!hasCashedPackStickers()) {
            Log.e(getClass().getSimpleName(), "Loading");
            callAsyncTaskPhoneAdapter();
        }

        if (userStickersButton != null &&
                phoneStickersButton != null &&
                templateStickerButton != null &&
                scratchButton != null &&
                topImage != null) {
            userStickersButton.setOnClickListener(this);
            phoneStickersButton.setOnClickListener(this);
            templateStickerButton.setOnClickListener(this);
            scratchButton.setOnClickListener(this);
            topImage.setOnClickListener(this);
        }

        if (isInLandscape && topImage != null)
            topImage.setVisibility(View.GONE);

//        for (int i = 1575; i < 2000; i++){
//            System.out.print((char) i );
//            System.out.println(" " + i);
//        }
//        Log.e(getClass().getSimpleName(), String.valueOf(Loader.isPersian("amir")));
//        Log.e(getClass().getSimpleName(), String.valueOf(Loader.isPersian("یب یسشب یبش یب سش")));
//        Log.e(getClass().getSimpleName(), String.valueOf(Loader.isPersian("354")));
//        Log.e(getClass().getSimpleName(), String.valueOf(Loader.isPersian("")));
//        Log.e(getClass().getSimpleName(), String.valueOf(Loader.isPersian("پ")));
//        System.out.println((int)'گ');
//        System.out.println((int)'چ');
//        System.out.println((int)'ژ');


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
            if (Loader.checkPermission(this)) {
                startActivity(new Intent(this, UserStickersActivity.class));
            } else {
                Loader.gainPermission(this, Loader.USER_STICKER_GAIN_PERMISSION);
                Toast.makeText(this, getResources().getString(R.string.need_permission_to_look_for_your_stickers), Toast.LENGTH_SHORT).show();
            }
        } else if (itemId == R.id.activity_main_phone_stickers) {
            if (Loader.checkPermission(this)) {
                startActivity(new Intent(this, PhoneStickersActivity.class));
            } else {
                Loader.gainPermission(this, Loader.PHONE_STICKERS_GAIN_PERMISSION);
                Toast.makeText(this, getResources().getString(R.string.need_permission_to_access_telegram_cache), Toast.LENGTH_SHORT).show();
            }
        } else if (itemId == R.id.activity_main_template_stickers) {
//            Loader.gainPermission(this, Loader.TEMPLATE_STICKERS_GAIN_PERMISSION);
//            if (Loader.checkPermission(this)) {
            startActivity(new Intent(this, TemplateStickersActivity.class));
//            } else {
//                Toast.makeText(this, getResources().getString(R.string.need_permission), Toast.LENGTH_LONG).show();
//            }
        } else if (itemId == R.id.activity_main_start_scratch_stickers) {
            if (Loader.checkPermission(this)) {
                instantiateChooser();
            } else {
                Loader.gainPermission(this, Loader.FROM_SCRATCH_GAIN_PERMISSION);
            }
        } else if (itemId == R.id.activity_main_text) {

//            Bot bot = new Bot();
//            bot.sendMessage();
//            OnBuyProRequested();
//            final String appName = "org.telegram.messenger";
//            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/stickers"));
////            myIntent.setType("text/plain");
//            myIntent.setPackage(appName);
////            myIntent.putExtra(Intent.EXTRA_TEXT, "Hi");
////            startActivity(Intent.createChooser(myIntent, "Share with"));
//            startActivity(myIntent);
////
////            Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/stickers"));
////            telegram.setPackage(appName);
////            startActivity(telegram);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Loader.USER_STICKER_GAIN_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(this, UserStickersActivity.class));
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, getResources().getString(R.string.need_permission_to_look_for_your_stickers), Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                return;
            }
            case Loader.FROM_SCRATCH_GAIN_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    instantiateChooser();
                }

                return;
            }
            case Loader.PHONE_STICKERS_GAIN_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(this, PhoneStickersActivity.class));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.need_permission_to_access_telegram_cache), Toast.LENGTH_LONG).show();
                }

                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            if (tempOutPutFile != null) tempOutPutFile.delete();
            return;
        }
        if (requestCode == REQUEST_SELECT_IMAGE) {
            Uri outputFile;
            Uri tempFileUri = Uri.fromFile(tempOutPutFile);

            if (data != null && (data.getAction() == null || !data.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE)))
                outputFile = data.getData();
            else
                outputFile = tempFileUri;

            rotation = (int) Loader.capturedRotationFix(Loader.getRealPathFromURI(outputFile, getContentResolver())); // this is being passed to the next activity and been used in rotation
            Crop.of(outputFile, tempFileUri).start(this);
        } else if (requestCode == Crop.REQUEST_CROP) {
//            Loader.rotateImage(tempOutPutFile.getAbsolutePath(), rotation);
            Intent intent = new Intent(this, EditImageActivity.class);
            intent.putExtra(BaseActivity.EDIT_IMAGE_URI, Uri.fromFile(tempOutPutFile));
            intent.putExtra(BaseActivity.NEED_ROTATION, rotation);
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
        firstLoadingDialog.hide();
        setPackCashStatus(true);
    }

    private void instantiateChooser() {
        tempOutPutFile = new File(BaseActivity.TEMP_OUTPUT_DIRECTORY, "temp_file.jpg");
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
        firstLoadingDialog = new AlertDialog.Builder(MainActivity.this)
                .setView(firstLoadView)
                .setCancelable(false)
                .create();
        firstLoadingDialog.show();

        firstLoadTextPercentage = (TextView) firstLoadView.findViewById(R.id.first_load_dialog_text_percentage);
        if (firstLoadTextPercentage != null) {
            firstLoadTextPercentage.setText(0 + "%");
        } else Log.e(getClass().getSimpleName(), "was null in preExecute");

    }

}