package main.java.impl;

import main.java.gui.Piece;

public class Move {

    private Position dest;
    private Piece piece;

    public Move(Piece piece, Position dest) {
        this.dest = dest;
        this.piece = piece;
    }

    public Position getOrigin() {
        return piece.getPosition();
    }

    public Position getDest() {
        return dest;
    }

    public Piece getPiece() {
        return piece;
    }
}
