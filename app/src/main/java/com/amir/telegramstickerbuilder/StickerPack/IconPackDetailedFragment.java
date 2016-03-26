package com.amir.telegramstickerbuilder.StickerPack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.telegramstickerbuilder.Icon.IconItem;
import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseFragment;

public class IconPackDetailedFragment extends BaseFragment implements IconPackAdapter.OnStickerClickListener {
    RecyclerView recyclerView;
    IconPackAdapter adapter;
    View view;
    String folder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_icon_detailed, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.template_sticker_icon_detailed_list);


        refresh(folder);
        return view;
    }

    @Override
    public void OnIconClicked(IconItem item) {

    }

    public void refresh(String folder) {
        this.folder = folder;
        if (recyclerView != null) {
            adapter = new IconPackAdapter(this, this, folder);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recyclerView.setAdapter(adapter);
            adapter.refresh();
        }
    }
}
