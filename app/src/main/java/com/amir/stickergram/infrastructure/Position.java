package com.amir.stickergram.infrastructure;

public class Position {
    private float top;
    private float left;

    public Position(float top, float left) {
        this.top = top;
        this.left = left;
    }

    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public void set(float left, float top) {
        this.left = left;
        this.top = top;

    }
}
