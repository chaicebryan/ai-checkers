package main.java.impl;

// Player is a player within the game.
// A player has an associated colour and side and is used to enable two player logic
public class Player {

    // The colour of pieces that the player controls
    private PieceType color;

    // Is the player human or AI
    private boolean isHuman;

    // What side of the board is the player starting from
    private Side side;

    public Player(PieceType color, boolean isHuman, Side side) {
        this.color = color;
        this.isHuman = isHuman;
        this.side = side;
    }

    public PieceType getColor() {
         return color;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public void setClass(boolean isHuman) {
        this.isHuman = isHuman;
    }

    public Side getSide() {
        return side;
    }

    @Override
    public String toString() {
        return side.toString();
    }
}
