package com.amir.telegramstickerbuilder.infrastructure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AsyncFirstLoad extends AsyncTask<Context, Integer, Void> {
    AsyncFirstTaskListener listener;
    BaseActivity activity;

    public AsyncFirstLoad(BaseActivity activity) {
        attach(activity);
    }

    public void attach(BaseActivity activity) {

        try {
            listener = (AsyncFirstTaskListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "Must implement AsyncPhoneTaskListener");
        }
        this.activity = activity;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onTaskStartListener();
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        try {
            String folders[] = contexts[0].getAssets().list("");
            int filesChecked = 0; // used to set percentage in the dialog
            for (String folder : folders) {
                String files[] = contexts[0].getAssets().list(folder);
                for (String file : files) {
                    InputStream inputStream = contexts[0].getAssets().open(folder + File.separator + file);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap != null) {
                        File thumbFile = new File(contexts[0].getCacheDir() + File.separator + "thumb_" + folder + "_" + file);
//                            Log.e(getClass().getSimpleName(), File.separator + "thumb_" + folder + "_" + files[i]);
                        if (!thumbFile.getParentFile().exists())
                            if (!thumbFile.getParentFile().mkdirs())
                                Log.e(getClass().getSimpleName(), "failed");

                        if (!thumbFile.exists())
                            if (!thumbFile.createNewFile())
                                Log.e(getClass().getSimpleName(), "failed");

                        OutputStream outputStream = new FileOutputStream(thumbFile);
                        ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3).compress(Bitmap.CompressFormat.WEBP, 85, outputStream);
//                            Log.e(getClass().getSimpleName(), String.valueOf(bitmap.getWidth() / 4) + " " + String.valueOf(bitmap.getHeight() / 4));
                        publishProgress((filesChecked++ * 100) / 219); // there are 219 sticker in the assets folder dividing bu 217 so we get 100%
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        listener.onTaskUpdateListener(values[0]);

    }

    public void detach() {
        activity = null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onTaskFinishedListener();
    }

    public interface AsyncFirstTaskListener {
        void onTaskStartListener();

        void onTaskUpdateListener(int percent);

        void onTaskFinishedListener();
    }

}