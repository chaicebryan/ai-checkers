package main.java.gui;

public class Board {

    public static int HEIGHT = 8;
    public static int WIDTH = 8;

    private Piece[][] state;

    public Board() {
        state = new Piece[HEIGHT][WIDTH];
    }

    public Piece[][] getState() {
        return state;
    }
}
