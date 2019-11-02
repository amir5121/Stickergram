package com.amir.stickergram.phoneStickers.organizedDetailed;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseDialogFragment;
import com.amir.stickergram.image.ImageReceiverCallBack;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.sticker.pack.user.OnStickerClickListener;
import com.amir.stickergram.sticker.pack.user.PackAdapter;
import com.amir.stickergram.sticker.pack.user.PackItem;

import java.io.File;

public class OrganizedStickersDetailedDialogFragment extends BaseDialogFragment implements OnStickerClickListener {
    private static final String FOLDER = "FOLDER";
    private boolean isImagePicker;
    private ImageReceiverCallBack imageListener;

    @Override
    public void onStart() {
        super.onStart();

        //making the dialog have round angels but it's color is overridden in the style
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.note_background);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            imageListener = (ImageReceiverCallBack) context;
        } catch (ClassCastException e) {
            //EMPTY
        }
    }

    public static OrganizedStickersDetailedDialogFragment newInstance(String folder, boolean isImagePicker) {
        OrganizedStickersDetailedDialogFragment fragment = new OrganizedStickersDetailedDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FOLDER, folder);
        bundle.putBoolean(Constants.IS_AN_IMAGE_PICKER, isImagePicker);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String folder = getArguments().getString(FOLDER);
        isImagePicker = getArguments().getBoolean(Constants.IS_AN_IMAGE_PICKER, false);

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
                    BaseActivity.Companion.getBASE_PHONE_ORGANIZED_THUMBNAIL_DIRECTORY()));

            if (BaseActivity.Companion.isTablet() || BaseActivity.Companion.isInLandscape()) {
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
        if (isImagePicker) {
            imageListener.receivedImage(item.getBitmap());
            dismiss();
        } else
            Loader.INSTANCE.loadStickerDialog(Uri.fromFile(new File(item.getDir())), (BaseActivity) getActivity());
    }

    @Override
    public void OnLongClicked(PackItem item) {

    }

    @Override
    public void folderDeleted() {

    }
}
