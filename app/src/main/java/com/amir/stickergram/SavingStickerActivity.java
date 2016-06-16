package com.amir.stickergram;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.sticker.icon.IconItem;
import com.amir.stickergram.sticker.icon.IconListFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.shape.CircleShape;

public class SavingStickerActivity extends BaseActivity implements IconListFragment.OnIconSelectedListener, View.OnClickListener {
    public static final String EXTRA_FOLDER = "EXTRA_FOLDER";

    Button createNewPackButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_sticker);
//        Toast.makeText(this,getString(R.string.every_sticker_must_be_in_a_pack),Toast.LENGTH_LONG).show();
        createNewPackButton = (Button) findViewById(R.id.activity_save_sticker_create_new_pack);
        if (createNewPackButton != null) {
            createNewPackButton.setOnClickListener(this);
        }

        new MaterialShowcaseView.Builder(this)
                .setDelay(400)
                .setTarget(createNewPackButton)
                .setContentText(R.string.you_can_create_new_package)
                .setDismissText(R.string.ok)
                .setDismissOnTouch(true)
                .singleUse("AMIR")
                .show();
    }

    @Override
    public void OnIconSelected(IconItem item) {
        goToStickerPack(item.getFolder());
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.activity_save_sticker_create_new_pack) {
            final View newTextDialogView = getLayoutInflater().inflate(R.layout.dialog_new_package, null);
            final EditText editText = (EditText) newTextDialogView.findViewById(R.id.dialog_set_new_text_text);
//            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (which == Dialog.BUTTON_POSITIVE) {
////                        String packFolder = editText.getText().toString();
////                        File folder = new File(BaseActivity.USER_STICKERS_DIRECTORY + packFolder + File.separator);
////                        if (!folder.mkdirs())
////                            Log.e(getClass().getSimpleName(), "Couldn't make the directory at: " + BaseActivity.USER_STICKERS_DIRECTORY + packFolder + File.separator);
////                        goToStickerPack(packFolder);
//                    }
//                    if (which == Dialog.BUTTON_NEGATIVE) {
//                    }
//                }
//            };

            final AlertDialog newTextDialog = new AlertDialog.Builder(this)
                    .setView(newTextDialogView)
                    .setTitle(getString(R.string.package_name))
                    .setPositiveButton(getString(R.string.done), null)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create();

            newTextDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    Button b = newTextDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            String text = editText.getText().toString();
                            File stickerDirectories = new File(USER_STICKERS_DIRECTORY);
                            List stickers = null;
                            if (stickerDirectories.exists())
                                stickers = Arrays.asList(stickerDirectories.list());

                            if (stickers != null && stickers.contains(text)) {
                                View nameAlreadyExistText = newTextDialogView.findViewById(R.id.dialog_new_package_already_exist);
                                if (nameAlreadyExistText != null)
                                    nameAlreadyExistText.setVisibility(View.VISIBLE);
                            } else if (!text.equals("") &&
                                    !text.contains("!") &&
                                    !text.contains("'") &&
                                    !text.contains("/") &&
                                    !text.contains("%") &&
                                    !text.contains("#") &&
                                    !text.contains("*") &&
                                    !text.contains("\\") &&
                                    !text.contains(":") &&
                                    !text.contains("|") &&
                                    !text.contains("<") &&
                                    !text.contains(">") &&
                                    !text.contains("?")) {
                                //todo: look around
                                Log.e(getClass().getSimpleName(), "Made it to here");
                                File folder = new File(BaseActivity.USER_STICKERS_DIRECTORY + text + File.separator);
                                if (folder.mkdirs())
                                    goToStickerPack(text);
                                newTextDialog.dismiss();
                            } else {
                                View symbolTextView = newTextDialogView.findViewById(R.id.dialog_new_package_symbol_text);
                                if (symbolTextView != null)
                                    symbolTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });
            newTextDialog.show();
//            newTextDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
//
//            editText.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                    Log.e(getClass().getSimpleName(), "before: " + count);
//                    if (count == 0)
//                        newTextDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
//                    else newTextDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if (count == 0)
//                        newTextDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
//                    else newTextDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
//
//                    Log.e(getClass().getSimpleName(), "on: " + count);
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    Log.e(getClass().getSimpleName(), "after: " + s.toString());
//                }
//            });
        }
    }

    private void goToStickerPack(String stickerFolder) {

        String dir = BaseActivity.USER_STICKERS_DIRECTORY + stickerFolder + File.separator;
        File folder = new File(dir);
        File[] files;
        Log.e(getClass().getSimpleName(), "Directory: " + dir);
        if (folder.exists() && folder.isDirectory())
            files = folder.listFiles();
        else throw new RuntimeException("Invalid Folder");
        try {
            File cashedSticker = new File(BaseActivity.TEMP_STICKER_CASH_DIR);
            if (cashedSticker.exists() && cashedSticker.isFile()) {
                Loader.copyFile(cashedSticker,
                        new File(dir + files.length + ".png"));

                Bitmap bitmap = BitmapFactory.decodeFile(BaseActivity.TEMP_STICKER_CASH_DIR);
                File thumbFile =
                        new File((BaseActivity.BASE_THUMBNAIL_DIRECTORY + File.separator + stickerFolder + "_" + files.length + ".png"));
                if (!thumbFile.getParentFile().exists())
                    if (!thumbFile.getParentFile().mkdirs())
                        Log.e(getClass().getSimpleName(), "failed");

                if (!thumbFile.exists())
                    if (!thumbFile.createNewFile())
                        Log.e(getClass().getSimpleName(), "File creation was failed");

                OutputStream outputStream = new FileOutputStream(thumbFile);
                ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3).compress(Bitmap.CompressFormat.PNG, 85, outputStream);
                cashedSticker.delete();
                finish();
                Intent intent = new Intent(this, UserStickersActivity.class);
                intent.putExtra(EXTRA_FOLDER, stickerFolder);
                startActivity(intent);
            } else Log.e(getClass().getSimpleName(), "file didn't exist");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long mBackPressed;

    @Override
    public void onBackPressed() {
        if (mBackPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.you_will_lose_progress), Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }


}
