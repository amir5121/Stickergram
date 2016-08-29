package com.amir.stickergram.arcList;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.HorizontalScrollView;

import com.amir.stickergram.R;

public class ArcScrollView extends HorizontalScrollView {
    public static float screenWidth;
    private float circleRadius;
    private float strokeWidth;
    private int width;
    private int startOffset;
    private boolean centerHorizontal = true;
    float elevation;
    //    private int level;
    private int height;
    private boolean useBestWidth = true;
    private boolean isClipping = false;
    private int prevChildBottom;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Rect mBounds;
    private boolean animationInProgress;

    public ArcScrollView(Context context) {
        super(context);
        startUp(context, null);
    }

    public ArcScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        startUp(context, attrs);
    }

    public ArcScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        startUp(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        startUp(context, attrs);
    }

    private void startUp(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArcScrollView);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.ArcScrollView_radius:
                    circleRadius = a.getDimension(R.styleable.ArcScrollView_radius, 0);
//                    Log.e(getClass().getSimpleName(), "radius: " + circleRadius);
                    break;
                case R.styleable.ArcScrollView_stroke_width:
                    strokeWidth = a.getDimension(R.styleable.ArcScrollView_stroke_width, 0);
                    break;
                case R.styleable.ArcScrollView_findBestWidth:
                    useBestWidth = a.getBoolean(R.styleable.ArcScrollView_findBestWidth, true);
                    break;
//                case R.styleable.ArcScrollView_level:
//                    level = a.getInteger(R.styleable.ArcScrollView_level, -1);
////                    event.setLevel(level);
//                    break;
            }
        }
        a.recycle();

        if (circleRadius == 0 || strokeWidth == 0)
            throw new RuntimeException("You need to specify radius and stoke width of your ArcScrollView and they must not be zero");

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        setHorizontalScrollBarEnabled(false);

        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            if (attrs.getAttributeName(i).equals("layout_centerHorizontal")) {
                centerHorizontal = attrs.getAttributeBooleanValue(i, true);
            }
        }
    }

    public boolean getIsCenterHorizontal() {
        return centerHorizontal;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, measureHeight(heightMeasureSpec));
        int visibleWidth = 0;
        if (getChildCount() == 1)
            visibleWidth = ((ArcLinearLayout) getChildAt(0)).getWidthOfTheVisibleCircle(circleRadius, strokeWidth + prevChildBottom);
//        Log.e(getClass().getSimpleName(), "width: " + MeasureSpec.getSize(widthMeasureSpec) + " visibleWidth: " + visibleWidth + " useBestWidth: " + useBestWidth);
        if (MeasureSpec.getSize(widthMeasureSpec) < visibleWidth || !useBestWidth || visibleWidth == 0) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            super.onMeasure(widthMeasureSpec, measureHeight(heightMeasureSpec));
//            Log.e(getClass().getSimpleName(), "-----didn't use bestWidth: " + MeasureSpec.getSize(widthMeasureSpec));
        } else {
//            super.onMeasure(View.MeasureSpec.makeMeasureSpec(visibleWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(visibleWidth, MeasureSpec.EXACTLY), measureHeight(heightMeasureSpec));
//            Log.e(getClass().getSimpleName(), "used bestWidth: " + visibleWidth);
        }


//        Log.e(getClass().getSimpleName(), "-----widthMeasureSpec: " + widthMeasureSpec + " heightMeasureSpec: " + heightMeasureSpec);
//        setMeasuredDimension(widthMeasureSpec, 1000);
//        newWidth = ((ArcLinearLayout) getChildAt(0)).getWidthOfTheVisibleCircle(circleRadius, getStrokeWidth(), width, height);
//        notifyScroll();
    }

    public int measureHeight(int measureSpec) {
        // taken from http://stackoverflow.com/questions/7420060/make-children-of-horizontalscrollview-as-big-as-the-screen
        // and from  http://stackoverflow.com/questions/14493732/what-are-widthmeasurespec-and-heightmeasurespec-in-android-custom-views
        // and of course a simple modification in one line of copied code :D

        int result = (int) (prevChildBottom + strokeWidth);
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
//        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        int screenWidth = display.getWidth();

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
//            Log.e(getClass().getSimpleName(), "EXACTLY");
            result = specSize;
        }
//        else {
//            // Measure the view
//            if (specMode == MeasureSpec.AT_MOST) {
//                // Respect AT_MOST value if that was what is called for by measureSpec
////                Log.e(getClass().getSimpleName(), "AT_MOST");
////                result = Math.min(result, specSize);
//            }
//        }
////        Log.d(getClass().getSimpleName(), "newHeight: " + result + " prevChildBottom: " + prevChildBottom + " specSize: " + specSize);
//        Log.d(getClass().getSimpleName(), "newHeight: " + result + " prevChildBottom: " + (prevChildBottom + strokeWidth) + " specSize: " + specSize);

//        return result;
//        return View.MeasureSpec.makeMeasureSpec((int) strokeWidth, MeasureSpec.EXACTLY);
        return View.MeasureSpec.makeMeasureSpec(result, MeasureSpec.EXACTLY);
//        return measureSpec;
    }

//    @Override
//    public void addView(View child, int width, int height) {
//        super.addView(child, width, height);
//        notify();
//        notifyScroll();
//    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

//        Log.e(getClass().getSimpleName(), "onWindowFocusChanged");
        if (width == 0) {
            width = getWidth();
            elevation = 0;
            startOffset = getLeft();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                elevation = getElevation();
            setBackground(new SemiCircleDrawable(getBackgroundColor(this), SemiCircleDrawable.Direction.BOTTOM, circleRadius, width, elevation));

        }
        if (height == 0) height = getHeight();

        if (getChildCount() == 1 && height != 0 && width != 0) {
            setMeasurements();
            ((ArcLinearLayout) getChildAt(0)).headsUp();
            notifyScroll();
        }
//        if (centerHorizontal) {
////            setLeft((int) (screenWidth / 2 - width / 2));
////            setLeft(((VerticalArcContainer) getParent()).getWidth() / 2 - width / 2);
//            Log.e(getClass().getSimpleName(), "width: " + width + " getWidth(): " + getWidth() + " height: " + height + " getHeight():" + getHeight() + " leftWasSet: " + getLeft());
//        }

//        Log.e(getClass().getSimpleName(), " mColor: " + mColor);

//        if (mColor != null)
//            setBackground(new SemiCircleDrawable(Color.parseColor(mColor), SemiCircleDrawable.Direction.BOTTOM, circleRadius, width, elevation));
//        else
//            setBackground(new SemiCircleDrawable(Color.BLUE, SemiCircleDrawable.Direction.BOTTOM, circleRadius, width, elevation));
//            setBackground(new SemiCircleDrawable(getBackgroundColor(this), SemiCircleDrawable.Direction.BOTTOM, circleRadius, width, elevation));


    }

    public void initIfNeeded() {
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mBounds = new Rect();
        }
    }

    public int getBackgroundColor(View view) {
        //http://stackoverflow.com/questions/8089054/get-the-background-color-of-a-button-in-android
        // The actual color, not the id.
        int color = Color.BLACK;

        if (view.getBackground() instanceof ColorDrawable) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                initIfNeeded();

                // If the ColorDrawable makes use of its bounds in the draw method,
                // we may not be able to get the color we want. This is not the usual
                // case before Ice Cream Sandwich (4.0.1 r1).
                // Yet, we change the bounds temporarily, just to be sure that we are
                // successful.
                ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();

                mBounds.set(colorDrawable.getBounds()); // Save the original bounds.
                colorDrawable.setBounds(0, 0, 1, 1); // Change the bounds.

                colorDrawable.draw(mCanvas);
                color = mBitmap.getPixel(0, 0);

                colorDrawable.setBounds(mBounds); // Restore the original bounds.
            } else {
                color = ((ColorDrawable) view.getBackground()).getColor();
            }
        }

        return color;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (Math.pow(ev.getX() - width / 2, 2) + Math.pow(ev.getY() - circleRadius, 2) >= circleRadius * circleRadius || !isEnabled()) {
                    return false;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        notifyScroll();
//        Log.e(getClass().getSimpleName(), "onScrollChanged");

    }

    private void notifyScroll() {
        try {
//            Log.e(getClass().getSimpleName(), "width: " + width + " getWidth(): " + getWidth() + " height: " + height + " getHeight():" + getHeight());
//            ((ArcLinearLayout) getChildAt(0)).notifyKids(circleRadius, width, height, startOffset, elevation);
            ((ArcLinearLayout) getChildAt(0)).notifyKids(startOffset, elevation);
        } catch (ClassCastException e) {
            throw new RuntimeException("ArcScrollView can only contain ArcLinearLayout");
        }
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new CustomOutline(w, h));
        }
    }

    //    public int getNewWidth(int prevChildBottom) {
//    public int getNewWidth() {
////        this.prevChildBottom = prevChildBottom;
//        if (useBestWidth) {
////            int visibleWidth = ((ArcLinearLayout) getChildAt(0)).getWidthOfTheVisibleCircle(circleRadius, getStrokeWidth() + prevChildBottom);
//            int visibleWidth = ((ArcLinearLayout) getChildAt(0)).getWidthOfTheVisibleCircle(circleRadius, strokeWidth + prevChildBottom);
//            Log.e(getClass().getSimpleName(), " radius: " + circleRadius + " prevChildBottom: " + prevChildBottom + " strokeWidth: " + strokeWidth);
////            int visibleWidth = ((ArcLinearLayout) getChildAt(0)).getWidthOfTheVisibleCircle(circleRadius, getStrokeWidth() );
//            if (getWidth() != 0)
//                isClipping = (getWidth() != 0 && visibleWidth > width);
//
////            requestLayout();
////            Log.e(getClass().getSimpleName(), "isClipping: " + isClipping + " visibleWidth: " + visibleWidth + " getWidth(): " + getWidth() + " width: " + width);
//            return visibleWidth;
//        }
//        return 0;
//    }

//    public int getLevel() {
//        return level;
//    }
//
//    public void setLevel(int level) {
//        this.level = level;
//    }

    public void setPrevChildBottom(int prevChildBottom) {
        this.prevChildBottom = prevChildBottom;
    }

    public void swapView(final ArcLinearLayout view) {
        if (!animationInProgress)
            if (view == null) { //in case you want to hide the view
                animate()
                        .alpha(0)
                        .translationY(strokeWidth + prevChildBottom)
                        .setDuration(500)
                        .setInterpolator(new BounceInterpolator())
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                animationInProgress = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                setVisibility(GONE);
                                animationInProgress = false;
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        })
                        .start();
            } else if (getVisibility() != GONE) {
                animate()
                        .alpha(0)
                        .translationY(strokeWidth + prevChildBottom)
                        .setDuration(700)
                        .setInterpolator(new BounceInterpolator())
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                animationInProgress = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                bringBackUp(view);
                                animationInProgress = false;
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        })
                        .start();
            } else {//if it is already hidden make it appear
                setTranslationY(strokeWidth + prevChildBottom);
                setVisibility(VISIBLE);
                bringBackUp(view);
            }


    }

    private void bringBackUp(ArcLinearLayout view) {
        removeView(getChildAt(0));
        addView(view, 0);
        setMeasurements();
        animate().
                alpha(1).
                translationY(0).
                setDuration(300).
                setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        animationInProgress = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        animationInProgress = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }).
                setInterpolator(new BounceInterpolator()).
                start();
        notifyScroll();
    }

    private void setMeasurements() {
        ArcLinearLayout child = ((ArcLinearLayout) getChildAt(0));
        child.setRadius(circleRadius);
        child.setContainerHeight(height);
        child.setContainerWidth(width);


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class CustomOutline extends ViewOutlineProvider {

        int width;
        int height;

        CustomOutline(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public void getOutline(View view, Outline outline) {
//            float yPow2 = (float) Math.pow(ArcScrollView.this.height, 2);
//            int radiusPow2 = (int) Math.pow(ArcScrollView.this.circleRadius, 2);
//            int x = (int) -(Math.sqrt(Math.abs(radiusPow2 - yPow2)) - ArcScrollView.this.width / 2); //this is the start position of the circle
//            int x = (int) -(Math.sqrt(Math.abs(radiusPow2 - yPow2)) - ArcScrollView.this.width / 2); //this is the start position of the circle
//            int y = (int) -(Math.sqrt(Math.abs(radiusPow2 - Math.pow(ArcScrollView.this.width / 2f, 2))) - ArcScrollView.this.circleRadius);

//            Log.e(getClass().getSimpleName(), "ArcScrollView height: " + ArcScrollView.this.height + " height: " + height + " ArcScrollView width: " + ArcScrollView.this.width + " width: " + width + " x: " + x + " y: " + y);


////            this is not totally correct  this will only work if the visible part of the circle is complete and not being cut off... weirdly the shadow will go on beyond the scope of the current view
//            //should use path..
//            if (!isClipping) {
            outline.setOval((int) (ArcScrollView.this.width / 2 - ArcScrollView.this.circleRadius),
                    0,
                    (int) (ArcScrollView.this.width / 2 + ArcScrollView.this.circleRadius),
                    (int) (ArcScrollView.this.circleRadius * 2));
//            } else {

//                Path path = new Path();
//                path.moveTo(0, height);
//                path.lineTo(width, height);
//                int radiusPow2 = (int) (circleRadius * circleRadius);
//                int yRight = (int) Math.abs(Math.sqrt(Math.abs(radiusPow2 - (-width / 2))) - circleRadius);
//                int yLeft = (int) Math.abs(Math.sqrt(Math.abs(radiusPow2 - (width / 2))) - circleRadius);
//
//                path.lineTo(width, yLeft);
//                path.addOval(0, yRight, width, yLeft, Path.Direction.CCW);
//                path.lineTo(0, height);
//
//                outline.setConvexPath(path);
//        }
        }
    }

}
