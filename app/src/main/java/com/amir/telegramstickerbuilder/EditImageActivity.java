package com.amir.telegramstickerbuilder;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.navdrawer.MainNavDrawer;

public class EditImageActivity extends BaseActivity {
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        setNavDrawer(new MainNavDrawer(this));
        Uri imageUri = getIntent().getParcelableExtra(BaseActivity.EDIT_IMAGE_URI);

        imageView = (ImageView) findViewById(R.id.activity_edit_image_image);

        if (imageView != null)
            imageView.setImageURI(imageUri);
    }
}
