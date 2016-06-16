package com.amir.stickergram.infrastructure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.amir.stickergram.base.BaseActivity;

public class TextItem {
    public static final int TEXT_BOLD = 0;
    public static final int TEXT_ITALIC = 1;
    public static final int TEXT_NORMAL = 2;
    public static final Typeface DEFAULT_FONT = Typeface.SERIF;
//    public static final int DEFAULT_TEXT_COLOR = 1430537284;


    public int hostWidth;
    public int hostHeight;
    private String text;
    private Position position;
    private int textColor;
    private int backgroundColor;
    private FontItem font;
    private int size;
    private int tilt;
    private int alpha;
    private Shadow shadow;
    private int textDirection;
    private int gravity;
    private Layout.Alignment alignment;
    private TextArea area;
    private boolean isSelected;
    private int textStrokeColor;

    private float scale;
    private int selectedColor;

    private int textWidth;
    private int textHeight;
    Bitmap hostBitmap;
    Matrix matrix;
    private float strokeWidth;

    public TextItem(String text, Bitmap hostBitmap) {
        scale = BaseActivity.density;

        this.hostBitmap = hostBitmap.copy(hostBitmap.getConfig(), true);
        hostWidth = hostBitmap.getWidth();
        hostHeight = hostBitmap.getHeight();

        textStrokeColor = Color.parseColor("#ffffffff");
        strokeWidth = 0;

        selectedColor = Color.parseColor("#55444444");
        isSelected = false;
        this.text = text;
        alpha = 1;
        backgroundColor = Color.TRANSPARENT;
        font = new FontItem("Mono Space", DEFAULT_FONT);
        gravity = Gravity.NO_GRAVITY;
        position = new Position(50, 50);
        tilt = 180;
        shadow = new Shadow(Color.BLACK, 0, 0, 0);
        size = 50;
        textColor = Color.parseColor("#1565c0");
        alignment = Layout.Alignment.ALIGN_OPPOSITE;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
            textDirection = View.TEXT_DIRECTION_LTR;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setAlignment(Layout.Alignment alignment) {
        this.alignment = alignment;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setFont(FontItem font) {
        this.font = font;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setTilt(int tilt) {
        this.tilt = tilt;
    }

    public void setShadow(Shadow shadow) {
        this.shadow = shadow;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTextDirection(int textDirection) {
        this.textDirection = textDirection;
    }

    public Layout.Alignment getAlignment() {
        return alignment;
    }

    public int getAlpha() {
        return alpha;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public FontItem getFont() {
        return font;
    }

    public int getGravity() {
        return gravity;
    }

    public TextArea getArea() {
        getTextBitmap(); //updating the area
        return area;
    }

    public Position getPosition() {
        return position;
    }

    public int getTilt() {
        return tilt;
    }

    public Shadow getShadow() {
        return shadow;
    }

    public int getSize() {
        return size;
    }

    public String getText() {
        return text;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getTextDirection() {
        return textDirection;
    }

    public Bitmap getTextBitmap() {
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(size);
        mPaint.setAlpha(alpha);
        mPaint.setTypeface(font.getTypeface());

        Rect bound = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bound);

        //todo: play around to get a more prefect area for your text plzz don't hardcode values like you are doing with below line
        //todo: let the user change the text padding
        textWidth = (int) mPaint.measureText(text, 0, text.length()) + 10;
//        Log.e(getClass().getSimpleName(), "textWidth: " + textWidth);
        textHeight = getTextHeight(text, textWidth, size, font.getTypeface()) + 10;
//        Log.e(getClass().getSimpleName(), "textHeight: " + textHeight);
        textHeight += textHeight / 3;

        int padding = 20;
        if (Loader.isPersian(text))
            padding += size/10;

        int bitmapWidth = (int) (textWidth + shadow.getDx() + strokeWidth + padding);
        int bitmapHeight = (int) (textHeight + shadow.getDy() + strokeWidth + padding);

        area = new TextArea(position, bitmapWidth, bitmapHeight);

        Bitmap bitmap = Bitmap.createBitmap(
                bitmapWidth,
                bitmapHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(backgroundColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(textStrokeColor);
        mPaint.setShadowLayer(shadow.getRadius(), shadow.getDx(), shadow.getDy(), shadow.getColor());

//        int yOffSet = (int) (size - (size / 15) + strokeWidth / 2 + padding / 2); //todo: this line is weird but it work. let it be.... no don't let it be it ain't working correctly on different fonts
//        int yOffSet = (int) (strokeWidth / 2 + textWidth/2);
        int yOffSet = (int) (strokeWidth / 2 + textHeight / 1.5);
//        float xOffSet = (size / 2) + strokeWidth / 2;
//        float xOffSet = bitmap.getWidth() / 2 - (textWidth / 2) + size / 2 + strokeWidth / 2 ;
        float xOffSet = (int) (10 + strokeWidth / 2 + padding / 2);
        canvas.drawText(text, xOffSet, yOffSet, mPaint);

        mPaint.setShadowLayer(0, 0, 0, 0);//removing the shadow to avoid redrawing it
        mPaint.setColor(textColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, xOffSet, yOffSet, mPaint);
        canvas.save();

        canvas.restore();
        return bitmap;
    }

    public Bitmap getFullTextBitmap(Bitmap hostBitmap) {
        Bitmap tempTextBitmap = getTextBitmap();
        Bitmap fullTextBitmap = hostBitmap.copy(hostBitmap.getConfig(), true);
        Canvas canvas = new Canvas(fullTextBitmap);
        Canvas textCanvas = new Canvas(tempTextBitmap);
        matrix = new Matrix();
//        matrix.postRotate(tilt - 180, textWidth / 2, textHeight / 2);
        matrix.postRotate(tilt - 180, tempTextBitmap.getWidth() / 2, tempTextBitmap.getHeight() / 2);
        matrix.postTranslate(position.getLeft(), position.getTop());
        if (isSelected) {
            textCanvas.drawColor(selectedColor);
            textCanvas.save();
            textCanvas.restore();
        }
        canvas.drawBitmap(tempTextBitmap, matrix, new Paint());
        canvas.save();
        canvas.restore();
        return fullTextBitmap;
    }

    public int getTextHeight(String text, int maxWidth, float textSize, Typeface typeface) {
//        http://egoco.de/post/19077604048/calculating-the-height-of-text-in-android
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        paint.setTextSize(textSize);
        paint.setTypeface(typeface);

        int lineCount = 0;

        int index = 0;
        int length = text.length();

        while (index < length - 1) {
            index += paint.breakText(text, index, length, true, maxWidth, null);
            lineCount++;
        }

        Rect bounds = new Rect();
        paint.getTextBounds(this.text, 0, this.text.length(), bounds);
//        if (Loader.isPersian(text))
//            paint.getTextBounds("اغ", 0, 2, bounds);
//        else paint.getTextBounds("Py", 0, 2, bounds);
        return (int) Math.floor(lineCount * bounds.height());
    }

    public int getTextWidth() {
        return textWidth;
    }

    public int getTextHeight() {
        return textHeight;
    }

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


    public int getTextStrokeColor() {
        return textStrokeColor;
    }

    public void setTextStrokeColor(int textStrokeColor) {
        this.textStrokeColor = textStrokeColor;
    }


    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
}
