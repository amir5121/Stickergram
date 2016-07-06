package com.amir.stickergram.fonts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.EditImageActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.infrastructure.FontItem;
import com.amir.stickergram.infrastructure.Loader;

import java.io.IOException;

public class AddFontFragment extends BaseFragment implements View.OnClickListener, FontAdapter.OnFontClickListener {
    private static final int REQUEST_NEW_FONT = 1000;
    View addButton;
    View howToButton;
    View loadingFrame;
    AddFontAdapter adapter;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_font, null, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.add_new_font_fragment_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadingFrame = view.findViewById(R.id.add_new_font_fragment_loading_frame);
        adapter = new AddFontAdapter((BaseActivity) getActivity(), this, loadingFrame);
        recyclerView.setAdapter(adapter);
        addButton = view.findViewById(R.id.add_new_font_fragment_add_font_button);
        howToButton = view.findViewById(R.id.add_new_font_fragment_how_to);

        howToButton.setOnClickListener(this);
        addButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.add_new_font_fragment_add_font_button) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, REQUEST_NEW_FONT);
        } else if (itemId == R.id.add_new_font_fragment_how_to) {
            View howToView = getActivity().getLayoutInflater().inflate(R.layout.how_to, null, false);
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(howToView)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setTitle(getString(R.string.how_to_add_font))
                    .create();
            dialog.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            try {
                String filePath = Loader.makeACopyToFontFolder(data.getData(), (BaseActivity) getActivity());
//                Log.e(getClass().getSimpleName(), "file path: " + filePath);
//                if (filePath == null)
//                    Log.e(getClass().getSimpleName(), "Failed to make a copy of the font!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (adapter != null) {
                adapter = null;
                adapter = new AddFontAdapter((BaseActivity) getActivity(), this, loadingFrame);
                recyclerView.setAdapter(adapter);
//                adapter.notifyItemInserted();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFontClicked(FontItem fontItem) {
        ((EditImageActivity) getActivity()).onFontItemSelected(fontItem);
        ((MainFontDialogFragment) getParentFragment()).dismiss();
    }
}