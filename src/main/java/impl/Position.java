package main.java.impl;

public class Position {

    // x position
    // y position
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // return x position
    public int getX() {
        return x;
    }

    // return y position
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "[" + x + " " + y + "]";
    }
}
