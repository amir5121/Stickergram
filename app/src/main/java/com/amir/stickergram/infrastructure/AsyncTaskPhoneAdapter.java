package com.amir.stickergram.infrastructure;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amir.stickergram.PhoneStickersActivity;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.sticker.single.SingleStickersAdapter;
import com.amir.stickergram.sticker.single.StickerItem;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class AsyncTaskPhoneAdapter extends AsyncTask<SingleStickersAdapter, Integer, Integer> {
    private static final Integer NON_EXISTENCE_CACHE_FOLDER_VALUE = -1;
    private static final Integer SUCCESS_VALUE = 0;
    private static final Integer NO_ITEM_VALUE = 1;
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
        this.context = activity;
    }

    @Override
    protected void onPreExecute() {
        listener.onTaskStartListener();
    }

    @Override
    protected synchronized Integer doInBackground(SingleStickersAdapter... params) {
        File folder = new File(PhoneStickersActivity.PHONE_STICKERS_DIRECTORY);
        if (!folder.exists()) {
            return NON_EXISTENCE_CACHE_FOLDER_VALUE;
        }
        int temp = 0;
        percent = 0;
        File files[] = folder.listFiles();
        int length = files.length;
        String thumbDirectory;
        DataSource dataSource = params[0].getDataSource();
        Set<String> updateSet = new HashSet<>();
        int filesChecked = 0;

        if (length == 0) {
            dataSource.updateSet(updateSet);
            return NO_ITEM_VALUE;
        }

        for (File file : files) {
//            Log.e(getClass().getSimpleName(), file.getName());
            filesChecked++;
            String name = file.getName();

            if (name.contains(".webp") && name.charAt(1) == '_' && !name.contains("temp")) {
                updateSet.add(file.getAbsolutePath());
                if (!dataSource.contain(file.getAbsolutePath())) {
                    thumbDirectory = baseThumbDir + name;
                    dataSource.update(new StickerItem(
                            file.getAbsolutePath(),
                            Loader.generateThumbnail(file.getAbsolutePath(), thumbDirectory),
                            StickerItem.IN_PHONE,
                            false,
                            true));
                }
                foundedStickersCount++;
                percent = (100 * filesChecked) / length;
//                Log.e(getClass().getSimpleName(), String.valueOf(percent));
                if (temp == percent) {
                    temp++;
                    publishProgress(percent, foundedStickersCount);
                }
            }
        }
        Log.e(getClass().getSimpleName(), "updateSet size" + updateSet.size());
        dataSource.updateSet(updateSet);
        if (foundedStickersCount == 0) {
            return NO_ITEM_VALUE;
        }

//        dataSource.updateSet(updateSet);
//        Log.e(getClass().getSimpleName(), "all the way through");
        return SUCCESS_VALUE;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (context != null)
//            Log.e(getClass().getSimpleName(), "was updated");
            listener.onTaskUpdateListener(values[0], values[1]);
    }

    @Override
    protected void onPostExecute(Integer aVoid) {
        if (aVoid.equals(NON_EXISTENCE_CACHE_FOLDER_VALUE))
            listener.onNoCashDirectoryListener();
        else if (aVoid.equals(NO_ITEM_VALUE))
            listener.onNoStickerWereFoundListener();
        else if (aVoid.equals(SUCCESS_VALUE))
            listener.onTaskFinishedListener();
    }


    public void detach() {
        context = null;
    }

    public interface AsyncPhoneTaskListener {
        void onTaskStartListener();

        void onTaskUpdateListener(int percent, int stickerCount);

        void onTaskFinishedListener();

        void onNoCashDirectoryListener();

        void onNoStickerWereFoundListener();
    }
}
