package com.amir.stickergram.image;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

public abstract class DrawableItem {
    protected static final int SELECTED_ITEM_COLOR = Color.parseColor("#55999999");
    protected static final int LIGHT_BLUE = Color.parseColor("#2196f3");
    public static final float COPY_OFFSET = 7;

    int hostWidth;
    int hostHeight;
    protected Position position;
    protected int size;
    protected int tilt;
    protected int alpha;
    boolean isSelected;
    DrawableArea area;
    Paint selectedItemPaint;

    DrawableItem(Bitmap hostBitmap) {
        hostWidth = hostBitmap.getWidth();
        hostHeight = hostBitmap.getHeight();
        isSelected = false;
        alpha = 1;
        position = new Position(50, 50);
        tilt = 180;
        size = 50;
        prepTools();
    }

    protected void prepTools() {
        selectedItemPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedItemPaint.setColor(LIGHT_BLUE);
        selectedItemPaint.setStyle(Paint.Style.STROKE);
        selectedItemPaint.setStrokeWidth(5);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTilt() {
        return tilt;
    }

    public void setTilt(int tilt) {
        this.tilt = tilt;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public DrawableArea getArea() {
        getDrawableBitmap(); //updating the area
        return area;
    }

    public void setArea(DrawableArea area) {
        this.area = area;
    }

    public Paint getSelectedItemPaint() {
        return selectedItemPaint;
    }

    public void setSelectedItemPaint(Paint selectedItemPaint) {
        this.selectedItemPaint = selectedItemPaint;
    }

    abstract Bitmap getDrawableBitmap();
    abstract Bitmap getDrawableFullBitmap();

    public void moveUp() {
        float top = position.getTop();
        position.setTop(top - 3);
    }

    public void moveDown() {
        float top = position.getTop();
        position.setTop(top + 3);
    }

    public void moveLeft() {
        float left = position.getLeft();
        position.setLeft(left - 3);
    }

    public void moveRight() {
        float left = position.getLeft();
        position.setLeft(left + 3);
    }

    public void centerHorizontal() {
        int height = getDrawableBitmap().getHeight();
        position.setTop(hostHeight / 2 - height/ 2);
    }

    public void centerVertical() {
        int width = getDrawableBitmap().getWidth();
        position.setLeft(hostWidth / 2 - width / 2);
    }

    public void alignBottom() {
        int height = getDrawableBitmap().getHeight();
        position.setTop(hostHeight - height);
    }

    public void alignTop() {
        position.setTop(0);
    }

    public void alignRight() {
        int width = getDrawableBitmap().getWidth();
        position.setLeft(hostWidth - width);
    }

    public void alignLeft() {
        position.setLeft(0);
    }
}
