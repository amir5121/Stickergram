package com.amir.stickergram.sticker.pack.template;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amir.stickergram.EditImageActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.serverHelper.ServerSticker;
import com.amir.stickergram.serverHelper.VolleySingleton;
import com.amir.stickergram.sticker.OnRefreshCallbacks;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.io.File;

public class TemplateIconPackDetailedFragment extends BaseFragment
        implements OnStickerClickListener, OnRefreshCallbacks, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private PackAdapter adapter;

    private RecyclerView recyclerView;
    private TextView folderText;
    private String folder;
    private String name;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button linkButton;
    private ServerSticker serverSticker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_icon_detailed, container, false);
        setFont((ViewGroup) view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_icon_detailed_swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        folderText = (TextView) view.findViewById(R.id.fragment_icon_detailed_text_folder);
        linkButton = (Button) view.findViewById(R.id.fragment_icon_detailed_link_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.template_sticker_icon_detailed_list);
        refresh(name, folder);
        return view;
    }


    public void refresh(String name, String enName) {
        this.folder = enName;
        this.name = name;
//        Log.e(getClass().getSimpleName(), "---enName is : " + enName);
        if (folderText == null) Log.e(getClass().getSimpleName(), "folderText was null");
        if (folderText != null && name != null) {
//            Log.e(getClass().getSimpleName(), "---name is : " + name);
            folderText.setText(name);
            folderText.setVisibility(View.VISIBLE);
        }
        if (enName == null) {
            Log.e(getClass().getSimpleName(), "Folder was null");
            return;
        }
        if (recyclerView != null) {
            adapter = new PackAdapter(this, enName);
            if (BaseActivity.isTablet || BaseActivity.isInLandscape)
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
            else recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recyclerView.setAdapter(adapter);
        }
        manageButton();
    }

    @Override
    public void onStickerClicked(PackItem item) {
        getDialogFor(item);
//        Log.e(getClass().getSimpleName(), "clicked: " + item.getEnName() + " " + item.getPosition());
    }

    private void getDialogFor(PackItem item) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_server_sticker, null, false);
        final ImageView stickerImage = (ImageView) view.findViewById(R.id.dialog_server_sticker_image);
        final View progressView = view.findViewById(R.id.dialog_server_sticker_loading);
        final View errorImage = view.findViewById(R.id.dialog_server_sticker_error);
        ImageLoader imageLoader = VolleySingleton.getInstance().getImageLoader();
//        String url = Constants.STICKERGRAM_URL + Constants.STICKERS + item.getEnName() + "/" + item.getPosition() + Constants.WEBP;
        String url;
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
            url = Constants.STICKERGRAM_URL + Constants.STICKERS + item.getEnName() + "/" + Constants.PNG_NO_DOT + "/" + item.getPosition() + Constants.PNG;
            Log.e(getClass().getSimpleName(), url);
        } else {
            url = Constants.STICKERGRAM_URL + Constants.STICKERS + item.getEnName() + "/" + item.getPosition() + Constants.WEBP;
        }
        final String dir = BaseActivity.CACHE_DIR + Constants.STICKERS + item.getEnName() + "/" + item.getPosition() + Constants.PNG;

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    Intent intent = new Intent(getActivity(), EditImageActivity.class);
                    intent.putExtra(Constants.EDIT_IMAGE_URI, Uri.fromFile(new File(dir)));
                    startActivity(intent);
                }
            }
        };

        final AlertDialog editStickerDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(getActivity().getString(R.string.edit_this_sticker))
                .setNegativeButton(getActivity().getString(R.string.no), listener)
                .setPositiveButton(getActivity().getString(R.string.yes), listener)
                .create();

        editStickerDialog.show();

        final Button positiveButton = editStickerDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);

        Bitmap bitmap = Loader.getCached(dir);
        if (bitmap == null) {
            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap mBitmap = response.getBitmap();
                    if (mBitmap != null) {
                        stickerImage.setImageBitmap(mBitmap);
                        Loader.cacheThumb(mBitmap, dir);
                        positiveButton.setEnabled(true);
                        progressView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressView.setVisibility(View.GONE);
                    errorImage.setVisibility(View.VISIBLE);
                    Log.e(getClass().getSimpleName(), "error getting the image");
                }
            });
        } else {
            stickerImage.setImageBitmap(bitmap);
            progressView.setVisibility(View.GONE);
            positiveButton.setEnabled(true);
        }

    }

    @Override
    public void OnRefreshFinished(boolean failed) {
        manageButton();
        swipeRefreshLayout.setRefreshing(false);
        if (failed)
            Toast.makeText(getContext(), getString(R.string.failed_to_refresh), Toast.LENGTH_LONG).show();
    }

    private void manageButton() {
        if (adapter != null && linkButton != null) {
            serverSticker = adapter.getServerSticker();
            if (serverSticker != null)
                if (serverSticker.getHasLink()) {
                    linkButton.setVisibility(View.VISIBLE);
                    if (Loader.deviceLanguageIsPersian())
                        linkButton.setText(serverSticker.getLinkNamePer());
                    else linkButton.setText(serverSticker.getLinkNameEn());
                    linkButton.setOnClickListener(this);
                } else {
                    linkButton.setVisibility(View.GONE);
                }
        }
    }

    @Override
    public void onRefresh() {
        if (adapter != null && swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
            adapter.updateItems();
        }
    }

    @Override
    public void onClick(View view) {
        if (serverSticker.getLink() != null) {
            Log.e(getClass().getSimpleName(), serverSticker.getLink());
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(serverSticker.getLink())));
        }
    }
}
