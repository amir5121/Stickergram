package com.amir.stickergram;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
//    int rotation = 0; // if the image is captured it would probably need to be rotated

    //todo: use snackBar instead of Toast

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavDrawer(new MainNavDrawer(this));

        setUpView();

        //coming from a share intent
        Intent mIntent = getIntent();
        if (mIntent != null) {
            String action = mIntent.getAction();
            if (action != null) {
                String type = mIntent.getType();
                if (type != null) {
                    if (action.equals(Intent.ACTION_SEND)) {
                        if (type.startsWith("image/")) {
                            tempOutPutFile = Loader.generateEmptyBitmapFile(this);
                            Loader.crop((Uri) mIntent.getParcelableExtra(Intent.EXTRA_STREAM), Uri.fromFile(tempOutPutFile), this);
                        }
                    }
                }
            }
        }

        if (!hasCashedPackStickers()) {
            callAsyncTaskPhoneAdapter();
        }

        if (pickAnImageDialog != null)
            pickAnImageDialog.dismiss();

        runShowCase();
    }

    private void setUpView() {
        userStickersButton = findViewById(R.id.activity_main_user_stickers_button);
        phoneStickersButton = findViewById(R.id.activity_main_phone_stickers);
        templateStickerButton = findViewById(R.id.activity_main_template_stickers);
        scratchButton = findViewById(R.id.activity_main_start_scratch_stickers);
        topImage = findViewById(R.id.activity_main_text);

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


            if (isInLandscape && topImage != null)
                topImage.setVisibility(View.GONE);

        }

    }

    private void runShowCase() {
        if (!isInLandscape) {
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(200); // half second between each showcase view
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
                File tempFile = Loader.generateEmptyBitmapFile(this);
                if (tempFile.exists() && tempOutPutFile.exists()) {
//                    Crop.of(Uri.fromFile(tempFile), Uri.fromFile(tempOutPutFile)).start(this);
                    Loader.crop(Uri.fromFile(tempFile), Uri.fromFile(tempOutPutFile), this);
                }
            } else {
                Loader.gainPermission(this, Loader.FROM_SCRATCH_GAIN_PERMISSION);
            }
        } else if (itemId == R.id.dialog_from_scratch_choose_a_picture) {
            chooseOrCapturePicture();
        } else if (itemId == R.id.activity_main_text) {
            if (BuildConfig.DEBUG) {
                setLocale("fa", MainActivity.class);
//            Log.e(getClass().getSimpleName(), "clicked");
//            File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/amir/");
//            if (!folder.exists()) {
//                Log.e(getClass().getSimpleName(), "folder didn't exist");
//            }
//            File files[] = folder.listFiles();
//            int length = files.length;
//
//            if (length == 0) {
//                Log.e(getClass().getSimpleName(), "folder was empty");
//            }
//            for (File file : files) {
//                Log.e(getClass().getSimpleName(), file.getName());
//                String name = file.getName();
//                if (name.contains(".png")) {
//                    Bitmap regionalBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                    if (regionalBitmap == null)
//                        Log.e(getClass().getSimpleName(), "bitmap was null");
//                    FileOutputStream outStream = null;
//                    try {
//                        outStream = new FileOutputStream(file.getAbsolutePath().replace("png", "webp"));
//                        if (regionalBitmap != null) {
//                            Log.e(getClass().getSimpleName(), file.getAbsolutePath().replace("png", "webp"));
//                            regionalBitmap.compress(Bitmap.CompressFormat.WEBP, 90, outStream);
//                        } else Log.e(getClass().getSimpleName(), "regionalBitmap was null");
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } finally {
//                        try {
//                            if (outStream != null)
//                                outStream.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }

//            Log.e(getClass().getSimpleName(), "Running");
//            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
//            File gpxfile = new File(root, "result.txt");
//            try {
//                FileWriter writer = new FileWriter(gpxfile);
////                try {
////                    AesCbcWithIntegrity.SecretKeys secretKeys = AesCbcWithIntegrity.generateKey();
////                    writer.append(secretKeys.toString());
////                    writer.append("\n\n\n\n\n\n");
////                    writer.append(AesCbcWithIntegrity.encrypt("3082036f30820257a00302010202047f4e9075300d06092a864886f70d01010b05003068310b3009060355040613023938310d300b0603550408130446617273310d300b060355040713044972616e310d300b060355040a1304616d6972310d300b060355040b1304616d6972311d301b06035504031314416d697220686f7365696e20486573686d617469301e170d3136303632313130313535335a170d3431303631353130313535335a3068310b3009060355040613023938310d300b0603550408130446617273310d300b060355040713044972616e310d300b060355040a1304616d6972310d300b060355040b1304616d6972311d301b06035504031314416d697220686f7365696e20486573686d61746930820122300d06092a864886f70d01010105000382010f003082010a0282010100c66daffe93cc77372514f0fa9be31ffe60b8baf09faef81a71479ae658aea030883bf5645446c6929d7481e0fecfd3049792c540e954b21e4773226f5aa15f49f574ceb0aef4c560655235dd9a48e5d9fa5a5ee00ca75675b63dc1ffe0f9b27694ecdb39ffec5802c7edbaf2a21220f9db23d1ce395612b026a13b23a415d34a8227dd478b59c7234e62d8a9da8745e554b562010423ed258d38e04ffc1382f099214d47a7bedda256de14a2efff5d0783e8fbae86de945dfb1699b4870fd7dec246b2b3dde5e90fc2e771e0dff32e5325c81ab7f10874aacdf2a81966fe9e373c5a178676084081815fa83c5cf7ae0a8587fefba8fb36578ba016bc4c9d2ba30203010001a321301f301d0603551d0e04160414132f4053df653d9a614f0f73a0b9e14c7abe6f4e300d06092a864886f70d01010b05000382010100abe26783f6e2423bed717ad0019dca534cf0b71f5f09139d0e1a688ecf8d43935bfd05f2d88207ec377350d2bda8869df14240a8bf87d6e3a52b4fee8b7374bbbcce05fd05651850c99340ae5756ed0407eb08758c3a4bdb33876abb02df126b785a7ae7e37ba22b49ceb598a2c14aaa9762aaa8b268eeb0ee163dc393379f6144e1267cae889e55ad1894e9ee94aa1bdc30e5012a67aa7c07539135437feb6690bc28501231fda0bc898f81d2e3e4ee152ff09c2607c5747829c5e4f0a3849fbcb061341aef918ff4bfb4cb93bc85067c33e46d4c13abf5b752522d2dfa9632a11a825807cec30609c85cdcc534baa5b814a655eec8365eb9285eb34562b5bd",
////                            secretKeys).toString());
////                    writer.flush();
////                    writer.close();
//                writer.append(AesCbcWithIntegrity.decryptString(new AesCbcWithIntegrity.CipherTextIvMac("rgKw/MerIRrbbaziThZ4bg==:kZcE/+0ggZIqDaWfrs4R4DehHHtPxkHTqO9DyRntF8s=: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"),
//                        AesCbcWithIntegrity.keys(BaseActivity.KEY)));
//                writer.flush();
//                writer.close();
////
////                } catch (GeneralSecurityException e) {
////                    e.printStackTrace();
////                }
//            } catch (GeneralSecurityException | IOException e) {
//                e.printStackTrace();
//            }
            }
        }
    }

    public void setLocale(String lang, Class mClass) {
//        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
//        conf.locale = myLocale;
        conf.locale = new Locale(lang);
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, mClass);
        startActivity(refresh);
        finish();
    }


    private void instantiateChooserDialog() {
//        tempOutPutFile = new File(BaseActivity.TEMP_OUTPUT_DIRECTORY, "temp_file.png");
        tempOutPutFile = Loader.generateEmptyBitmapFile(this);

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

//        if (tempOutPutFile == null)
//            Log.e(getClass().getSimpleName(), "tempOutputFile was null");
//        else Log.e(getClass().getSimpleName(), "tempOutputFile was not null");
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
            case Loader.PHONE_STICKERS_GAIN_PERMISSION: {
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
//            if (tempOutPutFile != null) tempOutPutFile.delete();
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

//            rotation = (int) Loader.capturedRotationFix(Loader.getRealPathFromURI(outputFile, getContentResolver())); // this is being passed to the next activity and been used in rotation
//            Log.e(getClass().getSimpleName(), "rotation: " + rotation);
            Loader.crop(outputFile, tempFileUri, this);

        } else if (requestCode == UCrop.REQUEST_CROP) {
            Intent intent = new Intent(this, EditImageActivity.class);
            intent.putExtra(BaseActivity.EDIT_IMAGE_URI, Uri.fromFile(tempOutPutFile));
//            intent.putExtra(BaseActivity.NEED_ROTATION, rotation);
            startActivity(intent);
        }
    }

    @Override
    public void onTaskStartListener() {
        instantiateLoadingDialog();
    }

    @Override
    public void onTaskUpdateListener(int percent) {
        if (firstLoadPercentageText != null) {
            if (Loader.deviceLanguageIsPersian()) {
                String temp = "% " + Loader.convertToPersianNumber(String.valueOf(percent));
                firstLoadPercentageText.setText(temp);
            } else {
                String temp = percent + " %";
                firstLoadPercentageText.setText(temp);
            }
        }
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

    @Override
    public void onBackPressed() {
        Loader.exit(this);
    }
}