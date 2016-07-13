package com.amir.stickergram.infrastructure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

@SuppressLint("ViewConstructor")
public class TouchImageView extends ImageView {
    private static final String TEXT_ITEM = "TEXT_ITEM";
    private TextItem textItem;
    private boolean isFirstTapOnStrokeColor = true;
    private boolean isFirstTapOnShadowColor = true;
    int mainBitmapWidth;
    int mainBitmapHeight;
    float widthScale;
    float heightScale;

    public TouchImageView(Context context, Bitmap mainBitmap) {
        super(context);
        this.textItem = new TextItem(mainBitmap);
        setLayoutParams();
        mainBitmapWidth = mainBitmap.getWidth();
        mainBitmapHeight = mainBitmap.getHeight();
        setImageBitmap(textItem.getFullTextBitmap());
    }

    public TouchImageView(Context context, Bundle bundle, Bitmap mainBitmap) {
        super(context);
        textItem = bundle.getParcelable(TEXT_ITEM);
        setLayoutParams();
        mainBitmapWidth = mainBitmap.getWidth();
        mainBitmapHeight = mainBitmap.getHeight();
        setImageBitmap(textItem.getFullTextBitmap());
    }

//    public TouchImageView(Parcel parcel) {
//
//    }

    public TextItem getTextItem() {
        return textItem;
    }

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

    public void setLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
        setScaleType(ScaleType.FIT_CENTER);
        setAdjustViewBounds(true);
        setClickable(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        widthScale = (float) textItem.hostWidth / getMeasuredWidth();
        heightScale = (float) textItem.hostHeight / getMeasuredHeight();
    }

    public void updateTextPosition(Position position, Position offsetPosition) {
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
        textItem.setPosition(actualPosition);
        setImageBitmap(textItem.getFullTextBitmap());

    }

    public void updateTextView() {
        setImageBitmap(textItem.getFullTextBitmap());
    }

    public Position isItMe(Position position) {
        TextArea area = textItem.getArea();
        float areaStartTop = area.getStartPosition().getTop();
        float areaEndTop = areaStartTop + area.getHeight();
        float areaStartLeft = area.getStartPosition().getLeft();
        float areaEndLeft = areaStartLeft + area.getWidth();

        float clickedTop = position.getTop() * heightScale;
        float clickedLeft = position.getLeft() * widthScale;

        int tilt = getTextItem().getTilt();
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
        textItem.setSelected(false);
        Bitmap temp = Bitmap.createBitmap(mainBitmapWidth, mainBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(textItem.getFullTextBitmap(), 0, 0, null);
        canvas.save();
        canvas.restore();
        return temp;
    }

    public void setAsSelected(boolean asSelected) {
        if (asSelected) {
            textItem.setSelected(true);
        } else {
            textItem.setSelected(false);
            updateTextView();
        }
    }

    public int getTextSize() {
        return textItem.getSize();
    }

    public void updateText(String newText) {
        textItem.setText(newText);
        updateTextView();
    }

    public void setTextItem(TextItem textItem) {
        this.textItem = textItem;
        updateTextView();
    }

    public void notPaid() {
        textItem.setTilt(180);
        Shadow shadow = textItem.getShadow();
        shadow.setDx(0);
        shadow.setDy(0);
        textItem.setShadow(shadow);
    }


    public Bundle getSaveBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TEXT_ITEM, textItem);
        return bundle;
    }
}
