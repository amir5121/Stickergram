package com.amir.stickergram.arcList;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

public class VerticalArcContainer extends ViewGroup {
    private static final String TAG = "VerticalArcContainer";
    int prevChildBottom = 0; //kinda doesn't need to be global
    List<Integer> childrenStrokes;
    private float totalHeight;
    private int animationBuffer;
    private ArcCallBack animationCallBack;

    public VerticalArcContainer(Context context) {
        super(context);
        startUp(context);
    }

    public VerticalArcContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        startUp(context);
    }

    public VerticalArcContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        startUp(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VerticalArcContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        startUp(context);
    }

    private void startUp(Context context) {
//        mScrollViewChildren = new HashMap<>();
        childrenStrokes = new ArrayList<>();

        try {
            animationCallBack = (ArcCallBack) context;
        } catch (ClassCastException e) {
            //INTENTIONALLY EMPTY
        }
    }

    public void hideChild(int id) {
        collapseChild(indexOfChild(findViewById(id)));
    }

    private void collapseChild(final int i) {
        final ArcScrollView child = (ArcScrollView) getChildAt(i);
        if (child != null) {
            final int size = childrenStrokes.size();
            if (i >= 0 && i < size)
                child.animate()
//                        .translationY(childrenStrokes.get(i) + child.getStrokeWidth())
//                        .translationY(prevChildBottom)
                        .translationY(totalHeight)
                        .setDuration(500)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (i == 0) animationCallBack.itAllKnockedOut();

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        })
//                        .alpha(.2f)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
        }
    }

    public void showChild(int id) {
        expandChild(indexOfChild(findViewById(id)));
    }


    public void expandChild(final int childPosition) {
        final View child = getChildAt(childPosition);
        if (child != null) {
            if (childPosition >= 0 && childPosition < childrenStrokes.size())
                child.animate()
                        .translationY(0)
                        .setDuration(400)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (childPosition == childrenStrokes.size() - 1)
                                    animationCallBack.itAllKnockedIn();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        })
//                        .alpha(1)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int rightOffset;
        int count = getChildCount();
//        childrenStrokes.clear();
//        mScrollViewChildren.clear();

//        Log.e(getClass().getSimpleName(), " onLayout was called");

        prevChildBottom = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
//            rightOffset = child.getLeft();
            rightOffset = child.getLeft();
            if (child instanceof ArcScrollView) {
                ArcScrollView currArcScrollView = ((ArcScrollView) child);
//                Log.e(getClass().getSimpleName(), " getWidth(): " + getWidth());
                if (currArcScrollView.getIsCenterHorizontal())
                    rightOffset = getWidth() / 2 - child.getMeasuredWidth() / 2;
//                Log.e(getClass().getSimpleName(), " rightOffset: " + rightOffset + " getWidth: " + getWidth() + " currArcScrollView.getWidth(): " + currArcScrollView.getWidth() + " child.getMeasuredWidth(): " + child.getMeasuredWidth());
//                Log.e(getClass().getSimpleName(), "count: " + count + " i: " + i);

//                int bestWidth = currArcScrollView.getNewWidth(childrenStrokes.get(count - i - 1));
//                int bestWidth = currArcScrollView.getNewWidth(childrenStrokes.get(i));
//                int bestWidth = currArcScrollView.getNewWidth();
//                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) //FML
//                    bestWidth *= 2;
//                int bestWidth = currArcScrollView.getNewWidth(prevChildBottom);
//                Log.e(getClass().getSimpleName(), "newWidth: " + bestWidth + " i: " + i + " prevChildBottom: " + prevChildBottom + " measuredWidth: " + child.getMeasuredWidth());
//                Log.e(getClass().getSimpleName(), "i: " + i + " prevChildBottom: " + prevChildBottom + " measuredWidth: " + child.getMeasuredWidth() + " bottom: " + childrenStrokes.get(count - 1));
//                Log.e(getClass().getSimpleName(), "-----i: " + i + " used bestWidth: " + bestWidth + " measuredWidth: " + child.getMeasuredWidth() + " rightOffset: " + rightOffset);
//                if (bestWidth != 0 && child.getMeasuredWidth() > bestWidth) {
//                    child.layout(
//                            rightOffset,
//                            prevChildBottom,
////                            rightOffset + child.getMeasuredWidth(),
//                            rightOffset + bestWidth,
//                            prevChildBottom + child.getMeasuredHeight());
////                            (int) (prevChildBottom + currArcScrollView.getStrokeWidth()));
////                            childrenStrokes.get(count - 1));
//                } else {
                child.layout(
                        rightOffset,
                        prevChildBottom,
                        rightOffset + child.getMeasuredWidth(),
//                            rightOffset + 100,
//                            prevChildBottom + child.getMeasuredHeight());
//                            (int) (prevChildBottom + currArcScrollView.getStrokeWidth()));
//                            prevChildBottom + childrenStrokes.get(count - i - 1));
//                            childrenStrokes.get(count - 1));
                        (int) totalHeight);

//                }
                prevChildBottom += currArcScrollView.getStrokeWidth();
//                mScrollViewChildren.put(currArcScrollView.getLevel(), currArcScrollView);
//                Log.e(getClass().getSimpleName(), "i: " + i + " level: " + currArcScrollView.getLevel() + " visibility: " + currArcScrollView.getVisibility());
            } else {
                Log.e(getClass().getSimpleName(), " onLayout else was called ****MUST NOT HAPPEN***");
//                child.layout(rightOffset, prevChildBottom,
//                        rightOffset + child.getMeasuredWidth(),
//                        prevChildBottom + child.getMeasuredHeight());
//                prevChildBottom += child.getMeasuredHeight();
//                childrenStrokes.add(child.getMeasuredHeight());
            }
//            childrenStrokes.add(prevChildBottom);
        }
//        Log.e(getClass().getSimpleName(), "onLayout prevChildBottom: " + prevChildBottom);
    }

    public void knockout() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final Handler handler = new Handler();
            final int finalI = i;
            animationBuffer++;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animationBuffer--;
                    collapseChild(finalI);
                }
            }, 75 * (animationBuffer));
        }
    }

    public void knockIn() {
        int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            animationBuffer++;
            final Handler handler = new Handler();
            final int finalI = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animationBuffer--;
                    expandChild(finalI);
                }
            }, 75 * animationBuffer);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //taken from http://stackoverflow.com/questions/12266899/onmeasure-custom-view-explanation

        //todo: optimize... wtf you have written
        int count = getChildCount();

//        int count = getChildCount();

        if (prevChildBottom == 0) {
//            prevChildBottom = 0;
//            int prevChildBottomReversed = 0;
            childrenStrokes.clear();
//            for (int i = 0; i < count; i++) {
            for (int i = count - 1; i >= 0; i--) {
                final View child = getChildAt(i);
//                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                if (child instanceof ArcScrollView) {
                    ArcScrollView currArcScrollView = ((ArcScrollView) child);
                    currArcScrollView.setPrevChildBottom(prevChildBottom);
                    childrenStrokes.add(prevChildBottom);
                    prevChildBottom += currArcScrollView.getStrokeWidth();
                    totalHeight += currArcScrollView.getStrokeWidth();
//                Log.e(getClass().getSimpleName(), "prevChildBottom: " + prevChildBottom);

                } else {
                    throw new RuntimeException("VerticalArcContainer can only contain ArcScrollView");
                }
            }
        }

//        for (int i = count - 1; i >= 0; i--) {
//            final View child = getChildAt(i);
////                measureChild(child, widthMeasureSpec, heightMeasureSpec);
//            if (child instanceof ArcScrollView) {
//                ArcScrollView currArcScrollView = ((ArcScrollView) child);
//                currArcScrollView.setPrevChildBottom(childrenStrokes.get(count - 1 - i));
//                Log.e(getClass().getSimpleName(), "i: " + i + " childrenStrokes.get(i): " + childrenStrokes.get(count - 1 - i));
//            }
//        }

//        Log.e(getClass().getSimpleName(), "onMeasure widthMeasureSpec: " + widthMeasureSpec + " heightMeasureSpec: " + heightMeasureSpec);


        int desiredHeight = prevChildBottom;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
//            Log.e(getClass().getSimpleName(), "width EXACTLY: " + width);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(widthMeasureSpec, widthSize);
//            Log.e(getClass().getSimpleName(), "width AT_MOST: " + width);
        } else {
            //Be whatever you want
            width = widthMeasureSpec;
//            Log.e(getClass().getSimpleName(), "width else: " + width);
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
//            Log.e(getClass().getSimpleName(), "height EXACTLY: " + height);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
//            height = Math.min(desiredHeight, heightSize);
            height = Math.min(desiredHeight, heightSize);
//            Log.e(getClass().getSimpleName(), "height AT_MOST: " + height);
        } else {
            //Be whatever you want
            height = desiredHeight;
//            Log.e(getClass().getSimpleName(), "height else: " + height);
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
//        Log.e(getClass().getSimpleName(), "onMeasure height is: " + height + " width: " + width + " prevChildBottom: " + prevChildBottom + " widthMeasureSpec: " + widthMeasureSpec + " heightMeasureSpec: " + heightMeasureSpec);

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
//            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            measureChild(child, View.MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));

        }

//        invalidate();
//        requestLayout();

    }
}
