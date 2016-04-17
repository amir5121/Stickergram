package com.amir.telegramstickerbuilder.infrastructure;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewGroupCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amir.telegramstickerbuilder.EditImageActivity;
import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.sticker.pack.PackItem;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import app.minimize.com.seek_bar_compat.SeekBarCompat;

public class Loader {
    private static final int THUMBNAIL_IMAGE_QUALITY = 85;
    private static final String TAG = "LOADER";

    public static final int TEXT_COLOR = 0;
    public static final int TEXT_SHADOW_COLOR = 1;

    public static void gainPermission(BaseActivity activity) {
        if (
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

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

        if (!destFile.exists()) {
            if (!destFile.createNewFile())
                return false;
        } else {
            destFile.delete();
            destFile.createNewFile();
        }

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
                if (which == Dialog.BUTTON_POSITIVE) {
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
        } else Log.e("Loader", "dialog_single_item_image was null");

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
                if (which == Dialog.BUTTON_POSITIVE) {
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

    public static String makeACopyToFontFolder(Uri uri, BaseActivity activity) throws IOException {
        Log.e(TAG, "fileName: " + getFileName(uri, activity));
        String fileName = getFileName(uri, activity);
        if (!fileName.toLowerCase().contains(".ttf")) {
            Toast.makeText(activity, activity.getString(R.string.choose_a_font), Toast.LENGTH_LONG).show();
            return null;
        }
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TSB/fonts/" + getFileName(uri, activity));// you can also use app's internal cache to store the file

        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs())
                return null;
        }

        if (!file.exists()) {
            if (!file.createNewFile()) {
                return null;
            }
        } else {
            if (!file.delete())
                if (!file.createNewFile())
                    return null;
        }

        FileOutputStream fos = new FileOutputStream(file);
        InputStream is = activity.getContentResolver().openInputStream(uri);
//        Log.e(getClass().getSimpleName(), String.valueOf(uri.getQueryParameterNames()));
        if (is == null) return null;
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            len = is.read(buffer);
            while (len != -1) {
                fos.write(buffer, 0, len);
                len = is.read(buffer);
            }

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    public static String getFileName(Uri uri, BaseActivity activity) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static SeekBarCompat getSeekBar(Context context,
                                           int range,
                                           int backGroundColor,
                                           int progressColor,
                                           int thumbColor,
                                           int defaultPosition,
                                           RelativeLayout viewGroup) {
        SeekBarCompat seekBar = new SeekBarCompat(context);
        seekBar.setMax(range);
//        seekBar.setBackgroundColor(backGroundColor);
        seekBar.setBackgroundColor(backGroundColor);
        seekBar.setProgressColor(progressColor);
        seekBar.setThumbColor(thumbColor);
        seekBar.setProgress(defaultPosition);

        float scale = context.getResources().getDisplayMetrics().density;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (50 * scale));
        params.addRule(RelativeLayout.ABOVE, R.id.activity_edit_image_buttons_container);
        params.setMargins((int) (10 * scale), 0, (int) (10 * scale), (int) (55 * scale));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.setMarginStart((int) (10 * scale));
            params.setMarginEnd((int) (10 * scale));
        }
        seekBar.setLayoutParams(params);
        seekBar.setVisibility(View.GONE);
        viewGroup.addView(seekBar);

        return seekBar;
    }

    public static void setColor(Context context, final TouchImageView touchImageView, final int type) {

        ColorPickerDialogBuilder
                .with(context)
                .setTitle(context.getString(R.string.choose_color))
                .initialColor(
                        (type == TEXT_COLOR) ?
                                touchImageView.getTextItem().getTextColor() :
                                touchImageView.getTextItem().getShadow().getColor())
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
//                        toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
//                        changeBackgroundColor(selectedColor);
                        if (type == TEXT_COLOR)
                            touchImageView.getTextItem().setTextColor(selectedColor);
                        else
                            touchImageView.getTextItem().getShadow().setColor(selectedColor);
                        touchImageView.updateTextView();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    public static boolean isPersian(String string) {
        for (int i = 0; i < string.length(); i++) {
            int charAsciiNum = (int) string.charAt(i);
            if ((charAsciiNum > 1575 && charAsciiNum < 1641) || charAsciiNum == 1662 || charAsciiNum == 1711 || charAsciiNum == 1670 || charAsciiNum == 1688)
                return true;
        }
        return false;
    }
}
