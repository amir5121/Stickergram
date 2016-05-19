package com.amir.telegramstickerbuilder;

import android.app.Dialog;
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

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.Loader;
import com.amir.telegramstickerbuilder.sticker.icon.IconItem;
import com.amir.telegramstickerbuilder.sticker.icon.IconListFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == Dialog.BUTTON_POSITIVE) {
                        String packFolder = editText.getText().toString();
                        File folder = new File(BaseActivity.USER_STICKERS_DIRECTORY + packFolder + File.separator);
                        if (!folder.mkdirs())
                            Log.e(getClass().getSimpleName(), "Couldn't make the directory at: " + BaseActivity.USER_STICKERS_DIRECTORY + packFolder + File.separator);
                        goToStickerPack(packFolder);
                    }
                    if (which == Dialog.BUTTON_NEGATIVE) {
                    }
                }
            };

            AlertDialog newTextDialog = new AlertDialog.Builder(this)
                    .setView(newTextDialogView)
                    .setTitle(getString(R.string.package_name))
                    .setPositiveButton(getString(R.string.done), listener)
                    .setNegativeButton(getString(R.string.cancel), listener)
                    .create();

            newTextDialog.show();
        }
    }

    private void goToStickerPack(String stickerFolder) {

        String dir = BaseActivity.USER_STICKERS_DIRECTORY + stickerFolder + File.separator;
        File folder = new File(dir);
        File[] files;
        if (folder.exists() && folder.isDirectory())
            files = folder.listFiles();
        else throw new RuntimeException("Invalid Folder");
        try {
            File cashedSticker = new File(BaseActivity.STICKER_CASH_DIR);
            if (cashedSticker.exists() && cashedSticker.isFile()) {
                Loader.copyFile(cashedSticker,
                        new File(dir + files.length + ".png"));

                Bitmap bitmap = BitmapFactory.decodeFile(BaseActivity.STICKER_CASH_DIR);
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
