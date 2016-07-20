package com.amir.stickergram.sticker.pack.template;

import com.amir.stickergram.serverHelper.ServerSticker;

public class PackItem extends ServerSticker {
    private int position;

    PackItem(ServerSticker serverSticker, int position) {
        super(serverSticker.getNum(),
                serverSticker.getEnName(),
                serverSticker.getPerName(),
                serverSticker.getMode(),
                serverSticker.getHasLink(),
                serverSticker.getLinkNameEn(),
                serverSticker.getLinkNamePer(),
                serverSticker.getLink());
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
