package com.amir.stickergram.image;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.Log;

import com.amir.stickergram.R;
import com.amir.stickergram.phoneStickers.organizedIcon.OrganizedStickersIconFragment;
import com.amir.stickergram.phoneStickers.unorganized.PhoneStickersUnorganizedFragment;

public class ImagePickerAdapter extends FragmentPagerAdapter {

    private static final int PAGES_NUM = 3;
    private static final String TAG = "ImagePickerAdapter";
    public static final int SERVER_STICKERS = 0;
    public static final int ORGANIZED_STICKER = 2;
    public static final int UNORGANIZED_STICKER = 1;
    private TemplateStickersFragment templateStickersFragment;
    private Context context;

    ImagePickerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case SERVER_STICKERS:
                if (templateStickersFragment == null)
                    templateStickersFragment = TemplateStickersFragment.newInstance(true);
                return templateStickersFragment;
            case ORGANIZED_STICKER: // Fragment # 0 - This will show FirstFragment
                return OrganizedStickersIconFragment.newInstance(true);
            case UNORGANIZED_STICKER:
                return PhoneStickersUnorganizedFragment.newInstance(true);
            default:
                Log.e(getClass().getSimpleName(), "case Default is happening... MUST NOT");
                throw new RuntimeException("asking for none existing fragment in " + getClass().getSimpleName());
        }
    }

    @Override
    public int getCount() {
        return PAGES_NUM;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case SERVER_STICKERS:
                return context.getString(R.string.template_stickers);
            case ORGANIZED_STICKER:
                return context.getString(R.string.organized_telegram_stickers);
            case UNORGANIZED_STICKER:
                return context.getString(R.string.telegram_sticker);
        }
        return context.getString(R.string.error);
    }
}
