package com.amir.stickergram.infrastructure

import android.content.Context
import android.os.AsyncTask
import android.util.Log

import com.amir.stickergram.base.BaseActivity
import com.amir.stickergram.sticker.single.SingleStickersAdapter
import com.amir.stickergram.sticker.single.StickerItem

import java.io.File
import java.util.HashSet

class AsyncTaskPhoneAdapter(activity: BaseActivity, listener: AsyncPhoneTaskListener) : AsyncTask<SingleStickersAdapter, Int, Int>() {
    private var context: Context? = null
    private var listener: AsyncPhoneTaskListener? = null
    private var baseThumbDir: String? = null
    private var foundedStickersCount = 0

    init {
        attach(activity, listener)
    }

    private fun attach(activity: BaseActivity, listener: AsyncPhoneTaskListener) {

        //        try {
        this.listener = listener
        //        } catch (ClassCastException e) {
        //            throw new ClassCastException(activity.toString()
        //                    + "Must implement AsyncPhoneTaskListener");
        //        }

        //        if (activity.getExternalCacheDir() != null)
        baseThumbDir = activity.externalCacheDir.toString() + File.separator + "phone_" + BaseActivity.chosenMode.pack
        //        else
        //            baseThumbDir = activity.getCacheDir().getAbsolutePath() + File.separator + "phone_" + BaseActivity.chosenMode.getPack();
        this.context = activity
    }

    override fun onPreExecute() {
        listener!!.onTaskStartListener()
    }

    @Synchronized
    override fun doInBackground(vararg params: SingleStickersAdapter): Int? {
        if (!Loader.checkPermission((context as BaseActivity?)!!))
            return NEED_PERMISSION

        val folder = File(Loader.activeStickerDir)
        if (!folder.exists()) {
            return CACHE_DIRECTORY_DID_NOT_EXIST
        }
        var temp = 0
        var percent: Int
        val files = folder.listFiles()
        val length = files!!.size
        val dataSource = params[0].dataSource
        val updateSet = HashSet<String>()
        var filesChecked = 0
        if (length == 0) {
            dataSource.updateSet(updateSet)
            return NO_ITEM_IN_CACHE_DIRECTORY
        }
        for (file in files) {
            //            Log.e(getClass().getSimpleName(), file.getName());
            filesChecked++
            val name = file.name

            try {

                if (file.exists() && name.contains(".webp") && name[1] == '_' && !name.contains("temp")) {
                    updateSet.add(file.absolutePath)
                    if (!dataSource.contain(file.absolutePath)) {
                        val thumbDirectory = Loader.generateThumbnail(file.absolutePath, baseThumbDir!! + name)
                        if (thumbDirectory != null)
                            dataSource.lazyAdd(StickerItem(
                                    file.absolutePath,
                                    Loader.generateThumbnail(file.absolutePath, thumbDirectory),
                                    false,
                                    true))
                    }
                    foundedStickersCount++

                    /*
                todo: concurrentModificationException on Nexus 5 take a look
                todo: it might be because of to many calls to shared preferences do it all at once ...
                todo: gather them up and write another method to do it all at once not requiring too many applies
                */
                }
            } catch (e: Exception) {
                Log.e(TAG, "doInBackground: Got an exception")
            }

            percent = 100 * filesChecked / length
            dataSource.apply()
            if (temp == percent) {
                //                Log.e(getClass().getSimpleName(), String.valueOf(percent));
                temp++
                publishProgress(percent, foundedStickersCount)
            }
        }
        Log.e(javaClass.simpleName, "updateSet size " + updateSet.size)
        dataSource.updateSet(updateSet)
        return if (foundedStickersCount == 0) {
            NO_ITEM_IN_CACHE_DIRECTORY
        } else ITEMS_WERE_ADDED

        //        dataSource.updateSet(updateSet);
        //        Log.e(getClass().getSimpleName(), "all the way through");

    }


    override fun onProgressUpdate(vararg values: Int?) {
        val percent = values[0]
        val stickerCount = values[1]
        if (context != null && percent != null && stickerCount != null)
            listener!!.onTaskUpdateListener(percent, stickerCount)
    }


    override fun onPostExecute(aVoid: Int) {
        if (aVoid == CACHE_DIRECTORY_DID_NOT_EXIST)
            listener!!.onNoCashDirectoryListener()
        else if (aVoid == NO_ITEM_IN_CACHE_DIRECTORY)
            listener!!.onNoStickerWereFoundListener()
        else if (aVoid == ITEMS_WERE_ADDED)
            listener!!.onTaskFinishedListener()
        else if (aVoid == NEED_PERMISSION)
            listener!!.onRequestReadWritePermission()
        context = null
    }


    override fun onCancelled() {
        super.onCancelled()
        context = null
    }

    interface AsyncPhoneTaskListener {
        fun onTaskStartListener()

        fun onTaskUpdateListener(percent: Int, stickerCount: Int)

        fun onTaskFinishedListener()

        fun onNoCashDirectoryListener()

        fun onNoStickerWereFoundListener()

        fun onRequestReadWritePermission()
    }

    companion object {
        private val CACHE_DIRECTORY_DID_NOT_EXIST = -1
        private val ITEMS_WERE_ADDED = 0
        private val NO_ITEM_IN_CACHE_DIRECTORY = 1
        private val NEED_PERMISSION = 2
        private val TAG = "AsyncTaskPhoneAdapter"
    }
}
