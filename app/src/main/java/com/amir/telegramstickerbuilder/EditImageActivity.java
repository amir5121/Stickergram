package com.amir.telegramstickerbuilder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.infrastructure.Loader;
import com.amir.telegramstickerbuilder.infrastructure.Position;
import com.amir.telegramstickerbuilder.infrastructure.TextItem;
import com.amir.telegramstickerbuilder.infrastructure.TouchImageView;
import com.amir.telegramstickerbuilder.navdrawer.MainNavDrawer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.minimize.com.seek_bar_compat.SeekBarCompat;

public class EditImageActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    TouchImageView selectedLayer;
    int layerCount;

    public Button moveButton;
    public Button sizeButton;
    public Button tiltButton;
    public Button textButton;
    public Button fontButton;
    public Button styleButton;
    SeekBarCompat sizeSeekBar;

    public RelativeLayout container;

    Bitmap mainBitmap;
    List<TouchImageView> items;

    Position offsetPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        setNavDrawer(new MainNavDrawer(this));
        items = new ArrayList<>();

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Example");

        mainBitmap = getBitmapFromExtra();
        if (mainBitmap == null) {
            Log.e(getClass().getSimpleName(), "mainBitmap was null");
            finish();
        }

        textButton = (Button) findViewById(R.id.activity_edit_image_text_button);
        fontButton = (Button) findViewById(R.id.activity_edit_image_font_button);
        moveButton = (Button) findViewById(R.id.activity_edit_image_move_button);
        sizeButton = (Button) findViewById(R.id.activity_edit_image_size_button);
        styleButton = (Button) findViewById(R.id.activity_edit_image_style_button);
        tiltButton = (Button) findViewById(R.id.activity_edit_image_tilt_button);
        sizeSeekBar = (SeekBarCompat) findViewById(R.id.activity_edit_image_size_seek_bar);
        setUpListener();

        container = (RelativeLayout) findViewById(R.id.activity_edit_image_images_container);
        if (container == null)
            throw new RuntimeException("Container was null add activity_edit_image_relative_layout_container to the view");

        setupImageView();

        layerCount = 0;
        selectedLayer = null;

        //todo: get layerCount and selectedLayer from the savedInstanceState and pass it to touchImageView
    }

    private void setUpListener() {
        if (styleButton != null) styleButton.setOnClickListener(this);
        if (moveButton != null) moveButton.setOnClickListener(this);
        if (sizeButton != null) sizeButton.setOnClickListener(this);
        if (tiltButton != null) tiltButton.setOnClickListener(this);
        if (textButton != null) textButton.setOnClickListener(this);
        if (fontButton != null) fontButton.setOnClickListener(this);
        if (sizeSeekBar != null) {
            sizeSeekBar.setOnSeekBarChangeListener(this);
            sizeSeekBar.setMax(mainBitmap.getWidth());
        }

    }

    private void setupImageView() {
        ImageView imageView = new ImageView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(params);
        imageView.setBackgroundColor(Color.parseColor("#ff2299"));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);
        imageView.setImageBitmap(mainBitmap);
        container.addView(imageView);
        imageView.setOnTouchListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_edit_image_add_new_note) {
            TouchImageView touchItem = new TouchImageView(this, new TextItem("TSB" + layerCount, this, mainBitmap), ++layerCount);
            setSelectedLayer(touchItem);
            items.add(touchItem);
            container.addView(touchItem);
//            Log.e(getClass().getSimpleName(), "layerCount: " + (layerCount - 1));
//            selectedLayer = items.get(layerCount - 1);
//            touchImageView.addNewText(savedInstanceState);
//            TextView textView = new TextView(this);
//            textView.setTextIsSelectable(true);
//            TextViewDraw.setPosition(textView, textItem.getPosition());
//            container.addView(textView);
//            bitmap = Bitmap.createBitmap(TextViewDraw.drawOverImage(textItem, imageBitmap, container, textView, touchImageView, this));

//            touchImageView.setImageBitmap(textProcess.getCombinedBitmap());
        } else if (itemId == R.id.menu_edit_image_save) {
            setLayerUnselected();
            mainBitmap = mainBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mainBitmap);

            for (TouchImageView imageItem : items) {
                canvas.drawBitmap(imageItem.getFinishedBitmap(), 0, 0, null);
            }

            Loader.saveBitmap(mainBitmap);
            //todo: finish off the activity
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_image, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                textButton.setText("Left: " + String.valueOf((int) event.getX()));
                sizeButton.setText("Top: " + String.valueOf((int) event.getY()));
                fontButton.setText("action Down");
                for (int i = items.size() - 1; i >= 0; i--) {
                    offsetPosition = items.get(i).isItMe(new Position(event.getY(), event.getX()));
                    if (offsetPosition != null) {
                        setSelectedLayer(items.get(i));
                        break;
                    } else {
                        setLayerUnselected();
                    }
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                textButton.setText("Left: " + String.valueOf(event.getX()));
                sizeButton.setText("Top: " + String.valueOf(event.getY()));
                fontButton.setText("action move");

                if (selectedLayer != null) {
                    selectedLayer.updateTextPosition(new Position(event.getY(), event.getX()), offsetPosition);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {//todo: set the text item unselect on action up
                textButton.setText(String.valueOf(event.getX()));
                sizeButton.setText(String.valueOf(event.getY()));
                fontButton.setText("action up");
                break;
            }


        }
        return true;
    }

    private void setLayerUnselected() {
        if (selectedLayer != null) {
            selectedLayer.setAsSelected(false);
            if (sizeSeekBar != null) sizeSeekBar.setVisibility(View.GONE);
            selectedLayer = null;
        }
    }

    public void setSelectedLayer(TouchImageView item) {
        if (item != null) {
            setLayerUnselected();
            selectedLayer = item;
            item.setAsSelected(true);
        }
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.activity_edit_image_size_button) {
            if (sizeSeekBar != null) {
                sizeSeekBar.setProgress(selectedLayer.getTextSize());
                sizeSeekBar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (selectedLayer != null && progress > 5)
            selectedLayer.setTextSize(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Nullable
    public Bitmap getBitmapFromExtra() {
        Uri imageUri = getIntent().getParcelableExtra(BaseActivity.EDIT_IMAGE_URI);
        Bitmap imageBitmap = null;
        try {
            if (imageUri == null) {
                String dirInAsset = getIntent().getStringExtra(BaseActivity.EDIT_IMAGE_DIR_IN_ASSET);
                imageBitmap = BitmapFactory.decodeStream(getAssets().open(dirInAsset));
            } else {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imageBitmap == null) {
            Log.e(getClass().getSimpleName(), "imageBitmap was null");
            finish();
        }
        return imageBitmap;
    }
}
