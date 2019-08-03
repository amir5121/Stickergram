package com.amir.stickergram.sticker.icon.user;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.R;
import com.amir.stickergram.UserStickersActivity;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.sticker.icon.IconItem;
import com.amir.stickergram.sticker.icon.OnIconSelectedListener;

public class UserIconListFragment extends BaseFragment implements IconAdapter.OnStickerClickListener {
    private RecyclerView recyclerView;
    private OnIconSelectedListener mCallback;


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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_user_sticker_icon, container, false);
        setFont((ViewGroup) view);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_template_list_list);
        if (recyclerView != null) {
            IconAdapter adapter = new IconAdapter((BaseActivity) getActivity(), this);

            if (BaseActivity.isInLandscape
                    || BaseActivity.isTablet)
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            else recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recyclerView.setAdapter(adapter);
        }

        return view;
    }


    @Override
    public void OnIconClicked(IconItem item) {//coming from the adapter
        mCallback.OnIconSelected(item);
    }

    @Override
    public void OnIconLongClicked(IconItem item) {
//        Loader.removeDirectory(new File(BaseActivity.USER_STICKERS_DIRECTORY + item.getFolder() + File.separator));
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

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}