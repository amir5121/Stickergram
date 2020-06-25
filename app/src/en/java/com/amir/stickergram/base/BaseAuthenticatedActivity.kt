package com.amir.stickergram.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amir.stickergram.MainActivity
import com.amir.stickergram.R
import com.amir.stickergram.infrastructure.Constants
import com.amir.stickergram.infrastructure.Loader.isAppInstalled
import com.amir.stickergram.util.IabHelper
import com.amir.stickergram.util.IabHelper.*
import com.amir.stickergram.util.IabResult
import com.amir.stickergram.util.Purchase
import com.tozny.crypto.android.AesCbcWithIntegrity
import com.tozny.crypto.android.AesCbcWithIntegrity.CipherTextIvMac
import java.io.UnsupportedEncodingException
import java.security.GeneralSecurityException
import java.util.*

abstract class BaseAuthenticatedActivity : AppCompatActivity() {
    private var preferences: SharedPreferences? = null
    private var mHelper: IabHelper? = null

    //    private static final String uuid = generateUUID();
    private var mPurchaseFinishedListener: OnIabPurchaseFinishedListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkSignature(this)
        preferences = getSharedPreferences(Constants.SETTING, Context.MODE_PRIVATE)
        isPaid = proStatus
        val base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApnUyi0eqv00Cxxqt0uU6cpLdXTvDw+S9/JhMBShXvVRkMxaA/kW4osqBRJwlYCAMUu9uilSWZmj9nAMB6wcBmkLllBSeNMMeVnBweecJaOUnfv2yNt09EU5JVwMQuHxXG+FsPc/wHOCboHaRQKaqWXQZUZvt+J9BkwQaPB1Ho3zAUnbo8ot6ycXmvKsAay3uIkalztCnKoMJOohft3LUwT6Fh2gXCn1KBnEBzRWPHltO219HeFuKTBqNw1A3cwoTBrm7MxIluaA8Cg7tLDmesOqrHCRAunOS9c7TPDmTOpsRuapwHJUpf9Hy4QOjfH8Y37BDxugrRniksxnZg41sLwIDAQAB"
        if (isAppInstalled(this, GOOGLE_PLAY_SERVICES_PACKAGE)) isPaymentAppInstalled = true
        if (isPaymentAppInstalled) {
            mHelper = IabHelper(this, base64EncodedPublicKey)
            mHelper!!.startSetup { result ->
                if (!result.isSuccess) {
                    Log.d(TAG, "In-app Billing setup failed: " +
                            result)
                } else {
                    inAppBillingSetupOk = true
                    Log.d(TAG, "In-app Billing is set up OK")
                    try {
                        if (!isPaid) mHelper!!.queryInventoryAsync(mGotInventoryListener)
                    } catch (e: IabAsyncInProgressException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        mPurchaseFinishedListener = object : OnIabPurchaseFinishedListener {
            override fun onIabPurchaseFinished(result: IabResult, purchase: Purchase) {
                if (result.isFailure) {
                    //Toast.makeText(BaseAuthenticatedActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
                    showErrorInPayment()
                } else if (purchase.sku == ITEM_SKU) {
                    Log.e(javaClass.simpleName, "Buy Pro")
                    setBuyProTrue()
                }
            }
        }

//        Toast.makeText(this, uuid, Toast.LENGTH_LONG).show();
    }

    // Listener that's called when we finish querying the items and
    // subscriptions we own
    var mGotInventoryListener = QueryInventoryFinishedListener { result, inventory ->
        Log.d(TAG, "Query inventory finished.")
        if (result.isFailure) {
            Log.e(TAG, "Query inventory failed.")
            //complain("Failed to query inventory: " + result);
            //showErrorInPayment();
            return@QueryInventoryFinishedListener
        }
        Log.d(TAG, "Query inventory was successful.")

        /*
         * Check for items we own. Notice that for each purchase, we check
         * the developer payload to see if it's correct! See
         * verifyDeveloperPayload().
         */

        // // Check for gas delivery -- if we own gas, we should fill up the
        // tank immediately
        val gasPurchase = inventory.getPurchase(ITEM_SKU)
        if (gasPurchase != null //&& verifyDeveloperPayload(gasPurchase)
        ) {
            Log.d(TAG, "We have gas. Consuming it.")
            setBuyProTrue()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (inAppBillingSetupOk) {
            if (!mHelper!!.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data)
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    fun requestProVersion() {
        try {
            if (isPaymentAppInstalled) {
                if (inAppBillingSetupOk) {
                    if (mHelper != null) {
                        mHelper!!.flagEndAsync()
                        mHelper!!.launchPurchaseFlow(this, ITEM_SKU, REQUEST_BUY_PRO,
                                mPurchaseFinishedListener, BUY_THE_AWESOME_STICKERGRAM_PRO_VERSION)
                    }
                } else Toast.makeText(this, getString(R.string.purchase_is_unavailable), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, getString(R.string.google_play_services_is_not_installed), Toast.LENGTH_SHORT).show()
            }
        } catch (e: IabAsyncInProgressException) {
            e.printStackTrace()
        }
    }

    private fun showErrorInPayment() {
        Toast.makeText(this, getString(R.string.payment_was_unsuccessful), Toast.LENGTH_LONG).show()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mHelper != null) try {
            mHelper!!.dispose()
        } catch (e: IabAsyncInProgressException) {
            e.printStackTrace()
        }
        mHelper = null
    }

    fun setBuyProTrue() {
        Toast.makeText(this, getString(R.string.thanks_purchasing_the_app), Toast.LENGTH_LONG).show()
        val editor = preferences!!.edit()
        editor.putBoolean(HAS_BOUGHT_PRO, true)
        editor.apply()
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    //        if (uuid.equals("ffffffff-d7da-9c68-ffff-ffffb45f73eb"))
//            return true;
//        if (BuildConfig.DEBUG) {
////            Toast.makeText(this, "is in debug mode", Toast.LENGTH_SHORT).show();
//            return true;
//        }
    //        }
//        return true;
//        return preferences.getBoolean(HAS_BOUGHT_PRO, false);
    protected val proStatus: Boolean
        protected get() =//        if (uuid.equals("ffffffff-d7da-9c68-ffff-ffffb45f73eb"))
//            return true;
//        if (BuildConfig.DEBUG) {
////            Toast.makeText(this, "is in debug mode", Toast.LENGTH_SHORT).show();
//            return true;
//        }
            preferences!!.getBoolean(HAS_BOUGHT_PRO, false)
    //        }
//        return true;
//        return preferences.getBoolean(HAS_BOUGHT_PRO, false);

    private fun checkSignature(context: Context) {
        if (context.applicationContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            return
        }
        //        if (Loader.checkLuckyPatcher(this)) {
//            Toast.makeText(this, getString(R.string.uninstall_lucky_patcher), Toast.LENGTH_LONG).show();
//            finish();
//        }
        try {
            @SuppressLint("PackageManagerGetSignatures") val signatures = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES).signatures
            if (signatures[0].toCharsString() != AesCbcWithIntegrity.decryptString(CipherTextIvMac("rgKw/MerIRrbbaziThZ4bg==:kZcE/+0ggZIqDaWfrs4R4DehHHtPxkHTqO9DyRntF8s=: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"),
                            AesCbcWithIntegrity.keys(Constants.KEY))) {
                Log.e(javaClass.simpleName, "signature was not a match")
                // Kill the process without warning. If someone changed the certificate
                // is better not to give a hint about why the app stopped working
                Process.killProcess(Process.myPid())
            }
        } catch (ex: PackageManager.NameNotFoundException) {
            // Must never fail, so if it does, means someone played with the apk, so kill the process
            Process.killProcess(Process.myPid())
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    } //todo make sure certificate is ok

    companion object {
        //todo: https://code.google.com/p/android/issues/detail?id=203555 android N support
        private const val TAG = "BaseAuthenticated"
        private const val REQUEST_BUY_PRO = 1001
        const val BUY_THE_AWESOME_STICKERGRAM_PRO_VERSION = "BuyTheAwesomeStickergramProVersion"
        private const val GOOGLE_PLAY_SERVICES_PACKAGE = "com.google.android.gms"
        @JvmField
        var isPaid = false
        private const val HAS_BOUGHT_PRO = "HAS_BOUGHT_PRO"
        private const val ITEM_SKU = "com.amir.stickergram.pro"
        private var inAppBillingSetupOk = false
        const val STROKE_WIDTH = 0
        private var isPaymentAppInstalled = false
        private fun generateUUID(): String {
            val uniquePseudoID = "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10
            val serial = Build.getRadioVersion()
            return UUID(uniquePseudoID.hashCode().toLong(), serial.hashCode().toLong()).toString()
        }
    }
}