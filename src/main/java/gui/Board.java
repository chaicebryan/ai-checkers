package main.java.gui;

import java.util.ArrayList;
import java.util.HashMap;

import main.java.impl.Position;

public class Board {

    public static int HEIGHT = 8;
    public static int WIDTH = 8;

    private Tile[][] state;

    public Board() {
        state = new Tile[HEIGHT][WIDTH];
    }

    public Tile[][] getState() {
        return state;
    }

    public void attemptMove(Piece piece, Position newPos, Player player) {
        if (moveIsValid(piece, newPos, player)) {
            System.out.println("valid");

            // Update tile content
            // Remove piece from old position
            this.tileAt(newPos).setPiece(piece);
            this.removePieceAt(piece.getPosition());

            // Move piece to new position
            piece.updatePositionTo(newPos);
            piece.relocate(newPos.getX() * Piece.WIDTH, newPos.getY() * Piece.HEIGHT);
        } else {
            // Snap back to original position
            piece.relocate(piece.getPosition().getX() * Piece.WIDTH, piece.getPosition().getY() * Piece.HEIGHT);
        }
    }

    private boolean moveIsValid(Piece piece, Position newPos, Player player) {
        System.out.println("Old position: " + piece.getPosition().getX() + ", " + piece.getPosition().getY());
        System.out.println("New position: " + newPos.getX() + ", " + newPos.getY());


        // Don't let pieces go off the board
        // This should come first to prevent any exceptions on further board operations
        if (outOfBounds(newPos) || placedOnWrongColour(newPos)) {
            System.out.println("bad");
            return false;
        }
        
        if (nonDiagonalMove(piece.getPosition(), newPos)) {
            return false;
        }

        if (tileAlreadyOccupied(newPos)) {
            return false;
        }

        if (!directionIsValid(piece, newPos, player)) {
            return false;
        }

        return true;
    }

    private boolean outOfBounds(Position newPos) {
        if (newPos.getX() > WIDTH-1 ||
                newPos.getY() > HEIGHT-1 ||
                newPos.getX() < 0 ||
                newPos.getY() < 0 ) {
            return true;
        }
        return false;
    }

    private boolean placedOnWrongColour(Position newPos) {
        return tileAt(newPos).getType()  == TileType.YELLOW;
    }

    private boolean nonDiagonalMove(Position oldPos, Position newPos) {
        return newPos.getX() == oldPos.getX() || newPos.getY() == oldPos.getY();
    }

    private boolean tileAlreadyOccupied(Position newPos) {
        return this.tileAt(newPos).hasPiece();
    }

    private boolean directionIsValid(Piece piece, Position newPos, Player player) {
        System.out.println("reached");
        if (!piece.isKing()) {
            if (player.getSide() == BoardSide.BOTTOM) {
                    return newPos.getY() <= piece.getPosition().getY();
            } else {
                    return newPos.getY() >= piece.getPosition().getY();
            }
        }
        return true;
    }

    private boolean pieceAtEdge(Piece piece) {
        Position pos = piece.getPosition();
        return pos.getX() != 0 && pos.getX() != WIDTH-1;
    }

    private Tile tileAt(Position pos) {
        return state[pos.getX()][pos.getY()];
    }

    private void removePieceAt(Position pos) {
        tileAt(pos).removePiece();
    }
}
