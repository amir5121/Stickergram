package com.amir.stickergram.sticker.icon;

import android.content.Context;

import com.amir.stickergram.base.BaseFragment;

public abstract class IconBaseFragment extends BaseFragment implements IconAdapter.OnStickerClickListener{
    OnIconSelectedListener mCallback;
    IconAdapter adapter;

    @Override
    public void OnIconClicked(IconItem item) {//coming from the adapter
        mCallback.OnIconSelected(item);
    }

    @Override
    public void OnNoItemWereFoundListener() {
        mCallback.OnNoStickerWereFoundListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnIconSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "Must implement OnIconSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface OnIconSelectedListener {// communicating with the activity
        void OnIconSelected(IconItem item);

        void OnNoStickerWereFoundListener();
    }
}
