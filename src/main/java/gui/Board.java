package main.java.gui;

import static main.java.gui.Tile.BL;
import static main.java.gui.Tile.BR;
import static main.java.gui.Tile.TL;
import static main.java.gui.Tile.TR;

import java.util.HashSet;
import java.util.Iterator;
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

    public Optional<Take> attemptMove(Player player, Move move, Set<Take> takes) {
        if (takes.contains(move)) {

            Take take = null;
            Iterator iter = takes.iterator();
            while (iter.hasNext()) {
                take = (Take) iter.next();
                if (take.equals(move)) {
                    break;
                }
            }
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

    public HashSet<Take> findForceTakes(Piece piece) {
        Tile currentTile = tileAt(piece.getPosition());
        Map<String, Position> surroundingTiles = currentTile.getSurrounding();

        Side side = piece.getSide();
        boolean isKing = piece.isKing();

        HashSet<Take> takes = new HashSet<>();

        if (!isKing) {
            if (side == Side.BOTTOM) {
                if (surroundingTiles.get(TL) != null && tileAt(surroundingTiles.get(TL)).hasPiece()) {
                    Piece potentialOpponent = tileAt(surroundingTiles.get(TL)).getPiece();
                    if (potentialOpponent.getSide() != side && tileAt(potentialOpponent.getPosition()).getSurrounding().get(TL) != null && !tileAt(tileAt(potentialOpponent.getPosition()).getSurrounding().get(TL)).hasPiece()) {
                        takes.add(new Take(piece, tileAt(potentialOpponent.getPosition()).getSurrounding().get(TL), potentialOpponent));
                    }
                }
                if (surroundingTiles.get(TR) != null && tileAt(surroundingTiles.get(TR)).hasPiece()) {
                    Piece potentialOpponent = tileAt(surroundingTiles.get(TR)).getPiece();
                    if (potentialOpponent.getSide() != side && tileAt(potentialOpponent.getPosition()).getSurrounding().get(TR) != null &&!tileAt(tileAt(potentialOpponent.getPosition()).getSurrounding().get(TR)).hasPiece()) {
                        takes.add(new Take(piece, tileAt(potentialOpponent.getPosition()).getSurrounding().get(TR), potentialOpponent));
                    }
                }
            } else {
                if (surroundingTiles.get(BL) != null && tileAt(surroundingTiles.get(BL)).hasPiece()) {
                    Piece potentialOpponent = tileAt(surroundingTiles.get(BL)).getPiece();
                    if (potentialOpponent.getSide() != side && tileAt(potentialOpponent.getPosition()).getSurrounding().get(BL) != null &&!tileAt(tileAt(potentialOpponent.getPosition()).getSurrounding().get(BL)).hasPiece()) {
                        takes.add(new Take(piece, tileAt(potentialOpponent.getPosition()).getSurrounding().get(BL), potentialOpponent));
                    }
                }
                if (surroundingTiles.get(BR) != null && tileAt(surroundingTiles.get(BR)).hasPiece()) {
                    Piece potentialOpponent = tileAt(surroundingTiles.get(BR)).getPiece();
                    if (potentialOpponent.getSide() != side && tileAt(potentialOpponent.getPosition()).getSurrounding().get(BR) != null &&!tileAt(tileAt(potentialOpponent.getPosition()).getSurrounding().get(BR)).hasPiece()) {
                        takes.add(new Take(piece, tileAt(potentialOpponent.getPosition()).getSurrounding().get(BR), potentialOpponent));
                    }
                }
            }
        } else {
            if (surroundingTiles.get(TL) != null && tileAt(surroundingTiles.get(TL)).hasPiece()) {
                Piece potentialOpponent = tileAt(surroundingTiles.get(TL)).getPiece();
                if (potentialOpponent.getSide() != side && tileAt(potentialOpponent.getPosition()).getSurrounding().get(TL) != null &&!tileAt(tileAt(potentialOpponent.getPosition()).getSurrounding().get(TL)).hasPiece()) {
                    takes.add(new Take(piece, tileAt(potentialOpponent.getPosition()).getSurrounding().get(TL), potentialOpponent));
                }
            }
            if (surroundingTiles.get(TR) != null && tileAt(surroundingTiles.get(TR)).hasPiece()) {
                Piece potentialOpponent = tileAt(surroundingTiles.get(TR)).getPiece();
                if (potentialOpponent.getSide() != side && tileAt(potentialOpponent.getPosition()).getSurrounding().get(TR) != null &&!tileAt(tileAt(potentialOpponent.getPosition()).getSurrounding().get(TR)).hasPiece()) {
                    takes.add(new Take(piece, tileAt(potentialOpponent.getPosition()).getSurrounding().get(TR), potentialOpponent));
                }
            }
            if (surroundingTiles.get(BL) != null && tileAt(surroundingTiles.get(BL)).hasPiece()) {
                Piece potentialOpponent = tileAt(surroundingTiles.get(BL)).getPiece();
                if (potentialOpponent.getSide() != side && tileAt(potentialOpponent.getPosition()).getSurrounding().get(BL) != null &&!tileAt(tileAt(potentialOpponent.getPosition()).getSurrounding().get(BL)).hasPiece()) {
                    takes.add(new Take(piece, tileAt(potentialOpponent.getPosition()).getSurrounding().get(BL), potentialOpponent));
                }
            }
            if (surroundingTiles.get(BR) != null && tileAt(surroundingTiles.get(BR)).hasPiece()) {
                Piece potentialOpponent = tileAt(surroundingTiles.get(BR)).getPiece();
                if (potentialOpponent.getSide() != side && tileAt(potentialOpponent.getPosition()).getSurrounding().get(BR) != null &&!tileAt(tileAt(potentialOpponent.getPosition()).getSurrounding().get(BR)).hasPiece()) {
                    takes.add(new Take(piece, tileAt(potentialOpponent.getPosition()).getSurrounding().get(BR), potentialOpponent));
                }
            }
        }
        return takes;
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

    // Determines whether or not a piece is at the edge of the board
    private boolean pieceAtEdge(Piece piece) {
        Position pos = piece.getPosition();
        return pos.getX() != 0 && pos.getX() != WIDTH-1;
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
