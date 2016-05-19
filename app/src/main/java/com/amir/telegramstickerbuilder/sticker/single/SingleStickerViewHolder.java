package com.amir.telegramstickerbuilder.sticker.single;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.amir.telegramstickerbuilder.R;

public class SingleStickerViewHolder extends RecyclerView.ViewHolder{
    ImageView stickerImageView;
//    ImageView selectedStickerImageView;

    public SingleStickerViewHolder(View itemView) {
        super(itemView);
        stickerImageView = (ImageView) itemView.findViewById(R.id.simple_sticker_item_image);
//        selectedStickerImageView = (ImageView) itemView.findViewById(R.id.list_view_item_selectedSticker_foreground);
    }

    public void populate(StickerItem item){
        itemView.setTag(item);
        stickerImageView.setImageBitmap(item.getThumbBitmap());

        if(item.isSelected())
            stickerImageView.setColorFilter(R.color.selected_item_foreground);
//            selectedStickerImageView.setVisibility(View.VISIBLE);
        else
            stickerImageView.clearColorFilter();
//            selectedStickerImageView.setVisibility(View.GONE);
    }
}
