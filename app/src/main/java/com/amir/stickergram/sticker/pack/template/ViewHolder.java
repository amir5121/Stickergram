package com.amir.stickergram.sticker.pack.template;

import android.graphics.Bitmap;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.serverHelper.VolleySingleton;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView iconImageView;
    private View progressView;
    private View errorImage;


    public ViewHolder(View itemView) {
        super(itemView);
        iconImageView = itemView.findViewById(R.id.item_pack_sticker_image);
        progressView = itemView.findViewById(R.id.item_pack_sticker_progress_container);
        errorImage = itemView.findViewById(R.id.item_pack_sticker_error);
    }

    void populate(PackItem item) {
        itemView.setTag(item);
        progressView.setVisibility(View.VISIBLE);
        ImageLoader imageLoader = VolleySingleton.getInstance().getImageLoader();
        String url = Constants.STICKERGRAM_URL + Constants.CACHE + item.getEnName() + "/" + item.getPosition() + Constants.PNG;
        final String dir = BaseActivity.Companion.getCACHE_DIR() + item.getEnName() + "/" + item.getPosition() + Constants.PNG;
        iconImageView.setVisibility(View.INVISIBLE);
        iconImageView.setImageBitmap(null);
        errorImage.setVisibility(View.GONE);
        Bitmap bitmap = Loader.getCachedImage(dir);
        if (bitmap == null) {
            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap mBitmap = response.getBitmap();
                    if (mBitmap != null) {
                        iconImageView.setImageBitmap(mBitmap);
                        iconImageView.setVisibility(View.VISIBLE);
                        Loader.cacheImage(mBitmap, dir);
                        progressView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    errorImage.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
                }
            });
        } else {
            iconImageView.setImageBitmap(bitmap);
            iconImageView.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.GONE);
        }

    }

}
