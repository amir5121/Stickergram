package com.amir.stickergram.base;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.amir.stickergram.BuildConfig;
import com.amir.stickergram.MainActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.util.IabHelper;
import com.amir.stickergram.util.IabResult;
import com.amir.stickergram.util.Inventory;
import com.amir.stickergram.util.Purchase;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public abstract class BaseAuthenticatedActivity extends AppCompatActivity {
    //todo: lucky patcher http://stackoverflow.com/questions/13445598/lucky-patcher-how-can-i-protect-from-it
    //todo: read the comment on that answer
    private static final String TAG = "BaseAuthenticated";
    private static final int REQUEST_BUY_PRO = 1001;
    public static final String BUY_THE_AWESOME_STICKERGRAM_PRO_VERSION = "BuyTheAwesomeStickergramProVersion";
    public static final String BAZAR_PACKAGE = "com.farsitel.bazaar";
    public static boolean isPaid;
    private static String HAS_BOUGHT_PRO = "HAS_BOUGHT_PRO";
    IabHelper mHelper;
    static final String ITEM_SKU = "com.amir.stickergram.pro";
    private SharedPreferences preferences;
    public static boolean inAppBillingSetupOk = false;
    public static boolean isPaymentAppInstalled = false;


    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
//    boolean mIsPremium = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkSignature(this);

        preferences = getSharedPreferences(Constants.SETTING, MODE_PRIVATE);

        isPaid = getProStatus();


        //todo the line below must change for bazzar sp should the check signature
        //todo: notice that google play has said to use different private keys based on the flavor
        String base64EncodedPublicKey =
                "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDKKu/aR+orhZFDEyn1ADsjAa5vRhdSqaJNAs6Sv4E3oeal3mLIn8wQNyDynMI0CgTlP5SuJkOwqllg6oZgrC5k1lDWCjWqfEpiVho65aQRNVIblyvNAUTLreb+MhzLkvGqHP9XuQ2v7Om2IyoYrNr4U+tHZ5CHu8lX4PFI6Vd1ithtI/QX4+KthavCpCFiEYTccC0vXlBn9NlDMzyG3BwI91dmzZl+5a/3hy3eMysCAwEAAQ==";


        if (Loader.isAppInstalled(this, BAZAR_PACKAGE))
            isPaymentAppInstalled = true;
        if (isPaymentAppInstalled) {
            mHelper = new IabHelper(this, base64EncodedPublicKey);

            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        Log.d(TAG, "In-app Bil7ling setup failed: " +
                                result);
                        inAppBillingSetupOk = false;

                    } else {
                        inAppBillingSetupOk = true;
                        Log.d(TAG, "In-app Billing is set up OK");
                        if (!isPaid) mHelper.queryInventoryAsync(mGotInventoryListener);
                    }
                }
            });
        }

        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                if (result.isFailure()) {
                    showErrorInPayment();
                    Log.e(getClass().getSimpleName(), "failed");
                    return;
                } else if (purchase.getSku().equals(ITEM_SKU)) {
                    Log.e(getClass().getSimpleName(), "Buy Pro");
                    setBuyProTrue();
                }

            }

        };
    }

    // Listener that's called when we finish querying the items and
    // subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                Log.e(TAG, "Query inventory failed.");
                //complain("Failed to query inventory: " + result);
                //showErrorInPayment();
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

                /*
                 * Check for items we own. Notice that for each purchase, we check
                 * the developer payload to see if it's correct! See
                 * verifyDeveloperPayload().
                 */

            // // Check for gas delivery -- if we own gas, we should fill up the
            // tank immediately
            Purchase gasPurchase = inventory.getPurchase(ITEM_SKU);
            if (gasPurchase != null
                //&& verifyDeveloperPayload(gasPurchase)
                    ) {
                Log.d(TAG, "We have gas. Consuming it.");
                setBuyProTrue();
                return;
            }
        }
    };

//
//    private void queryPurchasedItems() {
//        //check if user has bought "remove adds"
////        if (mHelper.isSetupDone() && !mHelper.isAsyncInProgress()) {
////            mHelper.queryInventoryAsync(mGotInventoryListener);
////        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        queryPurchasedItems();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        queryPurchasedItems();
//    }
//
//
//    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
//        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
//            Log.d(TAG, "Query inventory finished.");
//
//            if (result.isFailure()) {
//                Log.d(TAG, "Failed to query inventory: " + result);
//                return;
//            } else {
//                Log.d(TAG, "Query inventory was successful.");
//                // does the user have the premium upgrade?
//                mIsPremium = inventory.hasPurchase(ITEM_SKU);
//
//                if (mIsPremium && !isPaid)
//                    setBuyProTrue();
//
//                // add UI accordingly
//
//                Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
//            }
//
//            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
//        }
//    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (inAppBillingSetupOk) {
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    public void requestProVersion() {
        if (isPaymentAppInstalled) {
            if (inAppBillingSetupOk) {
                if (mHelper != null) {
                    mHelper.flagEndAsync();
                    mHelper.launchPurchaseFlow(this, ITEM_SKU, REQUEST_BUY_PRO,
                            mPurchaseFinishedListener, BUY_THE_AWESOME_STICKERGRAM_PRO_VERSION);
                }
            } else
                Toast.makeText(this, getString(R.string.purchase_is_unavailable), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.bazar_is_not_installed), Toast.LENGTH_SHORT).show();
        }
    }


    private void showErrorInPayment() {
        Toast.makeText(this, getString(R.string.payment_was_unsuccessful), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null)
            mHelper.dispose();
        mHelper = null;
    }

    public void setBuyProTrue() {
        Toast.makeText(this, getString(R.string.thanks_purchasing_the_app), Toast.LENGTH_LONG).show();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(HAS_BOUGHT_PRO, true);
        editor.apply();
        finish();
        startActivity(new Intent(this, MainActivity.class));
//        this.overridePendingTransition(0, 0);
    }

    public boolean getProStatus() {
//        if (BuildConfig.DEBUG) return true;
//            if (mIsPremium) return true;
        return preferences.getBoolean(HAS_BOUGHT_PRO, false);
//        }
//        return true;
//        return preferences.getBoolean(HAS_BOUGHT_PRO, false);
    }

    public void checkSignature(final Context context) {
        if ((context.getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            return;

        }
        if (Loader.checkLuckyPatcher(this)) {
            Toast.makeText(this, getString(R.string.uninstall_lucky_patcher), Toast.LENGTH_LONG).show();
            finish();
        }
        try {
            Signature[] signatures = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;

            if (!signatures[0].toCharsString().equals(AesCbcWithIntegrity.decryptString(new AesCbcWithIntegrity.CipherTextIvMac("rgKw/MerIRrbbaziThZ4bg==:kZcE/+0ggZIqDaWfrs4R4DehHHtPxkHTqO9DyRntF8s=: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"),
                    AesCbcWithIntegrity.keys(Constants.KEY)))) {
                Log.e(getClass().getSimpleName(), "signature was not a match");
                // Kill the process without warning. If someone changed the certificate
                // is better not to give a hint about why the app stopped working
                android.os.Process.killProcess(android.os.Process.myPid());
            } else Log.e(getClass().getSimpleName(), "signature was match");
        } catch (PackageManager.NameNotFoundException ex) {
            // Must never fail, so if it does, means someone played with the apk, so kill the process
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //todo make sure certificate is ok
}
