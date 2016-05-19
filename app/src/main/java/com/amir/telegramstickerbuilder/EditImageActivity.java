package com.amir.telegramstickerbuilder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.ScrollView;
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

import java.io.File;
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
    private static final String MAIN_FONT_DIALOG_FRAGMENT_TAG = "MAIN_FONT_DIALOG_FRAGMENT_TAG";
    TouchImageView selectedLayer;
    int layerCount;
    TouchImageView[] label;

    View scrollHider;
    Button sizeButton;
    Button tiltButton;
    Button shadowDx;
    Button shadowDy;
    Button textButton;
    Button fontButton;
    Button strokeColorButton;
    Button strokeWidthButton;
    Button textColorButton;
    Button shadowColorButton;
    Button textAlphaButton;
    Button textBackgroundColor;
    ImageButton moveUpButton;
    ImageButton moveDownButton;
    ImageButton moveLeftButton;
    ImageButton moveRightButton;
    SeekBarCompat sizeSeekBar;
    SeekBarCompat tiltSeekBar;
    SeekBarCompat shadowRadius;
    SeekBarCompat shadowDxSeekBar;
    SeekBarCompat shadowDySeekBar;
    SeekBarCompat strokeWidthSeekBar;

    FrameLayout textLayerContainer;
    RelativeLayout stickerContainer;
    Bitmap mainBitmap;

    List<TouchImageView> items;
    Position offsetPosition;

    //todo: removeThumb the useless layer count variable

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        items = new ArrayList<>();

        mainBitmap = getBitmapFromExtra();
        if (mainBitmap == null) {
            Log.e(getClass().getSimpleName(), "mainBitmap was null");
            finish();
            return;
        }

        setUpView();

        layerCount = 0;
        selectedLayer = null;

        if (!BaseActivity.isPaid) {
            label = new TouchImageView[2];
            addLabel();
        }

//        Log.e(getClass().getSimpleName(), String.valueOf(mainBitmap.getAllocationByteCount()));
//        Log.e(getClass().getSimpleName(), "byteCount:" + String.valueOf(mainBitmap.getByteCount()));
//        mainBitmap.setDensity(100);
//        Log.e(getClass().getSimpleName(), "byteCount:" + String.valueOf(mainBitmap.getByteCount()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_edit_image_add_new_note) {
            getNewTextDialog(true);
            return true;
        } else if (itemId == R.id.menu_edit_image_save) {
            if (Loader.checkPermission(this))
                instantiateSavingDialog();
            else {
                Toast.makeText(this, getResources().getString(R.string.need_permission_to_save_the_sticker), Toast.LENGTH_LONG).show();
                Loader.gainPermission(this, Loader.EDIT_ACTIVITY_GAIN_PERMISSION);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        if (requestCode == Loader.EDIT_ACTIVITY_GAIN_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                instantiateSavingDialog();
                Log.e(getClass().getSimpleName(), "called");
            } else {
                Toast.makeText(this, getResources().getString(R.string.need_permission_to_save_the_sticker), Toast.LENGTH_LONG).show();
            }
        }
    }


    private void instantiateSavingDialog() {
        final View newTextDialogView = getLayoutInflater().inflate(R.layout.dialog_finish_editing, null);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    setLayerUnselected();
                    mainBitmap = mainBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas canvas = new Canvas(mainBitmap);

                    for (TouchImageView imageItem : items) {
                        canvas.drawBitmap(imageItem.getFinishedBitmap(), 0, 0, null);
                    }
                    if (label != null) {
                        canvas.drawBitmap(label[0].getFinishedBitmap(), 0, 0, null);
                        canvas.drawBitmap(label[1].getFinishedBitmap(), 0, 0, null);
                    }
                    //TODO: check whether there is enough space left on the device
                    Loader.saveBitmapToCache(mainBitmap); // the SavingStickerActivity uses the cached image for the saving process
                    finish();
                    startActivity(new Intent(EditImageActivity.this, SavingStickerActivity.class));
                }
                if (which == Dialog.BUTTON_NEGATIVE) {
                }
            }
        };

        AlertDialog finishedEditing = new AlertDialog.Builder(this)
                .setView(newTextDialogView)
                .setPositiveButton(getString(R.string.yes), listener)
                .setNegativeButton(getString(R.string.no), listener)
                .create();

        finishedEditing.show();
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (selectedLayer != null) {
            if (itemId == R.id.include_buttons_size_button) {
                setVisibleSeekBar(selectedLayer.getTextSize(), sizeSeekBar);
            } else if (itemId == R.id.include_buttons_text_button) {
                getNewTextDialog(false);
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
                mainFontDialogFragment.show(getSupportFragmentManager(), MAIN_FONT_DIALOG_FRAGMENT_TAG);
            } else if (itemId == R.id.include_buttons_text_color) {
                Loader.setColor(this, selectedLayer, Loader.TEXT_COLOR);
            } else if (itemId == R.id.include_buttons_shadow_color) {
                if (selectedLayer.isFirstTapOnShadowColor()) {
                    selectedLayer.getTextItem().getShadow().setRadius(5);
                    selectedLayer.getTextItem().getShadow().setDx(5);
                    selectedLayer.getTextItem().getShadow().setDy(5);
                }
                selectedLayer.setFirstTapOnShadowColor(false);
                Loader.setColor(this, selectedLayer, Loader.TEXT_SHADOW_COLOR);
            } else if (itemId == R.id.include_buttons_shadow_radius) {
                setVisibleSeekBar(selectedLayer.getTextItem().getShadow().getRadius(), shadowRadius);
            } else if (itemId == R.id.include_buttons_shadow_dx) {
                setVisibleSeekBar(selectedLayer.getTextItem().getShadow().getDx(), shadowDxSeekBar);
            } else if (itemId == R.id.include_buttons_shadow_dy) {
                setVisibleSeekBar(selectedLayer.getTextItem().getShadow().getDy(), shadowDySeekBar);
            } else if (itemId == R.id.include_buttons_text_background) {
                Loader.setColor(this, selectedLayer, Loader.TEXT_BACKGROUND_COLOR);
            } else if (itemId == R.id.include_buttons_text_stroke_color) {
                if (selectedLayer.isFirstTapOnStrokeColor())
                    selectedLayer.getTextItem().setStrokeWidth(5);
                selectedLayer.setFirstTapOnStrokeColor(false);
                Loader.setColor(this, selectedLayer, Loader.TEXT_STROKE_COLOR);
            } else if (itemId == R.id.include_buttons_text_stroke_width) {
                setVisibleSeekBar((int) selectedLayer.getTextItem().getStrokeWidth(), strokeWidthSeekBar);
            }
        } else
            Toast.makeText(this, getString(R.string.select_a_text), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                Position pos = new Position(event.getY(), event.getX());
                if (label[0].isItMe(pos) != null || label[1].isItMe(pos) != null)
                    Toast.makeText(this, getString(R.string.upgrade_to_pro_to_delete_this_lable), Toast.LENGTH_LONG).show();
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
                selectedLayer.getTextItem().getShadow().setDy(progress);
                selectedLayer.updateTextView();
            } else if (seekBar == shadowDxSeekBar) {
                selectedLayer.getTextItem().getShadow().setDx(progress);
                selectedLayer.updateTextView();
            } else if (seekBar == strokeWidthSeekBar) {
                selectedLayer.getTextItem().setStrokeWidth(progress);
                selectedLayer.updateTextView();
            }
        }
    }


    /**
     * because it is not possible to set the activity_edit_image_images_container width and height to match_parent as we would not be able
     * to drag the text properly and if we set the activity_edit_image_images_container width and height to wrap_content the image
     * on high density phones would be too small i had to set the width and height at runtime
     * <p/>
     * here i get the width and the height of the activity_edit_image_main_frame_container so i know the measurements
     * for the scaling
     * <p/>
     * based on the orientation of the image if the width of the image is more than it's height i would need to set the activity_edit_image_images_container
     * width to match_parent and it's height should be scaled and vise versa
     * <p/>
     * also i need to set some margin on the image as it won't remain in the center after new width and height were set so i center
     * the image by setting margin on it
     */

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int stickerContainerWidth = stickerContainer.getWidth();
        int stickerContainerHeight = stickerContainer.getHeight();
        int bitmapHeight = mainBitmap.getHeight();
        int bitmapWidth = mainBitmap.getWidth();
        float scale;
        scale = (float) stickerContainerWidth / bitmapWidth;
        int scaledHeight = (int) (bitmapHeight * scale);
        int scaledWidth = (int) (bitmapWidth * scale);
        while (scaledHeight > stickerContainerHeight - 7 || scaledWidth > stickerContainerWidth - 7) {
            scale -= .1;
            scaledHeight = (int) (bitmapHeight * scale);
            scaledWidth = (int) (bitmapWidth * scale);
        }
        int marginTop = stickerContainerHeight / 2 - scaledHeight / 2;
        int marginStart = stickerContainerWidth / 2 - scaledWidth / 2;
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(scaledWidth, scaledHeight);
        params.setMargins(marginStart, 0, 0, 0);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
            params.setMarginStart(marginStart);
        params.setMargins(0, marginTop, 0, 0);
        textLayerContainer.setLayoutParams(params);
    }

    @Nullable
    public Bitmap getBitmapFromExtra() {
        Uri imageUri = getIntent().getParcelableExtra(BaseActivity.EDIT_IMAGE_URI);
        int rotation = getIntent().getIntExtra(BaseActivity.NEED_ROTATION, 0);
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
            return null;
        }
        int mainWidth = imageBitmap.getWidth();
        int mainHeight = imageBitmap.getHeight();
        imageBitmap = Loader.rotateImage(imageBitmap, rotation);
        Bitmap resBitmap;
        if (mainWidth != 512 && mainHeight != 512) {
            float scale;
            if (mainHeight > mainWidth) {
                scale = 512f / mainHeight;
                resBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) (mainWidth * scale), 512, false);
            } else {
                scale = 512f / mainWidth;

                resBitmap = Bitmap.createScaledBitmap(imageBitmap, 512, (int) (mainHeight * scale), false);
            }
        } else {
            resBitmap = imageBitmap;
        }
        return resBitmap;

    }

    private void setUpView() {
        textLayerContainer = (FrameLayout) findViewById(R.id.activity_edit_image_images_container);
        if (textLayerContainer == null)
            throw new RuntimeException("Container was null add activity_edit_image_relative_layout_container to the view");

        scrollHider = findViewById(R.id.activity_edit_image_scroll_view_hider);
        textAlphaButton = (Button) findViewById(R.id.include_buttons_shadow_radius);
        textButton = (Button) findViewById(R.id.include_buttons_text_button);
        fontButton = (Button) findViewById(R.id.include_buttons_font_button);
        sizeButton = (Button) findViewById(R.id.include_buttons_size_button);
        textBackgroundColor = (Button) findViewById(R.id.include_buttons_text_background);
        shadowDx = (Button) findViewById(R.id.include_buttons_shadow_dx);
        shadowDy = (Button) findViewById(R.id.include_buttons_shadow_dy);
        textColorButton = (Button) findViewById(R.id.include_buttons_text_color);
        tiltButton = (Button) findViewById(R.id.include_buttons_tilt_button);
        shadowColorButton = (Button) findViewById(R.id.include_buttons_shadow_color);
        strokeWidthButton = (Button) findViewById(R.id.include_buttons_text_stroke_width);
        strokeColorButton = (Button) findViewById(R.id.include_buttons_text_stroke_color);
        moveUpButton = (ImageButton) findViewById(R.id.activity_edit_image_move_up_button);
        moveDownButton = (ImageButton) findViewById(R.id.activity_edit_image_move_down_button);
        moveLeftButton = (ImageButton) findViewById(R.id.activity_edit_image_move_left_button);
        moveRightButton = (ImageButton) findViewById(R.id.activity_edit_image_move_right_button);
        stickerContainer = (RelativeLayout) findViewById(R.id.activity_edit_image_main_frame_container);

        ImageView imageView = (ImageView) findViewById(R.id.activity_edit_image_main_image);
        RelativeLayout mainContainer = (RelativeLayout) findViewById(R.id.activity_edit_image_main_container);
        ScrollView scrollView = (ScrollView) findViewById(R.id.include_buttons_scroll_view);
        if (isTablet) {
            if (scrollView != null) {
                RelativeLayout.LayoutParams params =
                        new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.height_of_the_button_scroll_view_on_tablet));
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                scrollView.setLayoutParams(params);
            }
        }
        if (imageView != null) {
            imageView.setImageBitmap(mainBitmap);
            imageView.setOnTouchListener(this);
        }
        sizeSeekBar = Loader.getSeekBar(this,
                mainBitmap.getWidth() / 2,
                ContextCompat.getColor(this, R.color.size_seek_bar_background_color),
                ContextCompat.getColor(this, R.color.size_seek_bar_progress_color),
                ContextCompat.getColor(this, R.color.size_seek_bar_thumb_color),
                0,
                mainContainer
        );
        strokeWidthSeekBar = Loader.getSeekBar(this,
                25,
                ContextCompat.getColor(this, R.color.stroke_width_seek_bar_background_color),
                ContextCompat.getColor(this, R.color.stroke_width_seek_bar_progress_color),
                ContextCompat.getColor(this, R.color.stroke_width_seek_bar_thumb_color),
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
        if (textBackgroundColor != null) textBackgroundColor.setOnClickListener(this);
        if (fontButton != null) fontButton.setOnClickListener(this);
        if (strokeWidthButton != null) strokeWidthButton.setOnClickListener(this);
        if (strokeColorButton != null) strokeColorButton.setOnClickListener(this);
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
            sizeSeekBar.setMax(mainBitmap.getWidth() / 2);
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
            shadowDxSeekBar.setProgress(0);
            shadowDxSeekBar.setOnSeekBarChangeListener(this);
        }
        if (shadowDySeekBar != null) {
            shadowDySeekBar.setProgress(0);
            shadowDySeekBar.setOnSeekBarChangeListener(this);
        }
        if (strokeWidthSeekBar != null) {
            strokeWidthSeekBar.setProgress(55);
            strokeWidthSeekBar.setOnSeekBarChangeListener(this);
        }
        deactivateButtons(true);

    }

    private void getNewTextDialog(final boolean asNewText) {
        final View newTextDialogView = getLayoutInflater().inflate(R.layout.dialog_set_new_text, null);
        final EditText editText = (EditText) newTextDialogView.findViewById(R.id.dialog_set_new_text_text);
        if (!asNewText) {
            editText.setText(selectedLayer.getTextItem().getText());
            editText.setSelection(selectedLayer.getTextItem().getText().length());
            editText.setTypeface(selectedLayer.getTextItem().getFont().getTypeface());
//            editText.setTextColor(selectedLayer.getTextItem().getTextColor());
        } else {
//            editText.setTextColor(TextItem.DEFAULT_TEXT_COLOR);
            editText.setTypeface(Typeface.MONOSPACE);
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
//                    Log.e(getClass().getSimpleName(),editText.getText().toString() );
                    if (asNewText && !editText.getText().toString().equals("")) {
                        TouchImageView touchItem =
                                new TouchImageView(EditImageActivity.this,
                                        new TextItem("", EditImageActivity.this, mainBitmap),
                                        ++layerCount,
                                        mainBitmap);
                        setSelectedLayer(touchItem);
                        items.add(touchItem);
                        textLayerContainer.addView(touchItem);
                    }
                    if (selectedLayer != null) {
                        selectedLayer.updateText(editText.getText().toString());
                        if (editText.getText().toString().equals("")) {
                            items.remove(items.indexOf(selectedLayer));
                            setLayerUnselected();
                        }
                    }
                }
            }
        };

        AlertDialog newTextDialog = new AlertDialog.Builder(this)
                .setView(newTextDialogView)
                .setTitle(getString(R.string.new_text))
                .setPositiveButton(getString(R.string.done), listener)
                .setNegativeButton(getString(R.string.cancel), listener)
                .setCancelable(false)
                .create();

        newTextDialog.show();
    }

    private void setLayerUnselected() {
//        if (items.size() == 1)
//            return;
        if (selectedLayer != null) {
            selectedLayer.setAsSelected(false);
            sizeSeekBar.setVisibility(View.GONE);
            tiltSeekBar.setVisibility(View.GONE);
            shadowRadius.setVisibility(View.GONE);
            shadowDxSeekBar.setVisibility(View.GONE);
            shadowDySeekBar.setVisibility(View.GONE);
            strokeWidthSeekBar.setVisibility(View.GONE);

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
                textAlphaButton != null &&
                textBackgroundColor != null &&
                strokeWidthButton != null &&
                strokeColorButton != null &&
                scrollHider != null) {
            textAlphaButton.setEnabled(!deactivate);
            textColorButton.setEnabled(!deactivate);
            sizeButton.setEnabled(!deactivate);
            shadowDy.setEnabled(!deactivate);
            shadowDx.setEnabled(!deactivate);
            tiltButton.setEnabled(!deactivate);
            textButton.setEnabled(!deactivate);
            fontButton.setEnabled(!deactivate);
            shadowColorButton.setEnabled(!deactivate);
            textBackgroundColor.setEnabled(!deactivate);
            strokeColorButton.setEnabled(!deactivate);
            strokeWidthButton.setEnabled(!deactivate);
            if (deactivate) {
                scrollHider.setVisibility(View.VISIBLE);
                moveUpButton.setVisibility(View.GONE);
                moveDownButton.setVisibility(View.GONE);
                moveLeftButton.setVisibility(View.GONE);
                moveRightButton.setVisibility(View.GONE);
            } else {
                scrollHider.setVisibility(View.GONE);
                moveUpButton.setVisibility(View.VISIBLE);
                moveDownButton.setVisibility(View.VISIBLE);
                moveLeftButton.setVisibility(View.VISIBLE);
                moveRightButton.setVisibility(View.VISIBLE);
            }

        }

    }

    private void setVisibleSeekBar(int progress, SeekBarCompat seekBar) {
        sizeSeekBar.setVisibility(View.GONE);
        shadowRadius.setVisibility(View.GONE);
        tiltSeekBar.setVisibility(View.GONE);
        shadowDxSeekBar.setVisibility(View.GONE);
        shadowDySeekBar.setVisibility(View.GONE);
        strokeWidthSeekBar.setVisibility(View.GONE);

        seekBar.setProgress(progress);
        seekBar.setVisibility(View.VISIBLE);
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

    //    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    public void onBackPressed() {
        if (mBackPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.press_back_button_again), Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }


    private void addLabel() {
        label[0] = new TouchImageView(this, new TextItem(getString(R.string.stickergram), this, mainBitmap), 0, mainBitmap);
        TextItem textItem = label[0].getTextItem();
        textItem.setBackgroundColor(ContextCompat.getColor(this, R.color.stickergram_label_background));
        textItem.setFont(new FontItem("stickergram Font", Typeface.SANS_SERIF));
        textItem.setStrokeWidth(10);
        textItem.setSize(32);
        textItem.setTextColor(ContextCompat.getColor(this, R.color.stickergram_label_color));
        textItem.setTextStrokeColor(ContextCompat.getColor(this, R.color.stickergram_label_stroke_color));
        Bitmap bitmap = textItem.getTextBitmap();
        int stickergramHeight = bitmap.getHeight();
        int stickergramWidth = bitmap.getWidth();
//        textItem.setPosition(new Position(mainBitmap.getHeight() - stickergramHeight, mainBitmap.getWidth() - stickergramWidth));

        int top = mainBitmap.getHeight() - stickergramHeight + 7;
        textItem.setPosition(new Position(top, 0));
        label[0].setTextItem(textItem);
        textLayerContainer.addView(label[0]);
        label[1] = new TouchImageView(this, new TextItem(getString(R.string.made), this, mainBitmap), 0, mainBitmap);
        textItem.setSize(18);
        textItem.setText(getString(R.string.made));
        textItem.setStrokeWidth(3);
        textItem.setBackgroundColor(0);
        bitmap = textItem.getTextBitmap();
//        textItem.setPosition(new Position(0,0));
        textItem.setPosition(new Position(top - bitmap.getHeight() / 2.3f,
                stickergramWidth / 2 - bitmap.getWidth() / 2));
        label[1].setTextItem(textItem);
        textLayerContainer.addView(label[1]);

    }

}