package com.amir.telegramstickerbuilder.infrastructure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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


    public int hostWidth;
    public int hostHeight;
    private String text;
    private Position position;
    private int textColor;
    private int backgroundColor;
    private Typeface font;
    private int size;
    private int rotation;
    private int alpha;
    private Shadow shadow;
    private int textDirection;
    private int gravity;
    private Paint.Style textStyle;
    private Layout.Alignment alignment;
    private TextArea area;
    private boolean isSelected;

    private float scale;
    private int selectedColor;

    public TextItem(String text, Context context, Bitmap hostBitmap) {
        scale = context.getResources().getDisplayMetrics().density;

        hostWidth = hostBitmap.getWidth();
        hostHeight = hostBitmap.getHeight();

        selectedColor = Color.parseColor("#55444444");
        isSelected = false;
        this.text = text;
        alpha = 1;
        backgroundColor = Color.TRANSPARENT;
        font = Typeface.MONOSPACE;
        gravity = Gravity.NO_GRAVITY;
        position = new Position(10, 10);
        rotation = 0;
        shadow = new Shadow(Color.BLACK, 5, 5, 2);
        size = 50;
        textColor = Color.YELLOW;
        alignment = Layout.Alignment.ALIGN_NORMAL;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
            textDirection = View.TEXT_DIRECTION_LTR; //todo: textDirection must be assigned based on the sdk version
        textStyle = Paint.Style.FILL;
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

    public void setFont(Typeface font) {
        this.font = font;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
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

    public void setTextStyle(Paint.Style textStyle) {
        this.textStyle = textStyle;
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

    public Typeface getFont() {
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

    public int getRotation() {
        return rotation;
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

    public Paint.Style getTextStyle() {
        return textStyle;
    }

    public Bitmap getTextBitmap() {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setShadowLayer(shadow.getRadius(), shadow.getdX(), shadow.getdY(), shadow.getColor());
        textPaint.setTextSize(size);
        textPaint.setAlpha(alpha);
        textPaint.setTypeface(font);
        textPaint.setColor(textColor);
        textPaint.setStyle(textStyle);

        Rect bound = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bound);
        int textWidth = (int) textPaint.measureText(text, 0, text.length());
        int textHeight = (int) ((getTextHeight(text, textWidth, size, font) + shadow.getdX()) * 1.5);
        area = new TextArea(position, textWidth, textHeight);

        StaticLayout staticLayout = new StaticLayout(
                text,
                textPaint,
                textWidth,
                alignment,
                1.0f,
                1.0f,
                true);

        Bitmap bitmap = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(backgroundColor);
        canvas.drawPaint(paint);
        canvas.save();
        canvas.translate(0, 0);

        staticLayout.draw(canvas);

        canvas.restore();
        //todo: note setting locale on the textPaint might come handy to support persian

        //todo: on the imageView responsible for this bitmap set: rotation

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
            canvas.drawBitmap(getTextBitmap(), position.getLeft(), position.getTop(), null);//todo: position must be reversed to work correctly for the unselcted method
            return fullTextBitmap;
        }
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

}
