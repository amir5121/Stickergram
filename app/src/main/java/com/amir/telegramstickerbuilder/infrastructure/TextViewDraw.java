package com.amir.telegramstickerbuilder.infrastructure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TextViewDraw {
    public static String TAG = "TextViewDraw";

    public static Bitmap drawOverImage(TextItem item, Bitmap bitmap, RelativeLayout relativeLayout, TextView textView, ImageView imageView, Context context) {
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();

        if (bitmapHeight > bitmapWidth){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            int imageViewHeight = imageView.getHeight();
            float heightScale = (float) bitmapHeight / imageViewHeight;
            float imageScaledWidth = bitmapWidth * heightScale;
            params.width = (int) imageScaledWidth;
            params.height  = imageViewHeight;
            imageView.setLayoutParams(params);
        }

        float scale = context.getResources().getDisplayMetrics().density;

        Paint paint = new Paint();

        paint.setTextSize(item.getSize() * scale);
        textView.setTextSize(item.getSize());

        paint.setColor(item.getTextColor());
        textView.setTextColor(item.getTextColor());

        paint.setShadowLayer(item.getShadow().getRadius(), item.getShadow().getDx(), item.getShadow().getDy(), item.getShadow().getColor());
        textView.setShadowLayer(item.getShadow().getRadius(), item.getShadow().getDx(), item.getShadow().getDy(), item.getShadow().getColor());

//        paint.setAlpha(item.getAlpha());
//        textView.setAlpha(item.getAlpha());

        paint.setTypeface(item.getFont().getTypeface());
        textView.setTypeface(item.getFont().getTypeface());

        float heightScale = (float) bitmap.getHeight() / relativeLayout.getHeight();
        float widthScale = (float) bitmap.getWidth() / relativeLayout.getWidth();
//
//        float heightScale = (float) relativeLayout.getHeight() / bitmap.getHeight();
//        float widthScale = (float) relativeLayout.getWidth() / bitmap.getWidth();

//        Rect bounds = new Rect();
//        paint.getTextBounds(item.getText(), 0, item.getText().length(), bounds);
//        float x = ((bitmap.getWidth() - bounds.width())) - item.getPosition().getLeft() * scale;
//        float y = ((bitmap.getHeight() - bounds.scaledHeight())) - item.getPosition().getTop() * scale;

//        float x = (item.getPosition().getLeft()) * heightScale + textView.getMinWidth();
//        float y = (item.getPosition().getTop()) * widthScale + textView.getLineHeight();

//        float x = textView.getLeft() * scale;
//        float y = textView.getTop() * scale;
//        float x = (item.getPosition().getLeft()) * scale;
//        float y = (item.getPosition().getTop()) * scale;

//        Log.e(TAG, "textView.getLeft(): " + textView.getTop() + " textView.getTop(): " + textView.getLeft());
        Log.e(TAG, "relativeLayout.getWidth(): " + relativeLayout.getWidth() + " relativeLayout.getHeight(): " + relativeLayout.getHeight());
        Log.e(TAG, "widthScale: " + widthScale + " heightScale: " + heightScale + " scale: " + scale);
        Log.e(TAG, "bitmap.getWidth(): " + bitmap.getWidth() + " bitmap.getHeight(): " + bitmap.getHeight());
//        Log.e(TAG, "x: " + x + " y: " + y);
//
//        textView.setText(item.getText());
//        Canvas canvas = new Canvas(bitmap);
//        canvas.drawText(item.getText(), x, y, paint);
//        canvas.save();

        return bitmap;
    }

//    public static void setPosition(TextView textView, Position position) {
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.leftMargin = position.getLeft();
//        params.topMargin = position.getTop();
//        textView.setLayoutParams(params);
    }
//}
