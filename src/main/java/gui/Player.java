package main.java.gui;

public class Player {

    private PieceType color;
    private boolean isHuman;
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
