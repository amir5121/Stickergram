package com.amir.stickergram

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.amir.stickergram.base.BaseActivity
import com.amir.stickergram.navdrawer.MainNavDrawer
import com.amir.stickergram.phoneStickers.AsyncStickersCut
import com.amir.stickergram.phoneStickers.CustomRecyclerView
import com.amir.stickergram.phoneStickers.organizedDetailed.OrganizedStickersDetailedDialogFragment
import com.amir.stickergram.phoneStickers.organizedIcon.OnStickerClickListener
import com.amir.stickergram.phoneStickers.organizedIcon.OrganizedStickersIconFragment
import com.amir.stickergram.phoneStickers.unorganized.PhoneStickersUnorganizedFragment
import com.amir.stickergram.sticker.icon.IconItem

class PhoneStickersActivity : BaseActivity(), OnStickerClickListener, AsyncStickersCut.AsyncCutCallbacks, CustomRecyclerView.RecyclerViewMovementCallbacks, PhoneStickersUnorganizedFragment.UnorganizedFragmentCallbacks {

    private var unorganizedFragment: PhoneStickersUnorganizedFragment? = null
    private var loadingFrame: View? = null
    private var organizedFragmentView: View? = null
    private var organizedFragmentHeight = 0
    private var isOrganizedFragmentHidden = false
    private var lastClickedIcon: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_stickers)
        setNavDrawer(MainNavDrawer(this))

        setFont(findViewById<View>(R.id.nav_drawer) as ViewGroup)
        setFont(findViewById<View>(R.id.activity_phone_stickers_main_container) as ViewGroup)

        unorganizedFragment = supportFragmentManager.findFragmentById(R.id.activity_phone_stickers_phone_stickers_unorganized_fragment) as PhoneStickersUnorganizedFragment?

        //        View unorganizedFragmentView = findViewById(R.id.activity_phone_stickers_organized_fragment_container);
        organizedFragmentView = findViewById(R.id.activity_phone_stickers_phone_stickers_organized_fragment)

        loadingFrame = findViewById(R.id.activity_phone_stickers_loading_frame)

        if (PhoneStickersUnorganizedFragment.isInCropMode) onSlideUpCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.phone_sticker_unoraganized_fragment_menu, menu)
        menu.getItem(1).isVisible = PhoneStickersUnorganizedFragment.isInCropMode
        menu.getItem(0).isVisible = !PhoneStickersUnorganizedFragment.isInCropMode
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.phone_sticker_activity_menu_item_refresh) {
            unorganizedFragment!!.onRefresh()
            Toast.makeText(this, getString(R.string.sweep_refresh_is_also_available), Toast.LENGTH_SHORT).show()
        } else if (itemId == R.id.phone_sticker_activity_menu_item_cut) {
            unorganizedFragment!!.isEnable = false
            onSlideDownCallback()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        if (PhoneStickersUnorganizedFragment.isInCropMode || !unorganizedFragment!!.isEnable) {
            if (!unorganizedFragment!!.isEnable) {
                unorganizedFragment!!.isEnable = true
                onSlideUpCallback()
                return
            }
            if (PhoneStickersUnorganizedFragment.isInCropMode) {
                unorganizedFragment!!.toggleCutMode()
            }
        } else {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun OnIconClicked(item: IconItem) {
        lastClickedIcon = item.folder

        if (PhoneStickersUnorganizedFragment.isInCropMode) {
            AsyncStickersCut(this, unorganizedFragment!!.getSelectedItems(), item.folder, this).execute()

        } else {
            OrganizedStickersDetailedDialogFragment
                    .newInstance(item.folder, true)
                    .show(supportFragmentManager, "dialog")
        }
    }

    override fun OnIconLongClicked(item: IconItem) {

    }

    override fun OnNoItemWereFoundListener() {
        Log.e(javaClass.simpleName, "OnNoItemWereFound")
    }

    override fun OnCreateNewFolderSelected() {
        //is handled in the Fragment
        //intentionally empty
    }

    override fun onCutStarted() {
        loadingFrame!!.isClickable = true
        loadingFrame!!.animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        unorganizedFragment!!.isEnable = true

        unorganizedFragment!!.hideSelectedList()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        organizedFragmentHeight = organizedFragmentView!!.height
    }

    override fun onCutFinished() {
        loadingFrame!!.isClickable = false
        loadingFrame!!.animate().alpha(0f).setDuration(300).start()

        if (lastClickedIcon != null)
            OrganizedStickersDetailedDialogFragment
                    .newInstance(lastClickedIcon, true)
                    .show(supportFragmentManager, "dialog")


        onSlideDownCallback()

        val fragment = supportFragmentManager.findFragmentById(R.id.activity_phone_stickers_phone_stickers_organized_fragment) as OrganizedStickersIconFragment?

        unorganizedFragment!!.toggleCutMode()
        fragment?.refreshItems()

    }


    override fun onSlideUpCallback() {
        if (!isOrganizedFragmentHidden) {
            isOrganizedFragmentHidden = true
            organizedFragmentView!!
                    .animate()
                    .translationY((-organizedFragmentHeight).toFloat())
                    .setDuration(ANIMATION_DURATION)
                    .alpha(0f)
                    .start()
        }
    }

    override fun onSlideDownCallback() {
        if (isOrganizedFragmentHidden && !PhoneStickersUnorganizedFragment.isInCropMode || !unorganizedFragment!!.isEnable) {
            slideDown()
        }
    }

    private fun slideDown() {
        isOrganizedFragmentHidden = false
        organizedFragmentView!!
                .animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(ANIMATION_DURATION)
                .start()
    }

    override fun cutModeToggled(enabled: Boolean) {
        invalidateOptionsMenu()
        unorganizedFragment!!.setSwipeRefreshEnable(!enabled)
        if (enabled) {
            onSlideUpCallback()
        } else {
            onSlideDownCallback()
        }
    }

    companion object {

        private const val ANIMATION_DURATION: Long = 500
    }
}
