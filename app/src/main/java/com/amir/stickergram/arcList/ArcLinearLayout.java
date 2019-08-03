package com.amir.stickergram.arcList;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.amir.stickergram.R;

public class ArcLinearLayout extends LinearLayout {
    private static final int MIN_PADDING = 30;
    float radiusPow2;
    private float elevation;
    private float radius;
    private int containerWidth;
    private int containerHeight;
    private int startOffset;
    private float itemsOffset;

    public ArcLinearLayout(Context context) {
        super(context);
        starUp(context, null);
    }

    public ArcLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        starUp(context, attrs);
    }

    public ArcLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        starUp(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        starUp(context, attrs);
    }

    private void starUp(Context context, AttributeSet attrs) {
        int minimumPadding = (int) (MIN_PADDING * getResources().getDisplayMetrics().density);
        setOrientation(LinearLayout.HORIZONTAL);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArcLinearLayout);
            final int N = a.getIndexCount();
            for (int i = 0; i < N; ++i) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case R.styleable.ArcLinearLayout_useMinPadding:
                        if (!a.getBoolean(R.styleable.ArcLinearLayout_useMinPadding, true)) {
                            minimumPadding = 0;
                        }
                        break;
                    case R.styleable.ArcLinearLayout_itemsOffset:
                        itemsOffset = a.getDimension(R.styleable.ArcLinearLayout_itemsOffset, 0);
                        break;
                }
            }
            a.recycle();
        }
//        Log.e(getClass().getSimpleName(), "minimumPadding: " + minimumPadding);
        setPadding(minimumPadding + getPaddingLeft(), getPaddingTop(), minimumPadding + getPaddingRight(), getPaddingBottom());
    }

    int getWidthOfTheVisibleCircle(float radius, float strokeWidth) {
        radiusPow2 = (float) Math.pow(radius, 2);

        //The Pythagorean is used in circle to calculate the width of the visible circle
        return (int) (2 * Math.sqrt(2 * (radius * strokeWidth) - strokeWidth * strokeWidth));
    }


    void headsUp() {
        if (getWidth() != 0) {
            float yPow2 = (float) Math.pow(containerHeight - radius, 2);
            float x = (float) -(Math.sqrt(Math.abs(radiusPow2 - yPow2)) - containerWidth / 2); //this is the start position of the circle
            if (containerWidth > getWidth()) {//if  this layout is not as big as the width of the visible circle (which also mean no scrolling)
                x += containerWidth / 2 - x - getWidth() / 2;
//                Log.e(getClass().getSimpleName(), "was smaller x: " + x + " containerWidth: " + containerWidth + " getWidth(): " + getWidth());
                Configuration config = getResources().getConfiguration();
                if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    setTranslationX(-x);
                    //in Right To Left layout
                } else setTranslationX(x);
//            setTranslationX(0);
//            Log.e(getClass().getSimpleName(), " x: " + x);
            } else {
                try {
                    ((ArcScrollView) getParent()).scrollTo((getWidth() - containerWidth) / 2, 0); // trying to scroll to the center of the scrollView which is kinda working
                } catch (ClassCastException e) {
                    throw new RuntimeException("ArcLinearLayout must be parented by a ArcScrollView");
                }
            }
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        Log.e(getClass().getSimpleName(), " onLayout was called: " + getWidth());
        headsUp();
        notifyKids(startOffset, elevation);
    }

    //    void notifyKids(float radius, int containerWidth, int containerHeight, int startOffset, float elevation) {
    void notifyKids(int startOffset, float elevation) {
        this.startOffset = startOffset;
        this.elevation = elevation;
        int[] pos = new int[2];
        int count = getChildCount();
        View currChild;
        float y;
        radiusPow2 = (float) Math.pow(radius, 2);

        float x;
//        Log.e(getClass().getSimpleName(), "radius: " + radius + " containerHeight: " + containerHeight + " containerWidth: " + containerWidth + " startOffset: " + startOffset + " elevation: " + elevation);
//        if (count > 1) {
//            currChild = getChildAt(2);
//            Log.e(getClass().getSimpleName(), "count: " + count + " pos[0]: " + pos[0] + " currChild.getWidth(): " + currChild.getWidth());
//        }

        for (int i = 0; i < count; i++) {
            currChild = getChildAt(i);
            currChild.setY(containerHeight);//making the current view invisible
            currChild.getLocationOnScreen(pos);
            x = pos[0];
            pos[0] -= startOffset;
//            if (pos[0] + currChild.getWidth() > 0 && pos[0] + currChild.getWidth() / 2 > startPos && pos[0] + currChild.getWidth() / 2 < endPos) { //check also if smaller than width of the container
            if (pos[0] + currChild.getWidth() > 0 && currChild.getWidth() != 0) { //check also if smaller than width of the container
                pos[0] -= containerWidth / 2;
                float xPow2 = (float) Math.pow(pos[0] + currChild.getWidth() / 2, 2);
                y = (float) Math.abs(Math.sqrt(Math.abs(radiusPow2 - xPow2)) - radius);
//                Log.e(getClass().getSimpleName(), " elevation: " + elevation + " offset: " + (y * 1.2f + elevation + itemsOffset));

//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
//                    currChild.setY(y * 1.2f);
//                else
                currChild.setY(y * 1.2f + elevation + itemsOffset);

//                currChild.setY(0);
//                Log.e(getClass().getSimpleName(), "------------posX: " + (pos[0] + currChild.getWidth() / 2) + " y: " + y);

//                float angle = (float) (Math.atan2(y, x) * (180 / Math.PI));
//                Log.e(getClass().getSimpleName(), "i: " + i + " angle is: " + (angle) + " posX: " + x + " posY:  " + y);
//                currChild.setRotation(angle);

            }

        }
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setContainerHeight(int containerHeight) {
        this.containerHeight = containerHeight;
    }

    public void setContainerWidth(int containerWidth) {
        this.containerWidth = containerWidth;
    }
}
