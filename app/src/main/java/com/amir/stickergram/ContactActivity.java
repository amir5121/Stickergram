package com.amir.stickergram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.navdrawer.MainNavDrawer;

public class ContactActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        setNavDrawer(new MainNavDrawer(this));

        setUpView();
    }

    private void setUpView() {
        View sendEmailButton = findViewById(R.id.activity_contact_send_email_button);
        View joinStickergramChannel = findViewById(R.id.activity_contact_join_channel);

//        View thanksText = findViewById(R.id.activity_contact_thanks_text);
//
//        thanksText.setBackground(new SemiCircleDrawable(Color.RED, SemiCircleDrawable.Direction.BOTTOM));

        if (sendEmailButton != null &&
                joinStickergramChannel != null) {
            sendEmailButton.setOnClickListener(this);
            joinStickergramChannel.setOnClickListener(this);
        }

        setFont((ViewGroup) findViewById(R.id.nav_drawer));
        setFont((ViewGroup) findViewById(R.id.activity_contact_main_container));

    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.activity_contact_send_email_button) {
            sendEmail();
        } else if (itemId == R.id.activity_contact_join_channel) {
            Loader.INSTANCE.joinToStickergramChannel(this);
        }
    }

    private void sendEmail() {
        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode(Constants.EMAIL) +
                "?subject=" + Uri.encode(getString(R.string.stickergram_feed_back));// +
//                "&body=" + Uri.encode("the body of the message");
        Uri uri = Uri.parse(uriText);

        send.setData(uri);
        startActivity(Intent.createChooser(send, getString(R.string.send_an_email)));
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
