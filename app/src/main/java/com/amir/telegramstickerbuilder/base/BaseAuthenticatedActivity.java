package com.amir.telegramstickerbuilder.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.amir.telegramstickerbuilder.util.IabHelper;

public abstract class BaseAuthenticatedActivity extends AppCompatActivity{

    IabHelper mHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: set the public key
        //String base64EncodedPublicKey = null;

        //mHelper = new IabHelper(this, base64EncodedPublicKey);
    }
}
