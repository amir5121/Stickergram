package com.amir.stickergram.backgroundRmover;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;

public class PointerViewBottom extends ImageView {
    private int height;
    private int width;

    public PointerViewBottom(Context context) {
        super(context);
        setLayoutParams();
        setVisibility(GONE);
        setImageResource(R.drawable.circle_drawable);
    }

    private void setLayoutParams() {
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams((int) (20 * BaseActivity.density), (int) (20 * BaseActivity.density));
        setLayoutParams(params);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width = w;
        this.height = h;

    }

    void updatePointer(float top, float left) {
        setVisibility(VISIBLE);
        setY(top - height / 2);
        setX(left - width / 2);
    }
}