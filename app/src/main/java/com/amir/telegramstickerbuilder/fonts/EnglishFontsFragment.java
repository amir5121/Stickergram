package com.amir.telegramstickerbuilder.fonts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.telegramstickerbuilder.EditImageActivity;
import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.base.BaseFragment;
import com.amir.telegramstickerbuilder.infrastructure.FontItem;

//public class EnglishFontsFragment extends BaseFragment implements EnglishFontAdapter.OnFontClickListener, EnglishFontAdapter.AsyncEnglishFontsListener {
    public class EnglishFontsFragment extends BaseFragment implements  FontAdapter.OnFontClickListener {

    View loadingFrame;

    public EnglishFontsFragment() {
    }

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
//        recyclerView.setAdapter(new EnglishFontAdapter((BaseActivity) getActivity(), this, this));
        recyclerView.setAdapter(new EnglishFontAdapter((BaseActivity) getActivity(), this, loadingFrame));
        return view;
    }


//    @Override
//    public void onEnglishFontClicked(FontItem fontItem) {
//        ((EditImageActivity) getActivity()).onFontItemSelected(fontItem);
//        ((MainFontDialogFragment) getParentFragment()).dismiss();
//    }

//    @Override
//    public void loadStarted() {
//        loadingFrame.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void loafFinished() {
//        loadingFrame.setVisibility(View.GONE);
//    }

    @Override
    public void onFontClicked(FontItem fontItem) {
        ((EditImageActivity) getActivity()).onFontItemSelected(fontItem);
        ((MainFontDialogFragment) getParentFragment()).dismiss();
    }

    public interface OnFontItemClicked {
        void onFontItemSelected(FontItem item);
    }

}
