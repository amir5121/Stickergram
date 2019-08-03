package com.amir.stickergram.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ViewGroup;
import android.widget.FrameLayout;

@SuppressLint("ViewConstructor")
public class TouchImageView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TEXT_ITEM = "TEXT_ITEM";
    private DrawableItem drawableItem;
    private boolean isFirstTapOnStrokeColor = true;
    private boolean isFirstTapOnShadowColor = true;
    private int mainBitmapWidth;
    private int mainBitmapHeight;
    private float widthScale;
    private float heightScale;

    public TouchImageView(Context context, Bitmap mainBitmap, DrawableItem drawableItem) {
        super(context);
        this.drawableItem = drawableItem;
        setLayoutParams();
        mainBitmapWidth = mainBitmap.getWidth();
        mainBitmapHeight = mainBitmap.getHeight();
        setImageBitmap(drawableItem.getDrawableFullBitmap());
    }

    TouchImageView(Context context, Bundle bundle, Bitmap mainBitmap) {
        super(context);
        drawableItem = bundle.getParcelable(TEXT_ITEM);
        setLayoutParams();
        mainBitmapWidth = mainBitmap.getWidth();
        mainBitmapHeight = mainBitmap.getHeight();
        setImageBitmap(drawableItem.getDrawableFullBitmap());
    }

    public TouchImageView(TouchImageView selectedLayer, Context context) {
        super(context);
        if (selectedLayer.getDrawableItem() instanceof TextItem)
            drawableItem = new TextItem((TextItem) selectedLayer.getDrawableItem());
        else
            drawableItem = new ImageItem((ImageItem) selectedLayer.getDrawableItem());

        isFirstTapOnShadowColor = selectedLayer.isFirstTapOnShadowColor;
        isFirstTapOnStrokeColor = selectedLayer.isFirstTapOnStrokeColor;
        mainBitmapWidth = selectedLayer.mainBitmapWidth;
        mainBitmapHeight = selectedLayer.mainBitmapHeight;
        widthScale = selectedLayer.widthScale;
        heightScale = selectedLayer.heightScale;
    }

//    public TextItem getTextItem() {
//        if (drawableItem instanceof TextItem)
//            return ((TextItem) drawableItem);
//        else return null;
//    }

    public void setFirstTapOnShadowColor(boolean firstTapOnShadowColor) {
        isFirstTapOnShadowColor = firstTapOnShadowColor;
    }

    public void setFirstTapOnStrokeColor(boolean firstTapOnStrokeColor) {
        isFirstTapOnStrokeColor = firstTapOnStrokeColor;
    }

    public boolean isFirstTapOnShadowColor() {
        return isFirstTapOnShadowColor;
    }

    public boolean isFirstTapOnStrokeColor() {
        return isFirstTapOnStrokeColor;
    }

    private void setLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
        setScaleType(ScaleType.FIT_CENTER);
        setAdjustViewBounds(true);
        setClickable(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        widthScale = (float) drawableItem.hostWidth / getMeasuredWidth();
        heightScale = (float) drawableItem.hostHeight / getMeasuredHeight();
    }

    void updateDrawablePosition(Position position, Position offsetPosition) {
        Position actualPosition;

        if (offsetPosition != null) {
            actualPosition = new Position(
                    position.getTop() * heightScale - offsetPosition.getTop(),
                    position.getLeft() * widthScale - offsetPosition.getLeft());
        } else {
            actualPosition = new Position(
                    position.getTop() * heightScale,
                    position.getLeft() * widthScale);
        }
        drawableItem.setPosition(actualPosition);
        setImageBitmap(drawableItem.getDrawableFullBitmap());

    }

    public void updateDrawable() {
        setImageBitmap(drawableItem.getDrawableFullBitmap());
    }

    Position isItMe(Position position) {
        DrawableArea area = drawableItem.getArea();
        float areaStartTop = area.getStartPosition().getTop();
        float areaEndTop = areaStartTop + area.getHeight();
        float areaStartLeft = area.getStartPosition().getLeft();
        float areaEndLeft = areaStartLeft + area.getWidth();

        float clickedTop = position.getTop() * heightScale;
        float clickedLeft = position.getLeft() * widthScale;

        int tilt = drawableItem.getTilt();
        Matrix matrix = new Matrix();
        // Initialize the array with our Coordinate
        float[] clickPoints = new float[2];
        clickPoints[0] = clickedLeft;
        clickPoints[1] = clickedTop;

        matrix.setRotate(-(tilt - 180),
                (areaStartLeft + areaEndLeft) / 2,
                (areaStartTop + areaEndTop) / 2);//undoing the rotation to check the text position
        matrix.mapPoints(clickPoints);

        float clickedLeftInverseRotated = clickPoints[0];
        float clickedTopInverseRotated = clickPoints[1];

        if (clickedTopInverseRotated < areaEndTop &&
                clickedTopInverseRotated > areaStartTop &&
                clickedLeftInverseRotated < areaEndLeft &&
                clickedLeftInverseRotated > areaStartLeft) {
            return new Position(clickedTop - areaStartTop, clickedLeft - areaStartLeft);
        }
        return null;
    }

    public Bitmap getFinishedBitmap() {
        drawableItem.setSelected(false);
        Bitmap temp = Bitmap.createBitmap(mainBitmapWidth, mainBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(drawableItem.getDrawableFullBitmap(), 0, 0, null);
        canvas.save();
        canvas.restore();
        return temp;
    }

    public void setAsSelected(boolean asSelected) {
        if (asSelected) {
            drawableItem.setSelected(true);
        } else {
            drawableItem.setSelected(false);
            updateDrawable();
        }
    }


    public void notPaid() {
        drawableItem.setTilt(180);
//        Shadow shadow = textItem.getShadow();
//        shadow.setDx(0);
//        shadow.setDy(0);
//        textItem.setShadow(shadow);
    }


    public Bundle getSaveBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TEXT_ITEM, (Parcelable) drawableItem);
        return bundle;
    }

    public DrawableItem getDrawableItem() {
        return drawableItem;
    }
}
