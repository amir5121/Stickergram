package com.amir.stickergram.infrastructure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.amir.stickergram.EditImageActivity;

@SuppressLint("ViewConstructor")
public class TouchImageView extends ImageView {
    private final int layerId;
    private TextItem textItem;
    private boolean isFirstTapOnStrokeColor;
    private boolean isFirstTapOnShadowColor;
    EditImageActivity activity;
    int scaledWidth;
    int scaledHeight;
    float widthScale;
    float heightScale;

    Bitmap latestTextLayer;
    Bitmap textLayer;
    Bitmap mainBitmap;

    public TouchImageView(Context context, TextItem textItem, int layerId, Bitmap mainBitmap) {
        super(context);
        activity = (EditImageActivity) context;
        this.textItem = textItem;
        this.layerId = layerId;
        setLayoutParams();
        this.mainBitmap = mainBitmap;
        this.textLayer = Bitmap.createBitmap(mainBitmap.getWidth(), mainBitmap.getHeight(), mainBitmap.getConfig());
//        setImageBitmap(textItem.getFullTextBitmap());
        setImageBitmap(textItem.getFullTextBitmap(textLayer));
        isFirstTapOnStrokeColor = true;
        isFirstTapOnShadowColor = true;

//        setClickable(true);

    }

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
//        setBackgroundColor(Color.parseColor("#55ff6622"));
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

    public int getLayerId() {
        return layerId;
    }

    public int[] getScaledMearsument() {
        int[] measurement = new int[2];
        measurement[0] = scaledWidth;
        measurement[1] = scaledHeight;
        return measurement;
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
        setImageBitmap(textItem.getFullTextBitmap(textLayer));

    }

    public void updateTextView() {
        setImageBitmap(textItem.getFullTextBitmap(textLayer));
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

    @Override
    public void setImageBitmap(Bitmap bm) {
        latestTextLayer = bm;
        Bitmap temp = Bitmap.createBitmap(mainBitmap.getWidth(), mainBitmap.getHeight(), mainBitmap.getConfig());
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(bm, 0, 0, null);
        canvas.save();
        canvas.restore();
        super.setImageBitmap(temp);
    }

    public Bitmap getFinishedBitmap() {
        textItem.setSelected(false);
        Bitmap temp = Bitmap.createBitmap(mainBitmap.getWidth(), mainBitmap.getHeight(), mainBitmap.getConfig());
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(latestTextLayer, 0, 0, null);
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

//    public void setTextSize(int progress) {
//        textItem.setSize(progress);
//        updateTextView();
//    }

    public int getTextSize() {
        return textItem.getSize();
    }

    public void updateText(String newText) {
        textItem.setText(newText);
        updateTextView();
    }

//    public void setTextTilt(int textTilt) {
//        textItem.setTilt(textTilt);
//        updateTextView();
//    }

    public void setTextItem(TextItem textItem) {
        this.textItem = textItem;
        updateTextView();
    }

    public void notPaid() {
        textItem.setTilt(180);
//        textItem.setTextStrokeColor(Color.parseColor(TextItem.DEFAULT_STROKE_COLOR));
        Shadow shadow = textItem.getShadow();
        shadow.setDx(0);
        shadow.setDy(0);
        textItem.setShadow(shadow);
    }

}
