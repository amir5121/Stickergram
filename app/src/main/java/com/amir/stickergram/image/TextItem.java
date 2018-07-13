package com.amir.stickergram.image;

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

public class TextItem extends DrawableItem implements Parcelable {
    public static final int TEXT_BOLD = 0;
    public static final int TEXT_ITALIC = 1;
    public static final int TEXT_NORMAL = 2;
    public static final Typeface DEFAULT_FONT = Typeface.SERIF;
    public static final String DEFAULT_STROKE_COLOR = "#1565c0";
    private static final String DEFAULT_TEXT_COLOR = "#ffffff";

    private String text;
    private int textColor;
    private int backgroundColor;
    private FontItem font;
    private Shadow shadow;
    private int textStrokeColor;
    private int textWidth;
    private int textHeight;
    private float strokeWidth;

    public TextItem(Bitmap hostBitmap) {
        super(hostBitmap);
        textStrokeColor = Color.parseColor(DEFAULT_STROKE_COLOR);
        strokeWidth = 6;
        this.text = "";
        backgroundColor = Color.TRANSPARENT;
        font = new FontItem("Mono Space", DEFAULT_FONT, FontItem.DEFAULTS, FontItem.SERIF);
        shadow = new Shadow(Color.BLACK, 0, 0, 0);
        textColor = Color.parseColor(DEFAULT_TEXT_COLOR);
    }

    private TextItem(Parcel parcel) {
        super(null);
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
        area = parcel.readParcelable(DrawableArea.class.getClassLoader());

        prepTools();
    }

    TextItem(TextItem drawableItem) {
        super(drawableItem.getDrawableFullBitmap());
        hostHeight = drawableItem.hostHeight;
        hostWidth = drawableItem.hostWidth;
        text = drawableItem.text;
        position = new Position(drawableItem.position.getTop() + DrawableItem.COPY_OFFSET, drawableItem.position.getLeft() + DrawableItem.COPY_OFFSET);
        textColor = drawableItem.textColor;
        backgroundColor = drawableItem.backgroundColor;
        font = new FontItem(drawableItem.getFont().getName(), drawableItem.getFont().getTypeface(), drawableItem.getFont().getType(), drawableItem.getFont().getDirectory());
        size = drawableItem.size;
        tilt = drawableItem.getTilt();
        alpha = drawableItem.alpha;
        shadow = new Shadow(drawableItem.getShadow().getColor(), drawableItem.getShadow().getDx(), drawableItem.getShadow().getDy(), drawableItem.getShadow().getRadius());
//        drawableItem.setSelected(false);
//        isSelected = true
        textStrokeColor = drawableItem.textStrokeColor;
        textWidth = drawableItem.textWidth;
        textHeight = drawableItem.textHeight;
        strokeWidth = drawableItem.strokeWidth;
        area = new DrawableArea(drawableItem.getArea().getStartPosition(), drawableItem.getArea().getWidth(), drawableItem.getArea().getHeight());

        prepTools();
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


    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setFont(FontItem font) {
        this.font = font;
    }

    public void setShadow(Shadow shadow) {
        this.shadow = shadow;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public FontItem getFont() {
        return font;
    }

    public int getTilt() {
        return tilt;
    }

    public Shadow getShadow() {
        return shadow;
    }

    public String getText() {
        return text;
    }

    public int getTextColor() {
        return textColor;
    }

    @Override
    Bitmap getDrawableBitmap() {
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

        area = new DrawableArea(position, bitmapWidth, bitmapHeight);

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

    @Override
    Bitmap getDrawableFullBitmap() {
        Bitmap tempTextBitmap = getDrawableBitmap();
        Bitmap fullTextBitmap = Bitmap.createBitmap(hostWidth, hostHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(fullTextBitmap);
        Canvas textCanvas = new Canvas(tempTextBitmap);
        Matrix matrix = new Matrix();
        matrix.postRotate(tilt - 180, tempTextBitmap.getWidth() / 2, tempTextBitmap.getHeight() / 2);
        matrix.postTranslate(position.getLeft(), position.getTop());
        if (isSelected) {
            textCanvas.drawColor(SELECTED_ITEM_COLOR);
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
