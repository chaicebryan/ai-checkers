package gui;

public class Board {

    public static int HEIGHT = 8;
    public static int WIDTH = 8;

    private Tile[][] state;

    public Board() {
        state = new Tile[HEIGHT][WIDTH];
    }
}
