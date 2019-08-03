package com.amir.stickergram.image;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.sticker.icon.template.TemplateIconListFragment;
import com.amir.stickergram.sticker.pack.template.TemplateIconPackDetailedFragment;

public class TemplateStickersFragment extends BaseFragment {

    private static final String ICONS_FRAGMENT = "ICONS_FRAGMENT";
    private static final String DETAILED_ICON_FRAGMENT = "DETAILED_ICON_FRAGMENT";
    private static final String TAG = "TemplateStickersFragmen";
    private View view;
    private boolean isImagePicker;

    public static TemplateStickersFragment newInstance(boolean isImagepicker) {

        Bundle args = new Bundle();

        args.putBoolean(Constants.IS_AN_IMAGE_PICKER, isImagepicker);

        TemplateStickersFragment fragment = new TemplateStickersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            isImagePicker = args.getBoolean(Constants.IS_AN_IMAGE_PICKER, false);
        }

        if (view == null)
            view = inflater.inflate(R.layout.fragment_template_stickers, container, false);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (view.findViewById(R.id.fragment_template_sticker_fragment_container) != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_template_sticker_fragment_container, new TemplateIconListFragment(), ICONS_FRAGMENT)
                    .commit();

            getChildFragmentManager().executePendingTransactions();
        }

    }

    public void instantiateFragment(String name, String enName) {
        if (view != null) {

            if (view.findViewById(R.id.fragment_template_sticker_fragment_container) != null) {
                TemplateIconPackDetailedFragment templateIconPackDetailedFragment = TemplateIconPackDetailedFragment.newInstance(isImagePicker);

                while (getChildFragmentManager().getBackStackEntryCount() >= 1)
                    getChildFragmentManager().popBackStackImmediate();


                getChildFragmentManager().
                        beginTransaction().
                        replace(R.id.fragment_template_sticker_fragment_container, templateIconPackDetailedFragment, DETAILED_ICON_FRAGMENT).
                        addToBackStack(null).
                        commit();

                templateIconPackDetailedFragment.refresh(name, enName);
            } else {
                Log.e(TAG, "instantiateFragment: ERROR");
            }
        } else {
            Log.e(TAG, "instantiateFragment: view was null");
        }
    }
}
