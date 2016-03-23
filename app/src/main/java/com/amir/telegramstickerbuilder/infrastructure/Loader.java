package com.amir.telegramstickerbuilder.infrastructure;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.views.StickerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Loader {
    public static boolean hasLoadedOnce = false;
    public static final String USER_STICKER_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    public static final String PHONE_STICKERS_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + "org.telegram.messenger" + File.separator + "cache" + File.separator;
//    public static final String PHONE_STICKERS_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "messenger" + File.separator + "cache" + File.separator;

    public static void loadPhoneStickers(DataSource dataSource) {
        File files[] = new File(PHONE_STICKERS_DIRECTORY).listFiles();


        for (File file : files) {
            String name = file.getName();
            if (name.contains(".webp") && name.charAt(1) == '_') {
//                Log.e("Loader: ", file.getAbsolutePath());
                dataSource.update(file.getAbsolutePath(), null, StickerItem.TYPE_IN_PHONE, false, true);
            }
        }
        Log.e("PhoneStickerActivity", "Got here");
        hasLoadedOnce = true;

//        new AsyncLoader().execute(dataSource);
    }

    private void loadUsersStickers() {
    }

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


    public static class AsyncLoader extends AsyncTask<DataSource, String, DataSource> {
        @Override
        protected DataSource doInBackground(DataSource... dataSources) {
            File files[] = new File(PHONE_STICKERS_DIRECTORY).listFiles();

            for (File file : files) {
                String name = file.getName();
                if (name.contains(".webp") && name.charAt(1) == '_') {
//                Log.e("Loader: ", file.getAbsolutePath());
                    dataSources[0].update(file.getAbsolutePath(), null, StickerItem.TYPE_IN_PHONE, false, true);
                }
            }
            return dataSources[0];
        }

        @Override
        protected void onPostExecute(DataSource dataSource) {
            super.onPostExecute(dataSource);


//            dataSource.setItems(dataSource.getDataSource().getAllItems());
//
//            dataSource.notifyDataSetChanged();
        }
    }
}
