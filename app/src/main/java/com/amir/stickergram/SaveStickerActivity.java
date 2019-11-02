package com.amir.stickergram;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.sticker.icon.IconItem;
import com.amir.stickergram.sticker.icon.OnIconSelectedListener;
import com.amir.stickergram.sticker.icon.user.UserIconListFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class SaveStickerActivity extends BaseActivity
        implements OnIconSelectedListener, View.OnClickListener {

    public static final String EXTRA_FOLDER = "EXTRA_FOLDER";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_sticker);

        if (Companion.isTablet()) {
            UserIconListFragment fragment =
                    (UserIconListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_save_sticker_user_stickers_fragment);
            if (fragment != null)
                fragment.updateAdapterForSavingActivity();
        }
        Button createNewPackButton = findViewById(R.id.activity_save_sticker_create_new_pack);
        if (createNewPackButton != null) {
            createNewPackButton.setOnClickListener(this);
        }

        setFont((ViewGroup) findViewById(R.id.nav_drawer));
        setFont((ViewGroup) findViewById(R.id.activity_save_sticker_main_container));

    }

    @Override
    public void OnIconSelected(IconItem item) {
        Toast.makeText(this, getString(R.string.sticker_was_added), Toast.LENGTH_SHORT).show();
        goToStickerPack(item.getName());
    }

    @Override
    public void OnNoStickerWereFoundListener() {
        //intentionally empty
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.activity_save_sticker_create_new_pack) {
            File stickerDirectories = new File(BaseActivity.USER_STICKERS_DIRECTORY);
            List stickers = null;
            String[] stickerListDir = stickerDirectories.list();
            if (stickerDirectories.exists() && stickerListDir != null)
                stickers = Arrays.asList(stickerListDir);

            if (!isPaid) {
                if (stickers != null) {
                    if (stickers.size() > 1) {
                        Toast.makeText(this, getString(R.string.you_can_only_create_two_pack_in_free_version), Toast.LENGTH_LONG).show();
                        return;
                    }
                } else Log.e(getClass().getSimpleName(), "stickers was null");
            }
            final View newTextDialogView = getLayoutInflater().inflate(R.layout.dialog_new_package, null);
            setFont((ViewGroup) newTextDialogView);
            final EditText editText = newTextDialogView.findViewById(R.id.dialog_set_new_text_text);

            final AlertDialog newTextDialog = new AlertDialog.Builder(this)
                    .setView(newTextDialogView)
//                    .setTitle(getString(R.string.package_name))
                    .setPositiveButton(getString(R.string.done), null)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create();

            final List finalStickers = stickers;
            newTextDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    Button b = newTextDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    SaveStickerActivity.this.setFont(b);
                    SaveStickerActivity.this.setFont(newTextDialog.getButton(AlertDialog.BUTTON_NEGATIVE));
                    b.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            String text = editText.getText().toString();

                            if (finalStickers != null && finalStickers.contains(text)) {
                                View nameAlreadyExistText = newTextDialogView.findViewById(R.id.dialog_new_package_already_exist);
                                if (nameAlreadyExistText != null)
                                    nameAlreadyExistText.setVisibility(View.VISIBLE);
                            } else if (text.length() > Constants.PACKAGE_NAME_LENGTH_LIMIT) {
                                View nameCantBeThisLong = newTextDialogView.findViewById(R.id.name_can_t_be_this_long);
                                if (nameCantBeThisLong != null)
                                    nameCantBeThisLong.setVisibility(View.VISIBLE);
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
                                    !text.contains(".") &&
                                    !text.contains("?")) {

                                int textLength = text.length();
                                while (text.charAt(textLength - 1) == ' ') {
                                    text = text.substring(0, textLength - 1);
                                    textLength = text.length();
                                }
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
        }
    }


    private void goToStickerPack(String stickerFolder) {

        String dir = BaseActivity.USER_STICKERS_DIRECTORY + stickerFolder + File.separator;
        File folder = new File(dir);
        File[] files;
//        Log.e(getClass().getSimpleName(), "Directory: " + dir);
        if (folder.exists() && folder.isDirectory())
            files = folder.listFiles();
        else throw new RuntimeException("Invalid Folder");
        try {
            File cashedSticker = new File(BaseActivity.Companion.getTEMP_STICKER_CASH_DIR());
            if (cashedSticker.exists() && cashedSticker.isFile() && files != null) {
                Loader.INSTANCE.copyFile(cashedSticker,
                        new File(dir + files.length + ".png"));

                Bitmap bitmap = BitmapFactory.decodeFile(BaseActivity.Companion.getTEMP_STICKER_CASH_DIR());
                File thumbFile =
                        new File((BaseActivity.Companion.getBASE_USER_THUMBNAIL_DIRECTORY() + File.separator + stickerFolder + "_" + files.length + ".png"));
                File parentFile = thumbFile.getParentFile();
                if (parentFile != null && !parentFile.exists())
                    if (!parentFile.mkdirs())
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
