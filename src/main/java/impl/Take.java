package main.java.impl;

import main.java.gui.Piece;
import main.java.gui.Player;

public class Take extends Move {

    private Piece target;

    public Take(Piece taker, Position dest, Player player, Piece target) {
        super(taker, dest, player);
        this.target = target;
    }

    public Piece getTarget() {
        return target;
    }
}
