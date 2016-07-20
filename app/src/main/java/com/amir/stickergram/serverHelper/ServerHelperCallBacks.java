package com.amir.stickergram.serverHelper;

import java.util.ArrayList;

public interface ServerHelperCallBacks {
    void onServerStickerListReceived(ArrayList<ServerSticker> list);

    void onDismissRefresh(boolean failed);
}
