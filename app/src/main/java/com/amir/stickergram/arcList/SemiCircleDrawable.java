package com.amir.stickergram.arcList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;

public class SemiCircleDrawable extends Drawable {
    //http://stackoverflow.com/questions/15962745/draw-a-semicircle-in-the-background-of-a-view

    private final float circleRadius;
    private final float elevation;
    private Paint paint;
    private RectF rectF;
    private int color;
    private Direction angle;
    private int width;

    public enum Direction {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

//    public SemiCircleDrawable() {
//        this(Color.BLUE, Direction.TOP, 0);
//    }

    public SemiCircleDrawable(int color, Direction angle, float circleRadius, int width, float elevation) {
        this.color = color;
        this.angle = angle;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        rectF = new RectF();
        this.elevation = elevation;
        this.width = width;
        this.circleRadius = circleRadius;
    }

    public int getColor() {
        return color;
    }

    /**
     * A 32bit color not a color resources.
     *
     * @param color
     */
    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();

//        canvas.drawCircle(BaseActivity.screenWidth / 2,
//                500 * BaseActivity.density,
//                500 * BaseActivity.density, paint);

//        canvas.drawCircle(ArcScrollView.screenWidth / 2,
        canvas.drawCircle(width / 2,
                circleRadius + elevation,
                circleRadius - elevation, paint);

//        Log.e(getClass().getSimpleName(), "circleRad: " + circleRadius);
//
//        Rect bounds = getBounds();
//
//        if (angle == Direction.LEFT || angle == Direction.RIGHT) {
//            canvas.scale(2, 1);
//            if (angle == Direction.RIGHT) {
//                canvas.translate(-(bounds.right / 2), 0);
//            }
//        } else {
//            canvas.scale(1, 2);
//            if (angle == Direction.BOTTOM) {
//                canvas.translate(0, -(bounds.bottom / 2));
//            }
//        }
//
//
//        rectF.set(bounds);
//
//        if (angle == Direction.LEFT)
//            canvas.drawArc(rectF, 90, 180, true, paint);
//        else if (angle == Direction.TOP)
//            canvas.drawArc(rectF, -180, 180, true, paint);
//        else if (angle == Direction.RIGHT)
//            canvas.drawArc(rectF, 270, 180, true, paint);
//        else if (angle == Direction.BOTTOM)
//            canvas.drawArc(rectF, 0, 180, true, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        // Has no effect
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // Has no effect
    }

    @Override
    public int getOpacity() {
        // Not Implemented
        return PixelFormat.TRANSLUCENT;
    }

}