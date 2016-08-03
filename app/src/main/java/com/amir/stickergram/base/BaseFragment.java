package com.amir.stickergram.base;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;

public class BaseFragment extends Fragment {


    public void setFont(ViewGroup group) {
        if (group != null) {
            int count = group.getChildCount();
            View v;
            for (int i = 0; i < count; i++) {
                v = group.getChildAt(i);
                if (v instanceof TextView) {
                    if (Loader.deviceLanguageIsPersian())
                        ((TextView) v).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Constants.APPLICATION_PERSIAN_FONT_ADDRESS_IN_ASSET));
                    else
                        ((TextView) v).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Constants.APPLICATION_ENGLISH_FONT_ADDRESS_IN_ASSET));
//                    ((TextView) v).setTypeface(Typeface.createFromAsset(getAssets(), "Exo2.otf"));
                } else if (v instanceof ViewGroup)
                    setFont((ViewGroup) v);
            }
        } else {
            Log.e(getClass().getSimpleName(), "viewGroup was null");
        }
    }
}
