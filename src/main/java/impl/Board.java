package main.java.impl;

import static main.java.gui.Tile.BL;
import static main.java.gui.Tile.BR;
import static main.java.gui.Tile.TL;
import static main.java.gui.Tile.TR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import main.java.gui.Piece;
import main.java.gui.Tile;

// The Board class is a state representation of the board that contains a number of operations for editing this state
public class Board {

    // Number of squares on each side (HEIGHT * WIDTH)
    public static final int HEIGHT = 8;
    public static final int WIDTH = 8;

    // Board is represented using a 2D array of Tile objects
    // Some tiles will contain pieces
    private Tile[][] state;

    // Creates new board of specified dimensions
    public Board() {
        state = new Tile[HEIGHT][WIDTH];
    }

    // Returns the underlying array representation of the board
    public Tile[][] getState() {
        return state;
    }

    // Makes the necessary state updates given a move
    public void acceptMove(Move move) {
        if (move instanceof Take) {
            Take take = (Take) move;
            Piece target = take.getTarget();
            removePieceAt(target.getPosition());
        }

        removePieceAt(move.getPiece().getPosition());
        tileAt(move.getDest()).setPiece(move.getPiece());
    }

    // Carries out a number of checks to determine if the attempted move is valid
    // It does this by considering the piece's current position and a specified new position
    // It also checks that this move is valid for the player, depending on which side they are on, on the board
    private boolean moveIsValid(Player player, Move move) {

        // Don't allow a player to move another players pieces
        if (player.getSide() != move.getPiece().getSide()) {
            move.setStatusCode(1);
            return false;
        }
        // Don't let pieces go off the board
        // This should come first to prevent any exceptions on further board operations
        if (outOfBounds(move.getDest())) {
            if (player.isHuman()) {
                move.setStatusCode(2);
            }
            return false;
        }

        // Limit distance moved per move to one square only
        if (Math.abs(move.getPiece().getPosition().getX() - move.getDest().getX()) > 1 || Math.abs(move.getPiece().getPosition().getY() - move.getDest().getY()) > 1 ) {
            if (player.isHuman()) {
                move.setStatusCode(3);
            }
            return false;
        }

        // Only diagonal moves are allowed for any move to be valid
        if (placedOnWrongColour(move.getDest())) {
            if (player.isHuman()) {
                move.setStatusCode(4);
            }
            return false;
        }

        // Can't move a piece to a tile that already contains a piece
        if (tileAlreadyOccupied(move.getDest())) {
            if (player.isHuman()) {
                move.setStatusCode(5);
            }
            return false;
        }

        // Players at the bottom of the board can only move up and vice versa
        // But exceptions are made for king pieces
        if (!directionIsValid(player, move)) {
            if (player.isHuman()) {
                move.setStatusCode(6);
            }
            return false;
        }
        return true;
    }

    // Get the failure code for a move
    public int returnFailureCode(Player player, Move move) {
        moveIsValid(player, move);
        return move.getStatusCode();
    }

    // Given a player and their list of pieces find all moves that can be made
    // with these pieces
    public ArrayList<Move> findValidMoves(Player player, List<Piece> pieces) {
        ArrayList<Move> moves = findPossibleDestinationsForPieces(pieces);
        moves.removeIf(move -> !moveIsValid(player, move));
        return moves;
    }

    // Given a list of pieces, for each piece, find all possible places it can move to
    // This method takes into account the surround area of each piece and adds each surrounding
    // area regardless of whether that area is already occupied. However it takes into account
    // when pieces are near the edge of the board by assigning non-existent destinations with Optional.EMPTY
    private ArrayList<Move> findPossibleDestinationsForPieces(List<Piece> pieces) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        pieces.forEach((piece) -> {
            if (!piece.isKing()) {
                if (piece.getSide() == Side.BOTTOM) {
                    tileAt(piece.getPosition()).getSurrounding().get(TL)
                            .ifPresent(position -> possibleMoves.add(new Move(piece, position)));
                    tileAt(piece.getPosition()).getSurrounding().get(TR)
                            .ifPresent(position -> possibleMoves.add(new Move(piece, position)));
                } else {
                    tileAt(piece.getPosition()).getSurrounding().get(BL)
                            .ifPresent(position -> possibleMoves.add(new Move(piece, position)));
                    tileAt(piece.getPosition()).getSurrounding().get(BR)
                            .ifPresent(position -> possibleMoves.add(new Move(piece, position)));
                }
            } else {
                tileAt(piece.getPosition()).getSurrounding().get(TL)
                        .ifPresent(position -> possibleMoves.add(new Move(piece, position)));
                tileAt(piece.getPosition()).getSurrounding().get(TR)
                        .ifPresent(position -> possibleMoves.add(new Move(piece, position)));
                tileAt(piece.getPosition()).getSurrounding().get(BL)
                        .ifPresent(position -> possibleMoves.add(new Move(piece, position)));
                tileAt(piece.getPosition()).getSurrounding().get(BR)
                        .ifPresent(position -> possibleMoves.add(new Move(piece, position)));

            }
        });
        return possibleMoves;
    }

    // Given a list of pieces identify any force takes for each
    // If no force takes are found then return an empty list
    public ArrayList<Take> findForceTakes(List<Piece> pieces) {
        ArrayList<Take> takes = new ArrayList<>();

        pieces.forEach((piece -> {
            Tile currentTile = tileAt(piece.getPosition());
            Map<String, Optional<Position>> surroundingTiles = currentTile.getSurrounding();
            Side side = piece.getSide();
            boolean isKing = piece.isKing();

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
        }));
        return takes;
    }

    // Given a piece, its surrounding tiles and a nearby square to search (either TL, TR, BL, BR)
    // return the force take associated with that square, if any
    private Optional<Take> getTakeIfAny(Piece piece, Map<String, Optional<Position>> surrounding, String nearby) {
        Piece potentialOpponent = tileAt(surrounding.get(nearby).get()).getPiece();

        if (potentialOpponent.getSide() != piece.getSide() && pieceIsVulnerable(potentialOpponent, nearby)) {
            return Optional.of(new Take(piece, tileAt(potentialOpponent.getPosition()).getSurrounding().get(nearby).get(), potentialOpponent));
        }
        return Optional.empty();
    }

    // If a square exists at a nearby (either TL, TR, BL, BR) position and has a piece
    // then return true, else false
    private boolean potentialTake(Map<String, Optional<Position>> surrounding, String nearBy) {
        return surrounding.get(nearBy).isPresent() && tileAt(surrounding.get(nearBy).get()).hasPiece();
    }

    // If a potential opponent's TL, TR, BL or BR is empty then this piece is vulnerable to a take
    private boolean pieceIsVulnerable(Piece potentialOpponent, String near) {
        return tileAt(potentialOpponent.getPosition()).getSurrounding().get(near).isPresent() &&
                !tileAt(tileAt(potentialOpponent.getPosition()).getSurrounding().get(near).get()).hasPiece();
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
            if (Side.BOTTOM == player.getSide()) {
                if (move.getDest().getY() >= piece.getPosition().getY()) {
                    return false;
                }
            } else if (piece.getSide() == Side.TOP)
                if (move.getDest().getY() <= piece.getPosition().getY()) {
                    return false;
                }
            } else if (piece.getSide() != player.getSide()) {
                return false;
            }
            return true;
    }

    public void printAsGrid() {
        String rows = "";
        String theRow = "";
            for (Tile[] row : state) {
                theRow = "";
                for (Tile tile : row) {
                    theRow += tile + "\t";
                }
                rows = theRow + "\n" + rows;
            }

        System.out.println(rows);
        System.out.println();
        System.out.println("--------------------------");
        System.out.println();
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
