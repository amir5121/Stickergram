package com.amir.stickergram.fonts;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amir.stickergram.R;
import com.amir.stickergram.image.FontItem;

public class FontViewHolder extends RecyclerView.ViewHolder {
    TextView textView;

    public FontViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.item_font_text);
        if (textView == null) Log.e(getClass().getSimpleName(), "textView was null here");
    }

    public void populate(FontItem item) {
        itemView.setTag(item);
        textView.setText(item.getName().replace(".ttf", ""));
        textView.setTypeface(item.getTypeface());
    }
}
