package com.amir.stickergram.infrastructure;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.sticker.single.SingleStickersAdapter;
import com.amir.stickergram.sticker.single.StickerItem;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class AsyncTaskPhoneAdapter extends AsyncTask<SingleStickersAdapter, Integer, Integer> {
    private static final Integer CACHE_DIRECTORY_DID_NOT_EXIST = -1;
    private static final Integer ITEMS_WERE_ADDED = 0;
    private static final Integer NO_ITEM_IN_CACHE_DIRECTORY = 1;
    private static final Integer NEED_PERMISSION = 2;
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
            baseThumbDir = activity.getExternalCacheDir().getAbsolutePath() + File.separator + "phone_" + BaseActivity.chosenMode.getPack();
        else
            baseThumbDir = activity.getCacheDir().getAbsolutePath() + File.separator + "phone_" + BaseActivity.chosenMode.getPack();
        this.context = activity;
    }

    @Override
    protected void onPreExecute() {
        listener.onTaskStartListener();
    }

    @Override
    protected synchronized Integer doInBackground(SingleStickersAdapter... params) {
        if (!Loader.checkPermission((BaseActivity) context))
            return NEED_PERMISSION;

        File folder = new File(Loader.getActiveStickerDir());
        if (!folder.exists()) {
            return CACHE_DIRECTORY_DID_NOT_EXIST;
        }
        int temp = 0;
        percent = 0;
        File files[] = folder.listFiles();
        int length = files.length;
        DataSource dataSource = params[0].getDataSource();
        Set<String> updateSet = new HashSet<>();
        int filesChecked = 0;
        if (length == 0) {
            dataSource.updateSet(updateSet);
            return NO_ITEM_IN_CACHE_DIRECTORY;
        }
        for (File file : files) {
//            Log.e(getClass().getSimpleName(), file.getName());
            filesChecked++;
            String name = file.getName();

            if (name.contains(".webp") && name.charAt(1) == '_' && !name.contains("temp") && file.exists()) {
                updateSet.add(file.getAbsolutePath());
                if (!dataSource.contain(file.getAbsolutePath())) {
                    String thumbDirectory = Loader.generateThumbnail(file.getAbsolutePath(), baseThumbDir + name);
                    if (thumbDirectory != null)
                        dataSource.update(new StickerItem(
                                file.getAbsolutePath(),
                                Loader.generateThumbnail(file.getAbsolutePath(), thumbDirectory),
                                StickerItem.IN_PHONE,
                                false,
                                true));
                }
                foundedStickersCount++;

                /*
                todo: concurrentModificationException on Nexus 5 take a look
                todo: it might be because of to many calls to shared preferences do it all at once ...
                todo: gather them up and write another method to do it all at once not requiring too many applies
                */
            }
            percent = (100 * filesChecked) / length;
            if (temp == percent) {
//                Log.e(getClass().getSimpleName(), String.valueOf(percent));
                temp++;
                publishProgress(percent, foundedStickersCount);
            }
        }
        Log.e(getClass().getSimpleName(), "updateSet size " + updateSet.size());
        dataSource.updateSet(updateSet);
        if (foundedStickersCount == 0) {
            return NO_ITEM_IN_CACHE_DIRECTORY;
        }

//        dataSource.updateSet(updateSet);
//        Log.e(getClass().getSimpleName(), "all the way through");
        return ITEMS_WERE_ADDED;

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (context != null)
//            Log.e(getClass().getSimpleName(), "was updated");
            listener.onTaskUpdateListener(values[0], values[1]);
    }

    @Override
    protected void onPostExecute(Integer aVoid) {
        if (aVoid.equals(CACHE_DIRECTORY_DID_NOT_EXIST))
            listener.onNoCashDirectoryListener();
        else if (aVoid.equals(NO_ITEM_IN_CACHE_DIRECTORY))
            listener.onNoStickerWereFoundListener();
        else if (aVoid.equals(ITEMS_WERE_ADDED))
            listener.onTaskFinishedListener();
        else if (aVoid.equals(NEED_PERMISSION))
            listener.onRequestReadWritePermission();
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

        void onRequestReadWritePermission();
    }
}
