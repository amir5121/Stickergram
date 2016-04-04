package com.amir.telegramstickerbuilder.infrastructure;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.amir.telegramstickerbuilder.PhoneStickersActivity;
import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.sticker.single.SingleStickersAdapter;
import com.amir.telegramstickerbuilder.sticker.single.StickerItem;

import java.io.File;

public class AsyncTaskPhoneAdapter extends AsyncTask<SingleStickersAdapter, Integer, Void> {
    BaseActivity activity;
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
        baseThumbDir = activity.getCacheDir().getAbsolutePath() + File.separator + "phone_";//todo: use externalCashDirectory
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
//        super.onPreExecute();
        listener.onTaskStartListener();
    }

    @Override
    protected Void doInBackground(SingleStickersAdapter... params) {
        //Todo: what happen if the directory doesn't exist
        File folder = new File(PhoneStickersActivity.PHONE_STICKERS_DIRECTORY);
        if (!folder.exists()){
            Toast.makeText(activity, PhoneStickersActivity.PHONE_STICKERS_DIRECTORY, Toast.LENGTH_LONG).show();
            return null;
        }
        File files[] = folder.listFiles();
        int length = files.length;
        String thumbDirectory;
        DataSource dataSource = params[0].getDataSource();

        for (int i = 0; i < length; i++) {
            String name = files[i].getName();
            if (!dataSource.contain(files[i].getAbsolutePath()))//TODO: what if the file was deleted from the memory and it's address was still in the sharedPreferences
                if (name.contains(".webp") && name.charAt(1) == '_') {
                    thumbDirectory = baseThumbDir + name;
                    dataSource.update(new StickerItem(
                            files[i].getAbsolutePath(),
                            Loader.generateThumbnail(files[i].getAbsolutePath(), thumbDirectory),
                            StickerItem.IN_PHONE,
                            false,
                            true));
                    foundedStickersCount++;
                }
            percent = (100 * i) / length;
            publishProgress(percent, foundedStickersCount);
        }

        if (foundedStickersCount == 0)
            listener.onTaskDismissedListener();

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (activity != null)
            listener.onTaskUpdateListener(values[0], values[1]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (activity != null)
            listener.onTaskFinishedListener();
    }

    public void detach() {
        activity = null;
    }

    public interface AsyncPhoneTaskListener {
        void onTaskStartListener();

        void onTaskDismissedListener();

        void onTaskUpdateListener(int percent, int stickerCount);

        void onTaskFinishedListener();
    }
}
