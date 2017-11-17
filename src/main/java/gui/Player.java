package main.java.gui;

// Player is a player within the game.
// A player has an associated colour and side and is used to enable two player logic
public class Player {

    // The colour of pieces that the player controls
    private PieceType color;

    // Is the player human or AI
    private boolean isHuman;

    // What side of the board is the player starting from
    private BoardSide side;

    public Player(PieceType color, boolean isHuman, BoardSide side) {
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

    public void setSide(BoardSide side) {
        this.side = side;
    }

    public BoardSide getSide() {
        return side;
    }
}
