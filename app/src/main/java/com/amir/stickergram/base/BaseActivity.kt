package com.amir.stickergram.base

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import com.amir.stickergram.AppType
import com.amir.stickergram.R
import com.amir.stickergram.UserStickersActivity
import com.amir.stickergram.infrastructure.Constants
import com.amir.stickergram.infrastructure.Loader
import com.amir.stickergram.mode.Mode
import com.amir.stickergram.navdrawer.NavDrawer
import java.io.File

abstract class BaseActivity : BaseAuthenticatedActivity() {

    private var preferences: SharedPreferences? = null

    var toolbar: Toolbar? = null
    private var navDrawer: NavDrawer? = null

    var userLanguage = 0

    val cachedJson: String?
        get() = preferences!!.getString(Constants.CACHED_JSON, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences(Constants.SETTING, Context.MODE_PRIVATE)

        setLanguage(preferences!!.getInt(Constants.LANGUAGE, AppType.DEFAULT_LANGUAGE))

        val metrics = resources.displayMetrics
        isTablet = metrics.widthPixels / metrics.density >= 600
        isInLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        density = resources.displayMetrics.density
        val externalCacheDir = externalCacheDir!!
        BASE_USER_THUMBNAIL_DIRECTORY = externalCacheDir.toString() + File.separator + "thumb_Stickers"
        BASE_PHONE_ORGANIZED_THUMBNAIL_DIRECTORY = externalCacheDir.toString() + File.separator + "thumb_phone_organized_Stickers"
        TEMP_STICKER_CASH_DIR = externalCacheDir.toString() + File.separator + "temp_sticker.png"
        TEMP_CROP_CASH_DIR = externalCacheDir.toString() + File.separator + "temp_crop.png"
        CACHE_DIR = cacheDir.absolutePath + "/"

        val externalDirectory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + Constants.STICKERGRAM
        else
            Environment.getExternalStorageDirectory().toString() + Constants.STICKERGRAM

        PICTURES_DIRECTORY = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + Constants.STICKERGRAM + File.separator
        } else {
            Environment.getExternalStorageDirectory().toString() + File.separator + "Pictures" + Constants.STICKERGRAM + File.separator
        }

        STICKERGRAM_ROOT = externalDirectory + File.separator
        USER_STICKERS_DIRECTORY = "$externalDirectory/.user/"
        BASE_PHONE_ORGANIZED_STICKERS_DIRECTORY = "$externalDirectory/.phone_organized/"
        BASE_PHONE_WHATSAPP_WEBP_DIRECTORY = "$externalDirectory/.webps/"
        FONT_DIRECTORY = "$externalDirectory/font/"



        chosenMode = Mode(preferences!!.getString(Constants.ACTIVE_PACK, null), this)
        if (chosenMode.pack == null) {
            val modes = Loader.getAllAvailableModes(this)
            if (modes.size > 0)
            //when one of the supported modes is installed
                setDefaultMode(modes[0])
            if (chosenMode.pack == null) {// if none of the supported modes is installed
                setDefaultMode(Mode(Constants.TELEGRAM_PACKAGE, this))
                Log.e(javaClass.simpleName, "no type of supported telegram was found and chosenMode was defaulted to original telegram")
            }
        }

        //        if (Loader.freeMemory() < 50 && !hasCashedPhoneStickersOnce()) {
        //            Toast.makeText(this, getString(R.string.low_storage_finish), Toast.LENGTH_LONG).show();
        //            Toast.makeText(this, getString(R.string.low_storage_finish), Toast.LENGTH_LONG).show();
        ////            finish();
        //        } else


    }


    fun setFont(textView: TextView?) {
        if (textView != null)
            if (Loader.deviceLanguageIsPersian())
                textView.typeface = Typeface.createFromAsset(assets, Constants.APPLICATION_PERSIAN_FONT_ADDRESS_IN_ASSET)
            else
                textView.typeface = Typeface.createFromAsset(assets, Constants.APPLICATION_ENGLISH_FONT_ADDRESS_IN_ASSET)
    }


    fun setFont(group: ViewGroup?) {
        if (group != null) {
            val count = group.childCount
            var v: View
            for (i in 0 until count) {
                v = group.getChildAt(i)
                if (v is TextView) {
                    setFont(v)
                } else if (v is ViewGroup)
                    setFont(v)
            }
        } else {
            Log.e(javaClass.simpleName, "viewGroup was null")
        }
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)

        toolbar = findViewById(R.id.include_toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            val ab = supportActionBar
            if (ab != null)
                ab.title = getString(R.string.app_name)
        }
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun setNavDrawer(navDrawer: NavDrawer) {
        this.navDrawer = navDrawer
        navDrawer.create()
    }

    fun hasCashedPhoneStickersOnce(): Boolean {
        return preferences!!.getBoolean(Constants.HAS_CASHED_PHONE_STICKERS, false)
    }

    fun setPhoneStickerCashStatus(status: Boolean) {
        val editor = preferences!!.edit()
        editor.putBoolean(Constants.HAS_CASHED_PHONE_STICKERS, status)
        editor.apply()
    }

    fun hasCashedPackStickers(): Boolean {
        return preferences!!.getBoolean(Constants.HAS_CASHED_PACK_STICKERS, false)
    }

    fun setPackCashStatus(status: Boolean) {
        val editor = preferences!!.edit()
        editor.putBoolean(Constants.HAS_CASHED_PACK_STICKERS, status)
        editor.apply()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Constants.USER_STICKER_GAIN_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this, UserStickersActivity::class.java))
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                Toast.makeText(this, resources.getString(R.string.need_permission_to_look_for_your_stickers), Toast.LENGTH_LONG).show()
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }

        }
    }

    fun setDefaultMode(mode: Mode) {
        chosenMode = mode
        val editor = preferences!!.edit()
        editor.putString(Constants.ACTIVE_PACK, mode.pack)
        editor.apply()
    }

    fun setLanguage(language: Int) {
        if (userLanguage != language) {
            userLanguage = language
            val editor = preferences!!.edit()
            editor.putInt(Constants.LANGUAGE, language)
            editor.apply()

            Loader.setLocale(language, this)
        }
    }

    fun cacheJsonResponse(response: String) {
        val editor = preferences!!.edit()
        editor.putString(Constants.CACHED_JSON, response)
        editor.apply()
    }

    fun restartActivity() {
        val refresh = Intent(this, this.javaClass)
        startActivity(refresh)
        finish()
    }

    companion object {
        private val TAG = "BaseActivity"
        var density: Float = 0.toFloat()
        lateinit var CACHE_DIR: String
        lateinit var TEMP_STICKER_CASH_DIR: String
        lateinit var TEMP_CROP_CASH_DIR: String
        lateinit var BASE_USER_THUMBNAIL_DIRECTORY: String
        lateinit var BASE_PHONE_ORGANIZED_THUMBNAIL_DIRECTORY: String
        lateinit var STICKERGRAM_ROOT: String
        lateinit var USER_STICKERS_DIRECTORY: String
        lateinit var PICTURES_DIRECTORY: String
        lateinit var BASE_PHONE_WHATSAPP_WEBP_DIRECTORY: String
        lateinit var BASE_PHONE_ORGANIZED_STICKERS_DIRECTORY: String
        lateinit var FONT_DIRECTORY: String

        var isTablet: Boolean = false
        var isInLandscape: Boolean = false

        lateinit var chosenMode: Mode
    }

}
