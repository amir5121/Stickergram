package com.amir.stickergram

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.amir.stickergram.base.BaseActivity
import com.amir.stickergram.infrastructure.Constants
import com.amir.stickergram.infrastructure.Loader
import com.amir.stickergram.navdrawer.MainNavDrawer
import com.tangxiaolv.telegramgallery.GalleryActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_nav_drawer.*
import java.io.File


class MainActivity : BaseActivity(), View.OnClickListener {

    //    private static final int ANTHON_REQUEST_CODE = 222;

    private var pickAnImageDialog: AlertDialog? = null
    private var tempOutPutFile: File? = null // this is where the captured com.amir.stickergram.image would be saved

    //todo: use snackBar instead of Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setNavDrawer(MainNavDrawer(this))

        setUpView()
//        Loader.gainPermissionAnthon(this, ANTHON_REQUEST_CODE)

        //coming from topMarginAnimation share intent
        val mIntent = intent
        if (mIntent != null) {
            val action = mIntent.action
            if (action != null) {
                val type = mIntent.type
                if (type != null) {
                    if (action == Intent.ACTION_SEND) {
                        if (type.contains("image")) {
                            tempOutPutFile = Loader.generateEmptyBitmapFile(this)
                            Loader.crop(mIntent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri, Uri.fromFile(tempOutPutFile), this, false)
                        }
                    }
                }
            }
        }

        if (pickAnImageDialog != null)
            pickAnImageDialog!!.dismiss()

    }

    private fun setUpView() {

        activity_main_user_stickers_button.setOnClickListener(this)
        activity_main_phone_stickers.setOnClickListener(this)
        activity_main_start_scratch_stickers.setOnClickListener(this)
        activity_main_text_container.setOnClickListener(this)


        if (isInLandscape)
            activity_main_text_container.visibility = View.GONE


        setFont(nav_drawer as ViewGroup)
        setFont(activity_main_main_container as ViewGroup)

    }

    override fun onClick(view: View) {
        val itemId = view.id
        //TODO: Animation

        if (itemId == R.id.activity_main_user_stickers_button) {
            if (Loader.checkPermission(this)) {
                startActivity(Intent(this, UserStickersActivity::class.java))
            } else {
                Loader.gainPermission(this, Constants.USER_STICKER_GAIN_PERMISSION)
                Toast.makeText(this, resources.getString(R.string.need_permission_to_look_for_your_stickers), Toast.LENGTH_SHORT).show()
            }
        } else if (itemId == R.id.activity_main_phone_stickers) {
            if (Loader.checkPermission(this)) {
                startActivity(Intent(this, PhoneStickersActivity::class.java))
            } else {
                Loader.gainPermission(this, Constants.PHONE_STICKERS_GAIN_PERMISSION)
                Toast.makeText(this, resources.getString(R.string.need_permission_to_access_telegram_cache), Toast.LENGTH_SHORT).show()
            }

        } else if (itemId == R.id.activity_main_start_scratch_stickers) {
            if (Loader.checkPermission(this)) {
                instantiateChooserDialog()
            } else {
                Loader.gainPermission(this, Constants.FROM_SCRATCH_GAIN_PERMISSION)
            }
        } else if (itemId == R.id.dialog_from_scratch_empty_image) {
            if (Loader.checkPermission(this)) {
                val tempFile = Loader.generateEmptyBitmapFile(this)
                if (tempFile.exists() && tempOutPutFile!!.exists()) {
                    Loader.crop(Uri.fromFile(tempFile), Uri.fromFile(tempOutPutFile), this, true)
                }
            } else {
                Loader.gainPermission(this, Constants.FROM_SCRATCH_GAIN_PERMISSION)
            }
        } else if (itemId == R.id.dialog_from_scratch_choose_a_picture) {
            chooseOrCapturePicture()
        } else if (itemId == R.id.activity_main_text_container) {
            if (!BuildConfig.DEBUG)
                Loader.joinToStickergramChannel(this)
        }
    }


    private fun instantiateChooserDialog() {
        tempOutPutFile = Loader.generateEmptyBitmapFile(this)

        if (!tempOutPutFile!!.mkdirs())
            Log.e(javaClass.simpleName, "could not make directory")
        val view = layoutInflater.inflate(R.layout.dialog_from_scratch, null)
        setFont(view as ViewGroup)
        if (isInLandscape) {
            val linearLayout = view.findViewById<View>(R.id.dialog_from_scratch_main_container) as LinearLayout
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.setPadding(20, 20, 20, 20)
        }
        val emptyImage = view.findViewById<View>(R.id.dialog_from_scratch_empty_image)
        val fromPicture = view.findViewById<View>(R.id.dialog_from_scratch_choose_a_picture)
        if (emptyImage == null || fromPicture == null)
            return
        else {
            emptyImage.setOnClickListener(this)
            fromPicture.setOnClickListener(this)
        }
        pickAnImageDialog = AlertDialog.Builder(this)
                .setView(view)
                .create()
        pickAnImageDialog!!.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.USER_STICKER_GAIN_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(this, UserStickersActivity::class.java))
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, resources.getString(R.string.need_permission_to_look_for_your_stickers), Toast.LENGTH_LONG).show()
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                return
            }
            Constants.FROM_SCRATCH_GAIN_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    instantiateChooserDialog()
                }

                return
            }
            Constants.PHONE_STICKERS_GAIN_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(this, PhoneStickersActivity::class.java))
                } else {
                    Toast.makeText(this, resources.getString(R.string.need_permission_to_save_the_sticker), Toast.LENGTH_LONG).show()
                }
                return
            }
        }// Loader.PHONE_STICKERS_GAIN_PERMISSION and Loader.USER_STICKER_GAIN_PERMISSION is checked in the BaseActivity because they can be called in different places
        // other 'case' lines to check for other
        // permissions this app might request
    }


    //process result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK)
            return

        if (requestCode == GALLERY_REQUEST_CODE) {

            val photos = data?.getStringArrayListExtra(GalleryActivity.PHOTOS)
            photos?.let {

                for (s in photos) {

                    Log.e(javaClass.name, "onActivityResult: $s")
                }
                Loader.crop(Uri.fromFile(File((photos)[0])), Uri.fromFile(tempOutPutFile), this, false)
            }
        }
    }

    private fun chooseOrCapturePicture() {
        GalleryActivity.openActivity(this@MainActivity, GALLERY_REQUEST_CODE, Constants.galleryConfig)
    }


    override fun onPause() {
        super.onPause()

        if (pickAnImageDialog != null)
            pickAnImageDialog!!.dismiss()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (pickAnImageDialog != null)
            pickAnImageDialog!!.dismiss()
    }

    override fun onBackPressed() {
        Loader.exit(this)
    }

    companion object {
        //        private const val REQUEST_SELECT_IMAGE = 999
//        private const val MAIN_ACTIVITY_SEQUENCE_ID = "MAIN_ACTIVITY_SEQUENCE_ID"
        const val GALLERY_REQUEST_CODE = 5654
//        private const val TAG = "MainActivity"
    }
}