package com.amir.telegramstickerbuilder.sticker.pack;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amir.telegramstickerbuilder.EditImageActivity;
import com.amir.telegramstickerbuilder.HowToActivity;
import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.base.BaseFragment;
import com.amir.telegramstickerbuilder.infrastructure.Loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class UserIconPackDetailedFragment extends BaseFragment implements IconPackAdapter.OnStickerClickListener {
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
    public void OnIconClicked(final PackItem item) {
        final BaseActivity activity = (BaseActivity) getActivity();
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    if (Loader.isTelegramInstalled(getContext())) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setPackage(BaseActivity.TELEGRAM_PACKAGE);
                        intent.setType("application/pdf");
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(item.getDir())));
                        startActivity(intent);
                    } else
                        Toast.makeText(activity, getString(R.string.telegram_is_not_installed), Toast.LENGTH_LONG).show();
                } else if (which == Dialog.BUTTON_NEUTRAL){
                    activity.finish();
                    startActivity(new Intent(activity, HowToActivity.class));
                }
            }
        };

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_send_sticker, null, false);
        ImageView stickerImage = (ImageView) view.findViewById(R.id.dialog_send_sticker_image_view);

        Bitmap bitmap = BitmapFactory.decodeFile(item.getDir());
        if (stickerImage != null)
            stickerImage.setImageBitmap(bitmap);

        AlertDialog deleteDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(activity.getString(R.string.do_you_want_to_send_this_sticker))
                .setNegativeButton(activity.getString(R.string.no), listener)
                .setPositiveButton(activity.getString(R.string.yes), listener)
                .setNeutralButton(getString(R.string.how_to), listener)
                .create();

        deleteDialog.show();

//        Loader.loadStickerDialog(Uri.fromFile(new File(item.getDir())), (BaseActivity) getActivity());
    }

    @Override
    public void OnLongClicked(final PackItem item) {
        final BaseActivity activity = (BaseActivity) getActivity();
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    Log.e(getClass().getSimpleName(), item.getDir());
                    File file = new File(item.getDir());
                    if (file.exists()) {
                        file.delete();
                        adapter.itemRemoved(item.getDir());
                    }
                }
            }
        };

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_single_item, null, false);
        ImageView stickerImage = (ImageView) view.findViewById(R.id.dialog_single_item_image);

        Bitmap bitmap = BitmapFactory.decodeFile(item.getDir());
        if (stickerImage != null)
            stickerImage.setImageBitmap(bitmap);

        AlertDialog deleteDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(activity.getString(R.string.do_you_want_to_delete_this_sticker))
                .setNegativeButton(activity.getString(R.string.cancel), listener)
                .setPositiveButton(activity.getString(R.string.delete), listener)
                .create();

        deleteDialog.show();
    }

    public void refresh(String folder) {
        this.folder = folder;
        if (folderText != null) {
            folderText.setText(folder);
//            Log.e(getClass().getSimpleName(), "Folder was: " + folder);
        } else Log.e(getClass().getSimpleName(), "folderText was null");
        if (folder == null) return;
        if (recyclerView != null) {
            adapter = new IconPackAdapter(this, this, folder, PackItem.TYPE_USER);
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
