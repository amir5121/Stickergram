package com.amir.telegramstickerbuilder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.Loader;
import com.amir.telegramstickerbuilder.views.MainNavDrawer;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    View userStickersButton;
    View phoneStickersButton;
    View templateStickerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavDrawer(new MainNavDrawer(this));

        userStickersButton = findViewById(R.id.activity_main_user_stickers_button);
        phoneStickersButton = findViewById(R.id.activity_main_phone_stickers);
        templateStickerButton = findViewById(R.id.activity_main_template_stickers);

        if (userStickersButton != null && phoneStickersButton != null && templateStickerButton != null) {
            userStickersButton.setOnClickListener(this);
            phoneStickersButton.setOnClickListener(this);
            templateStickerButton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();

        if (itemId == R.id.activity_main_user_stickers_button) {
            startActivity(new Intent(this, UserStickersActivity.class));
        } else if (itemId == R.id.activity_main_phone_stickers) {
            Loader.gainPermission(this);
            if (Loader.checkPermission(this)) {
                startActivity(new Intent(this, PhoneStickersActivity.class));
            } else {
                Toast.makeText(this, getResources().getString(R.string.need_permission), Toast.LENGTH_LONG).show();
            }
        } else if (itemId == R.id.activity_main_template_stickers) {
            startActivity(new Intent(this, TemplateStickersActivity.class));
        }
    }
}