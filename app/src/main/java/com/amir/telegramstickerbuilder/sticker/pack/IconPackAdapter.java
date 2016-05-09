package com.amir.telegramstickerbuilder.sticker.pack;

import android.content.res.AssetManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amir.telegramstickerbuilder.R;
import com.amir.telegramstickerbuilder.base.BaseActivity;
import com.amir.telegramstickerbuilder.base.BaseFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IconPackAdapter extends RecyclerView.Adapter<IconPackViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private final BaseFragment fragment;
    private final OnStickerClickListener listener;
    private final LayoutInflater inflater;
    private final int type;
    private String folder;
    public List<String> items;
    public PackItem lastLongClickedItem;

    public IconPackAdapter(BaseFragment fragment, OnStickerClickListener listener, String folder, int type) {
        this.fragment = fragment;
        this.listener = listener;
        this.folder = folder;
        this.inflater = fragment.getLayoutInflater(null);
        items = new ArrayList<>();
        this.type = type;
    }


    @Override
    public IconPackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_pack_sticker, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new IconPackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IconPackViewHolder holder, int position) {
        Log.e(getClass().getSimpleName(), "position: " + position);
        holder.populate(new PackItem(
                fragment.getContext(),
                folder,
                String.valueOf(position)/*as the name of the file */,
                type));
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
            Log.e(getClass().getSimpleName(), "OnLongClick: " + lastLongClickedItem.getDir());
            listener.OnLongClicked(lastLongClickedItem);
            return true;
        }

        return false;
    }

    public void itemRemoved(String dir) {
        lastLongClickedItem.removeThumb();

        int i = items.indexOf(dir);
        Log.e(getClass().getSimpleName(), "i: " + i);
        items.remove(i);
        notifyItemRemoved(i);
        renameAllFilesAfterThisPosition(i);
//        Log.e(getClass().getSimpleName(), "i after: " + i);
//        notifyItemRangeChanged(0,items.size());
        refresh();
//        notifyItemRange();
    }

    private void renameAllFilesAfterThisPosition(int i) {
        int length = items.size();
        while (i < length) {
            String currentName = BaseActivity.USER_STICKERS_DIRECTORY + folder + File.separator + (i + 1) + ".png";
            File file = new File(currentName);
            Log.e(getClass().getSimpleName(), "from: " + file.getAbsolutePath());
            if (file.exists()) {
                String newName = BaseActivity.USER_STICKERS_DIRECTORY + folder + File.separator + i + ".png";
                File temp = new File(newName);
//                items.set(items.indexOf(currentName), newName);
                notifyItemChanged(i);
                Log.e(getClass().getSimpleName(), "to: " + temp.getAbsolutePath());
                file.renameTo(temp);
            }
            File thumbFile = new File(BaseActivity.BASE_THUMBNAIL_DIRECTORY + File.separator + folder + "_" + (i + 1) + ".png");
            if (thumbFile.exists())
                thumbFile.renameTo(new File(BaseActivity.BASE_THUMBNAIL_DIRECTORY + File.separator + folder + "_" + i + ".png"));
            i++;
        }
    }

    public interface OnStickerClickListener {
        void OnIconClicked(PackItem item);

        void OnLongClicked(PackItem item);
    }

    public void refresh() {
        if (type == PackItem.TYPE_TEMPLATE) {
            try {
                AssetManager assets = fragment.getActivity().getAssets();
                String files[] = assets.list("Stickers/" + folder);
                for (String file : files) {
                    items.add(folder + File.separator + file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type == PackItem.TYPE_USER) {
            File folder = new File(BaseActivity.USER_STICKERS_DIRECTORY + this.folder + File.separator);
//            Log.e(getClass().getSimpleName(), BaseActivity.USER_STICKERS_DIRECTORY + this.folder + File.separator);
            if (folder.exists()) {
                if (folder.isDirectory()) {
                    items = null;
                    items = new ArrayList<>();
                    File[] files = folder.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        items.add(BaseActivity.USER_STICKERS_DIRECTORY + this.folder + File.separator + i + ".png");
                    }
//                    for (File file : files) {
//                        items.add(file.getAbsolutePath());
////                        Log.e(getClass().getSimpleName(), file.getAbsolutePath());
//                    }
//                    Collections.sort(items);
                } else
                    Log.e(getClass().getSimpleName(), folder.getAbsolutePath() + "was not a directory");
            } else Log.e(getClass().getSimpleName(), folder.getAbsolutePath() + "didn't exist");
        }
//        Collections.sort(items);
        for (String item : items)
            Log.e(getClass().getSimpleName(), item);
    }

    public void notifyItemRange() {
        notifyItemRangeChanged(0, items.size());
    }
}

