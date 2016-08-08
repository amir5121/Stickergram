package com.amir.stickergram.phoneStickers.organizedDetailed;

import android.app.Dialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.sticker.pack.user.OnStickerClickListener;
import com.amir.stickergram.sticker.pack.user.PackAdapter;
import com.amir.stickergram.sticker.pack.user.PackItem;

import java.io.File;

public class OrganizedStickersDetailedDialogFragment extends DialogFragment implements OnStickerClickListener {
    private static final String FOLDER = "FOLDER";

    @Override
    public void onStart() {
        super.onStart();

        //making the dialog have round angels but it's color is overridden in the style
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.note_background);
        }
    }

    public static OrganizedStickersDetailedDialogFragment newInstance(String folder) {
        OrganizedStickersDetailedDialogFragment fragment = new OrganizedStickersDetailedDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FOLDER, folder);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String folder = getArguments().getString(FOLDER);

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_phone_stickers_organized, null, false);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            view.setBackgroundColor(getResources().getColor(R.color.light_blue, null));
//        } else view.setBackgroundColor(getResources().getColor(R.color.light_blue));

        view.findViewById(R.id.fragment_phone_stickers_organized_top_container).setVisibility(View.VISIBLE);

        TextView title = (TextView) view.findViewById(R.id.fragment_phone_stickers_organized_title);
        title.setText(folder);

        View closeButton = view.findViewById(R.id.fragment_phone_sticker_organized_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        ((BaseActivity) getActivity()).setFont((ViewGroup) view);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_phone_stickers_organized_list);

        if (recyclerView != null) {
            recyclerView.setAdapter(new PackAdapter(
                    (BaseActivity) getActivity(),
                    this,
                    folder,
                    BaseActivity.BASE_PHONE_ORGANIZED_STICKERS_DIRECTORY,
                    BaseActivity.BASE_PHONE_ORGANIZED_THUMBNAIL_DIRECTORY));

            if (BaseActivity.isTablet || BaseActivity.isInLandscape) {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            }
        } else Log.e(getClass().getSimpleName(), "recyclerView was null");


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder.setView(view);

        return alertDialogBuilder.create();
    }

    @Override
    public void OnIconClicked(PackItem item) {
        Loader.loadStickerDialog(Uri.fromFile(new File(item.getDir())), (BaseActivity) getActivity());
    }

    @Override
    public void OnLongClicked(PackItem item) {

    }

    @Override
    public void folderDeleted() {

    }
}
