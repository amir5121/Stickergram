package com.amir.stickergram.infrastructure;

public class TextArea {
    private Position startPosition;
    private float width;
    private float height;

    public TextArea(Position startPosition, float width, float height) {
        this.height = height;
        this.startPosition = new Position(startPosition.getTop(), startPosition.getLeft());
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public float getWidth() {
        return width;
    }

}
