package com.amir.stickergram.mode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Loader;

import java.util.ArrayList;

public class ModeListAdapter extends BaseAdapter implements View.OnClickListener {
    private LayoutInflater inflater;
    private ArrayList<Mode> modesList;
    private BaseActivity activity;

    public ModeListAdapter(BaseActivity activity) {
        this.activity = activity;
        inflater = activity.getLayoutInflater();
        modesList = Loader.getAllAvailableModes(activity);
    }

    @Override
    public int getCount() {
        return modesList.size();
    }

    @Override
    public Object getItem(int i) {
        return modesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = inflater.inflate(R.layout.mode_item, viewGroup, false);
        Mode mode = modesList.get(i);
        ((TextView) view.findViewById(R.id.mode_item_text)).setText(mode.getName());
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.mode_item_radio_button);
        String chosenModePack = BaseActivity.chosenMode.getPack();

//        if () {
        if (chosenModePack != null && mode.getPack() != null && chosenModePack.equals(mode.getPack())) {
            radioButton.setChecked(true);
        } else radioButton.setChecked(false);
//        }
        view.setTag(mode);
        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() instanceof Mode) {
            Mode tempMode = (Mode) view.getTag();
            activity.setDefaultMode(tempMode);
            Toast.makeText(activity, activity.getString(R.string.mode_was_changed), Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();

        }
    }
}
