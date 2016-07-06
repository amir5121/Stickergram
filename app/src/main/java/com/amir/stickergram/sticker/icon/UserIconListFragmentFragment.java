package com.amir.stickergram.sticker.icon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.R;
import com.amir.stickergram.UserStickersActivity;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserIconListFragmentFragment extends IconBaseFragment {
    RecyclerView recyclerView;
    int length;
    //    View noStickerText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_icon_list, container, false);
//        noStickerText = view.findViewById(R.id.fragment_icon_list_no_sticker_text);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_template_list_list);
        if (recyclerView != null) {
            adapter = new IconAdapter((BaseActivity) getActivity(), this, IconItem.TYPE_USER) {
                @Override
                public List<String> getItems() throws IOException {
                    File file = new File(BaseActivity.USER_STICKERS_DIRECTORY);
                    if (!file.exists())
                        if (Loader.checkPermission((BaseActivity) getActivity()))
                            file.mkdirs();
                        else {
                            Loader.gainPermission((BaseActivity) getActivity(), 0);
                            getActivity().finish();
                        }
                    File[] files = file.listFiles();
                    if (files == null) {
                        Log.e(getClass().getSimpleName(), "files were null");
                        getActivity().finish();
                        return null;
                    }
                    length = files.length;
                    List<String> directories = new ArrayList<>();
                    for (int i = 0; i < length; i++) {
                        if (!files[i].isFile()) {
                            directories.add(files[i].getAbsolutePath());
                        }
                    }
                    return directories;
                }
            };
            if (BaseActivity.isInLandscape
                    || BaseActivity.isTablet)
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            else recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void OnNoItemWereFoundListener() {
        if (getActivity() instanceof UserStickersActivity) { //cause this fragment is also used in saveStickerActivity
            mCallback.OnNoStickerWereFoundListener();
        }
    }

    public void updateAdapterForSavingActivity() {
        if (BaseActivity.isTablet)
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6));
    }

}