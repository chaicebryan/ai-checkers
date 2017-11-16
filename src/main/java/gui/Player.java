package main.java.gui;

public class Player {

    private PieceType color;
    private boolean isHuman;

    public Player(PieceType color, boolean isHuman) {
        this.color = color;
        this.isHuman = isHuman;
    }

    public PieceType getColor() {
         return color;
    }

    public boolean isHuman() {
        return isHuman;
    }
}
