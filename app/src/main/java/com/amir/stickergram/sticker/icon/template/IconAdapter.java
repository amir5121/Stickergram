package com.amir.stickergram.sticker.icon.template;

import androidx.recyclerview.widget.RecyclerView;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.AppType;
import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.serverHelper.AndroidHiveServer;
import com.amir.stickergram.serverHelper.ServerHelperCallBacks;
import com.amir.stickergram.serverHelper.ServerSticker;
import com.amir.stickergram.sticker.OnRefreshCallbacks;
import com.amir.stickergram.sticker.icon.IconItem;
import com.amir.stickergram.sticker.icon.OnIconSelectedListener;

import java.util.ArrayList;
import java.util.List;

class IconAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener, ServerHelperCallBacks {
    private final BaseActivity activity;
    private final OnIconSelectedListener iconSelectedListener;
    private final OnRefreshCallbacks refreshListener;
    private final LayoutInflater inflater;
    public List<ServerSticker> items;
    private AndroidHiveServer helper;

    IconAdapter(BaseActivity activity, BaseFragment fragment) {
        this.activity = activity;
        try {
            this.iconSelectedListener = (OnIconSelectedListener) fragment;
            this.refreshListener = (OnRefreshCallbacks) fragment;
        } catch (ClassCastException e) {
            throw new RuntimeException("must implement OnStickerClickListener and OnRefreshCallBack");
        }
        this.inflater = activity.getLayoutInflater();
        helper = new AndroidHiveServer(activity, this);
        updateItems(false);
//        Log.e(getClass().getSimpleName(), "iconAdapter constructor was called");
    }

    void updateItems(boolean shouldInvalidate) {
        helper.updateStickerList(Constants.STICKERGRAM_URL + Constants.LIST_DIRECTORIES, shouldInvalidate);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_icon_sticker, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view, activity.getAssets());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name;
        if (Loader.INSTANCE.deviceLanguageIsPersian())
            name = items.get(position).getPerName();
//        else if (Loader.deviceLanguageIsRussian())
//            name = items.get(position).getRuName();
        else name = items.get(position).getEnName();

        if (name != null) {
            holder.populate(new IconItem(name, items.get(position).getEnName(), BaseActivity.USER_STICKERS_DIRECTORY));
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (items != null) {
            size = items.size();
        }
        if (size == 0) iconSelectedListener.OnNoStickerWereFoundListener();
        return size;
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() instanceof IconItem) {
            IconItem item = (IconItem) view.getTag();
            iconSelectedListener.OnIconSelected(item);
        }
    }

    @Override
    public void onServerStickerListReceived(ArrayList<ServerSticker> list) {
//        Log.e(getClass().getSimpleName(), "listIconReceived");
//        items = list;
        items = new ArrayList<>();
        for (ServerSticker item : list) {
            if (item.getMode() == Constants.ALL_FLAVORS) {
                items.add(item);
            } else if (item.getMode() == AppType.FLAVOR) {
                items.add(item);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onDismissRefresh(boolean failed) {
        refreshListener.OnRefreshFinished(failed);
    }
}
