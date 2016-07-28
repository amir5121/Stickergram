package com.amir.stickergram.imageProcessing;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Position;

@SuppressLint("ViewConstructor")
class RemoverView extends ImageView implements View.OnTouchListener, AsyncCallFloodFill.AsyncFloodFillCallbacks {
    private static final int INITIAL_RADIUS = 20;
    private static final int INITIAL_OFFSET = 90;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    //    private static final int REMOVE = 3;
    protected int mode = NONE;

    // remember some things for zooming
    private Position mid = new Position(0, 0);
    private Position start = new Position(0, 0);
    private float oldDist = 1f;
    private int radius = INITIAL_RADIUS;
    private boolean modeRemove = true;

    private Bitmap background;
    private int width;
    private int height;
    private Matrix matrix;
    private Matrix savedMatrix;
    private int backgroundWidth;
    private int backgroundHeight;
    private float scale;
    private int[] allPixels;
    private BaseActivity activity;
    private RemoverViewCallbacks listener;
    private int offset = INITIAL_OFFSET;
    private Canvas canvas;
    private Paint circleStroke;
    private boolean zoomMode = false;
    private int left;
    private int top;
    private boolean usingFloodFillPointer = false;
    private Paint linePaint;
    private Bitmap tempBitmap;
    private int tolerance = 15;

//    RemoverView(BaseActivity activity, RemoverViewCallbacks listener, Bitmap mBitmap, int[] pixels, int width, int height) {
    RemoverView(BaseActivity activity, RemoverViewCallbacks listener, Bitmap mBitmap) {
        super(activity);
        this.activity = activity;
        this.listener = listener;

        setLayoutParams();

//        if (mBitmap == null && pixels != null) {
//            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            mBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
//            mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//        }
        if (mBitmap != null) {
            background = mBitmap;
            background.setHasAlpha(true);
            backgroundWidth = background.getWidth();
            backgroundHeight = background.getHeight();
            canvas = new Canvas(Bitmap.createBitmap(backgroundWidth, backgroundHeight, Bitmap.Config.ARGB_8888));
            setImageBitmap(background);

            circleStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
            circleStroke.setColor(Color.RED);
            circleStroke.setStyle(Paint.Style.STROKE);
            circleStroke.setAlpha(200);
            circleStroke.setStrokeWidth(2);

            linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            linePaint.setColor(Color.RED);
            linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            linePaint.setAlpha(200);
            linePaint.setStrokeWidth(2);

            allPixels = new int[backgroundWidth * backgroundHeight];
            background.getPixels(allPixels, 0, backgroundWidth, 0, 0, backgroundWidth, backgroundHeight);

            matrix = new Matrix();
            savedMatrix = new Matrix();
        } else Log.e(getClass().getSimpleName(), "background was null");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (width != getWidth() && height != getHeight() && (getWidth() != 0 || getHeight() != 0)) {
            width = w;
            height = h;

            float heightScale = (float) height / backgroundHeight;
            float widthScale = (float) width / backgroundWidth;
            if (widthScale < heightScale) {
                scale = widthScale - .1f;
                matrix.postScale(scale, scale, width / 2, height / 2);
                float startX = ((width - backgroundWidth) / 2f) * scale;
                float startY = ((height - backgroundHeight) / 2f) * scale;
                matrix.postTranslate(startX, startY);
            } else {
                scale = heightScale - .1f;
                matrix.postScale(scale, scale, width / 2, height / 2);
                float startX = ((width - backgroundWidth) / 2f) * scale;
                float startY = ((height - backgroundHeight) / 2f) * scale;
                matrix.postTranslate(startX, startY);
            }
            setImageMatrix(matrix);
        }
    }

    private void setLayoutParams() {
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setScaleType(ScaleType.MATRIX);
        setLayoutParams(params);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                listener.dismissSeekBarContainers();
                if (!zoomMode) {
                    listener.updateBottomPointer(event.getY(), event.getX(), scale);
                    final float[] coor = getPointerCoordinates(this, event);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!usingFloodFillPointer)
                                drawTransparentCircle((int) coor[0], (int) coor[1], true);
                            else drawTransparentCircle((int) coor[0], (int) coor[1], false);
                        }
                    }).start();
                } else if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.getLeft();
                    float dy = event.getY() - start.getTop();
                    matrix.postTranslate(dx, dy);

                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        scale = (newDist / oldDist);
                        matrix.postScale(scale, scale, mid.getLeft(), mid.getTop());
                    }
                }
                break;
        }

        setImageMatrix(matrix);

        return true;
    }

    private void drawTransparentCircle(int left, int top, boolean changeBitmap) {
        if (background == null) return;
        this.left = left;
        this.top = top;
        left -= radius / 2;
        top = (int) (top - offset / scale - radius / 2);
        if (left < backgroundWidth) {
            int startRow = top * backgroundWidth;
            int endRow = startRow + backgroundWidth * radius;
            if (endRow < -radius) return;
            int mPixel;
            int i = startRow + left;
            int length = allPixels.length;
            int j;
            int currentLine;
            int radiusPowerTwo = (int) Math.pow(radius / 2, 2);
            int x;
            int y;
            final int xCenter = left + radius / 2;
            final int yCenter = getLine(i) + radius / 2;
            int xCenterPowTwo;
            int yCenterPowTwo;
            if (changeBitmap) {
                while (i <= endRow && i < length) {
                    currentLine = getLine(i);
                    x = left;
                    if (left < 0) {
                        currentLine++;
                    }
                    for (j = 0; j < radius; j++) {
                        if (i >= length) {
                            break;
                        } else if (i < 0) continue;
                        mPixel = allPixels[i];
                        y = getLine(i);
                        if (y == currentLine) {
                            xCenterPowTwo = (int) Math.pow(x - xCenter, 2);
                            yCenterPowTwo = (int) Math.pow(y - yCenter, 2);
                            if (xCenterPowTwo + yCenterPowTwo <= radiusPowerTwo) {
                                if (modeRemove) {
                                    if (Color.alpha(mPixel) != 0)
                                        if (xCenterPowTwo + yCenterPowTwo <= radiusPowerTwo - radius) {
                                            allPixels[i] = Color.argb(0, Color.red(mPixel), Color.green(mPixel), Color.blue(mPixel));
                                        } else
                                            allPixels[i] = Color.argb(50, Color.red(mPixel), Color.green(mPixel), Color.blue(mPixel));
                                } else {
                                    allPixels[i] = Color.argb(255, Color.red(mPixel), Color.green(mPixel), Color.blue(mPixel));
                                }
                            }
                        }
                        i++;
                        x++;
                    }
                    if (i > 0)
                        i -= radius;
                    i += backgroundWidth;
                }
                background.setPixels(allPixels, 0, backgroundWidth, 0, 0, backgroundWidth, backgroundHeight);
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Bitmap temp = background.copy(Bitmap.Config.ARGB_8888, true);
                    canvas.setBitmap(temp);
                    canvas.drawCircle(xCenter, yCenter, radius / 2, circleStroke);
                    if (usingFloodFillPointer) {
                        canvas.drawLine(xCenter - radius / 2, yCenter, xCenter + radius / 2, yCenter, linePaint);
                        canvas.drawLine(xCenter, yCenter - radius / 2, xCenter, yCenter + radius / 2, linePaint);
                    }
                    setImageBitmap(temp);
                }
            });
        }
    }

    private int getLine(int i) {
        return i / backgroundWidth;
    }


    private float[] getPointerCoordinates(ImageView view, MotionEvent e) {
        final int index = e.getActionIndex();
        final float[] coordinates = new float[]{e.getX(index), e.getY(index)};
        Matrix matrix = new Matrix();
        view.getImageMatrix().invert(matrix);
        matrix.postTranslate(view.getScrollX(), view.getScrollY());
        matrix.mapPoints(coordinates);
        return coordinates;
    }

    @NonNull
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

    Bitmap getFinishedBitmap() {
        return background;
    }

    boolean removeToggle() {
        return modeRemove = !modeRemove;
    }

    void setRadius(int radius) {
        this.radius = radius;
        drawTransparentCircle(left, top, false);
    }

    boolean changeZoomMode() {
        listener.dismissSeekBarContainers();
        return zoomMode = !zoomMode;
    }

    int getRadius() {
        return radius;
    }

    int getOffset() {
        return offset;
    }

    void setOffset(int offset) {
        this.offset = offset;
        drawTransparentCircle(left, top, false);
    }

    @Override
    public void floodFillFinished(int[] pixels) {
//        tempBitmap = emptyBitmap;
//        setImageBitmap(tempBitmap);
//        background = emptyBitmap;
        background.setPixels(pixels, 0, backgroundWidth, 0, 0, backgroundWidth, backgroundHeight);
        allPixels = pixels;
        setImageBitmap(background);
//        background.getPixels(allPixels, 0, backgroundWidth, 0, 0, backgroundWidth, backgroundHeight);

        listener.floodFillerFinished();
    }

//    public void applyFloodFill(boolean flag) {
//        setUsingFloodFillPointer(false);
//        if (tempBitmap == null) {
//            flag = false;
//            Toast.makeText(getContext(), getContext().getString(R.string.could_not_retrive_image), Toast.LENGTH_SHORT).show();
//        }
//        if (flag) {
//            background = tempBitmap;
//            tempBitmap = null;
//            background.getPixels(allPixels, 0, backgroundWidth, 0, 0, backgroundWidth, backgroundHeight);
//            setImageBitmap(background);
//        } else {
//            setImageBitmap(background);
//        }
//    }

    @Override
    public void onProgressStarted() {
        listener.floodFillerStarted();
    }

    void floodFill() {
        int top = (int) (this.top - offset / scale);

        new AsyncCallFloodFill(this, allPixels, left, top, tolerance, backgroundWidth, backgroundHeight).execute();
    }

    void setUsingFloodFillPointer(boolean usingFloodFillPointer) {
        this.usingFloodFillPointer = usingFloodFillPointer;
        drawTransparentCircle(left, top, false);
    }

    public void setTolerance(int tolerance) {
        this.tolerance = tolerance;
    }

    public int getTolerance() {
        return tolerance;
    }

    public int[] getPixels() {
        return allPixels;
    }

    public int getBackgroundHeight() {
        return backgroundHeight;
    }

    public int getBackgroundWidth() {
        return backgroundWidth;
    }

    interface RemoverViewCallbacks {
        void updateBottomPointer(float top, float left, float scale);

        void floodFillerFinished();

        void floodFillerStarted();

        void dismissSeekBarContainers();
    }
}
