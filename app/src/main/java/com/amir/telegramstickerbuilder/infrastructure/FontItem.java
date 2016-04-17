package com.amir.telegramstickerbuilder.infrastructure;

import android.graphics.Typeface;

public class FontItem {
    private final Typeface typeface;
    private String name;

    public FontItem(String name, Typeface typeface) {
        this.name = name;
        this.typeface = typeface;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Typeface getTypeface() {
        return typeface;
    }
}
