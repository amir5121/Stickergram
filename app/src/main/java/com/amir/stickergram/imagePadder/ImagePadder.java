package com.amir.stickergram.imagePadder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;

public class ImagePadder extends AsyncTask<Integer, Void, Void> {

    private static int[] pixels;
    private static boolean[] checked;
    private static int runningThreads = 0;

    private int paddingWidth;
    private int color;
    private int width;
    private int height;
    //    private Canvas canvas;
    private Bitmap paddedImage;
    private ImagePadderCallBacks listener;
    private Bitmap mainImage;

    ImagePadder(int color, int paddingWidth, Bitmap image, ImagePadderCallBacks listener) {
        mainImage = image;
        this.listener = listener;
        this.color = color;
        this.paddingWidth = paddingWidth;
//        if (ImagePadder.color == 0) {
//        }
        paddedImage = Bitmap.createBitmap(image.getWidth() + paddingWidth * 2, image.getHeight() + paddingWidth * 2, Bitmap.Config.ARGB_8888);
        width = paddedImage.getWidth();
        height = paddedImage.getHeight();

        if (ImagePadder.pixels == null) {
            Canvas canvas = new Canvas(paddedImage);
            canvas.drawBitmap(image, paddingWidth, paddingWidth, null);
            pixels = new int[paddedImage.getWidth() * paddedImage.getHeight()];
            checked = new boolean[paddedImage.getWidth() * paddedImage.getHeight()];
            paddedImage.getPixels(pixels, 0, paddedImage.getWidth(), 0, 0, paddedImage.getWidth() - 1, paddedImage.getHeight() - 1);
        }
    }

    @Override
    protected void onPreExecute() {
//        Log.e(getClass().getSimpleName(), "preExec was called");
        super.onPreExecute();
        runningThreads++;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
//        addPaddingToImage(integers[0]);

        int tempOuter, tempCenter, i, j, y, x;

//        Log.e(getClass().getSimpleName(), "paddingWidth: " + paddingWidth + " size: " + pixels.length + " width: " + paddedImage.getWidth() + " height: " + paddedImage.getHeight() + " isVisibleAlpha: " + isVisibleAlpha(-1));
//        Log.e(getClass().getSimpleName(), " start: " + integers[0]);

//        for ( i = 0; i < pixels.length; i++) {
//            pixels[i] = -1;
//        }


        for (x = 0; x < width - paddingWidth; x++) {
            if (x >= integers[0] && x < integers[0] + 100)
                for (y = 0; y < height - paddingWidth; y++) {
                    tempCenter = x + y * width;
                    if (!checked[tempCenter]) {
//                    checked[tempCenter] = true;
                        for (i = -paddingWidth; i <= paddingWidth; i++) {
                            for (j = -paddingWidth; j <= paddingWidth; j++) {
                                tempOuter = (x + i) + (y + j) * width;
                                if (tempCenter > 0 && tempOuter > 0 && isVisibleAlpha(pixels[tempOuter]) != isVisibleAlpha(pixels[tempCenter])) {
//                                if (x % 50 == 0)
//                                    Log.e(getClass().getSimpleName(), "x: " + x + " y: " + y + " paddingWidth: " + paddingWidth + " color: " + color + " tempCenter: " + tempCenter + " tempOuter: " + tempOuter);
                                    if (!isVisibleAlpha(pixels[tempOuter])) {
                                        checked[tempCenter] = true;
//                                    pixels[tempCenter] = color;
                                        break;
                                    } else {
                                        checked[tempOuter] = true;
//                                    pixels[tempOuter] = color;
                                    }
                                }
                            }
                        }
                    }
                }
        }

        Log.e(getClass().getSimpleName(), "called with: " + integers[0] + " runningThreads: " + runningThreads);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        runningThreads--;
//        Log.e(getClass().getSimpleName(), " postExec: " + runningThreads);
        if (runningThreads == 0) {
            Log.e(getClass().getSimpleName(), "all thread finished");

            for (int i = 0; i < pixels.length; i++) {
                if (checked[i])
                    pixels[i] = color;
            }
            paddedImage.setPixels(pixels, 0, paddedImage.getWidth(), 0, 0, paddedImage.getWidth() - 1, paddedImage.getHeight() - 1);
//            canvas.drawBitmap(mainImage, paddingWidth, paddingWidth, null);
//            int mainWidth = paddedImage.getWidth();
//            int mainHeight = paddedImage.getHeight();

            Canvas canvas = new Canvas(paddedImage);
            canvas.drawBitmap(mainImage, paddingWidth, paddingWidth, null);

            Bitmap resBitmap;
            if (width != 512 && height != 512) {
                float scale;
                if (height > width) {
                    scale = 512f / height;
                    resBitmap = Bitmap.createScaledBitmap(paddedImage, (int) (width * scale), 512, false);
                } else {
                    scale = 512f / width;

                    resBitmap = Bitmap.createScaledBitmap(paddedImage, 512, (int) (height * scale), false);
                }
            } else {
                resBitmap = paddedImage;
            }

//            listener.paddingFinished(paddedImage);
            listener.paddingFinished(resBitmap);

            pixels = null;
            checked = null;
        }
    }

    private static boolean isVisibleAlpha(int pixel) {

        return Color.alpha(pixel) < 10;
    }

    public static int getX(int width, int i) {
        return i - width * (i / width);
    }

    public static int getY(int width, int i) {
        return i / width;
    }

    interface ImagePadderCallBacks {
        void paddingFinished(Bitmap finishedBitmap);
    }

}
