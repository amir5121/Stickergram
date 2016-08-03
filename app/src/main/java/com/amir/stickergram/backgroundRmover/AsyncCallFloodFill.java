package com.amir.stickergram.backgroundRmover;

import android.os.AsyncTask;

public class AsyncCallFloodFill extends AsyncTask<Void, Void, Void> {
    private AsyncFloodFillCallbacks listener;
    //    private Bitmap bitmap;
    int[] pixels;
    private int top;
    private int left;
    private int tolerance;
    int width;
    int heigh;

    AsyncCallFloodFill(AsyncFloodFillCallbacks listener, int[] pixels, int left, int top, int tolerance, int width, int height) {
        this.listener = listener;
        this.width = width;
        this.heigh = height;
//        this.bitmap = bitmap;
        this.pixels = pixels;
        this.left = left;
        this.top = top;
        this.tolerance = tolerance;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onProgressStarted();
    }

    @Override
    protected Void doInBackground(Void... voids) {
//        QueueLinearFloodFiller floodFiller = new QueueLinearFloodFiller(bitmap.copy(Bitmap.Config.ARGB_8888, true));
        QueueLinearFloodFiller floodFiller = new QueueLinearFloodFiller(pixels, width, heigh);
        floodFiller.setTolerance(tolerance);
        floodFiller.floodFill(left, top);
//        bitmap = floodFiller.getImage();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.floodFillFinished(pixels);
    }

    interface AsyncFloodFillCallbacks {
        //        void floodFillFinished(Bitmap bitmap);
        void floodFillFinished(int[] pixels);

        void onProgressStarted();
    }
}
