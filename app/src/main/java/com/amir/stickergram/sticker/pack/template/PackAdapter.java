package com.amir.stickergram.sticker.pack.template;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.serverHelper.AndroidHiveServer;
import com.amir.stickergram.serverHelper.ServerHelperCallBacks;
import com.amir.stickergram.serverHelper.ServerSticker;
import com.amir.stickergram.sticker.OnRefreshCallbacks;

import java.util.ArrayList;

public class PackAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener, ServerHelperCallBacks {
    private LayoutInflater inflater;
    private OnStickerClickListener stickerClickListener;
    private OnRefreshCallbacks refreshCallbacks;
    private AndroidHiveServer helper;
    private PackItem serverSticker;
    private String folder;

    PackAdapter(BaseFragment fragment, String folder) {
        try {
            this.stickerClickListener = (OnStickerClickListener) fragment;
            this.refreshCallbacks = (OnRefreshCallbacks) fragment;
        } catch (ClassCastException e) {
            throw new RuntimeException("must implement OnStickerClickListener");
        }
        this.inflater = fragment.getLayoutInflater(null);
        helper = new AndroidHiveServer((BaseActivity) fragment.getActivity(), this);
        this.folder = folder;
        updateItems();
        Log.e(getClass().getSimpleName(), "PackAdapter constructor was called");
    }

    protected void updateItems() {
        helper.updateStickerList(Constants.STICKERGRAM_URL + Constants.LIST_DIRECTORIES, false);
        Log.e(getClass().getSimpleName(), "updateItems");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_pack_sticker, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        serverSticker = new PackItem(serverSticker, position);
        holder.populate(serverSticker);
    }

    @Override
    public int getItemCount() {
        if (serverSticker == null)
            return 0;
        return serverSticker.getNum();
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() instanceof PackItem) {
            PackItem item = (PackItem) view.getTag();
            stickerClickListener.onStickerClicked(item);
        }
    }

    @Override
    public void onServerStickerListReceived(ArrayList<ServerSticker> list) {
        Log.e(getClass().getSimpleName(), "listIconReceived");
        for (ServerSticker item : list) {
            if (folder.equals(item.getEnName())) {
                serverSticker = new PackItem(item, 0);
                break;
            }
        }
        refreshCallbacks.OnRefreshFinished(false);
        notifyDataSetChanged();
    }

    @Override
    public void onDismissRefresh(boolean failed) {
        Log.e(getClass().getSimpleName(), "onDismissRefresh");
        refreshCallbacks.OnRefreshFinished(failed);
        notifyDataSetChanged();
    }


//    public String getFolder() {
//        return folder;
//    }

    public PackItem getServerSticker() {
//        Log.e(getClass().getSimpleName(), "getServerSticker: " + serverSticker.getLink());
        return serverSticker;
    }
}
