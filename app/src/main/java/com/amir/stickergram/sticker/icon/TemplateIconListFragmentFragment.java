package com.amir.stickergram.sticker.icon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.MainActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TemplateIconListFragmentFragment extends IconBaseFragment {
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_icon_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_template_list_list);
        if (recyclerView != null) {
            adapter = new IconAdapter((BaseActivity) getActivity(), this, IconItem.TYPE_ASSET) {
                @Override
                public List<String> getItems() throws IOException {
                    return Arrays.asList(getActivity().getAssets().list("Stickers"));
                }
            };
            if (BaseActivity.isInLandscape || MainActivity.isTablet)
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            else recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void OnNoItemWereFoundListener() {
        //intentionally empty
    }
}