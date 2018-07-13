package com.amir.stickergram;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amir.stickergram.arcList.ArcCallBack;
import com.amir.stickergram.arcList.ArcLinearLayout;
import com.amir.stickergram.arcList.ArcScrollView;
import com.amir.stickergram.arcList.VerticalArcContainer;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.fonts.EnglishFontsFragment;
import com.amir.stickergram.fonts.MainFontDialogFragment;
import com.amir.stickergram.image.ImagePickerDialog;
import com.amir.stickergram.image.ImageReceiverCallBack;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.image.FontItem;
import com.amir.stickergram.image.ImageItem;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.image.OnMainImageViewTouch;
import com.amir.stickergram.image.TextItem;
import com.amir.stickergram.image.TouchImageView;
import com.amir.stickergram.phoneStickers.CustomRecyclerView;
import com.amir.stickergram.phoneStickers.organizedDetailed.OrganizedStickersDetailedDialogFragment;
import com.amir.stickergram.phoneStickers.organizedIcon.OnStickerClickListener;
import com.amir.stickergram.phoneStickers.unorganized.PhoneStickersUnorganizedFragment;
import com.amir.stickergram.sticker.icon.IconItem;
import com.amir.stickergram.sticker.icon.OnIconSelectedListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.tangxiaolv.telegramgallery.GalleryActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import app.minimize.com.seek_bar_compat.SeekBarCompat;

public class EditImageActivity
        extends BaseActivity
        implements
        View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener,
        EnglishFontsFragment.OnFontItemClicked,
        PhoneStickersUnorganizedFragment.UnorganizedFragmentCallbacks,
        CustomRecyclerView.RecyclerViewMovementCallbacks,
        ImageReceiverCallBack,
        OnStickerClickListener,
        OnIconSelectedListener,
        ArcCallBack {

    //todo: scaler... scale any com.amir.stickergram.image while editing it by dragging the top left corner of the com.amir.stickergram.image
    private static final String MAIN_FONT_DIALOG_FRAGMENT_TAG = "MAIN_FONT_DIALOG_FRAGMENT_TAG";
    public static final int MAX_TEXT_SIZE = 300;
    private static final String EDIT_IMAGE_STATE = "EDIT_IMAGE_STATE";
    private static final String TAG = EditImageActivity.class.getSimpleName();
    private static final int ADD_NEW_IMAGE = 5438;
    public static final String ADDED_IMAGE_ADDRESS = "ADDED_IMAGE_ADDRESS";
    public static final int INFO_CONTAINER_OFFSET = 70;
    public static final int REQUEST_CODE_ADD_IMAGE = 12354;
    private TouchImageView selectedLayer;

    public FrameLayout textLayerContainer;

    private VerticalArcContainer arcContainer;
    private SeekBarCompat sizeSeekBar;
    private SeekBarCompat tiltSeekBar;
    private SeekBarCompat shadowRadiusSeekBar;
    private SeekBarCompat shadowDxSeekBar;
    private SeekBarCompat shadowDySeekBar;

    private SeekBarCompat strokeWidthSeekBar;
    private RelativeLayout stickerContainer;
    private Bitmap mainBitmap;

    private View buyNoteContainer;
    private TextView buyNoteText;
    private OnMainImageViewTouch helper;
    private ArcScrollView tempArcContainer;
    private ArcLinearLayout strokeItemsView;
    private ArcLinearLayout shadowItemsView;
    private RelativeLayout mainContainer;
    private ImageView strokeButton;
    private ImageView shadowButton;
    private int toolbarHeight;
    private int mainContainerHeight;
    private TextView infoTextView;
    private int[] pos;
    private ImagePickerDialog imagePickerDialog;
    private View textColorButton;
    private View textBackgroundColor;
    private View fontButton;
    private View textButton;
    private ShowcaseView showcaseView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        mainBitmap = getBitmapFromExtra();
        if (mainBitmap == null) {
            Log.e(getClass().getSimpleName(), "mainBitmap was null");
            Toast.makeText(this, getString(R.string.there_was_a_problem_getting_the_picture), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setUpView();
        selectedLayer = null;
        if (savedInstanceState != null) {
            helper.recreateState(savedInstanceState.getBundle(EDIT_IMAGE_STATE));
        }
//        else getNewTextDialog(true);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (helper != null) {
            setLayerUnselected();
            outState.putBundle(EDIT_IMAGE_STATE, helper.getSaveState());
        }
    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        helper.create
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        if (requestCode == MainActivity.GALLERY_REQUEST_CODE) {

            //list of photos of seleced
            List<String> photos = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
            for (String s : photos) {

                Log.e(getClass().getName(), "onActivityResult: " + s);
            }
//            tempOutPutFile = Loader.generateEmptyBitmapFile(this, true);
//            Loader.crop(Uri.fromFile(new File(photos.get(0))), Uri.fromFile(Loader.generateEmptyBitmapFile(this, true)), this, false);

            Intent intent = new Intent(this, CropActivity.class);
            intent.putExtra(Constants.CROP_SOURCE, Uri.fromFile(new File(photos.get(0))));
            intent.putExtra(Constants.CROP_DESTINY, Uri.fromFile(Loader.generateEmptyBitmapFile(this, true)));
            intent.putExtra(Constants.IS_USING_EMPTY_IMAGE, false);
            intent.putExtra(Constants.LAUNCHED_TO_ADD_IMAGE, true);
            startActivityForResult(intent, ADD_NEW_IMAGE);
        } else if (requestCode == ADD_NEW_IMAGE) {
            receivedImage(BitmapFactory.decodeFile(data.getStringExtra(ADDED_IMAGE_ADDRESS)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_edit_image_add_new_text) {
            getNewTextDialog(true);
        } else if (itemId == R.id.menu_edit_image_save) {
            if (Loader.checkPermission(this))
                instantiateSavingDialog();
            else {
                Toast.makeText(this, getResources().getString(R.string.need_permission_to_save_the_sticker), Toast.LENGTH_LONG).show();
                Loader.gainPermission(this, Constants.EDIT_ACTIVITY_GAIN_PERMISSION);
            }
        } else if (itemId == R.id.menu_edit_image_add_new_image) {
            if (!Loader.checkPermission(this))
                Loader.gainPermission(this, REQUEST_CODE_ADD_IMAGE);
            else {
                getImagePickerDialog();
            }
        }

        showcaseView.hide();
        return true;
    }

    private void getImagePickerDialog() {
        if (imagePickerDialog == null)
            imagePickerDialog = new ImagePickerDialog();
        imagePickerDialog.show(getSupportFragmentManager(), "TAG");
    }

    private void instantiateSavingDialog() {
        final View newTextDialogView = getLayoutInflater().inflate(R.layout.dialog_finish_editing, null);
        setFont((ViewGroup) newTextDialogView);
        ImageView finishedImage = (ImageView) newTextDialogView.findViewById(R.id.dialog_finish_editing_image);
        setLayerUnselected();
        final Bitmap tempBitmap = helper.getFinishedBitmap();
//        mainBitmap = mainBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        Canvas canvas = new Canvas(mainBitmap);

        finishedImage.setImageBitmap(tempBitmap);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {

//                    if (Loader.freeMemory() > 2) {
                    Loader.saveBitmapToCache(tempBitmap); // the SaveStickerActivity uses the cached com.amir.stickergram.image for the saving process
                    finish();
                    startActivity(new Intent(EditImageActivity.this, SaveStickerActivity.class));
//                    } else
//                        Toast.makeText(EditImageActivity.this, getString(R.string.failed_to_save_the_sticker), Toast.LENGTH_LONG).show();
                }
                if (which == Dialog.BUTTON_NEGATIVE) {
                }
            }
        };

        final AlertDialog finishedEditing = new AlertDialog.Builder(this)
                .setView(newTextDialogView)
                .setPositiveButton(getString(R.string.yes), listener)
                .setNegativeButton(getString(R.string.no), listener)
                .create();

        finishedEditing.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                EditImageActivity.this.setFont(finishedEditing.getButton(AlertDialog.BUTTON_NEGATIVE));
                EditImageActivity.this.setFont(finishedEditing.getButton(AlertDialog.BUTTON_POSITIVE));
            }
        });

        finishedEditing.show();
    }

    private void manageShadowsFirstTap() {
        try {
            if (selectedLayer.isFirstTapOnShadowColor()) {
                ((TextItem) selectedLayer.getDrawableItem()).getShadow().setRadius(5);
                ((TextItem) selectedLayer.getDrawableItem()).getShadow().setDx(5);
                ((TextItem) selectedLayer.getDrawableItem()).getShadow().setDy(5);
            }
            selectedLayer.setFirstTapOnShadowColor(false);
        } catch (ClassCastException e) {
            setLayerUnselected();
        }
    }

    private void buyProNote(String string) {
        if (buyNoteContainer != null) buyNoteContainer.setVisibility(View.VISIBLE);
        if (buyNoteText != null) buyNoteText.setText(string);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int itemId = v.getId();
        if (itemId == R.id.activity_edit_image_main_image)
            helper.onTouch(selectedLayer, event);
        else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    clickAction(v, itemId, false);
                    break;
                case MotionEvent.ACTION_UP:
                    clickAction(v, itemId, true);
                    break;
            }
        }
        return true;
    }

    private void clickAction(View view, int itemId, boolean isActionUp) {
        if (itemId == R.id.include_pro_note_close) {
            if (buyNoteContainer != null) buyNoteContainer.setVisibility(View.GONE);
        } else if (itemId == R.id.include_pro_note_text) {
            if (isActionUp)
                requestProVersion();
        } else if (itemId == R.id.activity_edit_image_buttons_overlay_layer) {
            if (isActionUp)
                Toast.makeText(this, getString(R.string.select_a_text), Toast.LENGTH_LONG).show();
        } else if (selectedLayer != null) {
            try {
                if (itemId == R.id.include_buttons_size_button) {
                    if (!isActionUp)
                        showInfo(getString(R.string.size), view);
                    else {
                        setVisibleSeekBar(selectedLayer.getDrawableItem().getSize(), sizeSeekBar);
                    }
                } else if (itemId == R.id.include_buttons_text_button) {
                    if (isActionUp)
                        getNewTextDialog(false);
                } else if (itemId == R.id.include_buttons_tilt_button) {
                    if (!isActionUp)
                        showInfo(getString(R.string.tilt), view);
                    else {
                        if (!isPaid) buyProNote(getString(R.string.tilt_effect));
                        setVisibleSeekBar(selectedLayer.getDrawableItem().getTilt(), tiltSeekBar);
                    }
                } else if (itemId == R.id.include_buttons_remove) {
                    if (!isActionUp)
                        showInfo(getString(R.string.remove), view);
                    else {
                        textLayerContainer.removeView(selectedLayer);
                        helper.getItems().remove(selectedLayer);
                    }
                } else if (itemId == R.id.include_buttons_duplicate) {
                    if (!isActionUp)
                        showInfo(getString(R.string.duplicate), view);
                    else {
                        TouchImageView touchItem = new TouchImageView(selectedLayer, getApplicationContext());

                        touchItem.updateDrawable();

                        setSelectedLayer(touchItem);
                        helper.add(touchItem);
                        textLayerContainer.removeView(touchItem);
                        textLayerContainer.addView(touchItem);

                    }
                } else if (itemId == R.id.activity_edit_image_move_up_button) {
                    if (!isActionUp) {
                        showInfo(getString(R.string.move_up), view);
                    } else {
                        selectedLayer.getDrawableItem().moveUp();
                        selectedLayer.updateDrawable();
                    }
                } else if (itemId == R.id.activity_edit_image_move_down_button) {
                    if (!isActionUp) {
                        showInfo(getString(R.string.move_down), view);
                    } else {
                        selectedLayer.getDrawableItem().moveDown();
                        selectedLayer.updateDrawable();
                    }
                } else if (itemId == R.id.activity_edit_image_move_left_button) {
                    if (!isActionUp) {
                        showInfo(getString(R.string.move_left), view);
                    } else {
                        selectedLayer.getDrawableItem().moveLeft();
                        selectedLayer.updateDrawable();
                    }
                } else if (itemId == R.id.activity_edit_image_move_right_button) {
                    if (!isActionUp) {
                        showInfo(getString(R.string.move_right), view);
                    } else {
                        selectedLayer.getDrawableItem().moveRight();
                        selectedLayer.updateDrawable();
                    }
                } else if (itemId == R.id.activity_edit_image_move_center_vertical) {
                    if (!isActionUp) {
                        showInfo(getString(R.string.center_vertical), view);
                    } else {
                        selectedLayer.getDrawableItem().centerVertical();
                        selectedLayer.updateDrawable();
                    }
                } else if (itemId == R.id.activity_edit_image_move_align_left) {
                    if (!isActionUp) {
                        showInfo(getString(R.string.align_left), view);
                    } else {
                        selectedLayer.getDrawableItem().alignLeft();
                        selectedLayer.updateDrawable();
                    }
                } else if (itemId == R.id.activity_edit_image_move_align_right) {
                    if (!isActionUp) {
                        showInfo(getString(R.string.align_right), view);
                    } else {
                        selectedLayer.getDrawableItem().alignRight();
                        selectedLayer.updateDrawable();
                    }
                } else if (itemId == R.id.activity_edit_image_move_align_top) {
                    if (!isActionUp) {
                        showInfo(getString(R.string.align_top), view);
                    } else {
                        selectedLayer.getDrawableItem().alignTop();
                        selectedLayer.updateDrawable();
                    }
                } else if (itemId == R.id.activity_edit_image_move_align_bottom) {
                    if (!isActionUp) {
                        showInfo(getString(R.string.align_bottom), view);
                    } else {
                        selectedLayer.getDrawableItem().alignBottom();
                        selectedLayer.updateDrawable();
                    }
                } else if (itemId == R.id.activity_edit_image_move_center_horizontal) {
                    if (!isActionUp) {
                        showInfo(getString(R.string.move_right), view);
                    } else {
                        selectedLayer.getDrawableItem().centerHorizontal();
                        selectedLayer.updateDrawable();
                    }
                } else if (itemId == R.id.include_buttons_font_button) {
                    if (!isActionUp)
                        showInfo(getString(R.string.font), view);
                    else {
                        MainFontDialogFragment mainFontDialogFragment = new MainFontDialogFragment();
                        mainFontDialogFragment.show(getSupportFragmentManager(), MAIN_FONT_DIALOG_FRAGMENT_TAG);
                    }
                } else if (itemId == R.id.include_buttons_text_color) {
                    if (!isActionUp)
                        showInfo(getString(R.string.text_color), view);
                    else {
                        Loader.setColor(this, selectedLayer, Constants.TEXT_COLOR);
                    }
                } else if (itemId == R.id.include_buttons_shadow_color) {
                    if (!isActionUp)
                        showInfo(getString(R.string.shadow_color), view);
                    else {
                        manageShadowsFirstTap();
                        Loader.setColor(this, selectedLayer, Constants.TEXT_SHADOW_COLOR);
                    }
                } else if (itemId == R.id.include_buttons_shadow_radius) {
                    if (!isActionUp)
                        showInfo(getString(R.string.shadow_radius), view);
                    else {
                        setVisibleSeekBar(((TextItem) selectedLayer.getDrawableItem()).getShadow().getRadius(), shadowRadiusSeekBar);
                    }
                } else if (itemId == R.id.include_buttons_shadow_dx) {
//                if (!isPaid) buyProNote(getString(R.string.shadow_position_effect));
                    if (!isActionUp)
                        showInfo(getString(R.string.shadow_x), view);
                    else {
                        manageShadowsFirstTap();
                        setVisibleSeekBar(((TextItem) selectedLayer.getDrawableItem()).getShadow().getDx(), shadowDxSeekBar);
                    }
                } else if (itemId == R.id.include_buttons_shadow_dy) {
//                if (!isPaid) buyProNote(getString(R.string.shadow_position_effect));
                    if (!isActionUp)
                        showInfo(getString(R.string.shadow_y), view);
                    else {
                        manageShadowsFirstTap();
                        setVisibleSeekBar(((TextItem) selectedLayer.getDrawableItem()).getShadow().getDy(), shadowDySeekBar);
                    }
                } else if (itemId == R.id.include_buttons_text_background) {
                    if (!isActionUp)
                        showInfo(getString(R.string.text_background), view);
                    else {
                        Loader.setColor(this, selectedLayer, Constants.TEXT_BACKGROUND_COLOR);
                    }
                } else if (itemId == R.id.include_buttons_text_stroke_color) {
//                if (!isPaid)
//                    buyProNote(getString(R.string.stroke_color_is_only_available_in_blue_upgrade_to_pro_to_access_all_colors));
                    if (!isActionUp)
                        showInfo(getString(R.string.stroke_color), view);
                    else {
                        if (selectedLayer.isFirstTapOnStrokeColor())
                            ((TextItem) selectedLayer.getDrawableItem()).setStrokeWidth(((TextItem) selectedLayer.getDrawableItem()).getStrokeWidth());
                        selectedLayer.setFirstTapOnStrokeColor(false);
                        Loader.setColor(this, selectedLayer, Constants.TEXT_STROKE_COLOR);
                    }
                } else if (itemId == R.id.include_buttons_text_stroke_width) {
                    if (!isActionUp)
                        showInfo(getString(R.string.stroke_width), view);
                    else {
                        setVisibleSeekBar((int) ((TextItem) selectedLayer.getDrawableItem()).getStrokeWidth(), strokeWidthSeekBar);
                    }
                } else if (itemId == R.id.activity_edit_image_main_frame_container) {
                    if (isActionUp) setLayerUnselected();
                } else if (itemId == R.id.include_buttons_stroke) {
                    if (!isActionUp)
                        showInfo(getString(R.string.drop_shadow), view);
                    else {

                        tempArcContainer.swapView(strokeItemsView);
                        strokeButton.setBackgroundResource(R.drawable.ic_circle);
                        strokeButton.setImageResource(R.drawable.ic_stroke_blue);
                        int padding = (int) Loader.convertDpToPixel(2, this);
                        strokeButton.setPadding(padding, padding, padding, padding);
                        shadowButton.setBackgroundResource(0);
                        int defaultPadding = (int) Loader.convertDpToPixel(7, this);
                        shadowButton.setPadding(defaultPadding, 0, defaultPadding, 0);
                    }
                } else if (itemId == R.id.include_buttons_shadow) {
                    if (!isActionUp)
                        showInfo(getString(R.string.shadow), view);
                    else {
                        tempArcContainer.swapView(shadowItemsView);
                        shadowButton.setBackgroundResource(R.drawable.ic_circle);
                        int padding = (int) Loader.convertDpToPixel(2, this);
                        shadowButton.setPadding(padding, padding, padding, padding);
                        strokeButton.setBackgroundResource(0);
                        strokeButton.setImageResource(R.drawable.ic_stroke);
                        int defaultPadding = (int) Loader.convertDpToPixel(7, this);
                        shadowButton.setPadding(defaultPadding, 0, defaultPadding, 0);
//                    strokeButton.setPadding(0, 0, 0, 0);
                    }
                }

            } catch (ClassCastException e) {
                setLayerUnselected();
            }

            if (isActionUp) hideInfo();
        } else if (isActionUp)
            Toast.makeText(this, getString(R.string.select_a_text), Toast.LENGTH_LONG).show();
    }

    private void hideInfo() {
        if (infoTextView != null)
            infoTextView.setVisibility(View.GONE);
    }

    private void showInfo(String string, final View v) {
        v.getLocationOnScreen(pos);
        infoTextView.setVisibility(View.INVISIBLE);
        infoTextView.setText(string);
//        Log.e(getClass().getSimpleName(), "string: " + string + " x: " + event.getX() + " Y: " + event.getY());
        ViewTreeObserver viewTreeObserver = infoTextView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    infoTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (Loader.deviceLanguageIsPersian()) {
//                        Log.e(TAG, "x: " + pos[0] + " y: " + pos[1] + " marginStart: " + ((int) (ArcScrollView.screenWidth - pos[0]) - infoTextView.getWidth() / 2 + v.getWidth() / 2));
                        params.setMargins((int) (ArcScrollView.screenWidth - pos[0]),
                                (int) (pos[1] - INFO_CONTAINER_OFFSET * BaseActivity.density), 0, 0);
                        params.setMarginStart((int) (ArcScrollView.screenWidth - pos[0]) - infoTextView.getWidth() / 2 - v.getWidth() / 2);

                    } else {
                        params.setMargins(pos[0] - infoTextView.getWidth() / 2 + v.getWidth() / 2,
                                (int) (pos[1] - INFO_CONTAINER_OFFSET * BaseActivity.density), 0, 0);
                        params.setMarginStart(pos[0] - infoTextView.getWidth() / 2 + v.getWidth() / 2);
                    }
                    infoTextView.setLayoutParams(params);
//                    Log.e(getClass().getSimpleName(), "height: " + infoTextView.getHeight() + " width: " + infoTextView.getWidth());
                    infoTextView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void deselectSelectableButtons() {
        strokeButton.setBackgroundResource(0);
        strokeButton.setImageResource(R.drawable.ic_stroke);
        strokeButton.setPadding(0, 0, 0, 0);
        shadowButton.setBackgroundResource(0);
        shadowButton.setPadding(0, 0, 0, 0);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (selectedLayer != null) {
            try {
                if (progress > 5 && seekBar == sizeSeekBar) {
                    selectedLayer.getDrawableItem().setSize(progress);
                } else if (seekBar == tiltSeekBar) {
                    selectedLayer.getDrawableItem().setTilt(progress);
                } else if (seekBar == shadowRadiusSeekBar && progress > 0) {
                    ((TextItem) selectedLayer.getDrawableItem()).getShadow().setRadius(progress);
                } else if (seekBar == shadowDySeekBar) {
                    ((TextItem) selectedLayer.getDrawableItem()).getShadow().setDy(progress);
                } else if (seekBar == shadowDxSeekBar) {
                    ((TextItem) selectedLayer.getDrawableItem()).getShadow().setDx(progress);
                } else if (seekBar == strokeWidthSeekBar) {
                    ((TextItem) selectedLayer.getDrawableItem()).setStrokeWidth(progress);
                }
                selectedLayer.updateDrawable();
            } catch (ClassCastException e) {
                setLayerUnselected();
            }
        }
    }


    /**
     * because it is not possible to set the activity_edit_image_images_container width and height to match_parent as we would not be able
     * to drag the text properly and if we set the activity_edit_image_images_container width and height to wrap_content the com.amir.stickergram.image
     * on high density phones would be too small i had to set the width and height at runtime
     * <p/>
     * here i get the width and the height of the activity_edit_image_main_frame_container so i know the measurements
     * for the scaling
     * <p/>
     * based on the orientation of the com.amir.stickergram.image if the width of the com.amir.stickergram.image is more than it's height i would need to set the activity_edit_image_images_container
     * width to match_parent and it's height should be scaled and vise versa
     * <p/>
     * also i need to set some margin on the com.amir.stickergram.image as it won't remain in the center after new width and height were set so i center
     * the com.amir.stickergram.image by setting margin on it
     */

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //todo: should this be implemented in on setContentView????
//        Log.e(getClass().getSimpleName(), "toolbar height: " + getSupportActionBar().getHeight());
        if (mainContainerHeight == 0) {
            mainContainerHeight = mainContainer.getHeight();
            toolbarHeight = toolbar.getHeight();
            if (selectedLayer == null)
                stickerContainer.animate()
                        .translationY((mainContainerHeight - toolbarHeight) / 2 - stickerContainer.getHeight() / 2)
                        .setDuration(500);
//            Log.e(getClass().getSimpleName(), "toolbarHeight: " + toolbarHeight);
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
            params.setMarginStart(marginStart);
            params.setMargins(0, marginTop, 0, 0);
            textLayerContainer.setLayoutParams(params);
//        Log.e(getClass().getSimpleName(), "scale: " + scale);
        }

    }

    @Nullable
    public Bitmap getBitmapFromExtra() {
        Uri imageUri = getIntent().getParcelableExtra(Constants.EDIT_IMAGE_URI);
        Bitmap imageBitmap = null;
        try {
            if (imageUri == null) {
                String dirInAsset = getIntent().getStringExtra(Constants.EDIT_IMAGE_DIR_IN_ASSET);
                imageBitmap = BitmapFactory.decodeStream(getAssets().open(dirInAsset));
            } else {
//                Log.e(getClass().getSimpleName(), "updateBitmap from the extra: " + imageUri.toString());
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                if (imageBitmap == null)
//                    imageBitmap = BitmapFactory.decodeFile(Loader.getRealPathFromURI(imageUri, getContentResolver()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imageBitmap == null) {
            Log.e(getClass().getSimpleName(), "imageBitmap was null");
            finish();
            return null;
        }
//        int mainWidth = imageBitmap.getWidth();
//        int mainHeight = imageBitmap.getHeight();
//        Log.e(getClass().getSimpleName(), "mainWidth: " + mainWidth + " mainHeight: " + mainHeight);
//        imageBitmap = Loader.rotateImage(imageBitmap, rotation);
//        Bitmap resBitmap;
//        if (mainWidth != 512 && mainHeight != 512) {
//            float scale;
//            if (mainHeight > mainWidth) {
//                scale = 512f / mainHeight;
//                resBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) (mainWidth * scale), 512, false);
//            } else {
//                scale = 512f / mainWidth;
//                resBitmap = Bitmap.createScaledBitmap(imageBitmap, 512, (int) (mainHeight * scale), false);
//            }
//        } else {
//            Log.e(getClass().getSimpleName(), "both side were 512");
//            resBitmap = imageBitmap;
//        }
        return imageBitmap;

    }

    private void getNewTextDialog(final boolean asNewText) {
        final View newTextDialogView = getLayoutInflater().inflate(R.layout.dialog_set_new_text, null);
        final EditText editText = (EditText) newTextDialogView.findViewById(R.id.dialog_set_new_text_text);
        try {
            if (!asNewText) {
                editText.setText(((TextItem) selectedLayer.getDrawableItem()).getText());
                editText.setSelection(((TextItem) selectedLayer.getDrawableItem()).getText().length());
                editText.setTypeface(((TextItem) selectedLayer.getDrawableItem()).getFont().getTypeface());
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
                                            mainBitmap,
                                            new TextItem(mainBitmap));
                            setSelectedLayer(touchItem);
                            helper.add(touchItem);
                            textLayerContainer.removeView(touchItem);
                            textLayerContainer.addView(touchItem);
                        }
                        if (selectedLayer != null && (selectedLayer.getDrawableItem() instanceof TextItem)) {
                            int textLengthTemp = text.length();
                            if (textLengthTemp >= 1 && textLengthTemp <= 4 && text.charAt(textLengthTemp - 1) != ' ')
                                text += " ";
                            ((TextItem) selectedLayer.getDrawableItem()).setText(text);
                            selectedLayer.updateDrawable();
                            if (text.equals("")) {
                                helper.remove(selectedLayer);
                                setLayerUnselected();
                            }
                        }
                    } else if (which == Dialog.BUTTON_NEGATIVE) {
                        setLayerUnselected();
                    }
                }
            };

            final AlertDialog newTextDialog = new AlertDialog.Builder(this)
                    .setView(newTextDialogView)
                    .setMessage(getString(R.string.new_text))
                    .setPositiveButton(getString(R.string.done), listener)
                    .setNegativeButton(getString(R.string.cancel), listener)
                    .setCancelable(false)
                    .create();

            newTextDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    EditImageActivity.this.setFont((TextView) newTextDialog.findViewById(android.R.id.message));
                    EditImageActivity.this.setFont(newTextDialog.getButton(AlertDialog.BUTTON_NEGATIVE));
                    EditImageActivity.this.setFont(newTextDialog.getButton(AlertDialog.BUTTON_NEUTRAL));
                    EditImageActivity.this.setFont(newTextDialog.getButton(AlertDialog.BUTTON_POSITIVE));
                }
            });

            newTextDialog.show();
        } catch (ClassCastException e) {
            setLayerUnselected();
        }
    }

    public void setLayerUnselected() {
        if (selectedLayer != null) {
            Log.e(getClass().getSimpleName(), "setLayerUnselected was called ");
            if (!isPaid) selectedLayer.notPaid();
            selectedLayer.setAsSelected(false);
            sizeSeekBar.setVisibility(View.GONE);
            tiltSeekBar.setVisibility(View.GONE);
            shadowRadiusSeekBar.setVisibility(View.GONE);
            shadowDxSeekBar.setVisibility(View.GONE);
            shadowDySeekBar.setVisibility(View.GONE);
            strokeWidthSeekBar.setVisibility(View.GONE);

            selectedLayer = null;
            tempArcContainer.swapView(null);
            deselectSelectableButtons();
            deactivateButtons(true);
        }
    }

    public void setSelectedLayer(TouchImageView item) {

        if (item != null) {


            if (selectedLayer != item) {
                textLayerContainer.removeView(item);
                textLayerContainer.addView(item);
//                List<TouchImageView> items = helper.getItems();
//                for (TouchImageView view :
//                        items) {
//                    textLayerContainer.addView(view);
//
//                }
                setLayerUnselected();
                selectedLayer = item;
                item.setAsSelected(true);
                deactivateButtons(false);
            }
        }
    }

    private void updateArc(boolean selectedImage) {
        if (selectedImage) {
            strokeButton.setVisibility(View.GONE);
            shadowButton.setVisibility(View.GONE);
            fontButton.setVisibility(View.GONE);
            textColorButton.setVisibility(View.GONE);
            textBackgroundColor.setVisibility(View.GONE);
            textButton.setVisibility(View.GONE);
        } else {
            strokeButton.setVisibility(View.VISIBLE);
            shadowButton.setVisibility(View.VISIBLE);
            fontButton.setVisibility(View.VISIBLE);
            textColorButton.setVisibility(View.VISIBLE);
            textBackgroundColor.setVisibility(View.VISIBLE);
            textButton.setVisibility(View.VISIBLE);
        }
    }

    private void deactivateButtons(boolean deactivate) {
        if (arcContainer != null) {
            if (deactivate) {
                if (selectedLayer == null) {
                    hideInfo();
                    stickerContainer.animate()
                            .translationY((mainContainerHeight - toolbarHeight) / 2 - stickerContainer.getHeight() / 2)
                            .setDuration(500);
                    arcContainer.knockout();
                }
            } else {
                if (selectedLayer != null) {
                    stickerContainer.animate()
                            .translationY(0)
                            .setDuration(500);
                    arcContainer.knockIn();
                }
            }


        }

    }

    private void setVisibleSeekBar(int progress, SeekBarCompat seekBar) {
        sizeSeekBar.setVisibility(View.GONE);
        shadowRadiusSeekBar.setVisibility(View.GONE);
        tiltSeekBar.setVisibility(View.GONE);
        shadowDxSeekBar.setVisibility(View.GONE);
        shadowDySeekBar.setVisibility(View.GONE);
        strokeWidthSeekBar.setVisibility(View.GONE);

        seekBar.setProgress(progress);
        seekBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void itAllKnockedOut() {
        if (selectedLayer != null)
            updateArc(selectedLayer.getDrawableItem() instanceof ImageItem);
    }

    @Override
    public void itAllKnockedIn() {
        if (selectedLayer != null)
            updateArc(selectedLayer.getDrawableItem() instanceof ImageItem);
    }

    @Override
    public void onFontItemSelected(FontItem item) {
        if (selectedLayer != null) {
            try {
                ((TextItem) selectedLayer.getDrawableItem()).setFont(item);
                selectedLayer.updateDrawable();
            } catch (ClassCastException e) {
                setLayerUnselected();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_image, menu);
        menu.getItem(1);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        if (requestCode == Constants.EDIT_ACTIVITY_GAIN_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                instantiateSavingDialog();
            } else {
                Toast.makeText(this, getResources().getString(R.string.need_permission_to_save_the_sticker), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CODE_ADD_IMAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImagePickerDialog();
            }
        }
    }

    private void runShowCase() {
        Target homeTarget = new Target() {
            @Override
            public Point getPoint() {
                // Get approximate position of home icon's center
                int actionBarSize = toolbar.getHeight();
                int y = actionBarSize / 2;
                int x;
                if (Loader.deviceLanguageIsPersian()) {
                    x = actionBarSize;
                } else {
                    x = toolbar.getWidth() - actionBarSize;
                }
                return new Point(x, y);
            }
        };
        showcaseView = new ShowcaseView.Builder(this)
                .setStyle(R.style.CustomShowcaseTheme2)
                .hideOnTouchOutside()
//                .replaceEndButton(endButton)
                .setContentTitle(getString(R.string.add_new_layer))
                .setTarget(homeTarget)
                .build();

        showcaseView.hideButton();
    }

    private void setUpView() {

        textLayerContainer = (FrameLayout) findViewById(R.id.activity_edit_image_images_container);
        if (textLayerContainer == null)
            throw new RuntimeException("Container was null add activity_edit_image_relative_layout_container to the view");


        textButton = findViewById(R.id.include_buttons_text_button);
        View buttonsDeactivateLayer = findViewById(R.id.activity_edit_image_buttons_overlay_layer);
        fontButton = findViewById(R.id.include_buttons_font_button);
        View sizeButton = findViewById(R.id.include_buttons_size_button);
        textBackgroundColor = findViewById(R.id.include_buttons_text_background);
        textColorButton = findViewById(R.id.include_buttons_text_color);
        View tiltButton = findViewById(R.id.include_buttons_tilt_button);
        View moveUpButton = findViewById(R.id.activity_edit_image_move_up_button);
        View moveDownButton = findViewById(R.id.activity_edit_image_move_down_button);
        View moveLeftButton = findViewById(R.id.activity_edit_image_move_left_button);
        View moveRightButton = findViewById(R.id.activity_edit_image_move_right_button);
        findViewById(R.id.include_buttons_duplicate).setOnTouchListener(this);
        findViewById(R.id.include_buttons_remove).setOnTouchListener(this);
        findViewById(R.id.activity_edit_image_move_center_horizontal).setOnTouchListener(this);
        findViewById(R.id.activity_edit_image_move_center_vertical).setOnTouchListener(this);
        findViewById(R.id.activity_edit_image_move_align_bottom).setOnTouchListener(this);
        findViewById(R.id.activity_edit_image_move_align_top).setOnTouchListener(this);
        findViewById(R.id.activity_edit_image_move_align_right).setOnTouchListener(this);
        findViewById(R.id.activity_edit_image_move_align_left).setOnTouchListener(this);
        stickerContainer = (RelativeLayout) findViewById(R.id.activity_edit_image_main_frame_container);
        buyNoteContainer = findViewById(R.id.include_pro_note_container);
        buyNoteText = (TextView) findViewById(R.id.include_pro_note_text);
        View proNoteCloseButton = findViewById(R.id.include_pro_note_close);
        arcContainer = (VerticalArcContainer) findViewById(R.id.include_buttons_scroll_view);
        arcContainer.bringToFront();

        strokeButton = (ImageView) findViewById(R.id.include_buttons_stroke);
        strokeButton.setOnTouchListener(this);

        shadowButton = (ImageView) findViewById(R.id.include_buttons_shadow);
        shadowButton.setOnTouchListener(this);

        tempArcContainer = (ArcScrollView) findViewById(R.id.include_arc_buttons_temp_arc);

        strokeItemsView = (ArcLinearLayout) getLayoutInflater().inflate(R.layout.stroke_arc_linear_layout, arcContainer, false);
        View strokeWidthButton = strokeItemsView.findViewById(R.id.include_buttons_text_stroke_width);
        View strokeColorButton = strokeItemsView.findViewById(R.id.include_buttons_text_stroke_color);

        shadowItemsView = (ArcLinearLayout) getLayoutInflater().inflate(R.layout.shadow_arc_linear_layout, arcContainer, false);
        View shadowRadius = shadowItemsView.findViewById(R.id.include_buttons_shadow_radius);
        View shadowDx = shadowItemsView.findViewById(R.id.include_buttons_shadow_dx);
        View shadowDy = shadowItemsView.findViewById(R.id.include_buttons_shadow_dy);
        View shadowColorButton = shadowItemsView.findViewById(R.id.include_buttons_shadow_color);

        ImageView mainImageView = (ImageView) findViewById(R.id.activity_edit_image_main_image);
        helper = new OnMainImageViewTouch(this, mainBitmap, mainImageView);
        mainContainer = (RelativeLayout) findViewById(R.id.activity_edit_image_main_container);
//        View scrollView = findViewById(R.id.include_buttons_scroll_view);
//        if (isTablet && !isInLandscape) {
//            if (scrollView != null) {
//                RelativeLayout.LayoutParams params =
//                        new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.height_of_the_button_scroll_view_on_tablet));
//                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                scrollView.setLayoutParams(params);
//            }
//        }
        if (mainImageView != null) {
//            mainImageView.setImageBitmap(mainBitmap);
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

        shadowRadiusSeekBar = Loader.getSeekBar(this,
                20,
                ContextCompat.getColor(this, R.color.shadow_radius_seek_bar_background_color),
                ContextCompat.getColor(this, R.color.shadow_radius_tilt_seek_bar_progress_color),
                ContextCompat.getColor(this, R.color.shadow_radius_seek_bar_thumb_color),
                0,
                mainContainer
        );

        if (stickerContainer != null) stickerContainer.setOnTouchListener(this);
        if (buyNoteContainer != null && !isPaid) buyNoteContainer.setVisibility(View.VISIBLE);
        if (buttonsDeactivateLayer != null) buttonsDeactivateLayer.setOnTouchListener(this);
        if (textColorButton != null) textColorButton.setOnTouchListener(this);
        if (proNoteCloseButton != null) proNoteCloseButton.setOnTouchListener(this);
        if (sizeButton != null) sizeButton.setOnTouchListener(this);
        if (shadowRadius != null) shadowRadius.setOnTouchListener(this);
        if (tiltButton != null) {
//            if (!isPaid)
//                tiltButton.setText(getString(R.string.tilt_pro));
            tiltButton.setOnTouchListener(this);
        }
        if (textButton != null) textButton.setOnTouchListener(this);
        if (textBackgroundColor != null) textBackgroundColor.setOnTouchListener(this);
        if (fontButton != null) fontButton.setOnTouchListener(this);
        if (strokeWidthButton != null) strokeWidthButton.setOnTouchListener(this);
        if (strokeColorButton != null) strokeColorButton.setOnTouchListener(this);
        if (shadowDx != null) {
            shadowDx.setOnTouchListener(this);
        }
        if (shadowDy != null) {
            shadowDy.setOnTouchListener(this);
        }
        if (shadowColorButton != null) shadowColorButton.setOnTouchListener(this);
        if (buyNoteText != null) buyNoteText.setOnTouchListener(this);
        if (moveUpButton != null) {
            moveUpButton.setOnTouchListener(this);
        }
        if (moveDownButton != null) {
            moveDownButton.setOnTouchListener(this);
        }
        if (moveLeftButton != null) {
            moveLeftButton.setOnTouchListener(this);
        }
        if (moveRightButton != null) {
            moveRightButton.setOnTouchListener(this);
        }
        if (sizeSeekBar != null) {
            sizeSeekBar.setOnSeekBarChangeListener(this);
            sizeSeekBar.setMax(MAX_TEXT_SIZE);

        }
        if (tiltSeekBar != null) {
            tiltSeekBar.setProgress(180);
            tiltSeekBar.setOnSeekBarChangeListener(this);
        }
        if (this.shadowRadiusSeekBar != null) {
            this.shadowRadiusSeekBar.setProgress(2);
            this.shadowRadiusSeekBar.setOnSeekBarChangeListener(this);
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

        setFont((ViewGroup) findViewById(R.id.nav_drawer));
        setFont(mainContainer);

        arcContainer.knockout();

        infoTextView = new TextView(this);
        infoTextView.setTextSize(17);
        infoTextView.setTextColor(Color.WHITE);
//        infoTextView.setBackgroundColor(Color.BLUE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            infoTextView.setElevation(5);
        }
        infoTextView.setBackgroundResource(R.drawable.simple_sticker_background);
        infoTextView.setAlpha(.85f);
        infoTextView.setPadding(6, 6, 6, 6);
        setFont(infoTextView);
        mainContainer.addView(infoTextView);
        pos = new int[2];

        deactivateButtons(true);
        runShowCase();
    }

    @Override
    public void cutModeToggled(boolean enabled) {
        //INTENTIONALLY EMPTY
    }

    @Override
    public void onSlideUpCallback() {
        //INTENTIONALLY EMPTY
    }

    @Override
    public void onSlideDownCallback() {
        //INTENTIONALLY EMPTY
    }

    @Override
    public void receivedImage(Bitmap image) {
        image = Loader.createTrimmedBitmap(image);
        TouchImageView touchItem =
                new TouchImageView(EditImageActivity.this,
                        mainBitmap,
                        new ImageItem(mainBitmap, image));
        int newSize;
        if (image.getWidth() > 250 || image.getHeight() > 250) {
            newSize = touchItem.getDrawableItem().getSize() / 2;
        } else {
            newSize = touchItem.getDrawableItem().getSize();
        }
        touchItem.getDrawableItem().setSize(newSize);
        touchItem.updateDrawable();

        setSelectedLayer(touchItem);
        helper.add(touchItem);
        textLayerContainer.removeView(touchItem);
        textLayerContainer.addView(touchItem);


        if (imagePickerDialog != null)
            imagePickerDialog.dismiss();

    }

    @Override
    public void OnIconClicked(IconItem item) {
        OrganizedStickersDetailedDialogFragment
                .newInstance(item.getFolder(), true)
                .show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void OnIconLongClicked(IconItem item) {
        //INTENTIONALLY EMPTY
    }

    @Override
    public void OnNoItemWereFoundListener() {
        //INTENTIONALLY EMPTY
    }

    @Override
    public void OnCreateNewFolderSelected() {
        //INTENTIONALLY EMPTY
    }

    @Override
    public void OnIconSelected(IconItem item) {
        imagePickerDialog.onServerStickerIconClicked(item.getName(), item.getEnName());
    }

    @Override
    public void OnNoStickerWereFoundListener() {
        //INTENTIONALLY EMPTY
    }


}