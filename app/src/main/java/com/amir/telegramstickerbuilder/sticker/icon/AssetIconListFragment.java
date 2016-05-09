package com.amir.telegramstickerbuilder.sticker.icon;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.telegramstickerbuilder.MainActivity;
import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseActivity;

import java.io.IOException;

public class AssetIconListFragment extends IconListFragment implements IconAdapter.OnStickerClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_icon_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_template_list_list);
        if (recyclerView != null) {
            adapter = new IconAdapter((BaseActivity) getActivity(), this, IconItem.TYPE_ASSET) {
                @Override
                public String[] getItems() throws IOException {
                    return getActivity().getAssets().list("Stickers");
                }
            };
            if (BaseActivity.isInLandscape || MainActivity.isTablet)
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            else recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

}