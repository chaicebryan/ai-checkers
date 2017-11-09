package main.java.impl;

import main.java.gui.Piece;

public class Move {

    private Position origin;
    private Position dest;
    private Piece piece;

    public Move(Position origin, Position dest, Piece piece) {
        this.origin = origin;
        this.dest = dest;
        this.piece = piece;
    }
}
