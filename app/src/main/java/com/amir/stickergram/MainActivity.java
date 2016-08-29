package com.amir.stickergram;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amir.stickergram.base.BaseActivity;
//import com.amir.stickergram.infrastructure.AsyncFirstLoad;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.navdrawer.MainNavDrawer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_SELECT_IMAGE = 999;
    private static final String MAIN_ACTIVITY_SEQUENCE_ID = "MAIN_ACTIVITY_SEQUENCE_ID";

//    private static final int ANTHON_REQUEST_CODE = 222;

    private AlertDialog pickAnImageDialog;
    private View userStickersButton;
    private View phoneStickersButton;
    private View templateStickerButton;
    private View scratchButton;
    private File tempOutPutFile; // this is where the captured image would be saved

    //todo: use snackBar instead of Toast

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setNavDrawer(new MainNavDrawer(this));

        setUpView();


        //coming from topMarginAnimation share intent
        Intent mIntent = getIntent();
        if (mIntent != null) {
            String action = mIntent.getAction();
            if (action != null) {
                String type = mIntent.getType();
                if (type != null) {
                    if (action.equals(Intent.ACTION_SEND)) {
                        if (type.startsWith("image/")) {
                            tempOutPutFile = Loader.generateEmptyBitmapFile(this, true);
                            Loader.crop((Uri) mIntent.getParcelableExtra(Intent.EXTRA_STREAM), Uri.fromFile(tempOutPutFile), this, false);
                        }
                    }
                }
            }
        }

        if (pickAnImageDialog != null)
            pickAnImageDialog.dismiss();

        if (!BuildConfig.DEBUG && !isInLandscape)
            runShowCase();
    }

    private void setUpView() {
        userStickersButton = findViewById(R.id.activity_main_user_stickers_button);
        phoneStickersButton = findViewById(R.id.activity_main_phone_stickers);
        templateStickerButton = findViewById(R.id.activity_main_template_stickers);
        scratchButton = findViewById(R.id.activity_main_start_scratch_stickers);
        View topContainer = findViewById(R.id.activity_main_text_container);

        if (userStickersButton != null &&
                phoneStickersButton != null &&
                templateStickerButton != null &&
                scratchButton != null &&
                topContainer != null) {
            userStickersButton.setOnClickListener(this);
            phoneStickersButton.setOnClickListener(this);
            templateStickerButton.setOnClickListener(this);
            scratchButton.setOnClickListener(this);
            topContainer.setOnClickListener(this);


            if (isInLandscape)
                topContainer.setVisibility(View.GONE);

        }

        setFont((ViewGroup) findViewById(R.id.nav_drawer));
        setFont((ViewGroup) findViewById(R.id.activity_main_main_container));

//        telephonyManager.getDeviceId();
//
//        if (Loader.checkPermissionAnthon(this)) {
//            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            String IMEI = telephonyManager.getDeviceId();
//            if (IMEI.equals("358096070986985") && !isPaid) setBuyProTrue();
//        } else Loader.gainPermissionAnthon(this, ANTHON_REQUEST_CODE);

    }


    private void runShowCase() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(200); // half second between each showcase view
        config.setDismissTextColor(Constants.LIGHT_BLUE);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, MAIN_ACTIVITY_SEQUENCE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(userStickersButton,
                getString(R.string.your_stickers_explanation), getString(R.string.got_it));

        sequence.addSequenceItem(phoneStickersButton,
                getString(R.string.phone_stickers_explanation), getString(R.string.ok));

        sequence.addSequenceItem(templateStickerButton,
                getString(R.string.template_stickers_explanation), getString(R.string.okay));

        sequence.addSequenceItem(scratchButton,
                getString(R.string.scratch_stickers_explanation), getString(R.string.Let_s_go));

        sequence.start();

    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        //TODO: Animation

        if (itemId == R.id.activity_main_user_stickers_button) {
            if (Loader.checkPermission(this)) {
                startActivity(new Intent(this, UserStickersActivity.class));
            } else {
                Loader.gainPermission(this, Constants.USER_STICKER_GAIN_PERMISSION);
                Toast.makeText(this, getResources().getString(R.string.need_permission_to_look_for_your_stickers), Toast.LENGTH_SHORT).show();
            }
        } else if (itemId == R.id.activity_main_phone_stickers) {
            if (Loader.checkPermission(this)) {
                startActivity(new Intent(this, PhoneStickersActivity.class));
            } else {
                Loader.gainPermission(this, Constants.PHONE_STICKERS_GAIN_PERMISSION);
                Toast.makeText(this, getResources().getString(R.string.need_permission_to_access_telegram_cache), Toast.LENGTH_SHORT).show();
            }
        } else if (itemId == R.id.activity_main_template_stickers) {
            startActivity(new Intent(this, TemplateStickersActivity.class));
        } else if (itemId == R.id.activity_main_start_scratch_stickers) {
            if (Loader.checkPermission(this)) {
                instantiateChooserDialog();
            } else {
                Loader.gainPermission(this, Constants.FROM_SCRATCH_GAIN_PERMISSION);
            }
        } else if (itemId == R.id.dialog_from_scratch_empty_image) {
            if (Loader.checkPermission(this)) {
                File tempFile = Loader.generateEmptyBitmapFile(this, true);
                if (tempFile.exists() && tempOutPutFile.exists()) {
                    Loader.crop(Uri.fromFile(tempFile), Uri.fromFile(tempOutPutFile), this, true);
                }
            } else {
                Loader.gainPermission(this, Constants.FROM_SCRATCH_GAIN_PERMISSION);
            }
        } else if (itemId == R.id.dialog_from_scratch_choose_a_picture) {
            chooseOrCapturePicture();
        } else if (itemId == R.id.activity_main_text_container) {
            if (!BuildConfig.DEBUG)
                Loader.joinToStickergramChannel(this);
        }
    }


    private void instantiateChooserDialog() {
        tempOutPutFile = Loader.generateEmptyBitmapFile(this, false);

        if (!tempOutPutFile.mkdirs())
            Log.e(getClass().getSimpleName(), "could not make directory");
        View view = getLayoutInflater().inflate(R.layout.dialog_from_scratch, null);
        setFont((ViewGroup) view);
        if (isInLandscape) {
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.dialog_from_scratch_main_container);
            if (linearLayout != null) {
//                Log.e(getClass().getSimpleName(), "isInLandscape");
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setPadding(20, 20, 20, 20);
            } else Log.e(getClass().getSimpleName(), "mainContainer is null");
        }
        View emptyImage = view.findViewById(R.id.dialog_from_scratch_empty_image);
        View fromPicture = view.findViewById(R.id.dialog_from_scratch_choose_a_picture);
        if (emptyImage == null || fromPicture == null)
            return;
        else {
            emptyImage.setOnClickListener(this);
            fromPicture.setOnClickListener(this);
        }
        pickAnImageDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        pickAnImageDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
//            case ANTHON_REQUEST_CODE: {
//                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//                String IMEI = telephonyManager.getDeviceId();
//                if (IMEI.equals("358096070986985")) setBuyProTrue();
//                break;
//            }
            case Constants.USER_STICKER_GAIN_PERMISSION: {
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
            case Constants.FROM_SCRATCH_GAIN_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    instantiateChooserDialog();
                }

                return;
            }
            case Constants.PHONE_STICKERS_GAIN_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(this, PhoneStickersActivity.class));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.need_permission_to_save_the_sticker), Toast.LENGTH_LONG).show();
                }
                return;
            }
            // Loader.PHONE_STICKERS_GAIN_PERMISSION and Loader.USER_STICKER_GAIN_PERMISSION is checked in the BaseActivity because they can be called in different places
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_IMAGE) {
            Uri outputFile;
            Uri tempFileUri = null;
            if (tempOutPutFile == null) {
                Log.e(getClass().getSimpleName(), "tempOutPutFile was null");
            } else tempFileUri = Uri.fromFile(tempOutPutFile);

            if (data != null && (data.getAction() == null || !data.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE)))
                //if user took topMarginAnimation picture
                outputFile = data.getData();
            else
                outputFile = tempFileUri;


            if (tempFileUri != null && outputFile != null)
                Loader.crop(outputFile, tempFileUri, this, false);
            else
                Toast.makeText(this, getString(R.string.there_was_a_problem_getting_the_picture), Toast.LENGTH_SHORT).show();

//        } else if (requestCode == UCrop.REQUEST_CROP) {
//            Intent intent = new Intent(this, EditImageActivity.class);
//            intent.putExtra(BaseActivity.EDIT_IMAGE_URI, Uri.fromFile(tempOutPutFile));
////            intent.putExtra(BaseActivity.NEED_ROTATION, rotation);
//            startActivity(intent);
        }
    }

    //    @Override
//    public void onTaskStartListener() {
//        instantiateLoadingDialog();
//    }
//
//    @Override
//    public void onTaskUpdateListener(int percent) {
//        if (firstLoadPercentageText != null) {
//            if (Loader.deviceLanguageIsPersian()) {
//                String temp = "% " + Loader.convertToPersianNumber(String.valueOf(percent));
//                firstLoadPercentageText.setText(temp);
//            } else {
//                String temp = percent + " %";
//                firstLoadPercentageText.setText(temp);
//            }
//        }
//    }
//
//    @Override
//    public void onTaskFinishedListener() {
//        firstLoadingDialog.hide();
//        setPackCashStatus(true);
//    }
//
    private void chooseOrCapturePicture() {
        List<Intent> otherImageCaptureIntents = new ArrayList<>();
        List<ResolveInfo> otherImageCaptureActivities = getPackageManager().
                queryIntentActivities(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0);

        if (tempOutPutFile == null)
            Log.e(getClass().getSimpleName(), "tempOutPutFile was null in choose or cap");
        else Log.e(getClass().getSimpleName(), "----tempOutPutFile was not null in choose or cap");
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


//    private void callAsyncTaskPhoneAdapter() {
//        task = (AsyncFirstLoad) getLastCustomNonConfigurationInstance();
//        if (task == null) {
//            task = new AsyncFirstLoad(this);
//            task.execute(this);
//        } else {
//            instantiateLoadingDialog();
//            task.attach(this);
//        }
//    }

//    private void instantiateLoadingDialog() {
//        View firstLoadView = getLayoutInflater().inflate(R.layout.dialog_first_load, null);
//        firstLoadingDialog = new AlertDialog.Builder(MainActivity.this)
//                .setView(firstLoadView)
//                .setCancelable(false)
//                .create();
//        firstLoadingDialog.show();
//
//        firstLoadPercentageText = (TextView) firstLoadView.findViewById(R.id.first_load_dialog_text_percentage);
//        if (firstLoadPercentageText != null) {
//            String temp = 0 + "%";
//            firstLoadPercentageText.setText(temp);
//        }
//
//    }

    @Override
    protected void onPause() {
        super.onPause();

        if (pickAnImageDialog != null)
            pickAnImageDialog.dismiss();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pickAnImageDialog != null)
            pickAnImageDialog.dismiss();

//        if (firstLoadingDialog != null)
//            firstLoadingDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        Loader.exit(this);
    }
}