package com.amir.telegramstickerbuilder.sticker.pack;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.Loader;
import com.amir.telegramstickerbuilder.sticker.icon.IconItem;
import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseFragment;

import java.io.IOException;

public class TemplateIconPackDetailedFragment extends BaseFragment implements IconPackAdapter.OnStickerClickListener {
    RecyclerView recyclerView;
    IconPackAdapter adapter;
    View view;
    String folder;
    ProgressBar progressBar;
    TextView folderText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_icon_detailed, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.template_sticker_icon_detailed_list);
        folderText = (TextView) view.findViewById(R.id.fragment_icon_detailed_text_folder);
        refresh(folder); // this guy sets the adapter
        return view;
    }

    @Override
    public void OnIconClicked(PackItem item) {
        Loader.loadStickerDialog(item, (BaseActivity) getActivity());
    }

    @Override
    public void OnLongClicked(PackItem item) {
        Log.e(getClass().getSimpleName(), "item was long clicked");
    }

    public void refresh(String folder) {
        this.folder = folder;

        if (folderText != null) {
            folderText.setText(folder);
            Log.e(getClass().getSimpleName(), "Folder was: " + folder);
        } else Log.e(getClass().getSimpleName(), "folderText was null");
        if (folder == null) return;
        if (recyclerView != null) {
            adapter = new IconPackAdapter(this, this, folder, PackItem.TYPE_TEMPLATE);
            if (BaseActivity.isTablet || BaseActivity.isInLandscape)
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
            else
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

            recyclerView.setAdapter(adapter);
            setRetainInstance(false);
            new AsyncTaskPackAdapter().execute();

        }
    }

    public class AsyncTaskPackAdapter extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) view.findViewById(R.id.fragment_icon_detailed_progressBar);
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... folders) {
            adapter.refresh();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            if (recyclerView != null)
                recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyItemRange();
        }
    }
}
