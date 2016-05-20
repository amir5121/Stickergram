package com.amir.stickergram.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.util.IabHelper;
import com.amir.stickergram.util.IabResult;
import com.amir.stickergram.util.Purchase;

public abstract class BaseAuthenticatedActivity extends AppCompatActivity {
    private static final String TAG =
            "BaseAuthenticated";
    IabHelper mHelper;
    static final String ITEM_SKU = "pro";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: set the public key
        //String base64EncodedPublicKey = null;

        //mHelper = new IabHelper(this, base64EncodedPublicKey);

        String base64EncodedPublicKey =
                "<your license key here>";

        if (Loader.isAppInstalled(this, BaseActivity.BAZAR_PACKAGE)) {
            //todo set difference for persian and english version
            mHelper = new IabHelper(this, base64EncodedPublicKey);

            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        Log.d(TAG, "In-app Billing setup failed: " +
                                result);
                    } else {
                        Log.d(TAG, "In-app Billing is set up OK");
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void OnBuyProRequested() {
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "mypurchasetoken");
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                return;
            } else if (purchase.getSku().equals(ITEM_SKU)) {
                //todo: consumeItem
//                consumeItem();
//                buyButton.setEnabled(false);
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null)
            mHelper.dispose();
        mHelper = null;
    }
}
