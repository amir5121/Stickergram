package com.amir.telegramstickerbuilder.views;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.infrastructure.StickerItem;

public class StickerViewHolder extends RecyclerView.ViewHolder{
    ImageView stickerImageView;
    ImageView selectedStickerImageView;

    public StickerViewHolder(View itemView) {
        super(itemView);
        stickerImageView = (ImageView) itemView.findViewById(R.id.list_view_item_stickerItem);
        selectedStickerImageView = (ImageView) itemView.findViewById(R.id.list_view_item_selectedSticker_foreground);
    }

    public void populate(Context context, StickerItem item){
        itemView.setTag(item);
        stickerImageView.setImageBitmap(item.getThumbBitmap());

        if(item.isSelected())
            selectedStickerImageView.setVisibility(View.VISIBLE);
        else
            selectedStickerImageView.setVisibility(View.GONE);
    }
}
