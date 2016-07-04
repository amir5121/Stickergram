package com.amir.stickergram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.navdrawer.MainNavDrawer;
import com.amir.stickergram.navdrawer.NavDrawer;

public class ContactActivity extends BaseActivity implements View.OnClickListener {
    private static final String STICKERGRAM_FEED_BACK = "Stickergram feed back";
    View sendEmailButton;
    View joinStickergramChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        setNavDrawer(new MainNavDrawer(this));

        setUpView();
    }

    private void setUpView() {
        sendEmailButton = findViewById(R.id.activity_contact_send_email_button);
        joinStickergramChannel = findViewById(R.id.activity_contact_join_channel);

        if (sendEmailButton != null &&
                joinStickergramChannel != null) {
            sendEmailButton.setOnClickListener(this);
            joinStickergramChannel.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.activity_contact_send_email_button) {
            sendEmail();
        } else if (itemId == R.id.activity_contact_join_channel) {
            Loader.joinToStickergramChannel(this);
        }
    }

    protected void sendEmail() {
        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode(EMAIL) +
                "?subject=" + Uri.encode(STICKERGRAM_FEED_BACK);// +
//                "&body=" + Uri.encode("the body of the message");
        Uri uri = Uri.parse(uriText);

        send.setData(uri);
        startActivity(Intent.createChooser(send, getString(R.string.send_an_email)));
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
