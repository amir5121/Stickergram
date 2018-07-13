package com.amir.stickergram.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


public class ImageItem extends DrawableItem implements Parcelable {
    private static final String TAG = "ImageItem";
    private Bitmap originalBitmap;
    private int maxSize;
    private Bitmap bitmapCurrent;

    public ImageItem(Bitmap hostBitmap, Bitmap bitmap) {
        super(hostBitmap);
        this.originalBitmap = bitmap;
        bitmapCurrent = bitmap;
        size = hostWidth / 4;
        maxSize = hostWidth / 2;
    }


    private ImageItem(Parcel parcel) {
        super(null);
        hostWidth = parcel.readInt();
        hostHeight = parcel.readInt();
        position = parcel.readParcelable(Position.class.getClassLoader());
        size = parcel.readInt();
        tilt = parcel.readInt();
        alpha = parcel.readInt();
        isSelected = parcel.readByte() == 1;
        maxSize = parcel.readInt();
        area = parcel.readParcelable(DrawableArea.class.getClassLoader());
        originalBitmap = parcel.readParcelable(Bitmap.class.getClassLoader());

        prepTools();
    }

    ImageItem(ImageItem drawableItem) {
        super(drawableItem.getDrawableFullBitmap());
        hostHeight = drawableItem.hostHeight;
        hostWidth = drawableItem.hostWidth;
        position = new Position(drawableItem.position.getTop() + DrawableItem.COPY_OFFSET, drawableItem.position.getLeft() + DrawableItem.COPY_OFFSET);
        size = drawableItem.size;
        tilt = drawableItem.getTilt();
        alpha = drawableItem.alpha;
//        drawableItem.setSelected(false);
//        isSelected = true
        maxSize = drawableItem.maxSize;
        originalBitmap = drawableItem.originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmapCurrent = drawableItem.bitmapCurrent.copy(Bitmap.Config.ARGB_8888, true);
        area = new DrawableArea(drawableItem.getArea().getStartPosition(), drawableItem.getArea().getWidth(), drawableItem.getArea().getHeight());

        prepTools();
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(hostWidth);
        parcel.writeInt(hostHeight);
        parcel.writeParcelable(position, 0);
        parcel.writeInt(size);
        parcel.writeInt(tilt);
        parcel.writeInt(alpha);
        parcel.writeByte((byte) (isSelected ? 1 : 0));
        parcel.writeInt(maxSize);
        parcel.writeParcelable(area, 0);
        parcel.writeParcelable(originalBitmap, 0);

        Log.e(TAG, "writeToParcel: ");
    }

    @Override
    Bitmap getDrawableBitmap() {
        bitmapCurrent = Bitmap.createScaledBitmap(this.originalBitmap,
                (int) (this.originalBitmap.getWidth() * getScale()), (int) (this.originalBitmap.getHeight() * getScale()), false);
        area = new DrawableArea(position, bitmapCurrent.getWidth(), bitmapCurrent.getHeight());
        return bitmapCurrent;
    }

    private float getScale() {
        return size / (maxSize / 2f);
    }

    @Override
    Bitmap getDrawableFullBitmap() {
        Bitmap tempBitmap = getDrawableBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Bitmap fullTextBitmap = Bitmap.createBitmap(hostWidth, hostHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(fullTextBitmap);
        Canvas textCanvas = new Canvas(tempBitmap);
        Matrix matrix = new Matrix();
        matrix.postRotate(tilt - 180, tempBitmap.getWidth() / 2, tempBitmap.getHeight() / 2);
        matrix.postTranslate(position.getLeft(), position.getTop());
        if (isSelected) {
            textCanvas.drawColor(SELECTED_ITEM_COLOR);
            textCanvas.drawRect(0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), selectedItemPaint);
            textCanvas.save();
            textCanvas.restore();
        }
        canvas.drawBitmap(tempBitmap, matrix, new Paint());
        canvas.save();
        canvas.restore();
        return fullTextBitmap;

    }


    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel parcel) {
            return new ImageItem(parcel);
        }

        @Override
        public ImageItem[] newArray(int i) {
            return new ImageItem[0];
        }
    };
}
