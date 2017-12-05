package main.java.impl;

import main.java.gui.Piece;

// This class extends move but also contains a reference to a target piece that will be taken
// and removed from the board
public class Take extends Move {

    // The piece that will be taken
    private Piece target;

    public Take(Piece taker, Position dest, Piece target) {
        super(taker, dest);
        this.target = target;
    }

    // return the target piece
    public Piece getTarget() {
        return target;
    }

    @Override
    public boolean moveCausedKing() {
        return target.isKing() || dest.getY() == 0 || dest.getY() == Board.HEIGHT-1;
    }

    @Override
    public int hashCode() {
        return 41 * (41 + getOrigin().getX() + dest.getX() + getOrigin().getY() + dest.getY());
    }

    @Override
    public String toString() {
        return "[" + getOrigin().toString() + ", " + getDest().toString() + ", Attacker: " + piece.toString() + ", Victim: " + getTarget().toString() + "]";
    }
}
