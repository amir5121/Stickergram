package com.amir.telegramstickerbuilder;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.amir.telegramstickerbuilder.Icon.IconItem;
import com.amir.telegramstickerbuilder.Icon.IconListFragment;
import com.amir.telegramstickerbuilder.StickerPack.IconPackDetailedFragment;
import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.views.MainNavDrawer;

public class TemplateStickersActivity extends BaseActivity implements IconListFragment.OnIconSelectedListener {
    public static final String ICON_STICKER_ITEM_FOLDER = "ICON_STICKER_ITEM_FOLDER";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_stickers);
        setNavDrawer(new MainNavDrawer(this));

        if (findViewById(R.id.activity_template_sticker_fragment_container) != null) {
            if (savedInstanceState != null){
                return;
            }

            IconListFragment templateIconListFragment = new IconListFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_template_sticker_fragment_container, templateIconListFragment)
                    .commit();
            // new AsyncGen().execute();
        }
    }

    @Override
    public void OnStickerSelectedListener(IconItem item) {
        IconPackDetailedFragment iconStickerDetailedFragment;
        if (findViewById(R.id.activity_template_sticker_fragment_container) != null) {
            iconStickerDetailedFragment = new IconPackDetailedFragment();

            Bundle bundle = new Bundle();
            bundle.putString(ICON_STICKER_ITEM_FOLDER, item.getFolder());
            iconStickerDetailedFragment.setArguments(bundle);

            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.activity_template_sticker_fragment_container, iconStickerDetailedFragment).
                    addToBackStack(null).
                    commit();

            iconStickerDetailedFragment.refresh(item.getFolder());
        } else {
            iconStickerDetailedFragment =
                    (IconPackDetailedFragment) getSupportFragmentManager().findFragmentById(R.id.activity_template_stickers_detailed_fragment);

            iconStickerDetailedFragment.refresh(item.getFolder());
        }

    }

//    public class AsyncGen extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                int width;
//                int height;
//                String folders[] = getAssets().list("");
//                Log.e(getClass().getSimpleName(), String.valueOf(folders.length));
//                for (String folder : folders) {
//                    Log.e(getClass().getSimpleName(), folder);
//                    String files[] = getAssets().list(folder);
//                    for (String file : files) {
//                        String fileName = folder + File.separator + file;
//                        Log.e(getClass().getSimpleName(), fileName);
//                        InputStream stream = getAssets().open(fileName);
//                        Bitmap bitmap = BitmapFactory.decodeStream(stream);
//                        stream.close();
//                        if (bitmap != null) {
//                            width = bitmap.getWidth();
//                            height = bitmap.getHeight();
//                            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height);
//                            File file1 = new File(Environment.getExternalStorageDirectory() + File.separator + "TSB" + File.separator + folder + File.separator + "thumb" + file);
//                            if (!file1.getParentFile().exists())
//                                file1.getParentFile().mkdirs();
//
//                            if (!file1.exists()) {
//                                file1.createNewFile();
//                            }
//
//                            OutputStream outputStream = new FileOutputStream(file1);
//                            bitmap.compress(Bitmap.CompressFormat.WEBP, 60, outputStream);
//                            outputStream.close();
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }
}
