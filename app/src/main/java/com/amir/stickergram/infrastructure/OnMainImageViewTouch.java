package com.amir.stickergram.infrastructure;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.amir.stickergram.EditImageActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseAuthenticatedActivity;

import java.util.ArrayList;
import java.util.List;

public class OnMainImageViewTouch {

    protected static final int NONE = 0;
    protected static final int DRAG = 1;
    protected static final int ZOOM = 2;
    private static final String TOUCH_IMAGE_VIEW = "TOUCH_IMAGE_VIEW";
    private static final String TEXT_LAYER_NUMBER = "TEXT_LAYER_NUMBER";
    protected int mode = NONE;
    // remember some things for zooming
    protected Position mid = new Position(0, 0);
    protected float oldDist = 1f;
    protected float d = 0f;
    protected float newRot = 0f;
    protected float[] lastEvent = null;

    EditImageActivity activity;
    Position offsetPosition;
    Position offsetPositionSecondPointer;
    List<TouchImageView> items;
    TouchImageView[] label;
    Bitmap mainBitmap;

    public OnMainImageViewTouch(EditImageActivity activity, Bitmap mainBitmap, ImageView mainImageView) {
        this.activity = activity;
        this.mainBitmap = mainBitmap;
        items = new ArrayList<>();
        if (!BaseAuthenticatedActivity.isPaid) {
            addLabel();
        }
        mainImageView.setImageBitmap(getFinishedBitmap());
    }

    public void onTouch(TouchImageView selectedLayer, MotionEvent event) {
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mode = DRAG;
                Position pos = new Position(event.getY(), event.getX());
                if (label != null)
                    if (label[0].isItMe(pos) != null || label[1].isItMe(pos) != null)
                        Toast.makeText(activity, activity.getString(R.string.upgrade_to_pro_to_delete_this_label), Toast.LENGTH_LONG).show();
                for (int i = items.size() - 1; i >= 0; i--) {
                    offsetPosition = items.get(i).isItMe(new Position(event.getY(), event.getX()));
                    if (offsetPosition != null) {
                        activity.setSelectedLayer(items.get(i));
                        break;
                    } else {
                        activity.setLayerUnselected();
                    }
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
//                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    for (int i = items.size() - 1; i >= 0; i--) {
                        offsetPositionSecondPointer = items.get(i).isItMe(new Position(event.getY(1), event.getX(1)));
                        if (offsetPositionSecondPointer != null) {
                            mode = ZOOM;
                            Log.e(getClass().getSimpleName(), "mode is zoom");
                            break;
                        } else {
                            mode = DRAG;
                            Log.e(getClass().getSimpleName(), "mode is drag");
                        }
                    }
                }
                d = rotation(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE: {
                if (selectedLayer != null) {
                    if (mode == DRAG) {
                        selectedLayer.updateTextPosition(new Position(event.getY(), event.getX()), offsetPosition);
                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            int size = (int) (selectedLayer.getTextSize() + (newDist - oldDist) / 2);
//                            Log.e(getClass().getSimpleName(), "Size: " + size);
                            if (size > 15)
                                selectedLayer.getTextItem().setSize(size);
                            midPoint(mid, event);
                            oldDist = newDist;
                            selectedLayer.updateTextPosition(mid, midBetweenTwoPos(offsetPosition, offsetPositionSecondPointer));
                        }
                        newRot = rotation(event);
                        float r = newRot - d;
                        selectedLayer.getTextItem().setTilt((int) r + 180);
                        selectedLayer.updateTextView();
                    }
                }
                break;
            }
        }
    }

    private Position midBetweenTwoPos(Position offsetPosition, Position offsetPositionSecondPointer) {
        return new Position((offsetPosition.getTop() + offsetPositionSecondPointer.getTop()) / 2,
                (offsetPosition.getLeft() + offsetPositionSecondPointer.getLeft()) / 2);
    }

    /**
     * Determine the space between the first two fingers
     */
    public float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    public void midPoint(Position point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    public float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    public Bitmap getFinishedBitmap() {
        final Bitmap tempBitmap = mainBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(tempBitmap);

        for (TouchImageView imageItem : items) {
            canvas.drawBitmap(imageItem.getFinishedBitmap(), 0, 0, null);
        }
        if (label != null) {
            Log.e(getClass().getSimpleName(), "label was not  null");
            canvas.drawBitmap(label[0].getFinishedBitmap(), 0, 0, null);
            canvas.drawBitmap(label[1].getFinishedBitmap(), 0, 0, null);
        }

        return tempBitmap;
    }

    public void add(TouchImageView touchItem) {
        items.add(touchItem);
    }

    public void remove(TouchImageView selectedLayer) {
        items.remove(items.indexOf(selectedLayer));
    }

    private void addLabel() {
        label = new TouchImageView[2];
        int bitmapWidth = mainBitmap.getWidth();
        float labelDecrementRatio = 1600.0f;
        int strokeWidth = (bitmapWidth / 51) + Math.abs(bitmapWidth - 512) / 80;
//        Log.e(getClass().getSimpleName(), "strokeWidth: " + strokeWidth);
        int stickergramTextSize = (int) ((bitmapWidth / 18) + (labelDecrementRatio / bitmapWidth));
        int madeWithTextSize = (int) ((bitmapWidth / 30) + (labelDecrementRatio / bitmapWidth));
        Log.e(getClass().getSimpleName(), "stickergramTextSize: " + stickergramTextSize);
        Log.e(getClass().getSimpleName(), "madeWithTextSize: " + madeWithTextSize);
        label[0] = new TouchImageView(activity, mainBitmap);
        TextItem textItem = label[0].getTextItem();
        textItem.setText(activity.getString(R.string.stickergram));
//        textItem.setBackgroundColor(ContextCompat.getColor(this, R.color.stickergram_label_background));
        textItem.setFont(new FontItem("stickergram Font", Typeface.SANS_SERIF, FontItem.DEFAULTS, FontItem.SANS_SERIF));
        textItem.setStrokeWidth(strokeWidth);
        textItem.setSize(stickergramTextSize);
        textItem.setTextColor(ContextCompat.getColor(activity, R.color.stickergram_label_color));
        textItem.setTextStrokeColor(ContextCompat.getColor(activity, R.color.stickergram_label_stroke_color));
        Bitmap bitmap = textItem.getTextBitmap();
        int stickergramHeight = bitmap.getHeight();
        int stickergramWidth = bitmap.getWidth();
//        textItem.setPosition(new Position(mainBitmap.getHeight() - stickergramHeight, mainBitmap.getWidth() - stickergramWidth));

        int top = mainBitmap.getHeight() - stickergramHeight + 20;
        textItem.setPosition(new Position(top, -15));
        label[0].setTextItem(textItem);
        activity.textLayerContainer.addView(label[0]);
        label[1] = new TouchImageView(activity, mainBitmap);
        textItem = label[1].getTextItem();
        textItem.setFont(new FontItem("stickergram Font", Typeface.SANS_SERIF, FontItem.DEFAULTS, FontItem.SANS_SERIF));
        textItem.setSize(madeWithTextSize);
        textItem.setText(activity.getString(R.string.made));
        textItem.setStrokeWidth(4);
        textItem.setBackgroundColor(0);
        textItem.setTextColor(ContextCompat.getColor(activity, R.color.stickergram_label_color));
        textItem.setTextStrokeColor(ContextCompat.getColor(activity, R.color.stickergram_label_stroke_color));
        bitmap = textItem.getTextBitmap();
//        textItem.setPosition(new Position(0,0));
        textItem.setPosition(new Position(top - bitmap.getHeight() / 3.3f,
                stickergramWidth / 2 - bitmap.getWidth() / 2 - 15));
        label[1].setTextItem(textItem);
        activity.textLayerContainer.addView(label[1]);

    }

    public Bundle getSaveState() {
        Bundle bundle = new Bundle();
        bundle.putInt(TEXT_LAYER_NUMBER, items.size());
        for (int i = 0; i < items.size(); i++) {
            Log.e(getClass().getSimpleName(), "getSaveState: " + TOUCH_IMAGE_VIEW + i);
            bundle.putBundle(TOUCH_IMAGE_VIEW + i, items.get(i).getSaveBundle());
        }
        return bundle;
    }

    public void recreateState(Bundle bundle) {
        int size = bundle.getInt(TEXT_LAYER_NUMBER);
        for (int i = 0; i < size; i++) {
            Log.e(getClass().getSimpleName(), "recreateState: " + TOUCH_IMAGE_VIEW + i);
            TouchImageView touchItem = new TouchImageView(activity, bundle.getBundle(TOUCH_IMAGE_VIEW + i), mainBitmap);
            activity.textLayerContainer.addView(touchItem);
            items.add(touchItem);
        }

    }
}
