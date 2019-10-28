package com.amir.stickergram.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.amir.stickergram.EditImageActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseAuthenticatedActivity;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OnMainImageViewTouch {

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final String TOUCH_IMAGE_VIEW = "TOUCH_IMAGE_VIEW";
    private static final String DRAWABLE_LAYER_NUMBER = "DRAWABLE_LAYER_NUMBER";
    protected int mode = NONE;
    // remember some things for zooming
    private Position mid = new Position(0, 0);
    private float oldDist = 1f;
    private int oldTilt = 180;
    private float d = 0f;
    //    private float[] lastEvent = null;

    private EditImageActivity activity;
    private Position offsetPosition;
    private Position offsetPositionSecondPointer;
    private List<TouchImageView> items;
    private TouchImageView[] label;
    private Bitmap mainBitmap;

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
//                    if (label[0].isItMe(pos) != null || label[1].isItMe(pos) != null)
//                        Toast.makeText(activity, activity.getString(R.string.upgrade_to_pro_to_delete_this_label), Toast.LENGTH_LONG).show();
                    if (label[0].isItMe(pos) != null)
                        Toast.makeText(activity, activity.getString(R.string.upgrade_to_pro_to_delete_this_label), Toast.LENGTH_LONG).show();
                offsetPosition = null;
                for (int i = items.size() - 1; i >= 0; i--) {
                    offsetPosition = items.get(i).isItMe(new Position(event.getY(), event.getX()));
                    if (offsetPosition != null) {
                        Collections.swap(items, items.size() - 1, i);
                        activity.setSelectedLayer(items.get(items.size() - 1));
                        break;
                    }
                }
                if (offsetPosition == null) {
                    Log.e(getClass().getSimpleName(), " onTouch setLayerUnselected");
                    activity.setLayerUnselected();
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f && selectedLayer != null) {
                    oldTilt = selectedLayer.getDrawableItem().getTilt();
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
                break;
            case MotionEvent.ACTION_MOVE: {
                if (selectedLayer != null) {
                    if (mode == DRAG) {
                        selectedLayer.updateDrawablePosition(new Position(event.getY(), event.getX()), offsetPosition);
                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            int size = (int) (selectedLayer.getDrawableItem().getSize() + (newDist - oldDist) / 2);
//                            Log.e(getClass().getSimpleName(), "Size: " + size);
                            if (size > 15)
                                selectedLayer.getDrawableItem().setSize(size);
                            midPoint(mid, event);
                            oldDist = newDist;
                            selectedLayer.updateDrawablePosition(mid, midBetweenTwoPos(offsetPosition, offsetPositionSecondPointer));
                        }
//                        float newRot = rotation(event);
                        float r = rotation(event) - d;
                        selectedLayer.getDrawableItem().setTilt((int) r + oldTilt);
                        selectedLayer.updateDrawable();
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
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(Position point, MotionEvent event) {
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
    private float rotation(MotionEvent event) {
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
//            Log.e(getClass().getSimpleName(), "label was not  null");
            canvas.drawBitmap(label[0].getFinishedBitmap(), 0, 0, null);
//            canvas.drawBitmap(label[1].getFinishedBitmap(), 0, 0, null);
        } else Log.e(getClass().getSimpleName(), "label was null");

        return tempBitmap;
    }

    public void add(TouchImageView touchItem) {
        items.add(touchItem);
    }

    public void remove(TouchImageView selectedLayer) {
        items.remove(selectedLayer);
    }

    private void addLabel() {
        Log.e(getClass().getSimpleName(), "Added label");
        label = new TouchImageView[2];

        label[0] = new TouchImageView(activity, mainBitmap, new TextItem(mainBitmap));
        TextItem textItem = (TextItem) label[0].getDrawableItem();
        textItem.setText(activity.getString(R.string.stickergram));

        int bitmapWidth = mainBitmap.getWidth();
        float labelDecrementRatio = 1600.0f;
//        int strokeWidth = (bitmapWidth / 51) + Math.abs(bitmapWidth - 512) / 80 - 2;
        int strokeWidth = 5;
//        Log.e(getClass().getSimpleName(), "stroke width: " + strokeWidth);
        int stickergramTextSize;
        if (Loader.INSTANCE.deviceLanguageIsPersian()) {
            textItem.setFont(new FontItem(null, Typeface.createFromAsset(activity.getAssets(), Constants.APPLICATION_PERSIAN_FONT_ADDRESS_IN_ASSET), 0, null));
            stickergramTextSize = (int) ((bitmapWidth / 17) + (labelDecrementRatio / bitmapWidth));
        } else {
            textItem.setFont(new FontItem(null, Typeface.createFromAsset(activity.getAssets(), Constants.APPLICATION_ENGLISH_FONT_ADDRESS_IN_ASSET), 0, null));
            stickergramTextSize = (int) ((bitmapWidth / 18) + (labelDecrementRatio / bitmapWidth));
        }
//        int madeWithTextSize = (int) ((bitmapWidth / 30) + (labelDecrementRatio / bitmapWidth));
//        Log.e(getClass().getSimpleName(), "stickergramTextSize: " + stickergramTextSize);
//        Log.e(getClass().getSimpleName(), "madeWithTextSize: " + madeWithTextSize);


//        textItem.setShadow(new Shadow(Color.parseColor("#555555"), 5, 5, 0));
//        textItem.setFont(new FontItem("stickergram Font", Typeface.SANS_SERIF, FontItem.DEFAULTS, FontItem.SANS_SERIF));
        textItem.setStrokeWidth(strokeWidth);
        textItem.setSize(stickergramTextSize);
        textItem.setTextColor(ContextCompat.getColor(activity, R.color.stickergram_label_color));
        textItem.setTextStrokeColor(ContextCompat.getColor(activity, R.color.stickergram_label_stroke_color));
        Bitmap bitmap = textItem.getDrawableBitmap();
        int stickergramHeight = bitmap.getHeight();
//        int stickergramWidth = bitmap.getWidth();

        int top = mainBitmap.getHeight() - stickergramHeight + 20;
        textItem.setPosition(new Position(top, -15));
//        label[0].setTextItem(textItem);
        activity.textLayerContainer.addView(label[0]);
//        label[1] = new TouchImageView(activity, mainBitmap);
//        textItem = label[1].getTextItem();
//        textItem.setFont(new FontItem("stickergram Font", Typeface.SANS_SERIF, FontItem.DEFAULTS, FontItem.SANS_SERIF));
//        textItem.setSize(madeWithTextSize);
//        textItem.setText(activity.getString(R.string.made));
//        textItem.setStrokeWidth(4);
//        textItem.setBackgroundColor(0);
//        textItem.setTextColor(ContextCompat.getColor(activity, R.color.stickergram_label_color));
//        textItem.setTextStrokeColor(ContextCompat.getColor(activity, R.color.stickergram_label_stroke_color));
//        bitmap = textItem.getDrawableBitmap();
//        textItem.setPosition(new Position(top - bitmap.getHeight() / 3.3f,
//                stickergramWidth / 2 - bitmap.getWidth() / 2 - 15));
//        label[1].setTextItem(textItem);
//        activity.textLayerContainer.addView(label[1]);

    }

    public Bundle getSaveState() {
        Bundle bundle = new Bundle();
        bundle.putInt(DRAWABLE_LAYER_NUMBER, items.size());
        for (int i = 0; i < items.size(); i++) {
            Log.e(getClass().getSimpleName(), "getSaveState: " + TOUCH_IMAGE_VIEW + i);
            bundle.putBundle(TOUCH_IMAGE_VIEW + i, items.get(i).getSaveBundle());
        }
        return bundle;
    }

    public void recreateState(Bundle bundle) {
        int size = bundle.getInt(DRAWABLE_LAYER_NUMBER);
        for (int i = 0; i < size; i++) {
            Log.e(getClass().getSimpleName(), "recreateState: " + TOUCH_IMAGE_VIEW + i);
            Bundle touchViewBundle = bundle.getBundle(TOUCH_IMAGE_VIEW + i);
            if (touchViewBundle != null) {
                TouchImageView touchItem = new TouchImageView(activity, touchViewBundle, mainBitmap);
                activity.textLayerContainer.addView(touchItem);
                items.add(touchItem);
            }
        }

    }

    public List<TouchImageView> getItems() {
        return items;
    }
}
