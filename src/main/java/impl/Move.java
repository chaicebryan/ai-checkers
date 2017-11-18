package main.java.impl;

import main.java.gui.Piece;
import main.java.gui.Player;

public class Move {

    private Position dest;
    private Piece piece;
    private Player player;

    public Move(Piece piece, Position dest, Player player) {
        this.dest = dest;
        this.piece = piece;
        this.player = player;
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

    public Player getPlayer() {
        return player;
    }
}
