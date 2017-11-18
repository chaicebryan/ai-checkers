package main.java.impl;

import main.java.gui.Piece;
import main.java.gui.Player;

public class Take extends Move {

    private Piece target;

    public Take(Piece taker, Position dest, Piece target) {
        super(taker, dest);
        this.target = target;
    }

    public Piece getTarget() {
        return target;
    }
}
