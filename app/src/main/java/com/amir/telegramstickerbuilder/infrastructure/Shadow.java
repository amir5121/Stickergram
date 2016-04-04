package com.amir.telegramstickerbuilder.infrastructure;

public class Shadow {
    private int color;
    private int radius;
    private int dY;
    private int dX;

    public Shadow(int color, int dX, int dY, int radius) {
        this.color = color;
        this.dX = dX;
        this.dY = dY;
        this.radius = radius;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setdX(int dX) {
        this.dX = dX;
    }

    public void setdY(int dY) {
        this.dY = dY;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getColor() {
        return color;
    }

    public int getdX() {
        return dX;
    }

    public int getdY() {
        return dY;
    }

    public int getRadius() {
        return radius;
    }
}