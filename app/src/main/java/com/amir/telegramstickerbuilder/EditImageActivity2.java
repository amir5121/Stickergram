//package com.amir.telegramstickerbuilder;
//
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AlertDialog;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.SeekBar;
//import android.widget.Toast;
//
//import com.amir.telegramstickerbuilder.base.BaseActivity;
//import com.amir.telegramstickerbuilder.edit.EditToolsContainerFragment;
//import com.amir.telegramstickerbuilder.edit.FirstEditFragment;
//import com.amir.telegramstickerbuilder.fonts.EnglishFontsFragment;
//import com.amir.telegramstickerbuilder.fonts.MainFontDialogFragment;
//import com.amir.telegramstickerbuilder.infrastructure.FontItem;
//import com.amir.telegramstickerbuilder.infrastructure.Loader;
//import com.amir.telegramstickerbuilder.infrastructure.Position;
//import com.amir.telegramstickerbuilder.infrastructure.TextItem;
//import com.amir.telegramstickerbuilder.infrastructure.TouchImageView;
//import com.amir.telegramstickerbuilder.navdrawer.MainNavDrawer;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import app.minimize.com.seek_bar_compat.SeekBarCompat;
//
//public class EditImageActivity2 extends BaseActivity implements
//        View.OnTouchListener,
//        View.OnClickListener,
//        SeekBar.OnSeekBarChangeListener,
//        EnglishFontsFragment.OnFontItemClicked,
//        FirstEditFragment.ButtonsCallBack {
//    private static final String MAIN_FONT_DIALOG_FRAGMENT = "MAIN_FONT_DIALOG_FRAGMENT";
//    TouchImageView selectedLayer;
//    int layerCount;
//
//    EditToolsContainerFragment toolsContainerFragment;
//    ImageButton moveUpButton;
//    ImageButton moveDownButton;
//    ImageButton moveLeftButton;
//    ImageButton moveRightButton;
//    SeekBarCompat sizeSeekBar;
//    SeekBarCompat tiltSeekBar;
//    ViewGroup fragmentContainer;
//
//    public FrameLayout imageContainer;
//
//    Bitmap mainBitmap;
//    List<TouchImageView> items;
//
//    Position offsetPosition;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        Log.e(getClass().getSimpleName(), "Got Here1");
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_image2);
//        setNavDrawer(new MainNavDrawer(this));
//        items = new ArrayList<>();
//
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle("Example");
//        }
//
//        mainBitmap = getBitmapFromExtra();
//        if (mainBitmap == null) {
//            Log.e(getClass().getSimpleName(), "mainBitmap was null");
//            finish();
//        }
//
//        moveUpButton = (ImageButton) findViewById(R.id.activity_edit_image_move_up_button);
//        moveDownButton = (ImageButton) findViewById(R.id.activity_edit_image_move_down_button);
//        moveLeftButton = (ImageButton) findViewById(R.id.activity_edit_image_move_left_button);
//        moveRightButton = (ImageButton) findViewById(R.id.activity_edit_image_move_right_button);
//        sizeSeekBar = (SeekBarCompat) findViewById(R.id.activity_edit_image_size_seek_bar);
//        tiltSeekBar = (SeekBarCompat) findViewById(R.id.activity_edit_image_tilt_seek_bar);
////        fragmentContainer = (ViewGroup) findViewById(R.id.activity_edit_image_button_container);
//        toolsContainerFragment = (EditToolsContainerFragment) getSupportFragmentManager().findFragmentById(R.id.activity_edit_image_button_container);
//        setUpListener();
//
//        imageContainer = (FrameLayout) findViewById(R.id.activity_edit_image_images_container);
//        if (imageContainer == null)
//            throw new RuntimeException("Container was null add activity_edit_image_relative_layout_container to the view");
////
////        if(findViewById(R.id.activity_edit_image_button_container) != null) {
////            // if we are being restored from a previous state, then we dont need to do anything and should
////            // return or else we could end up with overlapping fragments.
////            if(savedInstanceState != null)
////                return;
////
////            // Create an instance of editorFrag
////             toolsContainerFragment = new EditToolsContainerFragment();
////
////            // add fragment to the fragment container layout
////            getSupportFragmentManager().
////                    beginTransaction().
////                    add(R.id.activity_edit_image_button_container, toolsContainerFragment).
////                    commit();
////        }
//        Log.e(getClass().getSimpleName(), "Got Here2");
////        else {
////            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
////                    ViewGroup.LayoutParams.MATCH_PARENT,
////                    ViewGroup.LayoutParams.MATCH_PARENT);
////            if (mainBitmap.getHeight() > mainBitmap.getWidth()) {
////                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
////                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
////                params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
////                imageContainer.setLayoutParams(params);
////                Log.e(getClass().getSimpleName(), "i got called");
////            } else {
////                params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
////                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
////                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
////                imageContainer.setLayoutParams(params);
////            }
////        }
//        setupMainImageView();
//
//        Log.e(getClass().getSimpleName(), "Got Here3");
//        layerCount = 0;
//        selectedLayer = null;
//
//        //todo: get layerCount and selectedLayer from the savedInstanceState and pass it to touchImageView
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int itemId = item.getItemId();
//        if (itemId == R.id.menu_edit_image_add_new_note) {
//            TouchImageView touchItem = new TouchImageView(this, new TextItem("TSB" + layerCount, this, mainBitmap), ++layerCount, mainBitmap);
//            setSelectedLayer(touchItem);
//            items.add(touchItem);
//            imageContainer.addView(touchItem);
//            return true;
//        } else if (itemId == R.id.menu_edit_image_save) {
//            setLayerUnselected();
//            mainBitmap = mainBitmap.copy(Bitmap.Config.ARGB_8888, true);
//            Canvas canvas = new Canvas(mainBitmap);
//
//            for (TouchImageView imageItem : items) {
//                canvas.drawBitmap(imageItem.getFinishedBitmap(), 0, 0, null);
//            }
//            Loader.saveBitmap(mainBitmap);
//            //todo: finish off the activity
//            return true;
//        }
//        return false;
//    }
//
//
//    @Override
//    public void onClick(View view) {
//        int itemId = view.getId();
//        if (selectedLayer != null) {
//            if (itemId == R.id.activity_edit_image_size_button) {
////                if (sizeSeekBar != null) {
////                    if (tiltSeekBar != null) tiltSeekBar.setVisibility(View.GONE);
////                    sizeSeekBar.setProgress(selectedLayer.getTextSize());
////                    sizeSeekBar.setVisibility(View.VISIBLE);
////                }
//            } else if (itemId == R.id.activity_edit_image_text_button) {
////                getNewTextDialog();
//            } else if (itemId == R.id.activity_edit_image_tilt_button) {
////                if (sizeSeekBar != null) sizeSeekBar.setVisibility(View.GONE);
////                tiltSeekBar.setProgress(selectedLayer.getTextItem().getTilt());
////                tiltSeekBar.setVisibility(View.VISIBLE);
//            } else if (itemId == R.id.activity_edit_image_move_up_button) {
//                moveUp();
//            } else if (itemId == R.id.activity_edit_image_move_down_button) {
//                moveDown();
//            } else if (itemId == R.id.activity_edit_image_move_left_button) {
//                moveLeft();
//            } else if (itemId == R.id.activity_edit_image_move_right_button) {
//                moveRight();
//            } else if (itemId == R.id.activity_edit_image_font_button) {
////                MainFontDialogFragment mainFontDialogFragment = new MainFontDialogFragment();
////                mainFontDialogFragment.show(getSupportFragmentManager(), MAIN_FONT_DIALOG_FRAGMENT);
//            } else if (itemId == R.id.activity_edit_image_style_button) {
//
//            }
//        } else Toast.makeText(this, getString(R.string.select_a_text), Toast.LENGTH_LONG).show();
//    }
//
//    //
////    @Override
////    public boolean onLongClick(View view) {
////        int itemId = view.getId();
////        if (itemId == R.id.activity_edit_image_move_up_button) {
////            moveUpWhilePressed();
////            return true;
////        } else if (itemId == R.id.activity_edit_image_move_down_button) {
////            return true;
////        } else if (itemId == R.id.activity_edit_image_move_right_button) {
////            return true;
////        } else if (itemId == R.id.activity_edit_image_move_left_button) {
////            return true;
////        }
////        return false;
////    }
////
////    private void moveUpWhilePressed() {
////        boolean isPressed = true;
////        while (isPressed) {
////            isPressed = moveUpButton.isPressed();
////            Log.e(getClass().getSimpleName(), "move up was called");
////            moveUp();
////
////        }
//    @Override
//    public void sizeButtonClicked() {
//        if (sizeSeekBar != null) {
//            if (tiltSeekBar != null) tiltSeekBar.setVisibility(View.GONE);
//            sizeSeekBar.setProgress(selectedLayer.getTextSize());
//            sizeSeekBar.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    public void editTextButtonClicked() {
//        getNewTextDialog();
//    }
//
//    @Override
//    public void fontButtonClicked() {
//        MainFontDialogFragment mainFontDialogFragment = new MainFontDialogFragment();
//        mainFontDialogFragment.show(getSupportFragmentManager(), MAIN_FONT_DIALOG_FRAGMENT);
//    }
//
//    @Override
//    public void tiltButtonClicked() {
//        if (sizeSeekBar != null) sizeSeekBar.setVisibility(View.GONE);
//        tiltSeekBar.setProgress(selectedLayer.getTextItem().getTilt());
//        tiltSeekBar.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        int action = event.getAction();
//        switch (action & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN: {
//                for (int i = items.size() - 1; i >= 0; i--) {
//                    offsetPosition = items.get(i).isItMe(new Position(event.getY(), event.getX()));
//                    if (offsetPosition != null) {
//                        setSelectedLayer(items.get(i));
//                        break;
//                    } else {
//                        setLayerUnselected();
//                    }
//                }
//                break;
//            }
//
//            case MotionEvent.ACTION_MOVE: {
//                if (selectedLayer != null) {
//                    selectedLayer.updateTextPosition(new Position(event.getY(), event.getX()), offsetPosition);
//                }
//                break;
//            }
//        }
//        return true;
//    }
//
//    private void setLayerUnselected() {
//        if (selectedLayer != null) {
//            selectedLayer.setAsSelected(false);
//            if (sizeSeekBar != null) sizeSeekBar.setVisibility(View.GONE);
//            if (tiltSeekBar != null) tiltSeekBar.setVisibility(View.GONE);
//            selectedLayer = null;
//            deactivateButtons(true);
//        }
//    }
//
//    public void setSelectedLayer(TouchImageView item) {
//        if (item != null) {
//            setLayerUnselected();
//            selectedLayer = item;
//            item.setAsSelected(true);
//            deactivateButtons(false);
//        }
//    }
//
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        if (selectedLayer != null && progress > 5 && seekBar == sizeSeekBar)
//            selectedLayer.setTextSize(progress);
//        if (selectedLayer != null && seekBar == tiltSeekBar) {
//            selectedLayer.setTextTilt(progress);
////            Log.e(getClass().getSimpleName(), "tilt seekBar changed value");
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_edit_image, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//    }
//
//    @Nullable
//    public Bitmap getBitmapFromExtra() {
//        Uri imageUri = getIntent().getParcelableExtra(BaseActivity.EDIT_IMAGE_URI);
//        Bitmap imageBitmap = null;
//        try {
//            if (imageUri == null) {
//                String dirInAsset = getIntent().getStringExtra(BaseActivity.EDIT_IMAGE_DIR_IN_ASSET);
//                imageBitmap = BitmapFactory.decodeStream(getAssets().open(dirInAsset));
//            } else {
//                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (imageBitmap == null) {
//            Log.e(getClass().getSimpleName(), "imageBitmap was null");
//            finish();
//        }
//        return imageBitmap;
//    }
//
//    private void setUpListener() {
//        if (moveUpButton != null) {
//            moveUpButton.setOnClickListener(this);
////            moveUpButton.setOnLongClickListener(this);
//        }
//        if (moveDownButton != null) {
//            moveDownButton.setOnClickListener(this);
////            moveDownButton.setOnLongClickListener(this);
//        }
//        if (moveLeftButton != null) {
//            moveLeftButton.setOnClickListener(this);
////            moveLeftButton.setOnLongClickListener(this);
//        }
//        if (moveRightButton != null) {
//            moveRightButton.setOnClickListener(this);
////            moveRightButton.setOnLongClickListener(this);
//        }
//        if (sizeSeekBar != null) {
//            sizeSeekBar.setOnSeekBarChangeListener(this);
//            sizeSeekBar.setMax(mainBitmap.getWidth());
//        }
//        if (tiltSeekBar != null) {
//            tiltSeekBar.setProgress(180);
//            tiltSeekBar.setOnSeekBarChangeListener(this);
//        }
//
//        deactivateButtons(true);
//
//    }
//
//    private void setupMainImageView() {
//        ImageView imageView = new ImageView(this);
////        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        imageView.setLayoutParams(params);
////        imageView.setBackgroundColor(Color.parseColor("#ff2299"));
//        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        imageView.setAdjustViewBounds(true);
//        imageView.setImageBitmap(mainBitmap);
//        imageContainer.addView(imageView);
//        imageView.setOnTouchListener(this);
//    }
//
//
//    private void getNewTextDialog() {
//        final View newTextDialogView = getLayoutInflater().inflate(R.layout.dialog_set_new_text, null);
//        final EditText editText = (EditText) newTextDialogView.findViewById(R.id.dialog_set_new_text_text);
//        editText.setText(selectedLayer.getTextItem().getText());
//        editText.setSelection(selectedLayer.getTextItem().getText().length());
//        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == Dialog.BUTTON_POSITIVE) {
//                    selectedLayer.updateText(editText.getText().toString());
//                }
//            }
//        };
//        AlertDialog newTextDialog = new AlertDialog.Builder(this)
//                .setView(newTextDialogView)
//                .setTitle(getString(R.string.new_text))
//                .setPositiveButton(getString(R.string.done), listener)
//                .setNegativeButton(getString(R.string.cancel), listener)
//                .setCancelable(true)
//                .create();
//
//        newTextDialog.show();
//    }
//    private void deactivateButtons(boolean deactivate) {
//        if (moveUpButton != null &&
//                moveDownButton != null &&
//                moveLeftButton != null &&
//                moveRightButton != null) {
//            if (deactivate) {
//                moveUpButton.setVisibility(View.GONE);
//                moveDownButton.setVisibility(View.GONE);
//                moveLeftButton.setVisibility(View.GONE);
//                moveRightButton.setVisibility(View.GONE);
//            } else {
//                moveUpButton.setVisibility(View.VISIBLE);
//                moveDownButton.setVisibility(View.VISIBLE);
//                moveLeftButton.setVisibility(View.VISIBLE);
//                moveRightButton.setVisibility(View.VISIBLE);
//            }
//
//        }
//        if (toolsContainerFragment != null){
//            toolsContainerFragment.deactivateButtons(deactivate);
//        }
//
//    }
//
////    }
//
//    private void moveRight() {
//        selectedLayer.getTextItem().moveRight();
//        selectedLayer.updateTextView();
//    }
//
//    private void moveLeft() {
//        selectedLayer.getTextItem().moveLeft();
//        selectedLayer.updateTextView();
//    }
//
//    private void moveDown() {
//        selectedLayer.getTextItem().moveDown();
//        selectedLayer.updateTextView();
//    }
//
//    private void moveUp() {
//        selectedLayer.getTextItem().moveUp();
//        selectedLayer.updateTextView();
//    }
//
//    @Override
//    public void onFontItemSelected(FontItem item) {
//        Log.e(getClass().getSimpleName(), "Called");
//        if (selectedLayer != null) {
//            selectedLayer.getTextItem().setFont(item);
//            selectedLayer.updateTextView();
//        }
//    }
//}
