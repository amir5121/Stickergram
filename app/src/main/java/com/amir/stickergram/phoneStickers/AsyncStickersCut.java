package com.amir.stickergram.phoneStickers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.sticker.single.StickerItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.List;

public class AsyncStickersCut extends AsyncTask<Void, Void, Void> {

    private final List<StickerItem> sourceItems;
    private final AsyncCutCallbacks listener;
    private final Context context;
    private String destinyFolder;

    public AsyncStickersCut(Context context, List<StickerItem> sourceItems, String destinyFolder, AsyncCutCallbacks listener) {
        this.sourceItems = sourceItems;
        this.destinyFolder = destinyFolder;
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onCutStarted();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        int size = sourceItems.size();
        int numberOfExistingStickers = numberOfExistingStickersInDestiny(destinyFolder);
        StringBuilder sb = new StringBuilder();
        String separator = "%";
        for (int i = 0; i < size; i++) {
//            Log.e(getClass().getSimpleName(), "folder: " + destinyFolder + " name: " + sourceItems.get(i).getName());
            Uri uri = sourceItems.get(i).getUri();
            String destiny = getDestinySticker(destinyFolder, i + numberOfExistingStickers);
            sb
                    .append(sourceItems.get(i).getName())
                    .append(separator)
                    .append(i + numberOfExistingStickers)
                    .append(separator)
                    .append(Loader.INSTANCE.isPersian(destinyFolder) ? destinyFolder : destinyFolder)
//                    .append((i != size - 1) ? "\n" : "");
                    .append("\n");
//            Log.e(getClass().getSimpleName(), " destinyDir: " + destiny);
            try {
                File dest = new File(destiny);
                if (!dest.exists()) dest.createNewFile();
                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
//                Log.e(getClass().getSimpleName(), "sourceUri: " + uri + " destinyDir: " + destiny);
                OutputStream os = new FileOutputStream(destiny);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 80, os);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            moveFile(uri, destiny);


            String sourceThumbDir = sourceItems.get(i).getThumbDirectory();
            String destinyThumbDir = getDestinyThumb(destinyFolder, i + numberOfExistingStickers);

            Log.e(getClass().getSimpleName(), " sourceThumbDir: " + sourceThumbDir + " destinyThumbDir: " + destinyThumbDir);

            try {
                Log.e(getClass().getSimpleName(), "copyRes: " + Loader.INSTANCE.copyFile(new File(sourceThumbDir), new File(destinyThumbDir)));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        writeToFile(sb.toString());
//        Log.e(getClass().getSimpleName(), sb.toString());
        return null;
    }

    private int numberOfExistingStickersInDestiny(String destinyFolder) {
        File[] files = new File(Constants.BASE_PHONE_ORGANIZED_STICKERS_DIRECTORY + File.separator + destinyFolder + "/").listFiles();
        if (files != null)
            return files.length;
        return 0;
    }

    private String getDestinyThumb(String destinyFolder, int i) {
        return BaseActivity.Companion.getBASE_PHONE_ORGANIZED_THUMBNAIL_DIRECTORY() + File.separator + destinyFolder + "_" + i + Constants.PNG;
    }

    private String getDestinySticker(String destinyFolder, int i) {
        return Constants.BASE_PHONE_ORGANIZED_STICKERS_DIRECTORY + destinyFolder + "/" + i + Constants.PNG;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onCutFinished();
    }

    private void writeToFile(String data) {
        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            File mFile = new File(Constants.MOVED_STICKERS_INFO);
            if (!mFile.exists())
                mFile.createNewFile();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(mFile, true));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


//    private void moveFile(String inputFilePath, String outputFilePath) {
//
//        try {
//
//            //create output directory if it doesn't exist
//            File dir = new File(outputFilePath);
//            if (!dir.exists()) {
//                dir.createNewFile();
//            }
////
//
//            InputStream in = new FileInputStream(inputFilePath);
//            OutputStream out = new FileOutputStream(outputFilePath);
//
//            byte[] buffer = new byte[1024];
//            int read;
//            while ((read = in.read(buffer)) != -1) {
//                out.write(buffer, 0, read);
//            }
//            in.close();
//
//            // write the output file
//            out.flush();
//            out.close();
//
//            // delete the original file
//            new File(inputFilePath).delete();
//
//
//        } catch (Exception e) {
//            Log.e("tag", e.getMessage());
//        }
//
//    }

    public interface AsyncCutCallbacks {
        void onCutStarted();

        void onCutFinished();
    }
}
