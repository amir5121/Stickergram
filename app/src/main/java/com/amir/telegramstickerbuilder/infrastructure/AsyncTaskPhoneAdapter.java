package com.amir.telegramstickerbuilder.infrastructure;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.amir.telegramstickerbuilder.PhoneStickersActivity;
import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.sticker.single.SingleStickersAdapter;
import com.amir.telegramstickerbuilder.sticker.single.StickerItem;

import java.io.File;

public class AsyncTaskPhoneAdapter extends AsyncTask<SingleStickersAdapter, Integer, Void> {
    Context context;
    AsyncPhoneTaskListener listener;
    String baseThumbDir;
    int percent;
    int foundedStickersCount = 0;

    public AsyncTaskPhoneAdapter(BaseActivity activity) {
        attach(activity);
    }

    public void attach(BaseActivity activity) {

        try {
            listener = (AsyncPhoneTaskListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "Must implement AsyncPhoneTaskListener");
        }
        if (activity.getExternalCacheDir() != null)
            baseThumbDir = activity.getExternalCacheDir().getAbsolutePath() + File.separator + "phone_";
        else baseThumbDir = activity.getCacheDir().getAbsolutePath() + File.separator + "phone_";
        //todo: use externalCashDirectory
        this.context = activity;
    }

    @Override
    protected void onPreExecute() {
//        super.onPreExecute();
        Log.e(getClass().getSimpleName(), "PreExe called");
        listener.onTaskStartListener();
    }

    @Override
    protected synchronized Void doInBackground(SingleStickersAdapter... params) {
        //Todo: what happen if the directory doesn't exist
        File folder = new File(PhoneStickersActivity.PHONE_STICKERS_DIRECTORY);
        if (!folder.exists()) {
            Toast.makeText(context, PhoneStickersActivity.PHONE_STICKERS_DIRECTORY, Toast.LENGTH_LONG).show();
            return null;
        }
        int temp = 0;
        percent = 0;
        File files[] = folder.listFiles();
        int length = files.length;
        String thumbDirectory;
        DataSource dataSource = params[0].getDataSource();

        System.gc();
        int i = 0;
        for (File file : files) {
            i++;
            String name = file.getName();
//            Log.e(getClass().getSimpleName(), name);
            //TODO: what if the file was deleted from the memory and it's address was still in the sharedPreferences
            //TODO: add all the directories to a new set and update the old one also write an updateSet method so you don't have any redundant sticker that doesn't exist
//            Log.e(getClass().getSimpleName(), name);
            if (!dataSource.contain(file.getAbsolutePath()))
                if (name.contains(".webp") && name.charAt(1) == '_' && !name.contains("temp")) {
                    thumbDirectory = baseThumbDir + name;
                    dataSource.update(new StickerItem(
                            file.getAbsolutePath(),
                            Loader.generateThumbnail(file.getAbsolutePath(), thumbDirectory),
                            StickerItem.IN_PHONE,
                            false,
                            true));
                    foundedStickersCount++;
                }
            percent = (100 * i) / length;
            if (temp == percent) {
                Log.e(getClass().getSimpleName(), "Called " + percent);
                temp++;
                publishProgress(percent, foundedStickersCount);
            }
        }

        if (foundedStickersCount == 0)
            listener.onTaskDismissedListener();

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (context != null)
//            Log.e(getClass().getSimpleName(), "was updated");
            listener.onTaskUpdateListener(values[0], values[1]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (context != null)
            listener.onTaskFinishedListener();
    }

    public void detach() {
        context = null;
    }

    public interface AsyncPhoneTaskListener {
        void onTaskStartListener();

        void onTaskDismissedListener();

        void onTaskUpdateListener(int percent, int stickerCount);

        void onTaskFinishedListener();
    }
}
