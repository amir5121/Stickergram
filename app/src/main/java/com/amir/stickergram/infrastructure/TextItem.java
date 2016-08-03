package com.amir.stickergram.infrastructure;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.Log;

public class TextItem implements Parcelable {
    public static final int TEXT_BOLD = 0;
    public static final int TEXT_ITALIC = 1;
    public static final int TEXT_NORMAL = 2;
    public static final Typeface DEFAULT_FONT = Typeface.SERIF;
    public static final String DEFAULT_STROKE_COLOR = "#1565c0";
    private static final String DEFAULT_TEXT_COLOR = "#ffffff";
    private static final int SELECTED_TEXT_COLOR = Color.parseColor("#55999999");
    private static final int LIGHT_BLUE = Color.parseColor("#2196f3");

    int hostWidth;
    int hostHeight;
    private String text;
    private Position position;
    private int textColor;
    private int backgroundColor;
    private FontItem font;
    private int size;
    private int tilt;
    private int alpha;
    private Shadow shadow;
    private boolean isSelected;
    private int textStrokeColor;
    private int textWidth;
    private int textHeight;
    private float strokeWidth;
    private TextArea area;
    private Paint selectedItemPaint;

    TextItem(Bitmap hostBitmap) {
        hostWidth = hostBitmap.getWidth();
        hostHeight = hostBitmap.getHeight();
        textStrokeColor = Color.parseColor(DEFAULT_STROKE_COLOR);
        strokeWidth = 6;
        isSelected = false;
        this.text = "";
        alpha = 1;
        backgroundColor = Color.TRANSPARENT;
        font = new FontItem("Mono Space", DEFAULT_FONT, FontItem.DEFAULTS, FontItem.SERIF);
        position = new Position(50, 50);
        tilt = 180;
        shadow = new Shadow(Color.BLACK, 0, 0, 0);
        size = 50;
        textColor = Color.parseColor(DEFAULT_TEXT_COLOR);

        prepTools();
    }

    TextItem(Parcel parcel) {
        hostWidth = parcel.readInt();
        hostHeight = parcel.readInt();
        text = parcel.readString();
        position = parcel.readParcelable(Position.class.getClassLoader());
        textColor = parcel.readInt();
        backgroundColor = parcel.readInt();
        font = parcel.readParcelable(FontItem.class.getClassLoader());
        size = parcel.readInt();
        tilt = parcel.readInt();
        alpha = parcel.readInt();
        shadow = parcel.readParcelable(Shadow.class.getClassLoader());
        isSelected = parcel.readByte() == 1;
        textStrokeColor = parcel.readInt();
        textWidth = parcel.readInt();
        textHeight = parcel.readInt();
        strokeWidth = parcel.readFloat();
        area = parcel.readParcelable(TextArea.class.getClassLoader());

        prepTools();
    }


    private void prepTools() {
        selectedItemPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedItemPaint.setColor(LIGHT_BLUE);
        selectedItemPaint.setStyle(Paint.Style.STROKE);
        selectedItemPaint.setStrokeWidth(5);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(hostWidth);
        parcel.writeInt(hostHeight);
        parcel.writeString(text);
        parcel.writeParcelable(position, 0);
        parcel.writeInt(textColor);
        parcel.writeInt(backgroundColor);
        parcel.writeParcelable(font, 0);
        parcel.writeInt(size);
        parcel.writeInt(tilt);
        parcel.writeInt(alpha);
        parcel.writeParcelable(shadow, 0);
        parcel.writeByte((byte) (isSelected ? 1 : 0));
        parcel.writeInt(textStrokeColor);
        parcel.writeInt(textWidth);
        parcel.writeInt(textHeight);
        parcel.writeFloat(strokeWidth);
        parcel.writeParcelable(area, 0);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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


    public int getAlpha() {
        return alpha;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public FontItem getFont() {
        return font;
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


    public Bitmap getTextBitmap() {
//        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(size);
        mPaint.setAlpha(alpha);
        mPaint.setTypeface(font.getTypeface());

        Rect bound = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bound);

        textWidth = (int) mPaint.measureText(text, 0, text.length()) + 10;
        textHeight = getTextHeight(text, textWidth, size, font.getTypeface()) + 10;
        textHeight += textHeight / 3;

        int padding = 20 + size / 10;

        int bitmapWidth = (int) (textWidth + shadow.getDx() + strokeWidth + padding);
        int bitmapHeight = (int) (textHeight + shadow.getDy() + strokeWidth + padding);

        area = new TextArea(position, bitmapWidth, bitmapHeight);

        Bitmap bitmap = Bitmap.createBitmap(
                bitmapWidth,
                bitmapHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(backgroundColor);
//        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(textStrokeColor);
        mPaint.setShadowLayer(shadow.getRadius(), shadow.getDx(), shadow.getDy(), shadow.getColor());

        int yOffSet = (int) (strokeWidth / 2 + textHeight / 1.5 + padding / 2 + size / 12);
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

    public Bitmap getFullTextBitmap() {
        Bitmap tempTextBitmap = getTextBitmap();
        Bitmap fullTextBitmap = Bitmap.createBitmap(hostWidth, hostHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(fullTextBitmap);
        Canvas textCanvas = new Canvas(tempTextBitmap);
        Matrix matrix = new Matrix();
        matrix.postRotate(tilt - 180, tempTextBitmap.getWidth() / 2, tempTextBitmap.getHeight() / 2);
        matrix.postTranslate(position.getLeft(), position.getTop());
        if (isSelected) {
            textCanvas.drawColor(SELECTED_TEXT_COLOR);
            textCanvas.drawRect(0, 0, tempTextBitmap.getWidth(), tempTextBitmap.getHeight(), selectedItemPaint);
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

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TextItem> CREATOR = new Creator<TextItem>() {
        @Override
        public TextItem createFromParcel(Parcel parcel) {
            return new TextItem(parcel);
        }

        @Override
        public TextItem[] newArray(int i) {
            return new TextItem[0];
        }
    };
}
