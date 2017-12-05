package main.java.impl;

import main.java.gui.Piece;

// This class represents a move on the board
// Moves contain:
// * The piece that is being moved
// * The origin of the piece (where it is moving from)
// * The destination the piece is moving to
// * A status code
public class Move {

    protected final Position dest;
    protected final Piece piece;
    protected final Position origin;
    protected int statusCode;

    public Move(Piece piece, Position dest) {
        this.dest = dest;
        this.piece = piece;
        origin = piece.getPosition();
    }

    // Return the position that the piece is moving from
    public Position getOrigin() {
        return origin;
    }

    // return the position that the piece is moving to
    public Position getDest() {
        return dest;
    }

    // Return the peices that is moving
    public Piece getPiece() {
        return piece;
    }

    // Return true if the move caused a king conversion
    public boolean moveCausedKing() {
        return dest.getY() == 0 || dest.getY() == Board.HEIGHT-1;
    }

    // Override equals to make it possible for moves to be equal to takes in some cases
    // This is useful when determining whether a users move is a take by taking a users
    // move and determining if a take exists that matches it
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

    // Set a status code for the move
    // This is what is used to display reasons as to why
    // a move was denied
    public void setStatusCode(int code) {
        this.statusCode = code;
    }

    // Return the status code for the move
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public int hashCode() {
        return 41 * (41 + getOrigin().getX() + dest.getX() + getOrigin().getY() + dest.getY());
    }

    @Override
    public String toString() {
        return origin + " to " + dest;
    }
}
