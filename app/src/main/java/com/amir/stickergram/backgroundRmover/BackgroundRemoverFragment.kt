package com.amir.stickergram.backgroundRmover

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

import com.amir.stickergram.R
import com.amir.stickergram.base.BaseActivity
import com.amir.stickergram.base.BaseFragment
import com.amir.stickergram.infrastructure.Constants

import java.io.IOException

import app.minimize.com.seek_bar_compat.SeekBarCompat

class BackgroundRemoverFragment : BaseFragment(), View.OnClickListener, RemoverView.RemoverViewCallbacks, SeekBar.OnSeekBarChangeListener {
    private var removerView: RemoverView? = null
    private var pointerViewBottom: PointerViewBottom? = null
    private var modeButton: ImageButton? = null
    private var radiusSeekBar: SeekBarCompat? = null
    private var offsetSeekBar: SeekBarCompat? = null
    private var toleranceSeekBar: SeekBarCompat? = null
    private var radiusContainer: View? = null
    private var offsetContainer: View? = null
    private var toleranceContainer: View? = null
    private var zoomToggleButton: ImageButton? = null
    private var loadingDialog: View? = null
    private var applyFloodFillMode = false
    private var floodFillerButton: ImageButton? = null
    private var listener: BackgroundRemoverFragmentCallbacks? = null
    private var backgroundButton: ImageButton? = null
    private var chessBackground: View? = null
    private var removeTag: TextView? = null
    private var hintContainer: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        try {
            listener = activity as BackgroundRemoverFragmentCallbacks?
        } catch (e: ClassCastException) {
            throw RuntimeException("Parent activity must implement BackgroundRemoverFragmentCallbacks")
        }

        val view = inflater.inflate(R.layout.fragment_remove_background, container, false)
        setFont(view as ViewGroup)
        modeButton = view.findViewById<View>(R.id.fragment_remove_background_repair_toggle_mode) as ImageButton
        modeButton!!.setOnClickListener(this)

        view.findViewById<View>(R.id.fragment_remove_background_radius_button).setOnClickListener(this)
        radiusSeekBar = view.findViewById<View>(R.id.fragment_remove_background_radius_seek_bar) as SeekBarCompat
        radiusSeekBar!!.setOnSeekBarChangeListener(this)
        radiusContainer = view.findViewById(R.id.fragment_remove_background_radius_container)

        zoomToggleButton = view.findViewById<View>(R.id.fragment_remove_background_mode_zoom_toggle) as ImageButton
        zoomToggleButton!!.setOnClickListener(this)

        loadingDialog = view.findViewById(R.id.fragment_remove_background_loading_dialog)

        offsetSeekBar = view.findViewById<View>(R.id.fragment_remove_background_offset_seek_bar) as SeekBarCompat
        offsetSeekBar!!.setOnSeekBarChangeListener(this)
        offsetContainer = view.findViewById(R.id.fragment_remove_background_offset_container)

        view.findViewById<View>(R.id.fragment_remove_background_mode_offset).setOnClickListener(this)

        backgroundButton = view.findViewById<View>(R.id.fragment_remove_background_background_button) as ImageButton
        backgroundButton!!.setOnClickListener(this)

        chessBackground = view.findViewById(R.id.fragment_remove_background_background)

        floodFillerButton = view.findViewById<View>(R.id.fragment_remove_background_flood_filler) as ImageButton
        floodFillerButton!!.setOnClickListener(this)
        toleranceSeekBar = view.findViewById<View>(R.id.fragment_remove_background_tolerance_seek_bar) as SeekBarCompat
        toleranceSeekBar!!.setOnSeekBarChangeListener(this)
        toleranceContainer = view.findViewById(R.id.fragment_remove_background_tolerance_container)

        removeTag = view.findViewById<View>(R.id.fragment_remove_background_remove_tag) as TextView
        hintContainer = view.findViewById(R.id.fragment_remove_background_hint_container)

        view.findViewById<View>(R.id.fragment_remove_background_hint_close_button).setOnClickListener(this)


        //            Bitmap mBitmap = Constants.getWorkingBitmap();
        //            if (mBitmap == null)
        val info = arguments
        if (info != null) {
            try {
                var mBitmap: Bitmap? = MediaStore.Images.Media.getBitmap(
                        activity!!.contentResolver, info.getParcelable<Parcelable>(Constants.EDIT_IMAGE_URI) as Uri)
                //                Loader.rotateImage(mBitmap, info.getInt(Constants.IMAGE_ROTATION));
                //            Constants.setWorkingBitmap(mBitmap);
                if (mBitmap != null) {
                    mBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true)
                    if (savedInstanceState != null)
                        mBitmap = if (savedInstanceState.getParcelable<Parcelable>(BITMAP_EXTRA) != null)
                            savedInstanceState.getParcelable<Parcelable>(BITMAP_EXTRA) as Bitmap?
                        else
                            mBitmap

                    removerView = RemoverView(activity as BaseActivity, this, mBitmap)
                } else {
                    Log.e(javaClass.simpleName, "bitmap was null")
                    Toast.makeText(context, getString(R.string.there_was_a_problem_getting_the_picture), Toast.LENGTH_LONG).show()
                    activity!!.finish()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            activity!!.finish()
            Toast.makeText(context, getString(R.string.there_was_a_problem_getting_the_picture), Toast.LENGTH_SHORT).show()
        }
        pointerViewBottom = PointerViewBottom(context)
        val surfaceContainer = view.findViewById<View>(R.id.fragment_remove_background_surface_container) as RelativeLayout
        surfaceContainer.addView(removerView)
        surfaceContainer.addView(pointerViewBottom)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BITMAP_EXTRA, removerView!!.finishedBitmap)
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu!!.clear()
        inflater!!.inflate(R.menu.crop_activity_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.crop_activity_menu_save) {
            //            File file = new File(BaseActivity.TEMP_CROP_CASH_DIR);
            //            createFolderStructure(file);
            //            try {
            //                removerView.getFinishedBitmap().compress(Bitmap.CompressFormat.PNG, 85, new FileOutputStream(file));
            listener!!.backgroundRemoverFinished(removerView!!.finishedBitmap!!)
            //                Intent intent = new Intent(getContext(), EditImageActivity.class);
            //                intent.putExtra(Constants.EDIT_IMAGE_URI, Uri.fromFile(file));
            //                startActivity(intent);
            //                getActivity().finish();

            //            } catch (FileNotFoundException e) {
            //                e.printStackTrace();
            //            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View) {
        val itemId = view.id
        if (itemId == R.id.fragment_remove_background_repair_toggle_mode) {
            if (removerView!!.removeToggle()) {
                removeTag!!.text = getText(R.string.remove)
                switchTo(REMOVE)
            } else {
                removeTag!!.text = getText(R.string.repair)
                switchTo(REPAIR)
                hintContainer!!.visibility = View.GONE
            }
        } else if (itemId == R.id.fragment_remove_background_mode_zoom_toggle) {
            if (removerView!!.changeZoomMode()) {
                //zoom mode enabled
                switchTo(ZOOM_ON)
            } else {
                switchTo(ZOOM_OFF)
            }
        } else if (itemId == R.id.fragment_remove_background_radius_button) {
            manageSeekBarsVisibility(radiusContainer)
            radiusSeekBar!!.progress = removerView!!.radius
        } else if (itemId == R.id.fragment_remove_background_mode_offset) {
            manageSeekBarsVisibility(offsetContainer)
            offsetSeekBar!!.progress = removerView!!.offset
        } else if (itemId == R.id.fragment_remove_background_flood_filler) {
            if (!applyFloodFillMode) {
                switchTo(SHOW_FLOOD_POINTER)
            } else {
                switchTo(APPLY_FLOOD)

            }
        } else if (itemId == R.id.fragment_remove_background_background_button) {
            if (chessBackground!!.visibility == View.GONE) {
                backgroundButton!!.setImageResource(R.drawable.ic_chess_white)
                backgroundButton!!.setBackgroundColor(LIGHT_BLUE)
                chessBackground!!.visibility = View.VISIBLE
            } else {
                backgroundButton!!.setImageResource(R.drawable.ic_chess_blue)
                backgroundButton!!.setBackgroundColor(Color.WHITE)
                chessBackground!!.visibility = View.GONE
            }
        } else if (itemId == R.id.fragment_remove_background_hint_close_button) {
            hintContainer!!.visibility = View.GONE
        }
    }

    private fun switchTo(mode: Int) {
        manageSeekBarsVisibility(null)
        when (mode) {
            REMOVE -> {
                removerView!!.setUsingFloodFillPointer(false)
                modeButton!!.setImageResource(R.drawable.ic_remove_blue)
                modeButton!!.setBackgroundColor(LIGHT_BLUE)
                dismissFloodPointer()
            }
            REPAIR -> {
                removerView!!.setUsingFloodFillPointer(false)
                modeButton!!.setImageResource(R.drawable.ic_repair)
                modeButton!!.setBackgroundColor(Color.WHITE)
                dismissFloodPointer()
            }
            ZOOM_ON -> {
                zoomToggleButton!!.setImageResource(R.drawable.ic_hand_blue)
                zoomToggleButton!!.setBackgroundColor(Color.WHITE)
                dismissFloodPointer()
            }
            ZOOM_OFF -> {
                zoomToggleButton!!.setImageResource(R.drawable.ic_hand_white)
                zoomToggleButton!!.setBackgroundColor(LIGHT_BLUE)
                dismissFloodPointer()
            }
            SHOW_FLOOD_POINTER -> {
                manageSeekBarsVisibility(toleranceContainer)
                toleranceSeekBar!!.progress = removerView!!.tolerance
                removerView!!.setUsingFloodFillPointer(true)
                floodFillerButton!!.setBackgroundColor(Color.WHITE)
                floodFillerButton!!.setImageResource(R.drawable.ic_done_blue)
                setApplyFloodFillMode(true)
            }
            APPLY_FLOOD -> {
                removerView!!.floodFill()
                removerView!!.setUsingFloodFillPointer(false)
                floodFillerButton!!.setBackgroundColor(LIGHT_BLUE)
                floodFillerButton!!.setImageResource(R.drawable.ic_flood_fill)
            }
        }

    }

    private fun dismissFloodPointer() {
        removerView!!.setUsingFloodFillPointer(false)
        floodFillerButton!!.setBackgroundColor(Color.parseColor("#1565c0"))
        floodFillerButton!!.setImageResource(R.drawable.ic_flood_fill)
        setApplyFloodFillMode(false)
    }

    private fun manageSeekBarsVisibility(view: View?) {
        var isVisible = false
        if (view != null)
            isVisible = view.visibility == View.VISIBLE
        radiusContainer!!.visibility = View.GONE
        offsetContainer!!.visibility = View.GONE
        toleranceContainer!!.visibility = View.GONE

        if (!isVisible && view != null) {
            view.visibility = View.VISIBLE
        }
    }


    override fun updateBottomPointer(top: Float, left: Float, scale: Float) {
        pointerViewBottom!!.updatePointer(top, left)
    }

    override fun floodFillerFinished() {
        loadingDialogDismiss(true)
        setApplyFloodFillMode(false)
        manageSeekBarsVisibility(null)
        //        applyFloodContainer.setVisibility(View.VISIBLE);
    }

    override fun floodFillerStarted() {
        loadingDialogDismiss(false)
    }

    override fun dismissSeekBarContainers() {
        manageSeekBarsVisibility(null)
    }

    private fun loadingDialogDismiss(dismiss: Boolean) {
        if (dismiss)
            loadingDialog!!.visibility = View.GONE
        else
            loadingDialog!!.visibility = View.VISIBLE
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        //        int seekBarId = seekBar.getId();
        if (seekBar === radiusSeekBar) {
            removerView!!.radius = seekBar.getProgress()
        } else if (seekBar === offsetSeekBar) {
            removerView!!.offset = seekBar.getProgress()
        } else if (seekBar === toleranceSeekBar) {
            removerView!!.tolerance = seekBar.getProgress()
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {

    }

    fun setApplyFloodFillMode(applyFloodFillMode: Boolean) {
        this.applyFloodFillMode = applyFloodFillMode
    }

    interface BackgroundRemoverFragmentCallbacks {
        fun backgroundRemoverFinished(finishedBitmap: Bitmap)
    }

    companion object {
        private val BITMAP_EXTRA = "BITMAP_EXTRA"
        private val REMOVE = 0
        private val REPAIR = 1
        private val ZOOM_ON = 2
        private val ZOOM_OFF = 3
        private val SHOW_FLOOD_POINTER = 4
        private val APPLY_FLOOD = 5
        private val BITMAP_WIDTH = "BITMAP_WIDTH"
        private val BITMAP_HEIGHT = "BITMAP_HEIGHT"
        private val LIGHT_BLUE = Color.parseColor("#1565c0")

        fun getInstance(bundle: Bundle): Fragment {
            val fragment = BackgroundRemoverFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}
