//package com.amir.telegramstickerbuilder.edit;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//
//import com.amir.telegramstickerbuilder.R;
//import com.amir.telegramstickerbuilder.base.BaseFragment;
//
//public class FirstEditFragment extends BaseFragment implements View.OnClickListener {
//
//    Button sizeButton;
//    Button tiltButton;
//    Button textButton;
//    Button fontButton;
////    Button styleButton;
//
//    ButtonsCallBack listener;
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_edit_buttons, container, false);
//
//        textButton = (Button) view.findViewById(R.id.activity_edit_image_text_button);
//        fontButton = (Button) view.findViewById(R.id.activity_edit_image_font_button);
//        sizeButton = (Button) view.findViewById(R.id.activity_edit_image_size_button);
////        styleButton = (Button) view.findViewById(R.id.activity_edit_image_style_button);
//        tiltButton = (Button) view.findViewById(R.id.activity_edit_image_tilt_button);
//        setUpListener();
//
//        return view;
//    }
//
//    @Override
//    public void onClick(View view) {
//        int itemId = view.getId();
//        if (itemId == R.id.activity_edit_image_text_button){
//            listener.editTextButtonClicked();
//        } else if (itemId == R.id.activity_edit_image_size_button){
//            listener.sizeButtonClicked();
//        } else if (itemId == R.id.activity_edit_image_tilt_button){
//            listener.tiltButtonClicked();
//        } else if (itemId == R.id.activity_edit_image_font_button){
//            listener.fontButtonClicked();
//        }
//    }
//
//    public void deactivateButtons(boolean deactivate) {
//        if (sizeButton != null &&
//                tiltButton != null &&
//                textButton != null &&
//                fontButton != null) {
//            sizeButton.setEnabled(!deactivate);
//            tiltButton.setEnabled(!deactivate);
//            textButton.setEnabled(!deactivate);
//            fontButton.setEnabled(!deactivate);
//        }
//    }
//
//    private void setUpListener(){
//        listener = (EditImageActivity2) getActivity();
//
//        if (sizeButton != null) sizeButton.setOnClickListener(this);
//        if (tiltButton != null) tiltButton.setOnClickListener(this);
//        if (textButton != null) textButton.setOnClickListener(this);
//        if (fontButton != null) fontButton.setOnClickListener(this);
//    }
//
//    public interface ButtonsCallBack{
//        void sizeButtonClicked();
//        void editTextButtonClicked();
//        void fontButtonClicked();
//        void tiltButtonClicked();
//    }
//}
