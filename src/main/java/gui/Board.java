package main.java.gui;

import static main.java.gui.Tile.BL;
import static main.java.gui.Tile.BR;
import static main.java.gui.Tile.TL;
import static main.java.gui.Tile.TR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import main.java.impl.Move;
import main.java.impl.Position;
import main.java.impl.Take;

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
    public boolean attemptMove(Player player, Move move) {
        if (moveIsValid(player, move)) {
            // Update tile content
            // Remove piece from old position
            this.tileAt(move.getDest()).setPiece(move.getPiece());
            this.removePieceAt(move.getPiece().getPosition());

            // Move piece to new position
            move.getPiece().updatePositionTo(move.getDest());
            move.getPiece().relocate(move.getDest().getX() * Piece.WIDTH,move.getDest().getY() * Piece.HEIGHT);

            return true;
        } else {
            // Snap back to original position
            move.getPiece().relocate(move.getPiece().getPosition().getX() * Piece.WIDTH, move.getPiece().getPosition().getY() * Piece.HEIGHT);
            return false;
        }
    }

    public Optional<Take> attemptMove(Move move, List<Take> takes) {
        if (takes.contains(move)) {

            Take take = takes.get(takes.indexOf(move));
            // Update tile content
            // Remove piece from old position
            this.tileAt(move.getDest()).setPiece(move.getPiece());
            this.removePieceAt(move.getPiece().getPosition());

            if (take != null) {
                this.removePieceAt(take.getTarget().getPosition());
            }
            // Move piece to new position
            move.getPiece().updatePositionTo(move.getDest());
            this.tileAt(move.getDest()).setPiece(move.getPiece());
            move.getPiece().relocate(move.getDest().getX() * Piece.WIDTH, move.getDest().getY() * Piece.HEIGHT);
            return Optional.of(take);
        } else {
            move.getPiece().relocate(move.getPiece().getPosition().getX() * Piece.WIDTH, move.getPiece().getPosition().getY() * Piece.HEIGHT);
            return Optional.empty();
        }
    }

    public void printContents() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j< HEIGHT; j++) {
                System.out.println(state[i][j].toString());
            }
        }
    }

    // Carries out a number of checks to determine if the attempted move is valid
    // It does this by considering the piece's current position and a specified new position
    // It also checks that this move is valid for the player, depending on which side they are on, on the board
    private boolean moveIsValid(Player player, Move move) {
        // Don't let pieces go off the board
        // This should come first to prevent any exceptions on further board operations
        if (outOfBounds(move.getDest()) || placedOnWrongColour(move.getDest())) {
            System.out.println("bad");
            return false;
        }

        // Only diagonal moves are allowed for any move to be valid
        if (nonDiagonalMove(move.getOrigin(), move.getDest())) {
            return false;
        }

        // Can't move a piece to a tile that already contains a piece
        if (tileAlreadyOccupied(move.getDest())) {
            System.out.println(true);
            return false;
        }

        // Players at the bottom of the board can only move up and vice versa
        // But exceptions are made for king pieces
        if (!directionIsValid(player, move)) {
            return false;
        }

        return true;
    }

    public ArrayList<Take> findForceTakes(Piece piece) {
        Tile currentTile = tileAt(piece.getPosition());
        Map<String, Position> surroundingTiles = currentTile.getSurrounding();
        Side side = piece.getSide();
        boolean isKing = piece.isKing();
        ArrayList<Take> takes = new ArrayList<>();

        if (!isKing) {
            if (side == Side.BOTTOM) {
                if (potentialTake(surroundingTiles, TL)) {
                    getTakeIfAny(piece, surroundingTiles, TL).ifPresent(takes::add);
                }
                if (potentialTake(surroundingTiles, TR)) {
                    getTakeIfAny(piece, surroundingTiles, TR).ifPresent(takes::add);
                }
            } else {
                if (potentialTake(surroundingTiles, BL)) {
                    getTakeIfAny(piece, surroundingTiles, BL).ifPresent(takes::add);
                }
                if (potentialTake(surroundingTiles, BR)) {
                    getTakeIfAny(piece, surroundingTiles, BR).ifPresent(takes::add);
                }
            }
        } else {
            if (potentialTake(surroundingTiles, TL)) {
                getTakeIfAny(piece, surroundingTiles, TL).ifPresent(takes::add);
            }
            if (potentialTake(surroundingTiles, TR)) {
                getTakeIfAny(piece, surroundingTiles, TR).ifPresent(takes::add);
            }
            if (potentialTake(surroundingTiles, BL)) {
                getTakeIfAny(piece, surroundingTiles, BL).ifPresent(takes::add);
            }
            if (potentialTake(surroundingTiles, BR)) {
                getTakeIfAny(piece, surroundingTiles, BR).ifPresent(takes::add);
            }
        }
        return takes;
    }

    private Optional<Take> getTakeIfAny(Piece piece, Map<String, Position> surrounding, String nearby) {
        Piece potentialOpponent = tileAt(surrounding.get(nearby)).getPiece();
        if (potentialOpponent.getSide() != piece.getSide() && pieceIsVulnarable(potentialOpponent, nearby)) {
            return Optional.of(new Take(piece, tileAt(potentialOpponent.getPosition()).getSurrounding().get(nearby), potentialOpponent));
        }
        return Optional.empty();
    }

    private boolean potentialTake(Map<String, Position> surrounding, String nearBy) {
        return surrounding.get(nearBy) != null && tileAt(surrounding.get(nearBy)).hasPiece();
    }

    private boolean pieceIsVulnarable(Piece potentialOpponent, String near) {
        return tileAt(potentialOpponent.getPosition()).getSurrounding().get(near) != null &&
                !tileAt(tileAt(potentialOpponent.getPosition()).getSurrounding().get(near)).hasPiece();
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
    private boolean directionIsValid(Player player, Move move) {
        Piece piece = move.getPiece();

        if (!move.getPiece().isKing()) {
            if (piece.getSide() == player.getSide() && Side.BOTTOM == player.getSide()) {
                if (move.getDest().getY() >= piece.getPosition().getY()) {
                    return false;
                }
            } else if (piece.getSide() == Side.TOP && piece.getSide() == player.getSide()){
                if (move.getDest().getY() <= piece.getPosition().getY()) {
                    return false;
                }
            } else if (piece.getSide() != player.getSide()) {
                return false;
            }
        }
        return true;
    }

    // Returns the tile at a specified position
    public Tile tileAt(Position pos) {
        return state[pos.getX()][pos.getY()];
    }

    // removes a piece from a tile at a specified position
    private void removePieceAt(Position pos) {
        tileAt(pos).removePiece();
    }
}
