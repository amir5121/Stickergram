package com.amir.stickergram;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;

import java.io.IOException;

public class ClickedFontActivity extends BaseActivity {

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
        View okButton = findViewById(R.id.activity_clicked_font_ok_button);
        if (okButton != null) okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setFont((ViewGroup) findViewById(R.id.nav_drawer));
        setFont((ViewGroup) findViewById(R.id.activity_clicked_font_main_container));


    }
}
