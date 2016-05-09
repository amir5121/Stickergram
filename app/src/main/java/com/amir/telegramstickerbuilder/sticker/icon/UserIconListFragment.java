package com.amir.telegramstickerbuilder.sticker.icon;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.telegramstickerbuilder.MainActivity;
import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseActivity;

import java.io.File;
import java.io.IOException;

public class UserIconListFragment extends IconListFragment {
    int length;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_icon_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_template_list_list);
        if (recyclerView != null) {
            adapter = new IconAdapter((BaseActivity) getActivity(), this, IconItem.TYPE_USER) {
                @Override
                public String[] getItems() throws IOException {
                    File file = new File(BaseActivity.USER_STICKERS_DIRECTORY);
                    if (!file.exists())
                        file.mkdirs();
                    //todo: gain permission cause you are making folder... probably a better idea to create the folder when the user hit create pack button
                    File[] files = file.listFiles();
                    if (files == null) {
                        Log.e(getClass().getSimpleName(), "files were null");
                        getActivity().finish();
                        return null;
                    }
                    length = files.length;
                    String[] directories = new String[length];
                    for (int i = 0; i < length; i++) {
                        directories[i] = files[i].getAbsolutePath();
                        Log.e(getClass().getSimpleName(), "----" + directories[i]);
                    }
                    return directories;
                }
            };
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                    || MainActivity.isTablet)
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            else recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

}