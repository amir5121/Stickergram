package com.amir.stickergram.sticker.pack;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseFragment;

public class TemplateIconPackDetailedFragment extends BaseFragment implements DetailedPackAdapter.OnStickerClickListener {
    public static final String EXPLOSM = "Explosm";
    private static final String ROES_1 = "Roes1";
    public static final String HTTP_WWW_EXPLOSM_NET = "http://www.explosm.net";
    public static final String INSTAGRAM_COM_MR_KAZZEMI = "https://www.instagram.com/mr.kazzemi/";
    RecyclerView recyclerView;
    DetailedPackAdapter adapter;
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
        if (folderText != null) folderText.setVisibility(View.GONE);
        refresh(folder); // this guy sets the adapter
        if (folder != null)
            if (folder.equals(EXPLOSM)) {
                Button linkButton = (Button) view.findViewById(R.id.fragment_icon_detailed_link_button);
                if (linkButton != null) {
                    linkButton.setVisibility(View.VISIBLE);
                    linkButton.setText(getString(R.string.explosm_net));
                    linkButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = HTTP_WWW_EXPLOSM_NET;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    });
                }
            } else if (folder.equals(ROES_1)) {
                Button linkButton = (Button) view.findViewById(R.id.fragment_icon_detailed_link_button);
                if (linkButton != null) {
                    linkButton.setVisibility(View.VISIBLE);
                    linkButton.setText(getString(R.string.contact_painter));
                    linkButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = INSTAGRAM_COM_MR_KAZZEMI;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    });
                }
            }
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

    @Override
    public void folderDeleted() {
        //empty
        //will never be called in this fragment
    }

    public void refresh(String folder) {
        this.folder = folder;

        if (folderText != null && folder != null) {
            folderText.setText(folder);
            folderText.setVisibility(View.VISIBLE);
//            Log.e(getClass().getSimpleName(), "Folder was: " + folder);
        } else Log.e(getClass().getSimpleName(), "folderText was null");
        if (folder == null) return;
        if (recyclerView != null) {
            adapter = new DetailedPackAdapter(this, this, folder, PackItem.TYPE_TEMPLATE);
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
