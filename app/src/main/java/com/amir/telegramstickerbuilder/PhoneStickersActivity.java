package com.amir.telegramstickerbuilder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.Loader;
import com.amir.telegramstickerbuilder.Single.StickerItem;
import com.amir.telegramstickerbuilder.views.MainNavDrawer;
import com.amir.telegramstickerbuilder.Single.SingleStickersAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhoneStickersActivity extends BaseActivity implements SingleStickersAdapter.OnStickerClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String PHONE_STICKERS_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + "org.telegram.messenger" + File.separator + "cache" + File.separator;
    private static final int THUMBNAIL_IMAGE_QUALITY = 85;

    View progressFrame;
    TextView progressTextPercentage;
    TextView progressFilesCount;
    RecyclerView recyclerView;
    SingleStickersAdapter adapter;
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_stickers);
        setNavDrawer(new MainNavDrawer(this));

        progressFrame = findViewById(R.id.progress_bar);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.activity_phone_stickers_swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.activity_users_stickers_list);
        progressTextPercentage = (TextView) findViewById(R.id.activity_phone_stickers_progressPercentage);
        progressFilesCount = (TextView) findViewById(R.id.activity_phone_stickers_progress_files_count);

        Log.e(getClass().getSimpleName(), "onCreate was called");

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this);
            swipeRefresh.setColorSchemeColors(
                    Color.parseColor("#FF00DDFF"),
                    Color.parseColor("#FF99CC00"),
                    Color.parseColor("#FFFFBB33"),
                    Color.parseColor("#FFFF4444"));
        }
        if (recyclerView != null) {
            adapter = new SingleStickersAdapter(this, this);
            recyclerView.setLayoutManager(new GridLayoutManager(PhoneStickersActivity.this, 3));
            recyclerView.setAdapter(adapter);
            Log.e(getClass().getSimpleName(), "adapter was set");
        }
        if (!Loader.hasLoadedOnce) {
            Log.e(getClass().getSimpleName(), "Loader was Called");
            new AsyncAdapter().execute();
        }
    }

    @Override
    public void OnStickerClicked(StickerItem item) {

    }

    @Override
    public void OnStickerLongClicked(StickerItem item) {

    }


    @Override
    public void onRefresh() {
        new AsyncAdapter().execute();

    }

    public class AsyncAdapter extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressFrame.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... params) {
//            Loader.loadPhoneStickers(adapter.getDataSource(), PhoneStickersActivity.this);
            File files[] = new File(PHONE_STICKERS_DIRECTORY).listFiles();
            int percent;

            int length = files.length;
            int matchesCount = 0;
            for (int i = 0; i < length; i++) {
                String name = files[i].getName();
                if (name.contains(".webp") && name.charAt(1) == '_') {
                    String thumbDirectory = PhoneStickersActivity.this.getCacheDir().getAbsolutePath() + File.separator + name;
                    StickerItem item = new StickerItem(
                            files[i].getAbsolutePath(),
                            generateThumbnail(files[i].getAbsolutePath(), thumbDirectory),
                            StickerItem.TYPE_IN_PHONE,
                            false,
                            true);
                    adapter.getDataSource().update(item);
                    matchesCount++;
                }
                percent = (100 * i) / length;
                publishProgress(length, percent, i, matchesCount);
            }
            Log.e("Loader", "hasLoadedOnce been set to true");

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressFilesCount.setText(String.valueOf(values[0]));
//            Log.e(getClass().getSimpleName(), String.valueOf(values[1]) + "%");
            progressTextPercentage.setText(String.valueOf(values[1]) + "%");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (swipeRefresh != null)
                swipeRefresh.setRefreshing(false);
            progressFrame.setVisibility(View.GONE);
            adapter.refresh();
        }
    }


    public static String generateThumbnail(String fromDirectory, String toDirectory) {
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fromDirectory), 100, 100);
        FileOutputStream outStream = null;

        try {
            outStream = new FileOutputStream(toDirectory);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.WEBP, THUMBNAIL_IMAGE_QUALITY, outStream);
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

}
