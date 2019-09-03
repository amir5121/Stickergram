package com.amir.stickergram.phoneStickers.organizedIcon;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.sticker.icon.IconItem;
import com.amir.stickergram.sticker.icon.user.IconAdapter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class OrganizedStickersIconFragment extends BaseFragment implements IconAdapter.OnStickerClickListener, OnStickerClickListener {
    private OnStickerClickListener listener;
    private OrganizedIconAdapter adapter;
    private boolean isImagePicker;

    public static OrganizedStickersIconFragment newInstance(boolean isImagePicker) {

        Bundle args = new Bundle();

        args.putBoolean(Constants.IS_AN_IMAGE_PICKER, isImagePicker);

        OrganizedStickersIconFragment fragment = new OrganizedStickersIconFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnStickerClickListener) context;
        } catch (ClassCastException e) {
            throw new RuntimeException(" you must implement OnStickerClick listener in order to use OrganizedStickersIconFragment");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_phone_stickers_organized, container, false);

        Bundle args = getArguments();
        if (args != null) {
            isImagePicker = args.getBoolean(Constants.IS_AN_IMAGE_PICKER, false);
        }

        setFont((ViewGroup) view);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_phone_stickers_organized_list);

        if (recyclerView != null) {
            adapter = new OrganizedIconAdapter((BaseActivity) getActivity(), this, isImagePicker);
            if (isImagePicker) {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));
            } else {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            }
            recyclerView.setAdapter(adapter);
        }


        return view;
    }

    @Override
    public void OnIconClicked(IconItem item) {
        listener.OnIconClicked(item);
    }

    @Override
    public void OnIconLongClicked(final IconItem item) {
        if (!isImagePicker) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == Dialog.BUTTON_POSITIVE) {
                        adapter.refresh(item.getFolder(), true);
                    }
                }
            };

            final AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setMessage(
                            !Loader.deviceLanguageIsPersian() ?
                                    getActivity().getString(R.string.delete) + " " + item.getName() + " " + getActivity().getString(R.string.pack)
                                    : getActivity().getString(R.string.pack) + " " + item.getName() + " " + getActivity().getString(R.string.delete))
                    .setPositiveButton(getString(R.string.delete), listener)
                    .setNegativeButton(getString(R.string.no), listener)
                    .create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    setFont((TextView) dialog.findViewById(android.R.id.message));
                    setFont(dialog.getButton(AlertDialog.BUTTON_NEGATIVE));
                    setFont(dialog.getButton(AlertDialog.BUTTON_POSITIVE));
                }
            });

            dialog.show();

        }
    }

    @Override
    public void OnNoItemWereFoundListener() {
        listener.OnNoItemWereFoundListener();
    }

    @Override
    public void OnCreateNewFolderSelected() {
        if (!isImagePicker)
            newOrganizedFolder();
    }

    public void newOrganizedFolder() {
        File stickerDirectories = new File(BaseActivity.Companion.getBASE_PHONE_ORGANIZED_STICKERS_DIRECTORY());
        List stickers = null;
        if (stickerDirectories.exists())
            stickers = Arrays.asList(stickerDirectories.list());

        if (!BaseActivity.isPaid) {
            if (stickers != null) {
                if (stickers.size() > 1) {
                    Toast.makeText(getActivity(), getString(R.string.you_can_only_create_two_pack_in_free_version), Toast.LENGTH_LONG).show();
                    return;
                }
            } else Log.e(getClass().getSimpleName(), "stickers was null");
        }
        final View newTextDialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_package, null);
        setFont((ViewGroup) newTextDialogView);
        final EditText editText = (EditText) newTextDialogView.findViewById(R.id.dialog_set_new_text_text);

        final AlertDialog newTextDialog = new AlertDialog.Builder(getActivity())
                .setView(newTextDialogView)
                .setPositiveButton(getString(R.string.done), null)
                .setNegativeButton(getString(R.string.cancel), null)
                .create();

        final List finalStickers = stickers;
        newTextDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button b = newTextDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                BaseActivity activity = (BaseActivity) getActivity();
                activity.setFont(b);
                activity.setFont(newTextDialog.getButton(AlertDialog.BUTTON_NEGATIVE));
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String text = editText.getText().toString();

                        if (finalStickers != null && finalStickers.contains(text)) {
                            View nameAlreadyExistText = newTextDialogView.findViewById(R.id.dialog_new_package_already_exist);
                            if (nameAlreadyExistText != null)
                                nameAlreadyExistText.setVisibility(View.VISIBLE);
                        } else if (text.length() > Constants.PACKAGE_NAME_LENGTH_LIMIT) {
                            View nameCantBeThisLong = newTextDialogView.findViewById(R.id.name_can_t_be_this_long);
                            if (nameCantBeThisLong != null)
                                nameCantBeThisLong.setVisibility(View.VISIBLE);
                        } else if (!text.equals("") &&
                                !text.contains("!") &&
                                !text.contains("'") &&
                                !text.contains("/") &&
                                !text.contains("%") &&
                                !text.contains("#") &&
                                !text.contains("*") &&
                                !text.contains("\\") &&
                                !text.contains(":") &&
                                !text.contains("|") &&
                                !text.contains("<") &&
                                !text.contains(">") &&
                                !text.contains(".") &&
                                !text.contains("?")) {

                            int textLength = text.length();
                            while (text.charAt(textLength - 1) == ' ' && textLength > 0) {
                                text = text.substring(0, textLength - 1);
                                textLength = text.length();
                            }
                            File folder = new File(BaseActivity.Companion.getBASE_PHONE_ORGANIZED_STICKERS_DIRECTORY() + text + File.separator);
                            if (folder.mkdirs())
                                adapter.refresh(text, false);
                            newTextDialog.dismiss();
                        } else {
                            View symbolTextView = newTextDialogView.findViewById(R.id.dialog_new_package_symbol_text);
                            if (symbolTextView != null)
                                symbolTextView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
        newTextDialog.show();

    }

    public void refreshItems() {

        adapter.notifyDataSetChanged();
    }
}
