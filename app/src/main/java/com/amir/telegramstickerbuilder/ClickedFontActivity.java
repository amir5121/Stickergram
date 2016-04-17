package com.amir.telegramstickerbuilder;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.Loader;

import java.io.IOException;

public class ClickedFontActivity extends BaseActivity {

    View okButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_font);


        Uri uri = getIntent().getData();
        if (uri != null)
            try {
                String filePath = Loader.makeACopyToFontFolder(uri, this);
                if (filePath == null) {
                    Toast.makeText(this, getString(R.string.couldn_t_add_the_font), Toast.LENGTH_LONG).show();
                    finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        else {
            Toast.makeText(this, getString(R.string.couldn_t_add_the_font), Toast.LENGTH_LONG).show();
            finish();
        }
        okButton = findViewById(R.id.activity_clicked_font_ok_button);
        if (okButton != null) okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
