package main.java.impl;

import main.java.gui.Piece;

public class Take extends Move {

    private Piece target;

    public Take(Position origin, Position dest, Piece taker, Piece target) {
        super(origin, dest, taker);
        this.target = target;
    }

    public Piece getTarget() {
        return target;
    }
}
