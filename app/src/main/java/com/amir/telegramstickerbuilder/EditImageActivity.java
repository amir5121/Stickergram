package com.amir.telegramstickerbuilder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.fonts.EnglishFontsFragment;
import com.amir.telegramstickerbuilder.fonts.MainFontDialogFragment;
import com.amir.telegramstickerbuilder.infrastructure.FontItem;
import com.amir.telegramstickerbuilder.infrastructure.Loader;
import com.amir.telegramstickerbuilder.infrastructure.Position;
import com.amir.telegramstickerbuilder.infrastructure.TextItem;
import com.amir.telegramstickerbuilder.infrastructure.TouchImageView;
import com.amir.telegramstickerbuilder.navdrawer.MainNavDrawer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.minimize.com.seek_bar_compat.SeekBarCompat;

public class EditImageActivity
        extends BaseActivity
        implements
        View.OnTouchListener,
        View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        EnglishFontsFragment.OnFontItemClicked {
    private static final String MAIN_FONT_DIALOG_FRAGMENT = "MAIN_FONT_DIALOG_FRAGMENT";
    TouchImageView selectedLayer;
    int layerCount;

    Button sizeButton;
    Button tiltButton;
    Button shadowDx;
    Button shadowDy;
    Button textButton;
    Button fontButton;
    Button textColorButton;
    Button shadowColorButton;
    Button textAlphaButton;
    ImageButton moveUpButton;
    ImageButton moveDownButton;
    ImageButton moveLeftButton;
    ImageButton moveRightButton;
    SeekBarCompat sizeSeekBar;
    SeekBarCompat tiltSeekBar;
    SeekBarCompat shadowRadius;
    SeekBarCompat shadowDxSeekBar;
    SeekBarCompat shadowDySeekBar;

    public FrameLayout container;

    Bitmap mainBitmap;
    List<TouchImageView> items;

    Position offsetPosition;

    //todo: don't let the text to move outside of the image view container
    //todo: remove the useless layer count variable

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image3);
        setNavDrawer(new MainNavDrawer(this));
        items = new ArrayList<>();

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Example");

        mainBitmap = getBitmapFromExtra();
        if (mainBitmap == null) {
            Log.e(getClass().getSimpleName(), "mainBitmap was null");
            finish();
        }

        setUpView();

        container = (FrameLayout) findViewById(R.id.activity_edit_image_images_container);
        if (container == null)
            throw new RuntimeException("Container was null add activity_edit_image_relative_layout_container to the view");
//        else {
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT);
//            if (mainBitmap.getHeight() > mainBitmap.getWidth()) {
//                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
//                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//                params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//                imageContainer.setLayoutParams(params);
//                Log.e(getClass().getSimpleName(), "i got called");
//            } else {
//                params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
//                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                imageContainer.setLayoutParams(params);
//            }
//        }
        setupMainImageView();

        layerCount = 0;
        selectedLayer = null;

        //todo: get layerCount and selectedLayer from the savedInstanceState and pass it to touchImageView
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_edit_image_add_new_note) {
            TouchImageView touchItem = new TouchImageView(this, new TextItem("TSB" + layerCount, this, mainBitmap), ++layerCount, mainBitmap);
            setSelectedLayer(touchItem);
            items.add(touchItem);
            container.addView(touchItem);
            return true;
        } else if (itemId == R.id.menu_edit_image_save) {
            setLayerUnselected();
            mainBitmap = mainBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mainBitmap);

            for (TouchImageView imageItem : items) {
                canvas.drawBitmap(imageItem.getFinishedBitmap(), 0, 0, null);
            }
            Loader.saveBitmap(mainBitmap);
            //todo: finish off the activity
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (selectedLayer != null) {
            if (itemId == R.id.include_buttons_size_button) {
                setVisibleSeekBar(selectedLayer.getTextSize(), sizeSeekBar);
            } else if (itemId == R.id.include_buttons_text_button) {
                getNewTextDialog();
            } else if (itemId == R.id.include_buttons_tilt_button) {
                setVisibleSeekBar(selectedLayer.getTextItem().getTilt(), tiltSeekBar);
            } else if (itemId == R.id.activity_edit_image_move_up_button) {
                moveUp();
            } else if (itemId == R.id.activity_edit_image_move_down_button) {
                moveDown();
            } else if (itemId == R.id.activity_edit_image_move_left_button) {
                moveLeft();
            } else if (itemId == R.id.activity_edit_image_move_right_button) {
                moveRight();
            } else if (itemId == R.id.include_buttons_font_button) {
                MainFontDialogFragment mainFontDialogFragment = new MainFontDialogFragment();
                mainFontDialogFragment.show(getSupportFragmentManager(), MAIN_FONT_DIALOG_FRAGMENT);
            } else if (itemId == R.id.include_buttons_text_color) {
                Loader.setColor(this, selectedLayer, Loader.TEXT_COLOR);
            } else if (itemId == R.id.include_buttons_shadow_color) {
                Loader.setColor(this, selectedLayer, Loader.TEXT_SHADOW_COLOR);
            } else if (itemId == R.id.include_buttons_shadow_radius) {
                setVisibleSeekBar(selectedLayer.getTextItem().getShadow().getRadius(), shadowRadius);
            } else if (itemId == R.id.include_buttons_shadow_dx) {
                setVisibleSeekBar(selectedLayer.getTextItem().getShadow().getDx() + 50, shadowDxSeekBar);
            } else if (itemId == R.id.include_buttons_shadow_dy) {
                setVisibleSeekBar(selectedLayer.getTextItem().getShadow().getDy() + 50, shadowDySeekBar);
            }
        } else Toast.makeText(this, getString(R.string.select_a_text), Toast.LENGTH_LONG).show();
    }

    private void setVisibleSeekBar(int progress, SeekBarCompat seekBar) {
        sizeSeekBar.setVisibility(View.GONE);
        shadowRadius.setVisibility(View.GONE);
        tiltSeekBar.setVisibility(View.GONE);
        shadowDxSeekBar.setVisibility(View.GONE);
        shadowDySeekBar.setVisibility(View.GONE);

        seekBar.setProgress(progress);
        seekBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
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
                if (selectedLayer != null) {
                    selectedLayer.updateTextPosition(new Position(event.getY(), event.getX()), offsetPosition);
                }
                break;
            }
        }
        return true;
    }

    private void setLayerUnselected() {
        if (selectedLayer != null) {
            selectedLayer.setAsSelected(false);
            sizeSeekBar.setVisibility(View.GONE);
            tiltSeekBar.setVisibility(View.GONE);
            shadowRadius.setVisibility(View.GONE);
            shadowDxSeekBar.setVisibility(View.GONE);
            shadowDySeekBar.setVisibility(View.GONE);

            selectedLayer = null;
            deactivateButtons(true);
        }
    }

    public void setSelectedLayer(TouchImageView item) {
        if (item != null) {
            setLayerUnselected();
            selectedLayer = item;
            item.setAsSelected(true);
            deactivateButtons(false);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (selectedLayer != null) {
            if (progress > 5 && seekBar == sizeSeekBar)
                selectedLayer.setTextSize(progress);
            else if (seekBar == tiltSeekBar)
                selectedLayer.setTextTilt(progress);
            else if (seekBar == shadowRadius && progress > 0) {
                selectedLayer.getTextItem().getShadow().setRadius(progress);
                selectedLayer.updateTextView();
            } else if (seekBar == shadowDySeekBar) {
                selectedLayer.getTextItem().getShadow().setdY(progress - 50);
                selectedLayer.updateTextView();
            } else if (seekBar == shadowDxSeekBar) {
                Log.e(getClass().getSimpleName(), "dx: " + progress);
                selectedLayer.getTextItem().getShadow().setdX(progress - 50);
                selectedLayer.updateTextView();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_image, menu);
        return super.onCreateOptionsMenu(menu);
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

    private void setUpView() {

        textAlphaButton = (Button) findViewById(R.id.include_buttons_shadow_radius);
        textButton = (Button) findViewById(R.id.include_buttons_text_button);
        fontButton = (Button) findViewById(R.id.include_buttons_font_button);
        sizeButton = (Button) findViewById(R.id.include_buttons_size_button);
        shadowDx = (Button) findViewById(R.id.include_buttons_shadow_dx);
        shadowDy = (Button) findViewById(R.id.include_buttons_shadow_dy);
        textColorButton = (Button) findViewById(R.id.include_buttons_text_color);
        tiltButton = (Button) findViewById(R.id.include_buttons_tilt_button);
        shadowColorButton = (Button) findViewById(R.id.include_buttons_shadow_color);
        moveUpButton = (ImageButton) findViewById(R.id.activity_edit_image_move_up_button);
        moveDownButton = (ImageButton) findViewById(R.id.activity_edit_image_move_down_button);
        moveLeftButton = (ImageButton) findViewById(R.id.activity_edit_image_move_left_button);
        moveRightButton = (ImageButton) findViewById(R.id.activity_edit_image_move_right_button);
        RelativeLayout mainContainer = (RelativeLayout) findViewById(R.id.activity_edit_image_main_container);
//        sizeSeekBar = (SeekBarCompat) findViewById(R.id.activity_edit_image_size_seek_bar);
        sizeSeekBar = Loader.getSeekBar(this,
                mainBitmap.getWidth(),
                ContextCompat.getColor(this, R.color.size_seek_bar_background_color),
                ContextCompat.getColor(this, R.color.size_seek_bar_progress_color),
                ContextCompat.getColor(this, R.color.size_seek_bar_thumb_color),
                0,
                mainContainer
        );
        shadowDxSeekBar = Loader.getSeekBar(this,
                100,
                ContextCompat.getColor(this, R.color.shadow_dx_seek_bar_background_color),
                ContextCompat.getColor(this, R.color.shadow_dx_seek_bar_progress_color),
                ContextCompat.getColor(this, R.color.shadow_dx_seek_bar_thumb_color),
                0,
                mainContainer
        );

        shadowDySeekBar = Loader.getSeekBar(this,
                100,
                ContextCompat.getColor(this, R.color.shadow_dy_seek_bar_background_color),
                ContextCompat.getColor(this, R.color.shadow_dy_seek_bar_progress_color),
                ContextCompat.getColor(this, R.color.shadow_dy_seek_bar_thumb_color),
                0,
                mainContainer
        );
//        tiltSeekBar = (SeekBarCompat) findViewById(R.id.activity_edit_image_tilt_seek_bar);
        tiltSeekBar = Loader.getSeekBar(this,
                360,
                ContextCompat.getColor(this, R.color.tilt_seek_bar_background_color),
                ContextCompat.getColor(this, R.color.tilt_seek_bar_progress_color),
                ContextCompat.getColor(this, R.color.tilt_seek_bar_thumb_color),
                0,
                mainContainer
        );

        shadowRadius = Loader.getSeekBar(this,
                20,
                ContextCompat.getColor(this, R.color.text_alpha_seek_bar_background_color),
                ContextCompat.getColor(this, R.color.text_alpha_tilt_seek_bar_progress_color),
                ContextCompat.getColor(this, R.color.text_alpha_seek_bar_thumb_color),
                0,
                mainContainer
        );

        if (textColorButton != null) textColorButton.setOnClickListener(this);
        if (sizeButton != null) sizeButton.setOnClickListener(this);
        if (textAlphaButton != null) textAlphaButton.setOnClickListener(this);
        if (tiltButton != null) tiltButton.setOnClickListener(this);
        if (textButton != null) textButton.setOnClickListener(this);
        if (fontButton != null) fontButton.setOnClickListener(this);
        if (shadowDx != null) shadowDx.setOnClickListener(this);
        if (shadowDy != null) shadowDy.setOnClickListener(this);
        if (shadowColorButton != null) shadowColorButton.setOnClickListener(this);
        if (moveUpButton != null) {
            moveUpButton.setOnClickListener(this);
//            moveUpButton.setOnLongClickListener(this);
        }
        if (moveDownButton != null) {
            moveDownButton.setOnClickListener(this);
//            moveDownButton.setOnLongClickListener(this);
        }
        if (moveLeftButton != null) {
            moveLeftButton.setOnClickListener(this);
//            moveLeftButton.setOnLongClickListener(this);
        }
        if (moveRightButton != null) {
            moveRightButton.setOnClickListener(this);
//            moveRightButton.setOnLongClickListener(this);
        }
        if (sizeSeekBar != null) {
            sizeSeekBar.setOnSeekBarChangeListener(this);
            sizeSeekBar.setMax(mainBitmap.getWidth());
        }
        if (tiltSeekBar != null) {
            tiltSeekBar.setProgress(180);
            tiltSeekBar.setOnSeekBarChangeListener(this);
        }
        if (shadowRadius != null) {
            shadowRadius.setProgress(2);
            shadowRadius.setOnSeekBarChangeListener(this);
        }
        if (shadowDxSeekBar != null) {
            shadowDxSeekBar.setProgress(55);
            shadowDxSeekBar.setOnSeekBarChangeListener(this);
        }
        if (shadowDySeekBar != null) {
            shadowDySeekBar.setProgress(55);
            shadowDySeekBar.setOnSeekBarChangeListener(this);
        }
        deactivateButtons(true);

    }

    private void setupMainImageView() {
        ImageView imageView = new ImageView(this);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
//        imageView.setBackgroundColor(Color.parseColor("#ff2299"));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);
        imageView.setImageBitmap(mainBitmap);
        container.addView(imageView);
        imageView.setOnTouchListener(this);
    }

    private void getNewTextDialog() {
        final View newTextDialogView = getLayoutInflater().inflate(R.layout.dialog_set_new_text, null);
        final EditText editText = (EditText) newTextDialogView.findViewById(R.id.dialog_set_new_text_text);
        editText.setText(selectedLayer.getTextItem().getText());
        editText.setSelection(selectedLayer.getTextItem().getText().length());
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    selectedLayer.updateText(editText.getText().toString());
                }
            }
        };
        AlertDialog newTextDialog = new AlertDialog.Builder(this)
                .setView(newTextDialogView)
                .setTitle(getString(R.string.new_text))
                .setPositiveButton(getString(R.string.done), listener)
                .setNegativeButton(getString(R.string.cancel), listener)
                .setCancelable(true)
                .create();

        newTextDialog.show();
    }


    private void deactivateButtons(boolean deactivate) {
        if (textColorButton != null &&
                sizeButton != null &&
                tiltButton != null &&
                shadowDy != null &&
                shadowDx != null &&
                textButton != null &&
                fontButton != null &&
                moveUpButton != null &&
                moveDownButton != null &&
                moveLeftButton != null &&
                moveRightButton != null &&
                shadowColorButton != null &&
                textAlphaButton != null) {
            textAlphaButton.setEnabled(!deactivate);
            textColorButton.setEnabled(!deactivate);
            sizeButton.setEnabled(!deactivate);
            shadowDy.setEnabled(!deactivate);
            shadowDx.setEnabled(!deactivate);
            tiltButton.setEnabled(!deactivate);
            textButton.setEnabled(!deactivate);
            fontButton.setEnabled(!deactivate);
            shadowColorButton.setEnabled(!deactivate);
            if (deactivate) {
                moveUpButton.setVisibility(View.GONE);
                moveDownButton.setVisibility(View.GONE);
                moveLeftButton.setVisibility(View.GONE);
                moveRightButton.setVisibility(View.GONE);
            } else {
                moveUpButton.setVisibility(View.VISIBLE);
                moveDownButton.setVisibility(View.VISIBLE);
                moveLeftButton.setVisibility(View.VISIBLE);
                moveRightButton.setVisibility(View.VISIBLE);
            }

        }

    }
//
//    @Override
//    public boolean onLongClick(View view) {
//        int itemId = view.getId();
//        if (itemId == R.id.activity_edit_image_move_up_button) {
//            moveUpWhilePressed();
//            return true;
//        } else if (itemId == R.id.activity_edit_image_move_down_button) {
//            return true;
//        } else if (itemId == R.id.activity_edit_image_move_right_button) {
//            return true;
//        } else if (itemId == R.id.activity_edit_image_move_left_button) {
//            return true;
//        }
//        return false;
//    }
//
//    private void moveUpWhilePressed() {
//        boolean isPressed = true;
//        while (isPressed) {
//            isPressed = moveUpButton.isPressed();
//            Log.e(getClass().getSimpleName(), "move up was called");
//            moveUp();
//
//        }
//    }

    private void moveRight() {
        selectedLayer.getTextItem().moveRight();
        selectedLayer.updateTextView();
    }

    private void moveLeft() {
        selectedLayer.getTextItem().moveLeft();
        selectedLayer.updateTextView();
    }

    private void moveDown() {
        selectedLayer.getTextItem().moveDown();
        selectedLayer.updateTextView();
    }

    private void moveUp() {
        selectedLayer.getTextItem().moveUp();
        selectedLayer.updateTextView();
    }

    @Override
    public void onFontItemSelected(FontItem item) {
        Log.e(getClass().getSimpleName(), "Called");
        if (selectedLayer != null) {
            selectedLayer.getTextItem().setFont(item);
            selectedLayer.updateTextView();
        }
    }
}
