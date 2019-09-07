package com.amir.stickergram

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import android.widget.Toast
import com.amir.stickergram.backgroundRmover.BackgroundRemoverFragment
import com.amir.stickergram.backgroundRmover.CropFragment
import com.amir.stickergram.base.BaseActivity
import com.amir.stickergram.imagePadder.ImagePadderFragment
import com.amir.stickergram.infrastructure.Constants
import kotlinx.android.synthetic.main.activity_crop.*

class CropActivity : BaseActivity(), CropFragment.CropFragmentCallbacks, BackgroundRemoverFragment.BackgroundRemoverFragmentCallbacks {
    private var hasUsedAnEmptyImage = false
    private var launchedToAddImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        setFont(activity_crop_main_container as ViewGroup)

        if (savedInstanceState != null) return

        val bundle = Bundle()
        val intent = intent
        if (intent != null) {
            hasUsedAnEmptyImage = intent.getBooleanExtra(Constants.IS_USING_EMPTY_IMAGE, false)
            launchedToAddImage = intent.getBooleanExtra(Constants.LAUNCHED_TO_ADD_IMAGE, false)
            bundle.putParcelable(Constants.CROP_SOURCE, intent.getParcelableExtra<Parcelable>(Constants.CROP_SOURCE))
            val destinyUri = intent.getParcelableExtra<Uri>(Constants.CROP_DESTINY)
            bundle.putParcelable(Constants.CROP_DESTINY, destinyUri)

        }

        supportActionBar!!.title = getString(R.string.crop)
        supportFragmentManager.beginTransaction().add(R.id.crop_fragment_container, CropFragment.newInstance(bundle)).commit()


    }

    override fun cropFinished(bundle: Bundle) {

        if (hasUsedAnEmptyImage) {
            val intent = Intent(this, EditImageActivity::class.java)
            //            intent.putExtra(Constants.EDIT_IMAGE_URI, destinyUri);
            intent.putExtra(Constants.EDIT_IMAGE_URI, bundle.getParcelable<Parcelable>(Constants.EDIT_IMAGE_URI))
            startActivity(intent)
            finish()
        } else {
            supportActionBar!!.title = getString(R.string.background_remover)
            supportFragmentManager.beginTransaction().replace(R.id.crop_fragment_container, BackgroundRemoverFragment.getInstance(bundle)).commit()
        }
    }

    private var mBackPressed: Long = 0

    override fun onBackPressed() {
        if (mBackPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            return
        } else {
            Toast.makeText(baseContext, getString(R.string.press_back_button_again), Toast.LENGTH_SHORT).show()
        }
        mBackPressed = System.currentTimeMillis()
    }

    override fun backgroundRemoverFinished(finishedBitmap: Bitmap) {
        supportActionBar!!.title = getString(R.string.image_stroke)
        supportFragmentManager.beginTransaction().replace(R.id.crop_fragment_container, ImagePadderFragment.getInstance(finishedBitmap, launchedToAddImage)).commit()
    }
}
