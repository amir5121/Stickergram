package com.amir.stickergram.fonts;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amir.stickergram.R;
import com.amir.stickergram.infrastructure.FontItem;

public class FontViewHolder extends RecyclerView.ViewHolder {
    TextView textView;

    public FontViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.item_font_text);
        if (textView == null) Log.e(getClass().getSimpleName(), "textView was null here");
    }

    public void populate(FontItem item) {
//        Log.e(getClass().getSimpleName(), item.getName());
        switch (item.getName()){
            case "aban.ttf":
                item.setName("آبان");
                break;
            case "Afsaneh.ttf":
                item.setName("افسانه");
                break;
            case "Ali.ttf":
                item.setName("علی");
                break;
            case "Amine.ttf":
                item.setName("امینه");
                break;
            case "andlso.ttf":
                item.setName("عندلسو");
                break;
            case "arabtype.ttf":
                item.setName("عرب تایپ");
                break;
            case "Armita.ttf":
                item.setName("آرمیتا");
                break;
            case "Arsoo.ttf":
                item.setName("عرسو");
                break;
            case "Atila.ttf":
                item.setName("آتیلا");
                break;
            case "DastNevis.ttf":
                item.setName("دست نویس");
                break;
            case "Duel.ttf":
                item.setName("دوئل");
                break;
            case "Farhood.ttf":
                item.setName("فرهود");
                break;
            case "Gam_Azad.ttf":
                item.setName("غم آزاد");
                break;
            case "Gol.ttf":
                item.setName("گل");
                break;
            case "Goldan.ttf":
                item.setName("گلدان");
                break;
            case "majallab.ttf":
                item.setName("مجلاب");
                break;
            case "Ordibehesht shablon.ttf":
                item.setName("اردیبهشت شابلون");
                break;
            case "Sane Jaleh.ttf":
                item.setName("سانه جاله");
                break;
            case "SimKhardar.ttf":
                item.setName("سیم خاردار");
                break;
            case "UrdType.ttf":
                item.setName("ارد تایپ");
                break;
            case "Zarghan Hadith.ttf":
                item.setName("زرقان خدیث");
                break;
            case "ZARGHAN.MOALA.ttf":
                item.setName("زرقان ملا");
                break;
            case "IranNastaliq.ttf":
                item.setName("ایران نسطعلیق");
                break;
        }
        itemView.setTag(item);
        textView.setText(item.getName().replace(".ttf", ""));
        textView.setTypeface(item.getTypeface());
    }
}
