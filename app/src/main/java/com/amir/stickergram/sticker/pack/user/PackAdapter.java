package com.amir.stickergram.sticker.pack.user;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.infrastructure.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PackAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private final BaseFragment fragment;
    private final OnStickerClickListener listener;
    private final LayoutInflater inflater;
    //    private final int type;
    private String folder;
    public List<String> items;
    public PackItem lastLongClickedItem;

    public PackAdapter(BaseFragment fragment, OnStickerClickListener listener, String folder) {
        this.fragment = fragment;
        this.listener = listener;
        this.folder = folder;
        this.inflater = fragment.getLayoutInflater(null);
        items = new ArrayList<>();
//        this.type = type;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_pack_sticker, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.populate(new PackItem(
                folder,
                String.valueOf(position)/*as the name of the file */));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() instanceof PackItem) {
            listener.OnIconClicked((PackItem) view.getTag());
        }

    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getTag() instanceof PackItem) {
            lastLongClickedItem = (PackItem) view.getTag();
            listener.OnLongClicked(lastLongClickedItem);
            return true;
        }

        return false;
    }

    public void itemRemoved(String dir) {
        lastLongClickedItem.removeThumb();

        int i = items.indexOf(dir);
        items.remove(i);
        notifyItemRemoved(i);
        renameAllFilesAfterThisPosition(i);
        refresh();
        if (items.isEmpty()) {
            File file = new File(BaseActivity.USER_STICKERS_DIRECTORY + folder);
            if (file.exists()) {
                file.delete();
                listener.folderDeleted();
            }
        }
    }

    private void renameAllFilesAfterThisPosition(int i) {
        int length = items.size();
        while (i < length) {
            String currentName = BaseActivity.USER_STICKERS_DIRECTORY + folder + File.separator + (i + 1) + Constants.PNG;
            File file = new File(currentName);
            if (file.exists()) {
                String newName = BaseActivity.USER_STICKERS_DIRECTORY + folder + File.separator + i + Constants.PNG;
                File temp = new File(newName);
                notifyItemChanged(i);
                file.renameTo(temp);
            }
            File thumbFile = new File(BaseActivity.BASE_THUMBNAIL_DIRECTORY + File.separator + folder + "_" + (i + 1) + Constants.PNG);
            if (thumbFile.exists())
                thumbFile.renameTo(new File(BaseActivity.BASE_THUMBNAIL_DIRECTORY + File.separator + folder + "_" + i + Constants.PNG));
            i++;
        }
    }

    public void refresh() {
        File folder = new File(BaseActivity.USER_STICKERS_DIRECTORY + this.folder + File.separator);
        if (folder.exists()) {
            if (folder.isDirectory()) {
                items = null;
                items = new ArrayList<>();
                File[] files = folder.listFiles();
                for (int i = 0; i < files.length; i++) { //lol ikr you are like why wouldn't list the files in that directory and i'm like cuz listing the file won't return to you a sorted list which is required in order for the delete function work properly
                    items.add(BaseActivity.USER_STICKERS_DIRECTORY + this.folder + File.separator + i + Constants.PNG);
                }
            } else
                Log.e(getClass().getSimpleName(), folder.getAbsolutePath() + "was not a directory");
        } else Log.e(getClass().getSimpleName(), folder.getAbsolutePath() + "didn't exist");
    }

    public void notifyItemRange() {
        notifyItemRangeChanged(0, items.size());
    }

}

