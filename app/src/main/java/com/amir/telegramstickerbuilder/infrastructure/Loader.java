package com.amir.telegramstickerbuilder.infrastructure;


import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.amir.telegramstickerbuilder.EditImageActivity;
import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.sticker.pack.PackItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class Loader {
    private static final int THUMBNAIL_IMAGE_QUALITY = 85;
    private static final String TAG = "LOADER";

    public static void gainPermission(BaseActivity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public static boolean checkPermission(BaseActivity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return true;
    }

    public static boolean copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            if (destFile.getParentFile().mkdirs())
                return false;

        if (!destFile.exists())
            if (!destFile.createNewFile())
                return false;


        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
        return true;
    }

    public static String generateThumbnail(String fromDirectory, String toDirectory) {
        Bitmap regionalBitmap = BitmapFactory.decodeFile(fromDirectory);
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(regionalBitmap, regionalBitmap.getWidth() / 3, regionalBitmap.getHeight() / 3);
        FileOutputStream outStream = null;

        try {
            outStream = new FileOutputStream(toDirectory);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, THUMBNAIL_IMAGE_QUALITY, outStream);
                return toDirectory;
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outStream != null)
                    outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap generateThumbnail(Bitmap regionalBitmap) {
        return ThumbnailUtils.extractThumbnail(regionalBitmap, regionalBitmap.getWidth(), regionalBitmap.getHeight());
    }

    public static void loadStickerDialog(final Uri uri, final BaseActivity activity) {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE){
                    Intent intent = new Intent(activity, EditImageActivity.class);
                    intent.putExtra(BaseActivity.EDIT_IMAGE_URI, uri);
                    //// TODO: Animation
                    activity.startActivity(intent);
                }
            }
        };

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_single_item, null, false);
        ImageView stickerImage = (ImageView) view.findViewById(R.id.dialog_single_item_image);

        if (stickerImage != null) {
            stickerImage.setImageURI(uri);
        }
        else Log.e("Loader", "dialog_single_item_image was null");

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setTitle(activity.getString(R.string.edit_this_sticker))
                .setNegativeButton(activity.getString(R.string.no), listener)
                .setPositiveButton(activity.getString(R.string.yes), listener)
                .create();

        dialog.getWindow().getAttributes().width = ActionBar.LayoutParams.MATCH_PARENT;
        dialog.getWindow().getAttributes().height = ActionBar.LayoutParams.MATCH_PARENT;

        dialog.show();
    }
    public static void loadStickerDialog(final PackItem item, final BaseActivity activity) {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE){
                    Intent intent = new Intent(activity, EditImageActivity.class);
                    intent.putExtra(BaseActivity.EDIT_IMAGE_DIR_IN_ASSET, item.getDirInAsset());
                    //// TODO: Animation
                    activity.startActivity(intent);
                }
            }
        };

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_single_item, null, false);
        ImageView stickerImage = (ImageView) view.findViewById(R.id.dialog_single_item_image);

        Bitmap bitmap = null;
        try {
            InputStream inputStream = item.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (stickerImage != null)
            stickerImage.setImageBitmap(bitmap);
        else Log.e("Loader", "dialog_single_item_image was null");

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setTitle(activity.getString(R.string.edit_this_sticker))
                .setNegativeButton(activity.getString(R.string.no), listener)
                .setPositiveButton(activity.getString(R.string.yes), listener)
                .create();

        dialog.show();
    }

    public static void saveBitmap(Bitmap mainBitmap) {
        OutputStream outputStream = null;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/amir.png");
        try {
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else file.createNewFile();
            outputStream = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (outputStream == null)
            Log.e(TAG, "outPutStream was null");
        else
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            mainBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

    }


}
