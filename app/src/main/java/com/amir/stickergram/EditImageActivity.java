package com.amir.stickergram;

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
import android.widget.TextView;
import android.widget.Toast;

import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.fonts.EnglishFontsFragment;
import com.amir.stickergram.fonts.MainFontDialogFragment;
import com.amir.stickergram.infrastructure.FontItem;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.infrastructure.Position;
import com.amir.stickergram.infrastructure.TextItem;
import com.amir.stickergram.infrastructure.TouchImageView;

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

    View buttonsDeactivateLayer;
    View proNoteCloseButton;
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
    Button textShadowRadius;
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
    private View buyNoteContainer;
    private TextView buyNoteText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        items = new ArrayList<>();

        mainBitmap = getBitmapFromExtra();
        if (mainBitmap == null) {
            Log.e(getClass().getSimpleName(), "mainBitmap was null");
            Toast.makeText(this, getString(R.string.there_was_a_problem_getting_the_picture), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setUpView();

        layerCount = 0;
        selectedLayer = null;

        if (!isPaid) {
            label = new TouchImageView[2];
            addLabel();
        }

        getNewTextDialog(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_edit_image_add_new_text) {
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

    private void instantiateSavingDialog() {
        final View newTextDialogView = getLayoutInflater().inflate(R.layout.dialog_finish_editing, null);
        ImageView finishedImage = (ImageView) newTextDialogView.findViewById(R.id.dialog_finish_editing_image);
        setLayerUnselected();
        final Bitmap tempBitmap = mainBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        mainBitmap = mainBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        Canvas canvas = new Canvas(mainBitmap);
        Canvas canvas = new Canvas(tempBitmap);

        for (TouchImageView imageItem : items) {
            canvas.drawBitmap(imageItem.getFinishedBitmap(), 0, 0, null);
        }
        if (label != null) {
            canvas.drawBitmap(label[0].getFinishedBitmap(), 0, 0, null);
            canvas.drawBitmap(label[1].getFinishedBitmap(), 0, 0, null);
        }

        finishedImage.setImageBitmap(tempBitmap);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {

                    if (Loader.freeMemory() > 2) {
                        Loader.saveBitmapToCache(tempBitmap); // the SavingStickerActivity uses the cached image for the saving process
                        finish();
                        startActivity(new Intent(EditImageActivity.this, SavingStickerActivity.class));
                    } else
                        Toast.makeText(EditImageActivity.this, getString(R.string.failed_to_save_the_sticker), Toast.LENGTH_LONG).show();
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

        if (itemId == R.id.include_pro_note_close) {
            if (buyNoteContainer != null) buyNoteContainer.setVisibility(View.GONE);
        } else if (itemId == R.id.include_pro_note_text)
            requestProVersion();
        else if (itemId == R.id.activity_edit_image_buttons_overlay_layer)
            Toast.makeText(this, getString(R.string.select_a_text), Toast.LENGTH_LONG).show();
        else if (selectedLayer != null) {
            if (itemId == R.id.include_buttons_size_button) {
                setVisibleSeekBar(selectedLayer.getTextSize(), sizeSeekBar);
            } else if (itemId == R.id.include_buttons_text_button) {
                getNewTextDialog(false);
            } else if (itemId == R.id.include_buttons_tilt_button) {
                if (!isPaid) buyProNote(getString(R.string.tilt_effect));
                setVisibleSeekBar(selectedLayer.getTextItem().getTilt(), tiltSeekBar);
            } else if (itemId == R.id.activity_edit_image_move_up_button) {
                selectedLayer.getTextItem().moveUp();
                selectedLayer.updateTextView();
            } else if (itemId == R.id.activity_edit_image_move_down_button) {
                selectedLayer.getTextItem().moveDown();
                selectedLayer.updateTextView();
            } else if (itemId == R.id.activity_edit_image_move_left_button) {
                selectedLayer.getTextItem().moveLeft();
                selectedLayer.updateTextView();
            } else if (itemId == R.id.activity_edit_image_move_right_button) {
                selectedLayer.getTextItem().moveRight();
                selectedLayer.updateTextView();
            } else if (itemId == R.id.include_buttons_font_button) {
                MainFontDialogFragment mainFontDialogFragment = new MainFontDialogFragment();
                mainFontDialogFragment.show(getSupportFragmentManager(), MAIN_FONT_DIALOG_FRAGMENT_TAG);
            } else if (itemId == R.id.include_buttons_text_color) {
                Loader.setColor(this, selectedLayer, Loader.TEXT_COLOR);
            } else if (itemId == R.id.include_buttons_shadow_color) {
                manageShadowsFirstTap();
                Loader.setColor(this, selectedLayer, Loader.TEXT_SHADOW_COLOR);
            } else if (itemId == R.id.include_buttons_shadow_radius) {
                setVisibleSeekBar(selectedLayer.getTextItem().getShadow().getRadius(), shadowRadius);
            } else if (itemId == R.id.include_buttons_shadow_dx) {
                if (!isPaid) buyProNote(getString(R.string.shadow_position_effect));
                manageShadowsFirstTap();
                setVisibleSeekBar(selectedLayer.getTextItem().getShadow().getDx(), shadowDxSeekBar);
            } else if (itemId == R.id.include_buttons_shadow_dy) {
                if (!isPaid) buyProNote(getString(R.string.shadow_position_effect));
                manageShadowsFirstTap();
                setVisibleSeekBar(selectedLayer.getTextItem().getShadow().getDy(), shadowDySeekBar);
            } else if (itemId == R.id.include_buttons_text_background) {
                Loader.setColor(this, selectedLayer, Loader.TEXT_BACKGROUND_COLOR);
            } else if (itemId == R.id.include_buttons_text_stroke_color) {
//                if (!isPaid)
//                    buyProNote(getString(R.string.stroke_color_is_only_available_in_blue_upgrade_to_pro_to_access_all_colors));
                if (selectedLayer.isFirstTapOnStrokeColor())
                    selectedLayer.getTextItem().setStrokeWidth(selectedLayer.getTextItem().getStrokeWidth());
                selectedLayer.setFirstTapOnStrokeColor(false);
                Loader.setColor(this, selectedLayer, Loader.TEXT_STROKE_COLOR);
            } else if (itemId == R.id.include_buttons_text_stroke_width) {
                setVisibleSeekBar((int) selectedLayer.getTextItem().getStrokeWidth(), strokeWidthSeekBar);
            }
        } else
            Toast.makeText(this, getString(R.string.select_a_text), Toast.LENGTH_LONG).show();
    }

    private void manageShadowsFirstTap() {
        if (selectedLayer.isFirstTapOnShadowColor()) {
            selectedLayer.getTextItem().getShadow().setRadius(5);
            selectedLayer.getTextItem().getShadow().setDx(5);
            selectedLayer.getTextItem().getShadow().setDy(5);
        }
        selectedLayer.setFirstTapOnShadowColor(false);
    }

    private void buyProNote(String string) {
        if (buyNoteContainer != null) buyNoteContainer.setVisibility(View.VISIBLE);
        if (buyNoteText != null) buyNoteText.setText(string);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                Position pos = new Position(event.getY(), event.getX());
                if (label != null)
                    if (label[0].isItMe(pos) != null || label[1].isItMe(pos) != null)
                        Toast.makeText(this, getString(R.string.upgrade_to_pro_to_delete_this_label), Toast.LENGTH_LONG).show();
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
            if (progress > 5 && seekBar == sizeSeekBar) {
                selectedLayer.getTextItem().setSize(progress);
            } else if (seekBar == tiltSeekBar) {
                selectedLayer.getTextItem().setTilt(progress);
            } else if (seekBar == shadowRadius && progress > 0) {
                selectedLayer.getTextItem().getShadow().setRadius(progress);
            } else if (seekBar == shadowDySeekBar) {
                selectedLayer.getTextItem().getShadow().setDy(progress);
            } else if (seekBar == shadowDxSeekBar) {
                selectedLayer.getTextItem().getShadow().setDx(progress);
            } else if (seekBar == strokeWidthSeekBar) {
                selectedLayer.getTextItem().setStrokeWidth(progress);
            }
            selectedLayer.updateTextView();
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
        //todo: should this be implemented in on setContentView????
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
//        imageBitmap = Loader.rotateImage(imageBitmap, rotation);
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

        buttonsDeactivateLayer = findViewById(R.id.activity_edit_image_buttons_overlay_layer);
        textShadowRadius = (Button) findViewById(R.id.include_buttons_shadow_radius);
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
        buyNoteContainer = findViewById(R.id.include_pro_note_container);
        buyNoteText = (TextView) findViewById(R.id.include_pro_note_text);
        proNoteCloseButton = findViewById(R.id.include_pro_note_close);

        ImageView mainImageView = (ImageView) findViewById(R.id.activity_edit_image_main_image);
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
        if (mainImageView != null) {
            mainImageView.setImageBitmap(mainBitmap);
            mainImageView.setOnTouchListener(this);
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

        if (buyNoteContainer != null && !isPaid) buyNoteContainer.setVisibility(View.VISIBLE);
        if (buttonsDeactivateLayer != null) buttonsDeactivateLayer.setOnClickListener(this);
        if (textColorButton != null) textColorButton.setOnClickListener(this);
        if (proNoteCloseButton != null) proNoteCloseButton.setOnClickListener(this);
        if (sizeButton != null) sizeButton.setOnClickListener(this);
        if (textShadowRadius != null) textShadowRadius.setOnClickListener(this);
        if (tiltButton != null) {
            if (!isPaid)
                tiltButton.setText(getString(R.string.tilt_pro));
            tiltButton.setOnClickListener(this);
        }
        if (textButton != null) textButton.setOnClickListener(this);
        if (textBackgroundColor != null) textBackgroundColor.setOnClickListener(this);
        if (fontButton != null) fontButton.setOnClickListener(this);
        if (strokeWidthButton != null) strokeWidthButton.setOnClickListener(this);
        if (strokeColorButton != null) strokeColorButton.setOnClickListener(this);
        if (shadowDx != null) {
            if (!isPaid) shadowDx.setText(getString(R.string.shadow_dx_pro));
            shadowDx.setOnClickListener(this);
        }
        if (shadowDy != null) {
            if (!isPaid) shadowDy.setText(getString(R.string.shadow_dy_pro));
            shadowDy.setOnClickListener(this);
        }
        if (shadowColorButton != null) shadowColorButton.setOnClickListener(this);
        if (buyNoteText != null) buyNoteText.setOnClickListener(this);
        if (moveUpButton != null) {
            moveUpButton.setOnClickListener(this);
        }
        if (moveDownButton != null) {
            moveDownButton.setOnClickListener(this);
        }
        if (moveLeftButton != null) {
            moveLeftButton.setOnClickListener(this);
        }
        if (moveRightButton != null) {
            moveRightButton.setOnClickListener(this);
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
        } else {
            editText.setTypeface(TextItem.DEFAULT_FONT);
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    String text = editText.getText().toString();
                    if (asNewText && !text.equals("")) {
                        TouchImageView touchItem =
                                new TouchImageView(EditImageActivity.this,
                                        new TextItem("", mainBitmap),
                                        ++layerCount,
                                        mainBitmap);
                        setSelectedLayer(touchItem);
                        items.add(touchItem);
                        textLayerContainer.addView(touchItem);
                    }
                    if (selectedLayer != null) {
                        int textLengthTemp = text.length();
                        if (textLengthTemp >= 1 && textLengthTemp <= 4 && text.charAt(textLengthTemp - 1) != ' ')
                            text += " ";
                        selectedLayer.updateText(text);
                        if (text.equals("")) {
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
        if (selectedLayer != null) {
            if (!isPaid) selectedLayer.notPaid();
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
                textShadowRadius != null &&
                textBackgroundColor != null &&
                strokeWidthButton != null &&
                strokeColorButton != null &&
                buttonsDeactivateLayer != null) {
            textShadowRadius.setEnabled(!deactivate);
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
                buttonsDeactivateLayer.setVisibility(View.VISIBLE);
                moveUpButton.setVisibility(View.GONE);
                moveDownButton.setVisibility(View.GONE);
                moveLeftButton.setVisibility(View.GONE);
                moveRightButton.setVisibility(View.GONE);
            } else {
                buttonsDeactivateLayer.setVisibility(View.GONE);
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
        label[0] = new TouchImageView(this, new TextItem(getString(R.string.stickergram), mainBitmap), 0, mainBitmap);
        TextItem textItem = label[0].getTextItem();
//        textItem.setBackgroundColor(ContextCompat.getColor(this, R.color.stickergram_label_background));
        textItem.setFont(new FontItem("stickergram Font", Typeface.SANS_SERIF));
        textItem.setStrokeWidth(10);
        textItem.setSize(32);
        textItem.setTextColor(ContextCompat.getColor(this, R.color.stickergram_label_color));
        textItem.setTextStrokeColor(ContextCompat.getColor(this, R.color.stickergram_label_stroke_color));
        Bitmap bitmap = textItem.getTextBitmap();
        int stickergramHeight = bitmap.getHeight();
        int stickergramWidth = bitmap.getWidth();
//        textItem.setPosition(new Position(mainBitmap.getHeight() - stickergramHeight, mainBitmap.getWidth() - stickergramWidth));

        int top = mainBitmap.getHeight() - stickergramHeight + 15;
        textItem.setPosition(new Position(top, -15));
        label[0].setTextItem(textItem);
        textLayerContainer.addView(label[0]);
        label[1] = new TouchImageView(this, new TextItem(getString(R.string.made), mainBitmap), 0, mainBitmap);
        textItem.setSize(20);
        textItem.setText(getString(R.string.made));
        textItem.setStrokeWidth(3);
        textItem.setBackgroundColor(0);
        bitmap = textItem.getTextBitmap();
//        textItem.setPosition(new Position(0,0));
        textItem.setPosition(new Position(top - bitmap.getHeight() / 3.3f,
                stickergramWidth / 2 - bitmap.getWidth() / 2 - 7.5f));
        label[1].setTextItem(textItem);
        textLayerContainer.addView(label[1]);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        if (requestCode == Loader.EDIT_ACTIVITY_GAIN_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                instantiateSavingDialog();
            } else {
                Toast.makeText(this, getResources().getString(R.string.need_permission_to_save_the_sticker), Toast.LENGTH_LONG).show();
            }
        }
    }

}