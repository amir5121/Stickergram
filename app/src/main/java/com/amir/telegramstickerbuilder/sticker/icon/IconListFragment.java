package com.amir.telegramstickerbuilder.sticker.icon;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.base.BaseFragment;

public class IconListFragment extends BaseFragment implements IconAdapter.OnStickerClickListener {
    RecyclerView recyclerView;
    IconAdapter adapter;
    OnIconSelectedListener mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_icon_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_template_list_list);
        if (recyclerView != null) {
            adapter = new IconAdapter((BaseActivity)getActivity(),this);
            if (BaseActivity.isTablet)
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            else recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

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