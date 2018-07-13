package com.amir.stickergram.serverHelper;

import android.util.Log;

import com.amir.stickergram.base.BaseActivity;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class AndroidHiveServer {
    private static final String TAG = "AndroidHiveServer";
    private ServerHelperCallBacks listener;
    private BaseActivity activity;

    public AndroidHiveServer(BaseActivity activity, ServerHelperCallBacks listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void updateStickerList(String url, boolean shouldInvalidate) {
        if (shouldInvalidate) {
            getStringFromURL(url);
            return;
        }

        try {
            Cache cache = VolleySingleton.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(url);
            if (entry != null) {
                String data = new String(entry.data, "UTF-8");
                createServerSticker(new JSONArray(data));
            } else if (activity.getCachedJson() != null) {
                createServerSticker(new JSONArray(activity.getCachedJson()));
            } else {
                Log.e(TAG, "cache was null");
                getStringFromURL(url);
            }
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

    private void getStringFromURL(String url) {
        // Tag used to cancel the request
        String tag_string_req = "string_req";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    createServerSticker(new JSONArray(response));
                    activity.cacheJsonResponse(response);
                    listener.onDismissRefresh(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onDismissRefresh(true);
            }
        });

        // Adding request to request queue
        VolleySingleton.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public void createServerSticker(JSONArray jsonArray) {
        int length = jsonArray.length();
        ArrayList<ServerSticker> names = new ArrayList<>(length);
        Log.e(getClass().getSimpleName(), "createServerSticker");
        for (int i = 0; i < length; i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Log.e(TAG, "createServerSticker: " + jsonObject.get(ServerSticker.EN_NAME));
                names.add(new ServerSticker(
                        Integer.parseInt((String) jsonObject.get(ServerSticker.NUM)),
                        (String) jsonObject.get(ServerSticker.EN_NAME),
                        (String) jsonObject.get(ServerSticker.PER_NAME),
                        Integer.parseInt((String) jsonObject.get(ServerSticker.MODE)),
                        (jsonObject.get(ServerSticker.HAS_LINK)).equals("1"),
                        (String) jsonObject.get(ServerSticker.LINK_NAME_EN),
                        (String) jsonObject.get(ServerSticker.LINK_NAME_PER),
                        (String) jsonObject.get(ServerSticker.LINK),
                        (String) jsonObject.get(ServerSticker.RUS_NAME)
                ));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        listener.onServerStickerListReceived(names);
        listener.onDismissRefresh(false);

    }
}
