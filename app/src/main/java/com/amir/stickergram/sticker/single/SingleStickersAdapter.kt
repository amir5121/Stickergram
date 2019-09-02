package com.amir.stickergram.sticker.single

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.amir.stickergram.R
import com.amir.stickergram.base.BaseActivity
import com.amir.stickergram.infrastructure.DataSource
import com.amir.stickergram.phoneStickers.unorganized.PhoneStickersUnorganizedFragment

import java.util.ArrayList

class SingleStickersAdapter(activity: BaseActivity, private val listener: OnStickerClickListener) : RecyclerView.Adapter<SingleStickerViewHolder>(), View.OnClickListener, View.OnLongClickListener {
    private val inflater: LayoutInflater

    private var items: MutableList<StickerItem>? = null
    val dataSource: DataSource

    init {
        this.inflater = activity.getLayoutInflater()
        dataSource = DataSource(activity)
        items = ArrayList() // things actually happen in refresh method
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleStickerViewHolder {
        val view = inflater.inflate(R.layout.item_simple_sticker, parent, false)
        view.setOnClickListener(this)
        view.setOnLongClickListener(this)
        return SingleStickerViewHolder(view)
    }

    override fun onBindViewHolder(holder: SingleStickerViewHolder, position: Int) {
        holder.populate(items!![position], PhoneStickersUnorganizedFragment.isInCropMode)
    }


    override fun getItemCount(): Int {
        return items!!.size
    }

    override fun onClick(view: View) {
        if (view.tag is StickerItem) {
            val item = view.tag as StickerItem
            listener.OnStickerClicked(item)
        }
    }

    override fun onLongClick(view: View): Boolean {
        if (view.tag is StickerItem) {
            val note = view.tag as StickerItem
            listener.OnStickerLongClicked(note)
        }
        return true
    }

    fun refreshPhoneSticker() {
        items = dataSource.allVisiblePhoneStickers
        //        Arrays.sort(items.toArray());

        if (items!!.size == 0)
            listener.OnNoItemExistedListener()
        notifyItemRangeChanged(0, items!!.size)
        notifyDataSetChanged()
    }

    fun hideItems(selectedItems: List<StickerItem>) {
        dataSource.hideItems(selectedItems)
    }

    fun updateItems(selectedItems: ArrayList<StickerItem>?) {
        //        String mString;
        //        String toMatch;
        val size = items!!.size
        if (selectedItems != null)
            for (selectedItem in selectedItems) {
                for (i in 0 until size) {
                    if (items!![i].name == selectedItem.name) {
                        items!![i] = selectedItem
                        break
                    }
                }
            }

    }


    interface OnStickerClickListener {
        fun OnStickerClicked(item: StickerItem)

        fun OnStickerLongClicked(item: StickerItem)

        fun OnNoItemExistedListener()
    }

}
