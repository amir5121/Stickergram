package com.amir.stickergram.sticker.icon.template;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amir.stickergram.R;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.stickergram.serverHelper.VolleySingleton;
import com.amir.stickergram.sticker.icon.IconItem;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView iconImageView;
    private TextView iconNameTextView;
    private View progressView;
    private View errorText;


    public ViewHolder(View itemView, AssetManager assets) {
        super(itemView);
        iconImageView = (ImageView) itemView.findViewById(R.id.template_sticker_icon_item_stickerItem);
        progressView = itemView.findViewById(R.id.item_icon_sticker_loading_container);
        errorText = itemView.findViewById(R.id.item_icon_sticker_error_text);
        iconNameTextView = (TextView) itemView.findViewById(R.id.template_sticker_icon_item_stickerName);
        if (Loader.deviceLanguageIsPersian())
            iconNameTextView.setTypeface(Typeface.createFromAsset(assets, Constants.APPLICATION_PERSIAN_FONT_ADDRESS_IN_ASSET));
        else
            iconNameTextView.setTypeface(Typeface.createFromAsset(assets, Constants.APPLICATION_ENGLISH_FONT_ADDRESS_IN_ASSET));

    }

    void populate(final IconItem item) {
        itemView.setTag(item);
        iconNameTextView.setText(item.getName());
        progressView.setVisibility(View.VISIBLE);
        iconImageView.setVisibility(View.INVISIBLE);
        iconImageView.setImageBitmap(null);
        final String url = Constants.STICKERGRAM_URL + Constants.CACHE + item.getEnName() + "/" + 5 + Constants.PNG;
        final String dir = BaseActivity.CACHE_DIR + item.getEnName() + "/" + 5 + Constants.PNG;
        Bitmap bitmap = Loader.getCachedImage(dir);

        //todo: http://stackoverflow.com/questions/23978828/how-do-i-use-disk-caching-in-picasso

        if (bitmap == null) {
            VolleySingleton volleySingleton = VolleySingleton.getInstance();
//            ImageLoader imageLoader = volleySingleton.getImageLoader();

            ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    if (response != null) {
                        iconImageView.setImageBitmap(response);
                        iconImageView.setVisibility(View.VISIBLE);
                        progressView.setVisibility(View.GONE);
                        Loader.cacheImage(response, dir);
                    }
                }
            }, 0, 0, null, null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorText.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
                }

            });
            volleySingleton.addToRequestQueue(imageRequest);

//            imageLoader.get(url, new ImageLoader.ImageListener() {
//                @Override
//                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                    Bitmap mBitmap = response.getBitmap();
//                    if (mBitmap != null) {
//
////                        if (response.getRequestUrl().contains(item.getEnName())) {
////                            Log.e("icon.templat.ViewHolder", "response url: " + response.getRequestUrl() + " url: " + url);
//                        iconImageView.setImageBitmap(mBitmap);
//                        iconImageView.setVisibility(View.VISIBLE);
//                        Loader.cacheImage(mBitmap, dir);
//                        progressView.setVisibility(View.GONE);
////                        }
//                    }
//                }
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    errorText.setVisibility(View.VISIBLE);
//                    progressView.setVisibility(View.GONE);
//                }
//            });
        } else {
            iconImageView.setVisibility(View.VISIBLE);
            iconImageView.setImageBitmap(bitmap);
            progressView.setVisibility(View.GONE);
        }
    }

}
