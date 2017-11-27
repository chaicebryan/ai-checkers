package main.java.impl;

import main.java.gui.Piece;

public class Move {

    protected Position dest;
    protected Piece piece;
    protected int statusCode;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;

        if (o instanceof Take) {
            Take take = (Take) move;
            return take.getOrigin().getX() == this.getOrigin().getX() &&
                    take.getDest().getX() == this.getDest().getX() &&
                    take.getOrigin().getY() == this.getOrigin().getY() &&
                    take.getDest().getY() == this.getDest().getY();
        }

        boolean result;
        result = move.getOrigin().getX() == this.getOrigin().getX() &&
                move.getDest().getX() == this.getDest().getX() &&
                move.getOrigin().getY() == this.getOrigin().getY() &&
                move.getDest().getY() == this.getDest().getY();

        return result;
    }

    public void setStatusCode(int code) {
        this.statusCode = code;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public int hashCode() {
        return 41 * (41 + getOrigin().getX() + dest.getX() + getOrigin().getY() + dest.getY());
    }

    @Override
    public String toString() {
        return piece.getPosition() + " to " + dest;
    }
}
