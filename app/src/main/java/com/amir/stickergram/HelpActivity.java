package com.amir.stickergram;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.navdrawer.MainNavDrawer;

public class HelpActivity extends BaseActivity implements View.OnClickListener {
    private static final CharSequence PUBLISH_COMMAND = "/publish";
    private static final CharSequence NEW_PACK_COMMAND = "/newpack";
    View activateBotButton;
    View createNewPack;
    View goToBot;
    View goToBot2;
    View goToBot3;
    View goToUserPackButton;
    View publishButton;
    View watchVideo;
    View watchAddSticker;

    //todo : add how to add sticker to a published pack https://youtu.be/-hB3qNd7dGk

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setNavDrawer(new MainNavDrawer(this));

        setUpView();
    }

    private void setUpView() {
        activateBotButton = findViewById(R.id.activity_faq_activate_bot);
        createNewPack = findViewById(R.id.activity_faq_new_pack);
        goToBot = findViewById(R.id.activity_faq_go_to_bot1);
        goToBot2 = findViewById(R.id.activity_faq_go_to_bot2);
        goToBot3 = findViewById(R.id.activity_faq_go_to_bot3);
        goToUserPackButton = findViewById(R.id.activity_faq_go_to_user_pack);
        publishButton = findViewById(R.id.activity_faq_publish);
        watchVideo = findViewById(R.id.activity_help_watch_video);
        watchAddSticker = findViewById(R.id.activity_help_watch_how_to_add_sticker_to_a_published_pack);


        if (activateBotButton != null &&
                createNewPack != null &&
                goToBot != null &&
                goToBot2 != null &&
                goToBot3 != null &&
                goToUserPackButton != null &&
                publishButton != null &&
                watchVideo != null &&
                watchAddSticker != null) {
            activateBotButton.setOnClickListener(this);
            createNewPack.setOnClickListener(this);
            goToBot.setOnClickListener(this);
            goToBot2.setOnClickListener(this);
            goToBot3.setOnClickListener(this);
            goToUserPackButton.setOnClickListener(this);
            publishButton.setOnClickListener(this);
            watchVideo.setOnClickListener(this);
            watchAddSticker.setOnClickListener(this);
            if (Loader.deviceLanguageIsPersian()) watchAddSticker.setVisibility(View.GONE);
            else watchAddSticker.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.activity_faq_activate_bot) {
            Loader.goToBotInTelegram(this);
        } else if (itemId == R.id.activity_faq_go_to_bot1 || itemId == R.id.activity_faq_go_to_bot2 || itemId == R.id.activity_faq_go_to_bot3) {
            Loader.goToBotInTelegram(this);
        } else if (itemId == R.id.activity_faq_go_to_user_pack) {
            Intent intent = new Intent(this, UserStickersActivity.class);
            startActivity(intent);
            finish();
        } else if (itemId == R.id.activity_faq_publish) {
            copyToClipboard(PUBLISH_COMMAND);
            Loader.goToBotInTelegram(this);
        } else if (itemId == R.id.activity_faq_new_pack) {
            copyToClipboard(NEW_PACK_COMMAND);
            Loader.goToBotInTelegram(this);
        } else if (itemId == R.id.activity_help_watch_video) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.link_to_video)));
            startActivity(intent);
        } else if (itemId == R.id.activity_help_watch_how_to_add_sticker_to_a_published_pack) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.link_to_video_add_sticker)));
            startActivity(intent);
        }
    }

    private void copyToClipboard(CharSequence text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
