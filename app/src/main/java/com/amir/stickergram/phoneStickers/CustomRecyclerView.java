package com.amir.stickergram.phoneStickers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class CustomRecyclerView extends RecyclerView {


    private RecyclerViewMovementCallbacks listener;

    public CustomRecyclerView(Context context) {
        super(context);
        prepRecyclerView(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        prepRecyclerView(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        prepRecyclerView(context);
    }

    void prepRecyclerView(Context context) {
        try {
            this.listener = (RecyclerViewMovementCallbacks) context;
        } catch (ClassCastException e) {
            throw new RuntimeException(context.getPackageName() + " you must implement  RecyclerViewMovementCallbacks");
        }

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy == 0) return;
                if (dy < 0) listener.onSlideDownCallback();
                else listener.onSlideUpCallback();
            }
        });
    }

    public interface RecyclerViewMovementCallbacks {
        void onSlideUpCallback();

        void onSlideDownCallback();
    }
}
