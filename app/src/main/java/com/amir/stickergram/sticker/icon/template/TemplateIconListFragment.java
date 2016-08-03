package com.amir.stickergram.sticker.icon.template;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.sticker.OnRefreshCallbacks;
import com.amir.stickergram.sticker.icon.IconItem;
import com.amir.stickergram.sticker.icon.OnIconSelectedListener;

public class TemplateIconListFragment extends BaseFragment
        implements OnIconSelectedListener, SwipeRefreshLayout.OnRefreshListener, OnRefreshCallbacks {
    private OnIconSelectedListener mCallback;
    private SwipeRefreshLayout refreshLayout;
    private View noItemFoundText;
    private IconAdapter adapter;

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

        View view = inflater.inflate(R.layout.fragment_template_sticker_icon, container, false);
        setFont((ViewGroup) view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_icon_list_swipeRefresh);
        refreshLayout.setOnRefreshListener(this);
        noItemFoundText = view.findViewById(R.id.fragment_icon_list_no_item_found_text);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_template_list_list);
        if (recyclerView != null) {
            adapter = new IconAdapter((BaseActivity) getActivity(), this);

            if (BaseActivity.isInLandscape || BaseActivity.isTablet)
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            else recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recyclerView.setAdapter(adapter);
        }
        Log.e(getClass().getSimpleName(), "onCreateView temFragment");

        return view;
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        adapter.updateItems(true);
    }

    @Override
    public void OnIconSelected(IconItem item) {
        mCallback.OnIconSelected(item);
    }

    @Override
    public void OnNoStickerWereFoundListener() {
        noItemFoundText.setVisibility(View.VISIBLE);
        if (BaseActivity.isInLandscape || BaseActivity.isTablet)
            noItemFoundText.setRotation(90);
    }

    @Override
    public void OnRefreshFinished(boolean failed) {
        noItemFoundText.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
        if (failed) {
            Toast.makeText(getContext(), getString(R.string.failed_to_refresh), Toast.LENGTH_LONG).show();
        }
    }
}