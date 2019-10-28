package com.amir.stickergram.sticker.pack.user

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amir.stickergram.R
import com.amir.stickergram.base.BaseActivity
import com.amir.stickergram.infrastructure.Constants
import java.io.File
import java.util.*

class PackAdapter(fragment: BaseActivity, private val listener: OnStickerClickListener, private val folder: String, private val baseDir: String, private val baseThumbDir: String) : RecyclerView.Adapter<ViewHolder>(), View.OnClickListener, View.OnLongClickListener {
    private val inflater: LayoutInflater = fragment.layoutInflater
    private var items: MutableList<String> = ArrayList()
    private var lastLongClickedItem: PackItem? = null

    init {
        refresh()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_pack_sticker, parent, false)
        view.setOnClickListener(this)
        view.setOnLongClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.populate(PackItem(
                folder,
                position.toString()/*as the name of the file */,
                baseThumbDir,
                baseDir))
    }

    override fun getItemCount(): Int {
        //        Log.e(getClass().getSimpleName(), "size is: " + items.size());
        return items.size
    }

    override fun onClick(view: View) {
        if (view.tag is PackItem) {
            listener.OnIconClicked(view.tag as PackItem)
        }

    }

    override fun onLongClick(view: View): Boolean {
        if (view.tag is PackItem) {
            lastLongClickedItem = view.tag as PackItem
            listener.OnLongClicked(lastLongClickedItem)
            return true
        }

        return false
    }

    fun itemRemoved(dir: String) {
        lastLongClickedItem!!.removeThumb()

        val i = items.indexOf(dir)
        notifyItemRemoved(i)
        items.removeAt(i)
        renameAllFilesAfterThisPosition(i)
        refresh()
        if (items.isEmpty()) {
            val file = File(baseDir + folder)
            if (file.exists()) {
                file.delete()
                listener.folderDeleted()
            }
        }
    }

    private fun renameAllFilesAfterThisPosition(i: Int) {
        var i = i
        val length = items.size
        while (i < length) {
            val currentName = baseDir + folder + File.separator + (i + 1) + Constants.PNG
            val file = File(currentName)
            if (file.exists()) {
                val newName = baseDir + folder + File.separator + i + Constants.PNG
                val temp = File(newName)
                notifyItemChanged(i)
                file.renameTo(temp)
            }
            val thumbFile = File(baseThumbDir + File.separator + folder + "_" + (i + 1) + Constants.PNG)
            if (thumbFile.exists())
                thumbFile.renameTo(File(baseThumbDir + File.separator + folder + "_" + i + Constants.PNG))
            i++
        }
    }

    fun refresh() {
        val folder = File(baseDir + this.folder + File.separator)
        if (folder.exists()) {
            if (folder.isDirectory) {
                items = ArrayList()
                val files = folder.listFiles()
                for (i in files!!.indices) { //lol ikr you are like why wouldn't list the files in that directory and i'm like cuz listing the file won't return to you a sorted list which is required in order for the delete function work properly

                    val pngDirectory = baseDir + this.folder + File.separator + i + Constants.PNG
                    items.add(pngDirectory)

                }
            } else {
                Log.e(javaClass.simpleName, folder.absolutePath + "was not a directory")
            }
        } else {
            Log.e(javaClass.simpleName, folder.absolutePath + "didn't exist")
        }
    }

    fun notifyItemRange() {
        notifyItemRangeChanged(0, items.size)
    }



}

