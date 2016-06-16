package com.amir.stickergram;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.AsyncFirstLoad;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.navdrawer.MainNavDrawer;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends BaseActivity implements View.OnClickListener, AsyncFirstLoad.AsyncFirstTaskListener {
    private static final int REQUEST_SELECT_IMAGE = 100;
    private static final int CROP_REQUEST = 101;
    private static final String MAIN_ACTIVITY_SEQUENCE_ID = "MAIN_ACTIVITY_SEQUENCE_ID";

    AlertDialog pickAnImageDialog;
    View userStickersButton;
    View phoneStickersButton;
    View templateStickerButton;
    View scratchButton;
    View topImage;
    AlertDialog firstLoadingDialog;
    TextView firstLoadPercentageText;
    AsyncFirstLoad task;
    File tempOutPutFile; // this is where the captured image would be saved
    int rotation = 0; // if the image is captured it would probably need to be rotated

    //todo: use snackBar instead of Toast

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavDrawer(new MainNavDrawer(this));

        userStickersButton = findViewById(R.id.activity_main_user_stickers_button);
        phoneStickersButton = findViewById(R.id.activity_main_phone_stickers);
        templateStickerButton = findViewById(R.id.activity_main_template_stickers);
        scratchButton = findViewById(R.id.activity_main_start_scratch_stickers);
        topImage = findViewById(R.id.activity_main_text);

        if (!hasCashedPackStickers()) {
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

        if (pickAnImageDialog != null)
            pickAnImageDialog.dismiss();

        runShowCase();
    }

    private void runShowCase() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(400); // half second between each showcase view
        config.setDismissTextColor(LIGHT_BLUE);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, MAIN_ACTIVITY_SEQUENCE_ID);
//        Log.e(getClass().getSimpleName(), "showcase is running");

        sequence.setConfig(config);

        sequence.addSequenceItem(userStickersButton,
                getString(R.string.your_stickers_explanation), getString(R.string.got_it));

        sequence.addSequenceItem(phoneStickersButton,
                getString(R.string.phone_stickers_explanation), getString(R.string.ok));

        sequence.addSequenceItem(templateStickerButton,
                getString(R.string.template_stickers_explanation), getString(R.string.okay));

        sequence.addSequenceItem(scratchButton,
                getString(R.string.scratch_stickers_explanation), getString(R.string.Let_s_go));

//        sequence.singleUse(null);
        sequence.start();
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
                instantiateChooserDialog();
            } else {
                Loader.gainPermission(this, Loader.FROM_SCRATCH_GAIN_PERMISSION);
            }
        } else if (itemId == R.id.dialog_from_scratch_empty_image) {
            if (Loader.checkPermission(this)) {
                File tempFile = generateEmptyBitmapFile();
                if (tempFile.exists() && tempOutPutFile.exists()) {
//                    Crop.of(Uri.fromFile(tempFile), Uri.fromFile(tempOutPutFile)).start(this);
                    Loader.crop(Uri.fromFile(tempFile), Uri.fromFile(tempOutPutFile), this);
                }
            } else {
                Loader.gainPermission(this, Loader.FROM_SCRATCH_GAIN_PERMISSION);
            }
        } else if (itemId == R.id.dialog_from_scratch_choose_a_picture)

        {
            chooseOrCapturePicture();
        } else if (itemId == R.id.activity_main_text) {
            Log.e(getClass().getSimpleName(), "clicked");
            File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/amir/");
            if (!folder.exists()) {
                Log.e(getClass().getSimpleName(), "folder didn't exist");
            }
            File files[] = folder.listFiles();
            int length = files.length;

            if (length == 0) {
                Log.e(getClass().getSimpleName(), "folder was empty");
            }
            for (File file : files) {
                Log.e(getClass().getSimpleName(), file.getName());
                String name = file.getName();
                if (name.contains(".png")) {
                    Bitmap regionalBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    if (regionalBitmap == null)
                        Log.e(getClass().getSimpleName(), "bitmap was null");
                    FileOutputStream outStream = null;
                    try {
                        outStream = new FileOutputStream(file.getAbsolutePath().replace("png", "webp"));
                        if (regionalBitmap != null) {
                            Log.e(getClass().getSimpleName(), file.getAbsolutePath().replace("png", "webp"));
                            regionalBitmap.compress(Bitmap.CompressFormat.WEBP, 90, outStream);
                        } else Log.e(getClass().getSimpleName(), "regionalBitmap was null");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (outStream != null)
                                outStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
//            Bot bot = new Bot();
//            bot.sendMessage();
//            requestProVersion();
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

    @NonNull
    private File generateEmptyBitmapFile() {
        File tempFile = new File(TEMP_STICKER_CASH_DIR);
        InputStream in;
        try {
            in = getAssets().open("empty.png");
            if (in == null) Log.e(getClass().getSimpleName(), "inputStream was null");
            OutputStream os = new FileOutputStream(tempFile);
            Loader.copyFile(in, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    private void instantiateChooserDialog() {
//        tempOutPutFile = new File(BaseActivity.TEMP_OUTPUT_DIRECTORY, "temp_file.png");
        tempOutPutFile = generateEmptyBitmapFile();

        if (!tempOutPutFile.mkdirs())
            Log.e(getClass().getSimpleName(), "could not make directory");
//        try {
//            if (tempOutPutFile.exists())
//                tempOutPutFile.delete();
//            if (!tempOutPutFile.createNewFile()) {
//                Toast.makeText(this, getResources().getString(R.string.could_not_make_file), Toast.LENGTH_SHORT).show();
//                return;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if (tempOutPutFile == null)
            Log.e(getClass().getSimpleName(), "tempOutputFile was null");
        else Log.e(getClass().getSimpleName(), "tempOutputFile was not null");
        View view = getLayoutInflater().inflate(R.layout.dialog_from_scratch, null);
        if (isInLandscape) {
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.dialog_from_scratch_main_container);
            if (linearLayout != null) {
                Log.e(getClass().getSimpleName(), "isInLandscape");
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
                    instantiateChooserDialog();
                }

                return;
            }
            case Loader.PHONE_STICKERS_GAIN_PERMISSION:{
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
        if (resultCode != RESULT_OK) {
            if (tempOutPutFile != null) tempOutPutFile.delete();
            return;
        }
        if (requestCode == REQUEST_SELECT_IMAGE) {
            Uri outputFile;
            Uri tempFileUri = Uri.fromFile(tempOutPutFile);

            if (data != null && (data.getAction() == null || !data.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE)))
                //if user took a picture
                outputFile = data.getData();
            else
                outputFile = tempFileUri;

            rotation = (int) Loader.capturedRotationFix(Loader.getRealPathFromURI(outputFile, getContentResolver())); // this is being passed to the next activity and been used in rotation
//            Crop.of(outputFile, tempFileUri).start(this);
            Loader.crop(outputFile, tempFileUri, this);


//            Loader.crop(outputFile, tempFileUri, this, CROP_REQUEST);
//        } else if (requestCode == Crop.REQUEST_CROP) {
        } else if (requestCode == UCrop.REQUEST_CROP) {
//        } else if (requestCode == CROP_REQUEST) {
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
        if (firstLoadPercentageText != null)
            firstLoadPercentageText.setText(percent + "%");
    }

    @Override
    public void onTaskFinishedListener() {
        firstLoadingDialog.hide();
        setPackCashStatus(true);
    }

    private void chooseOrCapturePicture() {
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

        firstLoadPercentageText = (TextView) firstLoadView.findViewById(R.id.first_load_dialog_text_percentage);
        if (firstLoadPercentageText != null) {
            String temp = 0 + "%";
            firstLoadPercentageText.setText(temp);
        }

    }

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

        if (firstLoadingDialog != null)
            firstLoadingDialog.dismiss();
    }
}