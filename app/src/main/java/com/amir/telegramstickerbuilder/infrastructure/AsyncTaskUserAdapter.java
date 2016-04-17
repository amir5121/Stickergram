package com.amir.telegramstickerbuilder.infrastructure;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.amir.telegramstickerbuilder.PhoneStickersActivity;
import com.amir.telegramstickerbuilder.UserStickersActivity;
import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.sticker.single.SingleStickersAdapter;
import com.amir.telegramstickerbuilder.sticker.single.StickerItem;

import java.io.File;

public class AsyncTaskUserAdapter extends AsyncTask<SingleStickersAdapter, Integer, Void> {
    BaseActivity activity;
    AsyncUserTaskListener listener;
    String baseThumbDir;

    public AsyncTaskUserAdapter(BaseActivity activity) {
        attach(activity);
    }

    public void attach(BaseActivity activity) {

        try {
            listener = (AsyncUserTaskListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "Must implement AsyncPhoneTaskListener");
        }
        if (activity.getExternalCacheDir() != null)
            baseThumbDir = activity.getExternalCacheDir().getAbsolutePath() + File.separator + "user";
        else baseThumbDir = activity.getCacheDir().getAbsolutePath() + File.separator + "user";
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onTaskStartListener();
    }

    @Override
    protected Void doInBackground(SingleStickersAdapter... params) {
        File folder = new File(UserStickersActivity.USER_STICKERS_DIRECTORY);
        if (!folder.exists()) {
            listener.onTaskDismissedListener();
            return null;
        }
        File files[] = folder.listFiles();
        if (files.length == 0) {
            listener.onTaskDismissedListener();
            return null;
        }
        String thumbDirectory;
        DataSource dataSource = params[0].getDataSource();

        for (File file : files) {
            String name = file.getName();
            thumbDirectory = baseThumbDir + name;
            dataSource.update(new StickerItem(
                    file.getAbsolutePath(),
                    Loader.generateThumbnail(file.getAbsolutePath(), thumbDirectory),
                    StickerItem.USER_STICKER,
                    false,
                    true));
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        if (activity != null)
            listener.onTaskFinishedListener();
    }

    public void detach() {
        activity = null;
    }

    public interface AsyncUserTaskListener {
        void onTaskStartListener();

        void onTaskDismissedListener();

        void onTaskFinishedListener();
    }
}
