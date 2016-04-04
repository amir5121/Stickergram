package com.amir.telegramstickerbuilder.infrastructure;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amir.telegramstickerbuilder.EditImageActivity;

public class TouchImageView extends ImageView {
    private final int layerId;

    EditImageActivity activity;
    int scaledWidth;
    int scaledHeight;
    float widthScale;
    float heightScale;
    private TextItem textItem;

    public TouchImageView(Context context, TextItem textItem, int layerId) {
        super(context);
        activity = (EditImageActivity) context;
        this.textItem = textItem;
        this.layerId = layerId;
        setLayoutParams();
        setImageBitmap(textItem.getFullTextBitmap());

    }

    public TextItem getTextItem() {
        return textItem;
    }


    public void setLayoutParams() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
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
        Position actualPosition = new Position(position.getTop() * heightScale - offsetPosition.getTop(), position.getLeft() * widthScale - offsetPosition.getLeft());
        textItem.setPosition(actualPosition);
        setImageBitmap(textItem.getFullTextBitmap());

    }

    public void updateText() {
        setImageBitmap(textItem.getFullTextBitmap());
    }

    public Position isItMe(Position position) {
        TextArea area = textItem.getArea();

        float areaStartTop = area.getStartPosition().getTop();
        float areaEndTop = area.getStartPosition().getTop() + area.getHeight();
        float areaStartLeft = area.getStartPosition().getLeft();
        float areaEndLeft = area.getStartPosition().getLeft() + area.getWidth();

        float clickedTop = position.getTop() * heightScale;
        float clickedLeft = position.getLeft() * widthScale;

        if (clickedTop < areaEndTop && clickedTop > areaStartTop && clickedLeft < areaEndLeft && clickedLeft > areaStartLeft) {
            return new Position(clickedTop - areaStartTop, clickedLeft - areaStartLeft);
        }
        return null;
    }


    public Bitmap getFinishedBitmap() {
        textItem.setSelected(false);
        return textItem.getFullTextBitmap();
    }

    public void setAsSelected(boolean asSelected) {
        if (asSelected) {
            textItem.setSelected(true);
        } else {
            textItem.setSelected(false);
            updateText();
        }
    }

    public void setTextSize(int progress) {
        textItem.setSize(progress);
        Log.e(getClass().getSimpleName(), "setTextSize was called");
        updateText();
    }

    public int getTextSize() {
        return textItem.getSize();
    }
//
//    @Override
//    public void onClick(View view) {
//        int itemId = view.getId();
//        if (itemId == R.id.activity_edit_image_size_button) {
//            Log.e(getClass().getSimpleName(), "size button was hit");
//            SeekBarCompat seekBarCompat = new SeekBarCompat(activity);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//            seekBarCompat.setLayoutParams(params);
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                seekBarCompat.setProgressBackgroundColor(activity.getResources().getColor(R.color.size_seek_bar_background_color, null));
//                seekBarCompat.setProgressColor(activity.getResources().getColor(R.color.size_seek_bar_progress_color, null));
//                seekBarCompat.setThumbColor(activity.getResources().getColor(R.color.size_seek_bar_thumb_color, null));
//            } else {
//                seekBarCompat.setProgressBackgroundColor(activity.getResources().getColor(R.color.size_seek_bar_background_color));
//                seekBarCompat.setProgressColor(activity.getResources().getColor(R.color.size_seek_bar_progress_color));
//                seekBarCompat.setThumbColor(activity.getResources().getColor(R.color.size_seek_bar_thumb_color));
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
//                //todo: check out onDraw
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
//        mainBitmap = textProcess.getCombinedBitmap();
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
//            mainBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//
//    }
}
