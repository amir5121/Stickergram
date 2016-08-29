package com.amir.stickergram.arcList;

public class ScrollEvent {
    private int radius;
    private boolean unregister = false;
    private int level;

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean getUnregistred() {
        return unregister;
    }

    public void setUnregister(boolean unregister) {
        this.unregister = unregister;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
