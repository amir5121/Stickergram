package com.amir.telegramstickerbuilder.infrastructure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.amir.telegramstickerbuilder.EditImageActivity;

@SuppressLint("ViewConstructor")
public class TouchImageView extends ImageView {
    private final int layerId;
    private TextItem textItem;
    private boolean isFirstTapOnStrokeColor;
    private boolean isFirstTapOnShadowColor;
    EditImageActivity activity;
    int scaledWidth;
    int scaledHeight;
    float widthScale;
    float heightScale;

    Bitmap latestTextLayer;
    Bitmap textLayer;
    Bitmap mainBitmap;

    public TouchImageView(Context context, TextItem textItem, int layerId, Bitmap mainBitmap) {
        super(context);
        activity = (EditImageActivity) context;
        this.textItem = textItem;
        this.layerId = layerId;
        setLayoutParams();
        this.mainBitmap = mainBitmap;
        this.textLayer = Bitmap.createBitmap(mainBitmap.getWidth(), mainBitmap.getHeight(), mainBitmap.getConfig());
//        setImageBitmap(textItem.getFullTextBitmap());
        setImageBitmap(textItem.getFullTextBitmap2(textLayer));
        isFirstTapOnStrokeColor = true;
        isFirstTapOnShadowColor = true;

    }

    public TextItem getTextItem() {
        return textItem;
    }

    public void setFirstTapOnShadowColor(boolean firstTapOnShadowColor) {
        isFirstTapOnShadowColor = firstTapOnShadowColor;
    }

    public void setFirstTapOnStrokeColor(boolean firstTapOnStrokeColor) {
        isFirstTapOnStrokeColor = firstTapOnStrokeColor;
    }

    public boolean isFirstTapOnShadowColor() {
        return isFirstTapOnShadowColor;
    }

    public boolean isFirstTapOnStrokeColor() {
        return isFirstTapOnStrokeColor;
    }

    public void setLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
//        setBackgroundColor(Color.parseColor("#55ff6622"));
        setScaleType(ScaleType.FIT_CENTER);
        setAdjustViewBounds(true);
        setClickable(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        widthScale = (float) textItem.hostWidth / getMeasuredWidth();
        heightScale = (float) textItem.hostHeight / getMeasuredHeight();
    }

    public int getLayerId() {
        return layerId;
    }

    public int[] getScaledMearsument() {
        int[] measurement = new int[2];
        measurement[0] = scaledWidth;
        measurement[1] = scaledHeight;
        return measurement;
    }

    public void updateTextPosition(Position position, Position offsetPosition) {
        if (offsetPosition != null) {
            Position actualPosition = new Position(
                    position.getTop() * heightScale - offsetPosition.getTop(),
                    position.getLeft() * widthScale - offsetPosition.getLeft());
            textItem.setPosition(actualPosition);
            setImageBitmap(textItem.getFullTextBitmap2(textLayer));
//        setImageBitmap(textItem.getFullTextBitmap());
        } else {
            Position actualPosition = new Position(
                    position.getTop() * heightScale,
                    position.getLeft() * widthScale);
            textItem.setPosition(actualPosition);
            setImageBitmap(textItem.getFullTextBitmap2(textLayer));

        }
    }

    public void updateTextView() {
        setImageBitmap(textItem.getFullTextBitmap2(textLayer));
//        setImageBitmap(textItem.getFullTextBitmap());
    }

    public Position isItMe(Position position) {
        TextArea area = textItem.getArea();
//
//        float actualTilt = Math.abs(tilt - 180);
//        actualTilt = (actualTilt >= 90 && actualTilt != 180) ? 90 - actualTilt % 90 : actualTilt;
//
//        if (actualTilt == 180) actualTilt = 0;
//        float v = actualTilt / 90;
//
//
        float areaStartTop = area.getStartPosition().getTop();
        float areaEndTop = area.getStartPosition().getTop() + area.getHeight();// * (1/tilt)  ;
        float areaStartLeft = area.getStartPosition().getLeft();
        float areaEndLeft = area.getStartPosition().getLeft() + area.getWidth();

//        float midOfTopLine = (areaStartTop + areaEndTop) / 2;

        float textCenterTop = (areaStartTop + areaEndTop) / 2;
        float textCenterLeft = (areaStartLeft + areaEndLeft) / 2;

        float clickedTop = position.getTop() * heightScale;
        float clickedLeft = position.getLeft() * widthScale;

//        Log.e(getClass().getSimpleName(), "-tilt: " + (-(tilt - 180)));

        int tilt = getTextItem().getTilt();

//        Log.e(getClass().getSimpleName(), "-----clickedTop: " + (clickedTop) + " clickedLeft: " + (clickedLeft));
        Matrix matrix = new Matrix();
//        Log.e(getClass().getSimpleName(), "tilt: " + (-(tilt - 180)));

        // Initialize the array with our Coordinate
        float[] clickPoints = new float[2];
//        float[] startPoints = new float[2];
//
        clickPoints[0] = clickedLeft;
        clickPoints[1] = clickedTop;

//        startPoints[0] = areaStartTop;
//        startPoints[1] = areaStartLeft;

//        textItem.getRotatedPoint(new Point((int) clickedLeft, (int) clickedTop));
        matrix.setRotate(-(tilt - 180), textCenterLeft, textCenterTop);//rotate by the center of the text in the image
        matrix.mapPoints(clickPoints);
//        matrix.mapPoints(startPoints);

        clickedLeft = clickPoints[0];
        clickedTop = clickPoints[1];

//        areaStartTop = startPoints[0];
//        areaStartLeft = startPoints[1];

//        float[] values = new float[9];
//        matrix.getValues(values);
//        clickedTop = values[2] + areaStartTop;
//        clickedLeft = values[5] + areaStartLeft;
//        Log.e(getClass().getSimpleName(), "values[3]")
//        Log.e(getClass().getSimpleName(), "clickedTop: " + (clickedTop) + " clickedLeft: " + (clickedLeft));
//        float tempClickTop = clickedTop;
//        if (clickedTop > midOfTopLine && actualTilt < 90) tempClickTop -= area.getWidth() * v;
//        else tempClickTop += area.getWidth() * v;
//
//        if (clickedTop > midOfTopLine && actualTilt > 90) tempClickTop += area.getWidth() * v;
//        else tempClickTop -= area.getWidth() * v;

        if (clickedTop < areaEndTop && clickedTop > areaStartTop && clickedLeft < areaEndLeft && clickedLeft > areaStartLeft) {
//            Matrix matrix1 = new Matrix();
//            float[] startPoints = new float[2];
//
//            clickPoints[0] = areaStartTop;
//            clickPoints[1] = areaStartLeft;
//
//            matrix.mapPoints(startPoints);
//            areaStartTop = startPoints[0];
//            areaStartLeft = startPoints[1];
//            matrix.setRotate(-(tilt - 180), textItem.getTextWidth() / 2, textItem.getTextHeight() / 2);
//
//            areaStartTop = clickPoints[0];
//            areaStartLeft = clickPoints[1];
//
            return new Position(clickedTop - areaStartTop, clickedLeft - areaStartLeft);
        }
        return null;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        latestTextLayer = bm;
//        Bitmap temp = mainBitmap.copy(mainBitmap.getConfig(), true);
        Bitmap temp = Bitmap.createBitmap(mainBitmap.getWidth(), mainBitmap.getHeight(), mainBitmap.getConfig());
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(bm, 0, 0, null);
//        return textItem.getFullTextBitmap2(textLayer);
        canvas.save();
        canvas.restore();
        super.setImageBitmap(temp);

    }

    public Bitmap getFinishedBitmap() {
        textItem.setSelected(false);
//        return textItem.getFullTextBitmap();
//        mainBitmap = mainBitmap.copy(mainBitmap.getConfig(), true);
        Bitmap temp = Bitmap.createBitmap(mainBitmap.getWidth(), mainBitmap.getHeight(), mainBitmap.getConfig());
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(latestTextLayer, 0, 0, null);
//        return textItem.getFullTextBitmap2(textLayer);
        canvas.save();
        canvas.restore();
        return temp;
    }

    public void setAsSelected(boolean asSelected) {
        if (asSelected) {
            textItem.setSelected(true);
        } else {
            textItem.setSelected(false);
            updateTextView();
        }
    }

    public void setTextSize(int progress) {
        textItem.setSize(progress);
//        Log.e(getClass().getSimpleName(), "setTextSize was called");
        updateTextView();
    }

    public int getTextSize() {
        return textItem.getSize();
    }

    public void updateText(String newText) {
        textItem.setText(newText);
        updateTextView();
    }

    public void setTextTilt(int textTilt) {
        textItem.setTilt(textTilt);
        updateTextView();
    }

    public void setTextItem(TextItem textItem) {
        this.textItem = textItem;
        updateTextView();
    }


//
//    @Override
//    public void onClick(View view) {
//        int itemId = view.getId();
//        if (itemId == R.id.activity_edit_image_size_button) {
//            Log.e(getClass().getSimpleName(), "size button was hit");
//            SeekBarCompat seekBarCompat = new SeekBarCompat(context);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//            seekBarCompat.setLayoutParams(params);
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                seekBarCompat.setProgressBackgroundColor(context.getResources().getColor(R.color.size_seek_bar_background_color, null));
//                seekBarCompat.setProgressColor(context.getResources().getColor(R.color.size_seek_bar_progress_color, null));
//                seekBarCompat.setThumbColor(context.getResources().getColor(R.color.size_seek_bar_thumb_color, null));
//            } else {
//                seekBarCompat.setProgressBackgroundColor(context.getResources().getColor(R.color.size_seek_bar_background_color));
//                seekBarCompat.setProgressColor(context.getResources().getColor(R.color.size_seek_bar_progress_color));
//                seekBarCompat.setThumbColor(context.getResources().getColor(R.color.size_seek_bar_thumb_color));
//            }
////            seekBarCompat.prog
//        }
//    }


    //    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        Log.e(getClass().getSimpleName(), "widthScale: " + widthScale + " heightScale: " + heightScale);
//        switch (action & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN: {
////                listener.whoHasBeenTapped(new Position((int) (event.getX() * widthScale), (int) (event.getY() * heightScale)));
////                textButton.setText(String.valueOf((int) event.getX()) + "yeay");
////                sizeButton.setText(String.valueOf((int) event.getY()) + "yeay");
//                break;
//            }
//
//            case MotionEvent.ACTION_MOVE: {
////                finishedBitmap = textItem.getFullTextBitmap(new Position((int) (event.getX() * widthScale), (int) (event.getY() * heightScale)));
////                setImageBitmap(finishedBitmap);
////                setImageBitmap(textProcess.updatePosition(new Position((int) event.getX(), (int) event.getY())));
////                textButton.setText(String.valueOf((int) event.getX()));
////                sizeButton.setText(String.valueOf((int) event.getY()));
//
//                break;
//            }
//        }
//        return true;
//    }
//


//    public interface OnTextClickListener {
//        void whoHasBeenTapped(Position position);
//    }

//    public void saveProgress() {
//        textLayer = textProcess.getCombinedBitmap();
//        OutputStream outputStream = null;
//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/amir.png");
//        try {
//            if (file.exists()) {
//                file.delete();
//                file.createNewFile();
//            } else file.createNewFile();
//            outputStream = new FileOutputStream(file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (outputStream == null)
//            Log.e(getClass().getSimpleName(), "outPutStream was null");
//        else
////                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//            textLayer.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//
//    }
}
