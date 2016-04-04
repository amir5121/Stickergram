package com.amir.telegramstickerbuilder.infrastructure;

import android.util.Log;
import android.view.View;

public class Position {
    float top;
    float left;

    public Position(View view) {
//        top = getRelativeTop(view);
//        left = getRelativeLeft(view);
        left = view.getTop();
        top = view.getLeft();
        Log.e(getClass().getSimpleName(), "X: " + top + " left: " + left);
    }

    public Position(float top, float left){
        this.top = top;
        this.left = left;
    }
    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    private int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }
}
