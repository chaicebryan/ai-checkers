package main.java.gui;

import main.java.impl.Position;

// The Board class is a state representation of the board that contains a number of operations for editing this state
public class Board {

    // Number of squares on each side (HEIGHT * WIDTH)
    public static int HEIGHT = 8;
    public static int WIDTH = 8;

    // Board is represented using a 2D array of Tile objects
    // Some tiles will contain pieces
    private Tile[][] state;

    public Board() {
        state = new Tile[HEIGHT][WIDTH];
    }

    public Tile[][] getState() {
        return state;
    }

    // Attempts a move from the pieces original position to a specified new position
    // If the move is valid and was made then this method returns true so that we can end the players turn
    public boolean attemptMove(Piece piece, Position newPos, Player player) {
        if (moveIsValid(piece, newPos, player)) {

            // Update tile content
            // Remove piece from old position
            this.tileAt(newPos).setPiece(piece);
            this.removePieceAt(piece.getPosition());

            // Move piece to new position
            piece.updatePositionTo(newPos);
            piece.relocate(newPos.getX() * Piece.WIDTH, newPos.getY() * Piece.HEIGHT);

            return true;
        } else {
            // Snap back to original position
            piece.relocate(piece.getPosition().getX() * Piece.WIDTH, piece.getPosition().getY() * Piece.HEIGHT);
            return false;
        }
    }

    // Carries out a number of checks to determine if the attempted move is valid
    // It does this by considering the piece's current position and a specified new position
    // It also checks that this move is valid for the player, depending on which side they are on, on the board
    private boolean moveIsValid(Piece piece, Position newPos, Player player) {
        // Don't let pieces go off the board
        // This should come first to prevent any exceptions on further board operations
        if (outOfBounds(newPos) || placedOnWrongColour(newPos)) {
            System.out.println("bad");
            return false;
        }

        // Only diagonal moves are allowed for any move to be valid
        if (nonDiagonalMove(piece.getPosition(), newPos)) {
            return false;
        }

        // Can't move a piece to a tile that already contains a piece
        if (tileAlreadyOccupied(newPos)) {
            return false;
        }

        // Players at the bottom of the board can only move up and vice versa
        // But exceptions are made for king pieces
        if (!directionIsValid(piece, newPos, player)) {
            return false;
        }

        return true;
    }

    // Stops pieces from moving off of the board
    private boolean outOfBounds(Position newPos) {
        if (newPos.getX() > WIDTH-1 ||
                newPos.getY() > HEIGHT-1 ||
                newPos.getX() < 0 ||
                newPos.getY() < 0 ) {
            return true;
        }
        return false;
    }

    // Enforces the rule that only Brown (or Black) squares may be occupied
    private boolean placedOnWrongColour(Position newPos) {
        return tileAt(newPos).getType()  == TileType.YELLOW;
    }

    // Determines if a move is diagonal or not
    private boolean nonDiagonalMove(Position oldPos, Position newPos) {
        return newPos.getX() == oldPos.getX() || newPos.getY() == oldPos.getY();
    }

    // Returns whether or not a given tile already has a piece
    private boolean tileAlreadyOccupied(Position newPos) {
        return this.tileAt(newPos).hasPiece();
    }

    // Determines whether or not the direction a piece is being moved is valid given a number of factors
    // What side of the board does the piece belong to
    // Whether or not the piece is a king or not
    private boolean directionIsValid(Piece piece, Position newPos, Player player) {
        if (!piece.isKing()) {
            if (piece.getBoardSide() == player.getSide() && BoardSide.BOTTOM == player.getSide()) {
                if (newPos.getY() >= piece.getPosition().getY()) {
                    return false;
                }
            } else if (piece.getBoardSide() == BoardSide.TOP && piece.getBoardSide() == player.getSide()){
                if (newPos.getY() <= piece.getPosition().getY()) {
                    return false;
                }
            } else if (piece.getBoardSide() != player.getSide()) {
                return false;
            }
        }
        return true;
    }

    // Determines whether or not a piece is at the edge of the board
    private boolean pieceAtEdge(Piece piece) {
        Position pos = piece.getPosition();
        return pos.getX() != 0 && pos.getX() != WIDTH-1;
    }

    // Returns the tile at a specified position
    private Tile tileAt(Position pos) {
        return state[pos.getX()][pos.getY()];
    }

    // removes a piece from a tile at a specified position
    private void removePieceAt(Position pos) {
        tileAt(pos).removePiece();
    }
}
