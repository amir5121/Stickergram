package com.amir.stickergram.fonts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.EditImageActivity;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.infrastructure.FontItem;
import com.amir.stickergram.R;

    public class PersianFontsFragment extends BaseFragment implements FontAdapter.OnFontClickListener {
    public View loadingFrame;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fonts, container, false);
        loadingFrame = view.findViewById(R.id.dialog_font_loading_frame);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dialog_font_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new PersianFontAdapter((BaseActivity) getActivity(), this, loadingFrame));
        return view;
    }


    @Override
    public void onFontClicked(FontItem fontItem) {
        ((EditImageActivity)getActivity()).onFontItemSelected(fontItem);
        ((MainFontDialogFragment)getParentFragment()).dismiss();
    }

}
