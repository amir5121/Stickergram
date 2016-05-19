package com.amir.telegramstickerbuilder.infrastructure;

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
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

public class TextItem {
    public static final int TEXT_BOLD = 0;
    public static final int TEXT_ITALIC = 1;
    public static final int TEXT_NORMAL = 2;
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

    public TextItem(String text, Context context, Bitmap hostBitmap) {
        scale = context.getResources().getDisplayMetrics().density;

        this.hostBitmap = hostBitmap.copy(hostBitmap.getConfig(), true);
        hostWidth = hostBitmap.getWidth();
        hostHeight = hostBitmap.getHeight();

        textStrokeColor = Color.parseColor("#ffffffff");
        strokeWidth = 0;

        selectedColor = Color.parseColor("#55444444");
//        Log.e(getClass().getSimpleName(), String.valueOf(selectedColor));
        isSelected = false;
        this.text = text;
        alpha = 1;
        backgroundColor = Color.TRANSPARENT;
        font = new FontItem("Mono Space", Typeface.MONOSPACE);
        gravity = Gravity.NO_GRAVITY;
        position = new Position(10, 10);
        tilt = 180;
        shadow = new Shadow(Color.BLACK, 0, 0, 0);
        size = 50;
        textColor = Color.YELLOW;
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
        getTextBitmap();
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

//        mPaint.setStyle(Paint.Style.STROKE);
//        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//        textPaint.setShadowLayer(shadow.getRadius(), shadow.getDx(), shadow.getDy(), shadow.getColor());
//        textPaint.setTextSize(size);
//        textPaint.setAlpha(alpha);
//        textPaint.setTypeface(font.getTypeface());
//        textPaint.setColor(textColor);
//        textPaint.setStyle(textStyle);

        Rect bound = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bound);
//        textPaint.getTextBounds(text, 0, text.length(), bound);

        textWidth = (int) mPaint.measureText(text, 0, text.length()) + 10;
//        textWidth = (int) textPaint.measureText(text, 0, text.length());
        textHeight = getTextHeight(text, textWidth, size, font.getTypeface()) + shadow.getDy() + 10;

//        if (Loader.isPersian(getText()))
        Log.e(getClass().getSimpleName(), "textHeight: " + textHeight);
        textHeight += textHeight / 3;
        textWidth += textWidth / 5;

//        StaticLayout staticLayout = new StaticLayout(
//                text,
//                textPaint,
//                textWidth,
//                alignment,
//                1.0f,
//                1.0f,
//                true);


////        Log.e(getClass().getSimpleName(), "textHeight: " + textHeight + " textWidth: " + textWidth);
////        float actualTilt = Math.abs(tilt - 180);
////        float remain = actualTilt % 90;

////        Log.e(getClass().getSimpleName(), "remain: " + remain);
////        actualTilt = (actualTilt >= 90 && actualTilt != 180) ? 90 - actualTilt % 90 : actualTilt;
////        if (actualTilt == 180) actualTilt = 0;
////        Log.e(getClass().getSimpleName(), "actualTilt: " + actualTilt);
//        // / ((textWidth * ((tilt - 180) / 180)) + 1);
////        if (actualTilt > 90) actualTilt = actualTilt/45;
//
////        float v = actualTilt / 90;
////        Log.e(getClass().getSimpleName(), "v: " + v);
////        Log.e(getClass().getSimpleName(), "  ------ textHeight: " + textHeight + " textWidth " + textWidth);
////        int tempTextWidth = textWidth;
////        textWidth = (int) (textWidth + (textHeight * v) - (textWidth * v));
////        textHeight = (int) (textHeight + (tempTextWidth * v) - (textHeight * v));
////        Log.e(getClass().getSimpleName(), "textHeight: " + textHeight + " textWidth " + textWidth);
//
//
////        area = new TextArea(position, textWidth, textHeight);
        area = new TextArea(position, textWidth, textHeight);

        Bitmap bitmap = Bitmap.createBitmap(
                (int) (textWidth + ((shadow.getDx() > 0) ? shadow.getDx() * (scale + 0.5) : 0) + strokeWidth),
                (int) (textHeight + strokeWidth),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(backgroundColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(textStrokeColor);
        mPaint.setShadowLayer(shadow.getRadius(), shadow.getDx(), shadow.getDy(), shadow.getColor());

        int yOffSet = (int) (size - (size / 15) + strokeWidth / 2); //this line is weird but it work. let it be

//        float xOffSet = (size / 2) + strokeWidth / 2;
        float xOffSet = bitmap.getWidth() / 2 - (textWidth / 2) + size / 2 + strokeWidth / 2;
        canvas.drawText(text, xOffSet, yOffSet, mPaint);

//        mPaint.setShadowLayer(shadow.getRadius(), shadow.getDx(), shadow.getDy(), shadow.getColor());
        mPaint.setShadowLayer(0, 0, 0, 0);//removing the shadow to avoid redrawing it
        mPaint.setColor(textColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, xOffSet, yOffSet, mPaint);
        canvas.save();

//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(textStrokeColor);
//        paint.setStrokeWidth(strokeWidth);
//        paint.setTypeface(font.getTypeface());
//        paint.setTextAlign(A);
//        canvas.drawPaint(paint);
//        canvas.save();

////        canvas.translate(-textWidth, -textHeight);

////
////        Bitmap copy = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
////
////        Canvas newCanvas = new Canvas(copy);
////        Matrix matrix = new Matrix();
////        matrix.setRotate (tilt - 180, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
////        newCanvas.setMatrix(matrix);
////
////        canvas.drawBitmap(copy,0,0,null);
//
////        canvas.rotate(tilt - 180, textWidth / 2, textHeight / 2);
////        canvas.rotate(tilt - 180, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
////        canvas.translate((-(textWidth / 2) * v), (textHeight / 2) * v);
//
////        canvas.translate(textWidth, textHeight);
////        canvas.rotate(tilt - 180, textWidth + bound.exactCenterX(), textHeight + bound.exactCenterY());
////        canvas.translate(-textWidth, -textHeight);
//
////        Log.e(getClass().getSimpleName(), "x: " + (-(textWidth / 2) * v) + " y: " + (textHeight / 3) * v);

////        canvas.drawBitmap(bitmap, 0, 0, textPaint);
////        staticLayout.draw(canvas);

//        Matrix matrix = new Matrix();
//        matrix.setRotate(tilt - 180, bitmap.getWidth()/2, bitmap.getHeight()/2);
//        newCanvas.setMatrix(matrix);
//        canvas.drawBitmap(bitmap, matrix, null);

        canvas.restore();
        return bitmap;
    }

    public Bitmap getFullTextBitmap() {
        if (isSelected) {
            Bitmap tempTextBitmap = getTextBitmap();
            Canvas canvas = new Canvas(tempTextBitmap);
            canvas.drawColor(selectedColor);
            Bitmap fullTextBitmap = Bitmap.createBitmap(hostWidth, hostHeight, Bitmap.Config.ARGB_8888);
            Canvas mainCanvas = new Canvas(fullTextBitmap);
            mainCanvas.drawBitmap(tempTextBitmap, position.getLeft(), position.getTop(), null);
            return fullTextBitmap;

        } else {
            Bitmap fullTextBitmap = Bitmap.createBitmap(hostWidth, hostHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(fullTextBitmap);
            canvas.drawBitmap(getTextBitmap(), position.getLeft(), position.getTop(), null);
            return fullTextBitmap;
        }
    }

    public Bitmap getFullTextBitmap2(Bitmap hostBitmap) {
        Bitmap tempTextBitmap = getTextBitmap();
        Bitmap fullTextBitmap = hostBitmap.copy(hostBitmap.getConfig(), true);
        Canvas canvas = new Canvas(fullTextBitmap);
//        Log.e(getClass().getSimpleName(), "-----tilt = " + (tilt - 180));
        Canvas textCanvas = new Canvas(tempTextBitmap);
        matrix = new Matrix();
        matrix.postRotate(tilt - 180, textWidth / 2, textHeight / 2);
        matrix.postTranslate(position.getLeft(), position.getTop());
//        textCanvas.setMatrix(matrix);
        if (isSelected) {
//            textCanvas.rotate(tilt - 180, textWidth / 2, textHeight / 2);
            textCanvas.drawColor(selectedColor);
            textCanvas.save();
            textCanvas.restore();
        }
//        else {
//            Bitmap fullTextBitmap = Bitmap.createBitmap(hostWidth, hostHeight, Bitmap.Config.ARGB_8888);
//
//            textCanvas.save();
//            textCanvas.restore();
//            canvas.drawBitmap(tempTextBitmap, position.getLeft(), position.getTop(), null);
//            canvas.save();
//            canvas.restore();
//            return fullTextBitmap;
//        }
//            Bitmap fullTextBitmap = Bitmap.createBitmap(hostWidth, hostHeight, Bitmap.Config.ARGB_8888);
//            Bitmap fullTextBitmap = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888);
//            Canvas mainCanvas = new Canvas(fullTextBitmap);
//            mainCanvas.drawBitmap(tempTextBitmap, position.getLeft(), position.getTop(), null);
//        RotateBitmap(tempTextBitmap, tilt - 180);

        canvas.drawBitmap(tempTextBitmap, matrix, new Paint());
//        canvas.drawBitmap(tempTextBitmap, position.getLeft(), position.getTop(), null);
        canvas.save();
        canvas.restore();
        return fullTextBitmap;
    }

//    public Point getRotatedPoint(Point point) {
//        float[] startPoints = new float[2];
//
//        startPoints[0] = point.x;
//        startPoints[1] = point.y;
//
//        matrix.setRotate(tilt + 180, textWidth / 2, textHeight / 2);
//        matrix.mapPoints(startPoints);
//
//        return point;
//
//    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle, source.getWidth() / 2, source.getHeight() / 2);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static int getTextHeight(String text, int maxWidth, float textSize, Typeface typeface) {
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
        paint.getTextBounds("Py", 0, 2, bounds);
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
