package com.amir.stickergram.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.amir.stickergram.R;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.util.IabHelper;
import com.amir.stickergram.util.IabResult;
import com.amir.stickergram.util.Purchase;

public abstract class BaseAuthenticatedActivity extends AppCompatActivity {
    private static final String TAG = "BaseAuthenticated";
    private static final int REQUEST_BUY_PRO = 100001;
    public static final String BUY_THE_AWESOME_STICKERGRAM_PRO_VERSION = "BuyTheAwesomeStickergramProVersion";
    private static final String GOOGLE_PLAY_SERVICES_PACKAGE = "com.google.android.gms";
    public static final String BAZAR_PACKAGE = "com.farsitel.bazaar";
    public static boolean isPaid;
    private static String HAS_BOUGHT_PRO;
    IabHelper mHelper;
    static final String ITEM_SKU = "android.test.purchased";
    private SharedPreferences preferences;
    public static boolean inAppBillingSetupOk = false;
    public static boolean isPaymentAppInstalled = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(BaseActivity.SETTING, MODE_PRIVATE);
        isPaid = getProStatus();

        String base64EncodedPublicKey =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApnUyi0eqv00Cxxqt0uU6cpLdXTvDw+S9/JhMBShXvVRkMxaA/kW4osqBRJwlYCAMUu9uilSWZmj9nAMB6wcBmkLllBSeNMMeVnBweecJaOUnfv2yNt09EU5JVwMQuHxXG+FsPc/wHOCboHaRQKaqWXQZUZvt+J9BkwQaPB1Ho3zAUnbo8ot6ycXmvKsAay3uIkalztCnKoMJOohft3LUwT6Fh2gXCn1KBnEBzRWPHltO219HeFuKTBqNw1A3cwoTBrm7MxIluaA8Cg7tLDmesOqrHCRAunOS9c7TPDmTOpsRuapwHJUpf9Hy4QOjfH8Y37BDxugrRniksxnZg41sLwIDAQAB";


        //todo set difference for persian and english version
        if (Loader.isAppInstalled(this, BAZAR_PACKAGE))
            isPaymentAppInstalled = true;
        if (isPaymentAppInstalled) {
            mHelper = new IabHelper(this, base64EncodedPublicKey);

            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        Log.d(TAG, "In-app Billing setup failed: " +
                                result);
                        Toast.makeText(BaseAuthenticatedActivity.this, "inAppBilling setup was failed", Toast.LENGTH_LONG).show();
                    } else {
                        inAppBillingSetupOk = true;
                        Toast.makeText(BaseAuthenticatedActivity.this, "inAppBilling was successful!!", Toast.LENGTH_LONG).show();
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

    public void requestProVersion() {
        if (isPaymentAppInstalled) {
            if (inAppBillingSetupOk)
                mHelper.launchPurchaseFlow(this, ITEM_SKU, REQUEST_BUY_PRO,
                        mPurchaseFinishedListener, BUY_THE_AWESOME_STICKERGRAM_PRO_VERSION);
            else Toast.makeText(this, "inAppBilling setup was failed", Toast.LENGTH_LONG).show();
        } else {
            //todo this toast must vary in english and persian version
            Toast.makeText(this, getString(R.string.google_play_services_is_not_installed), Toast.LENGTH_SHORT).show();
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                showErrorInPayment();
                return;
            } else if (purchase.getSku().equals(ITEM_SKU)) {
                Log.e(getClass().getSimpleName(), "Buy Pro");
                buyPro();
            }

        }

    };

    private void showErrorInPayment() {
        Toast.makeText(this, getString(R.string.payment_was_unsuccessful), Toast.LENGTH_LONG).show();
    }

    private void buyPro() {
        setBuyProTrue();
        Toast.makeText(this, getString(R.string.to_apply_the_effect_restart_the_app), Toast.LENGTH_LONG).show();
        finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null)
            mHelper.dispose();
        mHelper = null;
    }


    public void setBuyProTrue() {
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean(HAS_BOUGHT_PRO, true);
//        editor.apply();
    }

    public boolean getProStatus() {
        return preferences.getBoolean(HAS_BOUGHT_PRO, false);
//        return true;
    }
}
