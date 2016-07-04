package com.amir.stickergram.infrastructure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.Log;

import com.amir.stickergram.base.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AsyncFirstLoad extends AsyncTask<Context, Integer, Void> {
    private static final String STICKERS = "Stickers";
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
            String folders[] = contexts[0].getAssets().list(STICKERS);
            int filesChecked = 0; // used to set percentage// in the dialog
            OutputStream outputStream = null;
            for (String folder : folders) {
                String files[] = contexts[0].getAssets().list(STICKERS + File.separator + folder);
                for (String file : files) {
                    InputStream inputStream = contexts[0].getAssets().open(STICKERS + File.separator + folder + File.separator + file);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap != null) {

                        // replacing webp with png and compressing it as a png so on devices with lower apis transparent background remain transparent and doesn't turn black
                        File thumbFile = new File((BaseActivity.BASE_THUMBNAIL_DIRECTORY + File.separator + folder + "_" + file).replace(".webp", ".png"));
//                        File thumbFile = new File((BaseActivity.BASE_THUMBNAIL_DIRECTORY + File.separator + folder + "_" + file));

                        if (!thumbFile.getParentFile().exists())
                            if (!thumbFile.getParentFile().mkdirs())
                                Log.e(getClass().getSimpleName(), "failed");

                        if (!thumbFile.exists())
                            if (!thumbFile.createNewFile())
                                Log.e(getClass().getSimpleName(), "File creation was failed");

                        outputStream = new FileOutputStream(thumbFile);
                        ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3).compress(Bitmap.CompressFormat.PNG, 85, outputStream);
                        publishProgress((filesChecked++ * 100) / 225); // there are 213 sticker in the assets folder dividing bu 217 so we get 100%
                    }
                }
            }
            if (outputStream != null) {
                outputStream.close();
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