package com.amir.telegramstickerbuilder.infrastructure;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TextProcess {
    public static final String TAG = "TextProcess";
    private Bitmap regionalBitmap;
    private TextItem textItem;
    public float scale;


    public TextProcess(TextItem textItem, Bitmap regionalBitmap, Context context) {
        this.textItem = textItem;
        this.regionalBitmap = regionalBitmap;
        scale = context.getResources().getDisplayMetrics().density;
    }

    public void setTextItem(TextItem textItem) {
        this.textItem = textItem;
    }

    public TextItem getTextItem() {
        return textItem;
    }

//    public Bitmap getTextBitmap() {
//        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//        textPaint.setShadowLayer(textItem.getShadow().getRadius(), textItem.getShadow().getdX(), textItem.getShadow().getdY(), textItem.getShadow().getColor());
//        textPaint.setTextSize(textItem.getSize());
//        textPaint.setAlpha(textItem.getAlpha());
//        textPaint.setTypeface(textItem.getFont());
//        textPaint.setColor(textItem.getTextColor());
//        textPaint.setStyle(textItem.getTextStyle());
//
//        Rect bound = new Rect();
//        textPaint.getTextBounds(textItem.getText(), 0, textItem.getText().length(), bound);
//        float textWidth = textPaint.measureText(textItem.getText(), 0, textItem.getText().length());
//
//        StaticLayout staticLayout = new StaticLayout(
//                textItem.getText(),
//                textPaint,
//                (int) textWidth,
//                textItem.getAlignment(),
//                1.0f,
//                1.0f,
//                true);
//        Bitmap bitmap = Bitmap.createBitmap((int) textWidth, (int) (bound.scaledHeight() * scale) * 2, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setStyle(Paint.Style.FILL);
//
//        paint.setColor(textItem.getBackgroundColor());
//        canvas.drawPaint(paint);
//        canvas.save();
//        canvas.translate(0, 0);
//
//        staticLayout.draw(canvas);
//
//        canvas.restore();
//        //todo: note setting locale on the textPaint might come handy to support persian
//
//        //todo: on the imageView responsible for this bitmap set: rotation
//
//        return bitmap;
//    }

//    public Bitmap getFullTextBitmap() {
//        Bitmap fullTextBitmap = Bitmap.createBitmap(regionalBitmap.getWidth(), regionalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(fullTextBitmap);
//        canvas.drawBitmap(getTextBitmap(), textItem.getPosition().getLeft(), textItem.getPosition().getTop(), null);
//        return fullTextBitmap;
//    }

    //
//    public Bitmap getCombinedBitmap() { //todo: write this somewhere else
//        regionalBitmap = regionalBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        Canvas canvas = new Canvas(regionalBitmap);
//        canvas.drawBitmap(getFullTextBitmap(), 0, 0, null);
//        return regionalBitmap;
//    }

//    public Bitmap updatePosition(Position position) {
//        textItem.setPosition(position);
//        return getFullTextBitmap();
//    }

    public static void setPosition(TextView textView) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 0;
        params.topMargin = 0;
        textView.setLayoutParams(params);
    }

    public static Bitmap getScaledImageBitmap(Bitmap imageBitmap, int width, int height) {
//        final int[] imageViewWidth = new int[1];
//        final int[] imageViewHeight = new int[1];
//        int finalImageViewWidth, finalImageViewHeight;
//        ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
//        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
//                imageViewHeight[0] = imageView.getMeasuredHeight();
//                imageViewWidth[0] = imageView.getMeasuredWidth();
                Log.e(TAG, "imageViewHeight: " + width + " imageViewWidth: " + height);
//                return true;
//            }
//        });
        return imageBitmap;
    }


//    public static Bitmap bitmapOverBitmap(Bitmap mainBitmap, Bitmap textBitmap, Position position, Context context) {
//        float scale = context.getResources().getDisplayMetrics().density;
//        mainBitmap = mainBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        Canvas canvas = new Canvas(mainBitmap);
//        canvas.drawBitmap(textBitmap, position.getTop() * scale, position.getLeft() * scale, new Paint());
//        return mainBitmap;
//    }

//    public static Bitmap drawText(String text, int textWidth, int textSize) {
//        // Get text dimensions
//        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//        textPaint.setStyle(Paint.Style.FILL);
//        textPaint.setColor(Color.BLACK);
//        textPaint.setTextSize(textSize);
//        StaticLayout mTextLayout = new StaticLayout(text, textPaint,
//                textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
//
//        // Create bitmap and canvas to draw to
//        Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Bitmap.Config.RGB_565);
//        Canvas c = new Canvas(b);
//
//        // Draw background
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
//                | Paint.LINEAR_TEXT_FLAG);
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.WHITE);
//        c.drawPaint(paint);
//
//        // Draw text
//        c.save();
//        c.translate(0, 0);
//        mTextLayout.draw(c);
//        c.restore();
//
//        return b;
//    }
//
//    public static Bitmap getFullBitmap(Bitmap textBitmap, Bitmap regionalBitmap, Position position) {
//
//        Bitmap bitmap = Bitmap.createBitmap(regionalBitmap.getWidth(), regionalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas emptyCanvas = new Canvas(bitmap);
//        emptyCanvas.drawBitmap(textBitmap, position.getTop(), position.getLeft(), null);
//
//        regionalBitmap = regionalBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        Canvas canvas = new Canvas(regionalBitmap);
//
////        Canvas canvas = new Canvas(bitmap);
////        canvas.drawBitmap(regionalBitmap, 0, 0, null);
////        canvas.save();
////        canvas.drawBitmap(textBitmap, position.getTop(), position.getLeft(), null);
//        canvas.drawBitmap(bitmap, 0, 0, null);
//        return regionalBitmap;
////        return bitmap;
//    }

//    private static int getRelativeLeft(View myView) {

//        if (myView.getParent() == myView.getRootView())
//            return myView.getLeft();
//        else
//            return myView.getLeft() + getRelativeTop((View) myView.getParent());
    //    }
//    private static int getRelativeTop(View myView) {
//
//        if (myView.getParent() == myView.getRootView())
//            return myView.getTop();
//        else
//            return myView.getTop() + getRelativeLeft((View) myView.getParent());
//    }
//    public static void applyAttribute(TextView textView, TextItem item, Context context) {
//        textView.setText(item.getText());
//
//        Log.e("textProcess", "left: " + item.getPosition().getLeft() + " top: " + item.getPosition().getTop());
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.leftMargin = item.getPosition().getTop(); // in pixel
//        params.topMargin = item.getPosition().getLeft();
//        textView.setLayoutParams(params);
//
////        textView.setTop(item.getPosition().getLeft());
////        textView.setLeft(item.getPosition().getTop());
//
//        textView.setTextColor(item.getTextColor());
//
//        textView.setBackgroundColor(item.getBackgroundColor());
//
//        textView.setTypeface(item.getFont());
//
////        textView.setGravity(item.getGravity());
//
//        textView.setTextSize(item.getSize());
//
//        textView.setRotation(item.getRotation());
//
//        textView.setAlpha(item.getAlpha());
//
//        textView.setShadowLayer(item.getShadow().getRadius(), item.getShadow().getdX(), item.getShadow().getdY(), item.getShadow().getColor());
//
////        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
////            textView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
////        }
//
//    }


//    public static Bitmap getBitmapTextPaint(TextItem item, Context context) {
//        float scale = context.getResources().getDisplayMetrics().density;
//
//        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//        textPaint.setShadowLayer(item.getShadow().getRadius(), item.getShadow().getdX(), item.getShadow().getdY(), item.getShadow().getColor());
//        textPaint.setTextSize(item.getSize());
//        textPaint.setAlpha(item.getAlpha());
//        textPaint.setTypeface(item.getFont());
//        textPaint.setColor(item.getTextColor());
//        textPaint.setStyle(item.getTextStyle());
//
//        Rect bound = new Rect();
//        textPaint.getTextBounds(item.getText(), 0, item.getText().length(), bound);
//        float textWidth = textPaint.measureText(item.getText(), 0, item.getText().length());
//
//        StaticLayout staticLayout = new StaticLayout(
//                item.getText(),
//                textPaint,
//                (int) textWidth,
//                item.getAlignment(),
//                1.0f,
//                1.0f,
//                true);
//
//        Bitmap bitmap = Bitmap.createBitmap((int) textWidth, (int) (bound.scaledHeight() * scale), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setStyle(Paint.Style.FILL);
//
//        paint.setColor(item.getBackgroundColor());
//        canvas.drawPaint(paint);
//        canvas.save();
//        canvas.translate(0, 0);
//
//        staticLayout.draw(canvas);
//
//        canvas.restore();
//        //todo: note setting locale on the textPaint might come handy to support persian
//
//        //todo: on the imageView responsible for this bitmap set: rotation
//
//        return bitmap;
//    }

//    public static Bitmap drawOverImage(TextItem item, Bitmap bitmap, RelativeLayout relativeLayout, TextView textView, Context context) {
//        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//
//        float scale = context.getResources().getDisplayMetrics().density;
//
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setTextSize(item.getSize() * scale);
////        paint.setTextSize(20 * scale);
//        paint.setColor(item.getTextColor());
////        paint.setColor(Color.WHITE);
////        paint.setColor(Color.rgb(61, 61, 61));
//        paint.setShadowLayer(item.getShadow().getRadius(), item.getShadow().getdX(), item.getShadow().getdY(), item.getShadow().getColor());
//        Log.e(TAG, "radius: " + item.getShadow().getRadius() + " dx: " + item.getShadow().getdX() + " dy: " + item.getShadow().getdY() + " color: " + item.getShadow().getColor());
////        paint.setShadowLayer(2, 5, 5, Color.parseColor("#c9be35f9"));
//        Log.e(TAG, "color: " + Color.parseColor("#c9be35f9"));
//        paint.setAlpha(item.getAlpha());
////        paint.setAlpha(1);
//        paint.setTypeface(item.getFont());
////        paint.setTypeface(Typeface.MONOSPACE);
//
////        float x = item.getPosition().getLeft();
////        float y = item.getPosition().getTop();
//        float heightScale = (float) bitmap.getHeight() / relativeLayout.getHeight();
//        float widthScale = (float) bitmap.getWidth() / relativeLayout.getWidth();
////        float scale2 = Math.min(relativeLayout.getWidth()/ textView.getWidth(),
////                relativeLayout.getHeight()/ textView.getLineHeight());
////        float heightScale = textView.getLineHeight() * scale2;
////        float widthScale = textView.getWidth() * scale2;
////        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
//
////        } else Log.e(TAG, "FUCK");
//
//
////
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
//
////        Rect bounds = new Rect();
////        paint.getTextBounds(item.getText(), 0, item.getText().length(), bounds);
////        float x = ((bitmap.getWidth() - bounds.width())) - item.getPosition().getLeft() * scale;
////        float y = ((bitmap.getHeight() - bounds.scaledHeight())) - item.getPosition().getTop() * scale;
//        float x = (item.getPosition().getLeft()) * heightScale;
//        float y = (item.getPosition().getTop()) * widthScale;
////        float x = textView.getLeft() * scale;
////        float y = textView.getTop() * scale;
////        float x = (item.getPosition().getLeft()) * scale;
////        float y = (item.getPosition().getTop()) * scale;
//
//        Log.e(TAG, "textView.getLeft(): " + textView.getTop() + " textView.getTop(): " + textView.getLeft());
//        Log.e(TAG, "relativeLayout.getWidth(): " + relativeLayout.getWidth() + " relativeLayout.getHeight(): " + relativeLayout.getHeight());
//        Log.e(TAG, "widthScale: " + widthScale + " heightScale: " + heightScale + " scale: " + scale);
//        Log.e(TAG, "bitmap.getWidth(): " + bitmap.getWidth() + " bitmap.getHeight(): " + bitmap.getHeight());
////        Log.e("TextProcess", "left: " + x * heightScale * scale + " top: " + y * widthScale * scale);
////        canvas.drawText(item.getText(), x * heightScale * scale, y * widthScale * scale, paint);
////        Log.e("TextProcess", "TextView left position: " + getRelativeTop(textView) + "TextView top position: " + getRelativeLeft(textView));
//        Log.e(TAG, "x: " + x + " y: " + y);
//
//        Canvas canvas = new Canvas(bitmap);
//        canvas.drawText(item.getText(), x, y, paint);
//        return bitmap;
//    }
//
//    /**
//     * This method converts dp unit to equivalent pixels, depending on device density.
//     *
//     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
//     * @param context Context to get resources and device specific display metrics
//     * @return A float value to represent px equivalent to dp depending on device density
//     */
//    public static float convertDpToPixel(float dp, Context context) {
//        Resources resources = context.getResources();
//        DisplayMetrics metrics = resources.getDisplayMetrics();
//        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
//        return px;
//    }
//
//    /**
//     * This method converts device specific pixels to density independent pixels.
//     *
//     * @param px      A value in px (pixels) unit. Which we need to convert into db
//     * @param context Context to get resources and device specific display metrics
//     * @return A float value to represent dp equivalent to px value
//     */
//    public static float convertPixelsToDp(float px, Context context) {
//        Resources resources = context.getResources();
//        DisplayMetrics metrics = resources.getDisplayMetrics();
//        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
//        return dp;
//    }
}
