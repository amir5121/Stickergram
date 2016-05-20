package com.amir.stickergram.sticker.icon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.amir.stickergram.base.BaseFragment;

public abstract class IconListFragment extends BaseFragment implements IconAdapter.OnStickerClickListener {
    RecyclerView recyclerView;
    IconAdapter adapter;
    OnIconSelectedListener mCallback;

    @Override
    public void OnIconClicked(IconItem item) {
        ((OnIconSelectedListener) getActivity()).OnIconSelected(item);
    }

    public interface OnIconSelectedListener {
        void OnIconSelected(IconItem item);
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
}