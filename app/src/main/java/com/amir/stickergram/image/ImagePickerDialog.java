package com.amir.stickergram.image;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amir.stickergram.CropActivity;
import com.amir.stickergram.EditImageActivity;
import com.amir.stickergram.MainActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseDialogFragment;
import com.amir.stickergram.infrastructure.Constants;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.viewpagerindicator.PageIndicator;

public class ImagePickerDialog extends BaseDialogFragment {

    public static final int ADD_IMAGE_REQUEST = 2134;

    private ImagePickerAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_image_picker, container, false);
        View addImageButton = view.findViewById(R.id.dialog_image_picker_add_image);
        ((BaseActivity) getActivity()).setFont((ViewGroup) view);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.dialog_image_picker_view_pager);
        adapter = new ImagePickerAdapter(getChildFragmentManager(), getContext());
        viewPager.setAdapter(adapter);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryActivity.openActivity(getActivity(), MainActivity.GALLERY_REQUEST_CODE, Constants.galleryConfig);

            }
        });

        PageIndicator indicator = (PageIndicator) view.findViewById(R.id.dialog_image_picker_titles);
        indicator.setViewPager(viewPager);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void onServerStickerIconClicked(String name, String enName) {
        ((TemplateStickersFragment) adapter.getItem(ImagePickerAdapter.SERVER_STICKERS)).instantiateFragment(name, enName);
    }

}
