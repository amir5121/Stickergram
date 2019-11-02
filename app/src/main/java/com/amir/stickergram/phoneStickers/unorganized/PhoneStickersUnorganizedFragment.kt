package com.amir.stickergram.phoneStickers.unorganized

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.amir.stickergram.MainActivity
import com.amir.stickergram.PhoneStickersActivity
import com.amir.stickergram.R
import com.amir.stickergram.base.BaseActivity
import com.amir.stickergram.base.BaseFragment
import com.amir.stickergram.image.ImageReceiverCallBack
import com.amir.stickergram.infrastructure.AsyncTaskPhoneAdapter
import com.amir.stickergram.infrastructure.Constants
import com.amir.stickergram.infrastructure.Loader
import com.amir.stickergram.sticker.single.SingleStickersAdapter
import com.amir.stickergram.sticker.single.StickerItem

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

class PhoneStickersUnorganizedFragment : BaseFragment(), SingleStickersAdapter.OnStickerClickListener, SwipeRefreshLayout.OnRefreshListener, AsyncTaskPhoneAdapter.AsyncPhoneTaskListener {

    private var loadingTextPercentage: TextView? = null
    private var loadingStickersCount: TextView? = null
    private var noStickerText: TextView? = null
    private var dialog: AlertDialog? = null
    private var adapter: SingleStickersAdapter? = null
    private var swipeRefresh: SwipeRefreshLayout? = null

    private var selectedItems: ArrayList<StickerItem>? = null
    private var stickerCount = 0
    private var percent = 0

    private var onRefreshWasCalled = false
    private var listener: UnorganizedFragmentCallbacks? = null
    private var enable = true
    private var enableView: View? = null
    private var isAnImagePicker: Boolean = false
    private var imageListener: ImageReceiverCallBack? = null

    //    public View getRecyclerView() {
    //        return recyclerView;
    //    }

    //        Log.e(getClass().getSimpleName(), "enable: " + enable);
    var isEnable: Boolean
        get() = enable
        set(enable) {
            this.enable = enable
            enableView!!.isClickable = !enable
            if (enable) {
                enableView!!.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .start()
            } else {
                enableView!!.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            this.listener = context as UnorganizedFragmentCallbacks?
        } catch (e: ClassCastException) {
            throw RuntimeException(context.packageName + " must implement UnorganizedFragmentCallbacks")
        }

        try {
            this.imageListener = context as ImageReceiverCallBack?
        } catch (e: ClassCastException) {
            //do Nothing
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true

        val args = arguments
        if (args != null) {
            isAnImagePicker = args.getBoolean(Constants.IS_AN_IMAGE_PICKER, false)
        }

        val view = inflater.inflate(R.layout.fragment_phone_stickers_unorganized, container, false)

        setFont(view as ViewGroup)
        swipeRefresh = view.findViewById<View>(R.id.activity_phone_stickers_unorganized_swipeRefresh) as SwipeRefreshLayout
        val recyclerView = view.findViewById<View>(R.id.activity_phone_stickers_unorganized_list) as RecyclerView
        noStickerText = view.findViewById<View>(R.id.activity_phone_stickers_no_cached_text) as TextView

        enableView = view.findViewById(R.id.fragment_phone_stickers_unorganized_enable_view)

        if (swipeRefresh != null) {
            swipeRefresh!!.setOnRefreshListener(this)
            swipeRefresh!!.setColorSchemeColors(
                    Color.parseColor("#FF00DDFF"),
                    Color.parseColor("#FF99CC00"),
                    Color.parseColor("#FFFFBB33"),
                    Color.parseColor("#FFFF4444"))
        }
        adapter = SingleStickersAdapter((activity as BaseActivity?)!!, this)
        adapter!!.refreshPhoneSticker()
        if (BaseActivity.isTablet || BaseActivity.isInLandscape) {
            recyclerView.layoutManager = GridLayoutManager(context, 5)
        } else {
            recyclerView.layoutManager = GridLayoutManager(context, 3)
        }
        recyclerView.adapter = adapter

        isInCropMode = false
        enable = true
        if (savedInstanceState != null) {
            val typed_value = TypedValue()
            activity!!.theme.resolveAttribute(androidx.appcompat.R.attr.actionBarSize, typed_value, true)
            swipeRefresh!!.setProgressViewOffset(false, 0, resources.getDimensionPixelSize(typed_value.resourceId))
            swipeRefresh!!.isRefreshing = savedInstanceState.getBoolean(IS_REFRESHING, false)
            isEnable = savedInstanceState.getBoolean(IS_ENABLE, true)
            isInCropMode = savedInstanceState.getBoolean(IS_IN_CROP_MODE, false)
            swipeRefresh!!.isEnabled = !isInCropMode
            selectedItems = savedInstanceState.getParcelableArrayList(SELECTED_ITEMS)
            adapter!!.updateItems(selectedItems)
            adapter!!.notifyDataSetChanged()
        }

        if (!(activity as BaseActivity).hasCashedPhoneStickersOnce()) {
            callAsyncTaskPhoneAdapter()
        }

        if (savedInstanceState != null && loadingTextPercentage != null && loadingStickersCount != null) {
            percent = savedInstanceState.getInt(PERCENT)
            stickerCount = savedInstanceState.getInt(STICKER_COUNT)
            loadingTextPercentage!!.text = "$percent%"
            loadingStickersCount!!.text = stickerCount.toString()
        }


        return view
    }

    override fun OnStickerClicked(item: StickerItem) {
        if (!isAnImagePicker) {
            if (item.bitmap == null) {
                (activity as BaseActivity).setPhoneStickerCashStatus(false)
                callAsyncTaskPhoneAdapter()
            } else if (!isInCropMode) {
                Loader.loadStickerDialog(item.uri, (activity as BaseActivity?)!!)
            } else {
                manageSelectedItems(item)
            }
        } else {
            imageListener!!.receivedImage(item.bitmap)
        }
    }

    private fun manageSelectedItems(item: StickerItem) {
        if (selectedItems != null) {
            if (item.isSelected) {
                item.isSelected = false
                //                adapter.updateItem(item);
                selectedItems!!.remove(item)
                if (selectedItems!!.size == 0) {
                    selectedItems = null
                    toggleCutMode()
                }
            } else {
                item.isSelected = true
                //                adapter.updateItem(item);
                selectedItems!!.add(item)
            }
            adapter!!.notifyDataSetChanged()

        } else {
            Log.e(javaClass.getSimpleName(), "selectedItems was null")
        }
    }

    override fun OnStickerLongClicked(item: StickerItem) {
        if (!isAnImagePicker) {
            if (!isInCropMode) {
                selectedItems = ArrayList()
                toggleCutMode()
            }
            manageSelectedItems(item)
        }
    }

    fun toggleCutMode() {
        if (isInCropMode && selectedItems != null) {
            for (item in selectedItems!!)
                item.isSelected = false
        }
        isInCropMode = !isInCropMode
        listener!!.cutModeToggled(isInCropMode)
        swipeRefresh!!.isEnabled = !isInCropMode
        adapter!!.notifyDataSetChanged()
    }

    fun getSelectedItems(): List<StickerItem>? {
        return selectedItems
    }

    fun hideSelectedList() {
        if (selectedItems != null) {
            adapter!!.hideItems(selectedItems!!)

            adapter!!.refreshPhoneSticker()
        } else
            Log.e(javaClass.simpleName, "SelectedItems was null")
    }


    override fun OnNoItemExistedListener() {
        if (noStickerText != null) noStickerText!!.visibility = View.VISIBLE
    }

    override fun onRefresh() {
        onRefreshWasCalled = true
        Log.e(javaClass.simpleName, "OnRefresh was called")
        callAsyncTaskPhoneAdapter()
    }

    override fun onTaskStartListener() {
        if (!(activity as BaseActivity).hasCashedPhoneStickersOnce())
        //to not to show the loading firstLoadingDialog if user is doing a swipeRefresh
            instantiateLoadingDialog()
        else
            swipeRefresh!!.isRefreshing = true
    }

    @SuppressLint("SetTextI18n")
    override fun onTaskUpdateListener(percent: Int, stickerCount: Int) {
        this.percent = percent
        this.stickerCount = stickerCount

        if (loadingTextPercentage != null && loadingStickersCount != null) {
            var percentTemp = percent.toString()
            var stickerCountTemp = stickerCount.toString()
            if (Loader.deviceLanguageIsPersian()) {
                percentTemp = Loader.convertToPersianNumber(percentTemp)
                loadingTextPercentage!!.text = "% $percentTemp"
                stickerCountTemp = Loader.convertToPersianNumber(stickerCountTemp)
            } else
                loadingTextPercentage!!.text = "$percentTemp %"

            loadingStickersCount!!.text = stickerCountTemp

        }

    }

    override fun onTaskFinishedListener() {
        if (noStickerText != null) noStickerText!!.visibility = View.GONE
        manageView()
    }

    private fun manageView() {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }

        if (swipeRefresh != null)
            swipeRefresh!!.isRefreshing = false
        if (activity != null)
            (activity as BaseActivity).setPhoneStickerCashStatus(true)
        adapter!!.refreshPhoneSticker()
    }

    override fun onNoCashDirectoryListener() {
        if (!BaseActivity.chosenMode.isAvailable)
            Toast.makeText(activity, getString(R.string.telegram_is_not_installed), Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(activity, getString(R.string.couldn_t_find_telegram_cash_directory), Toast.LENGTH_LONG).show()
        if (!isAnImagePicker) {
            activity!!.finish()
        } else
            dialog!!.dismiss()
    }

    override fun onNoStickerWereFoundListener() {
        if (noStickerText != null)
            noStickerText!!.visibility = View.VISIBLE
        manageView()
    }

    override fun onRequestReadWritePermission() {
        Loader.gainPermission(activity as BaseActivity, Constants.PHONE_STICKERS_GAIN_PERMISSION)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IS_REFRESHING, swipeRefresh!!.isRefreshing)
        outState.putInt(STICKER_COUNT, stickerCount)
        outState.putInt(PERCENT, percent)
        outState.putBoolean(IS_ENABLE, enable)
        outState.putBoolean(IS_IN_CROP_MODE, isInCropMode)
        if (selectedItems != null) outState.putParcelableArrayList(SELECTED_ITEMS, selectedItems)
        super.onSaveInstanceState(outState)
    }

    private fun callAsyncTaskPhoneAdapter() {
        val file = File(BaseActivity.STICKERGRAM_ROOT + ".nomedia")
        File(BaseActivity.STICKERGRAM_ROOT).mkdirs()
        try {
            file.parentFile.mkdirs()
            Log.e(javaClass.simpleName, file.toString())
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (!swipeRefresh!!.isRefreshing || onRefreshWasCalled) {
            AsyncTaskPhoneAdapter(activity as BaseActivity, this).execute(adapter)
            onRefreshWasCalled = false
        }
    }

    private fun instantiateLoadingDialog() {
        val loadingDialogView = activity!!.layoutInflater.inflate(R.layout.dialog_phone_stickers_loading, null, false)
        setFont(loadingDialogView as ViewGroup)
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
            Log.e(javaClass.simpleName, "instantiateDialog firstLoadingDialog was set to null")
        }
        dialog = AlertDialog.Builder(context!!)
                .setView(loadingDialogView)
                .setCancelable(false)
                .create()

        dialog!!.show()

        loadingTextPercentage = loadingDialogView.findViewById<View>(R.id.phone_loading_dialog_text_percentage) as TextView
        loadingStickersCount = loadingDialogView.findViewById<View>(R.id.phone_loading_dialog_total_sticker) as TextView
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Constants.PHONE_STICKERS_GAIN_PERMISSION) {
            //            Log.e(getClass().getSimpleName(), "------here");
            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                activity!!.finish()
                startActivity(Intent(context, PhoneStickersActivity::class.java))
                activity!!.overridePendingTransition(0, 0)

                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                Log.e(javaClass.simpleName, "permission denied")
                activity!!.finish()
                startActivity(Intent(activity, MainActivity::class.java))
                activity!!.overridePendingTransition(0, 0)
                Toast.makeText(activity, resources.getString(R.string.need_permission_to_look_for_your_stickers), Toast.LENGTH_LONG).show()
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }

            return
        }
    }

    override fun onDestroy() {
        if (dialog != null) {
            Log.e(javaClass.simpleName, "onDestroy firstLoadingDialog was set to null")
            dialog!!.dismiss()
            dialog = null
        }
        super.onDestroy()
    }

    fun setSwipeRefreshEnable(enabled: Boolean) {
        swipeRefresh!!.isEnabled = enabled
    }


    interface UnorganizedFragmentCallbacks {
        fun cutModeToggled(enabled: Boolean)
    }

    companion object {

        private val TAG = "PhoneStickersUnorganizedFragment"
        private const val IS_REFRESHING = "IS_REFRESHING"
        private const val STICKER_COUNT = "STICKER_COUNT"
        private const val PERCENT = "PERCENT"
        private const val IS_ENABLE = "IS_ENABLE"
        private const val SELECTED_ITEMS = "SELECTED_ITEMS"
        private const val IS_IN_CROP_MODE = "IS_IN_CROP_MODE"

        var isInCropMode = false

        fun newInstance(isAnImagePicker: Boolean): BaseFragment {

            val args = Bundle()
            args.putBoolean(Constants.IS_AN_IMAGE_PICKER, isAnImagePicker)
            val fragment = PhoneStickersUnorganizedFragment()
            fragment.arguments = args
            return fragment
        }

    }
}
