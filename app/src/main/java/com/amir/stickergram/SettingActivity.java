package com.amir.stickergram;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.amir.stickergram.arcList.ArcScrollView;
import com.amir.stickergram.arcList.VerticalArcContainer;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.mode.ModeListAdapter;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.mode.Mode;
import com.amir.stickergram.navdrawer.MainNavDrawer;
import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableWeightLayout;

import java.util.ArrayList;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SettingsActivity";
    private ExpandableWeightLayout mExpandLayout;
    private View includeModes;
    private View includeLanguages;
    private int visibleItem;
    private Button languageButton;
    private Button modeButton;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setNavDrawer(new MainNavDrawer(this));


        languageButton = (Button) findViewById(R.id.activity_setting_language);
        modeButton = (Button) findViewById(R.id.activity_setting_mode);
        mExpandLayout = (ExpandableWeightLayout) findViewById(R.id.activity_setting_expandable_layout);

        if (Loader.INSTANCE.getAllAvailableModes(this).size() <= 1) {
            modeButton.setVisibility(View.GONE);
        }

        includeModes = getLayoutInflater().inflate(R.layout.include_supported_modes, null, false);
        includeLanguages = getLayoutInflater().inflate(R.layout.include_language, null, false);
        setFont((ViewGroup) includeLanguages);
        setFont((ViewGroup) includeModes);

        View persianLanguageContainer = includeLanguages.findViewById(R.id.activity_setting_persian_language_container);
        View englishLanguageContainer = includeLanguages.findViewById(R.id.activity_setting_english_language_container);
        includeLanguages.findViewById(R.id.activity_setting_russian_language_container).setOnClickListener(this);
        includeLanguages.findViewById(R.id.activity_setting_german_language_container).setOnClickListener(this);

        englishLanguageContainer.setOnClickListener(this);
        persianLanguageContainer.setOnClickListener(this);
        languageButton.setOnClickListener(this);
        modeButton.setOnClickListener(this);
        instantiateListOfAvailableModes();

        manageRadioButton();

        setFont((ViewGroup) findViewById(R.id.nav_drawer));
        setFont((ViewGroup) findViewById(R.id.activity_setting_main_container));

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.activity_setting_language:
                manageView(includeLanguages);
                break;
            case R.id.activity_setting_mode:
                manageView(includeModes);
                break;
            case R.id.activity_setting_persian_language_container:
                setLanguage(Constants.PERSIAN_LANGUAGE);
                restartActivity();
                break;
            case R.id.activity_setting_english_language_container:
                setLanguage(Constants.ENGLISH_LANGUAGE);
                restartActivity();
                break;
            case R.id.activity_setting_russian_language_container:
                setLanguage(Constants.RUSSIAN_LANGUAGE);
                restartActivity();
                break;
            case R.id.activity_setting_german_language_container:
                setLanguage(Constants.GERMAN_LANGUAGE);
                restartActivity();
                break;
        }
    }

    private void manageView(final View view) {
        final int currentViewId = view.getId();
        if (!mExpandLayout.isExpanded()) {
//            isFirstTap = false;
            mExpandLayout.removeAllViews();
            mExpandLayout.addView(view);
        }
        if (mExpandLayout.isExpanded())
            mExpandLayout.collapse();
        else mExpandLayout.expand();

        mExpandLayout.setListener(new ExpandableLayoutListener() {
            @Override
            public void onAnimationStart() {
                languageButton.setEnabled(false);
                modeButton.setEnabled(false);
            }

            @Override
            public void onAnimationEnd() {
                if (currentViewId != visibleItem) {
                    visibleItem = currentViewId;
                    mExpandLayout.removeAllViews();
                    mExpandLayout.addView(view);
                    if (!mExpandLayout.isExpanded())
                        mExpandLayout.expand();
                }
                languageButton.setEnabled(true);
                modeButton.setEnabled(true);
            }

            @Override
            public void onPreOpen() {

            }

            @Override
            public void onPreClose() {

            }

            @Override
            public void onOpened() {

            }

            @Override
            public void onClosed() {
                Log.e(getClass().getSimpleName(), "onClosed is called");
            }
        });
    }
//    @Override
//    public void onClick(final View v) {
//        switch (v.getId()) {
//            case R.id.activity_setting_language:
//                mExpandLayout.moveChild(0);
//                break;
//            case R.id.activity_setting_mode:
//                mExpandLayout.moveChild(1);
//                break;
//            case R.id.activity_setting_persian_language_container:
////                manageRadioButton();
//                setLanguage(Loader.PERSIAN_LANGUAGE);
//                restartActivity();
//                break;
//            case R.id.activity_setting_english_language_container:
////                manageRadioButton();
//                setLanguage(Loader.ENGLISH_LANGUAGE);
//                restartActivity();
//                break;
////            case R.id.activity_setting_system_language_container:
////                setLanguage(Loader.SYSTEM_LANGUAGE);
//////                manageRadioButton();
////                restartActivity();
////                break;
//        }
//    }

    private void manageRadioButton() {
        RadioButton persianLanguageRadioButton = includeLanguages.findViewById(R.id.activity_setting_persian_radio_button);
//        RadioButton systemLanguageRadioButton = (RadioButton) findViewById(R.id.activity_setting_system_radio_button);
        RadioButton englishLanguageRadioButton = includeLanguages.findViewById(R.id.activity_setting_english_radio_button);


        switch (getUserLanguage()) {
            case Constants.PERSIAN_LANGUAGE:
                persianLanguageRadioButton.setChecked(true);
                break;
            case Constants.ENGLISH_LANGUAGE:
                englishLanguageRadioButton.setChecked(true);
                break;
//            case Loader.SYSTEM_LANGUAGE:
//                systemLanguageRadioButton.setChecked(true);
//                break;
        }


    }


    private void instantiateListOfAvailableModes() {
        ListView listView = (ListView) includeModes.findViewById(R.id.activity_setting_modes_list);
        final ArrayList<Mode> modes = Loader.INSTANCE.getAllAvailableModes(this);
        ModeListAdapter adapter = new ModeListAdapter(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setDefaultMode(modes.get(i));

                Toast.makeText(SettingActivity.this, getString(R.string.mode_was_changed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    //        View butt1 = findViewById(R.id.butt1);
//        View butt2 = findViewById(R.id.butt2);
//
//        final View includeButtons = getLayoutInflater().inflate(R.layout.include_buttons, null);
//        final View include2 = getLayoutInflater().inflate(R.layout.include_pro_note, null);
//        include2.setVisibility(View.VISIBLE);
//
//        butt1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (expandableLayout.isExpanded()) {
//                    expandableLayout.collapse();
//                }
//                expandableLayout.removeAllViews();
//                expandableLayout.addView(includeButtons);
//                expandableLayout.expand();
//            }
//        });
//
//        butt2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (expandableLayout.isExpanded())
//                    expandableLayout.collapse();
//                expandableLayout.removeAllViews();
//                expandableLayout.addView(include2);
//                expandableLayout.expand();
////                expandableLayout2.toggle();
//            }
//        });
//
}

