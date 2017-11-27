package main.java.gui;

import static main.java.gui.Tile.BL;
import static main.java.gui.Tile.BR;
import static main.java.gui.Tile.TL;
import static main.java.gui.Tile.TR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public Boolean attemptMove(Player player, Move move) {
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

            switch (move.getStatusCode()) {
                case 1: Game.updates.appendText("Out Of Bounds\n");
                break;
                case 2: Game.updates.appendText("Can't move that far\n");
                break;
                case 3: Game.updates.appendText("Only diagonal moves allowed\n");
                break;
                case 4: Game.updates.appendText("Tile already occupied\n");
                break;
                case 5: Game.updates.appendText("This piece cannot move in that direction\n");
                break;
            }
            // Snap back to original position
            move.getPiece().relocate(move.getPiece().getPosition().getX() * Piece.WIDTH, move.getPiece().getPosition().getY() * Piece.HEIGHT);
            return false;
        }
    }

    public Optional<Take> attemptMove(Move move, List<Take> takes) {
        if (takes.contains(move)) {

            Take take = takes.get(takes.indexOf(move));
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

        if (player.getSide() != move.getPiece().getSide()) {
            return false;
        }
        // Don't let pieces go off the board
        // This should come first to prevent any exceptions on further board operations
        if (outOfBounds(move.getDest())) {
            if (player.isHuman()) {
                move.setStatusCode(1);
            }
            return false;
        }

        if (Math.abs(move.getPiece().getPosition().getX() - move.getDest().getX()) > 1 || Math.abs(move.getPiece().getPosition().getY() - move.getDest().getY()) > 1 ) {
            if (player.isHuman()) {
                move.setStatusCode(2);
            }
            return false;
        }

        // Only diagonal moves are allowed for any move to be valid
        if (placedOnWrongColour(move.getDest())) {
            if (player.isHuman()) {
                move.setStatusCode(3);
            }
            return false;
        }

        // Can't move a piece to a tile that already contains a piece
        if (tileAlreadyOccupied(move.getDest())) {
            if (player.isHuman()) {
                move.setStatusCode(4);
            }
            return false;
        }

        // Players at the bottom of the board can only move up and vice versa
        // But exceptions are made for king pieces
        if (!directionIsValid(player, move)) {
            if (player.isHuman()) {
                move.setStatusCode(5);
            }
            return false;
        }

        return true;
    }

    public ArrayList<Move> findValidMoves(Player player, List<Piece> pieces) {
        ArrayList<Move> moves = findPossibleDestinationsForPieces(pieces);

        moves.removeIf(move -> !moveIsValid(player, move));
        return moves;
    }

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

    private Optional<Take> getTakeIfAny(Piece piece, Map<String, Optional<Position>> surrounding, String nearby) {
        Piece potentialOpponent = tileAt(surrounding.get(nearby).get()).getPiece();

        if (potentialOpponent.getSide() != piece.getSide() && pieceIsVulnarable(potentialOpponent, nearby)) {
            return Optional.of(new Take(piece, tileAt(potentialOpponent.getPosition()).getSurrounding().get(nearby).get(), potentialOpponent));
        }
        return Optional.empty();
    }

    private boolean potentialTake(Map<String, Optional<Position>> surrounding, String nearBy) {
        return surrounding.get(nearBy).isPresent() && tileAt(surrounding.get(nearBy).get()).hasPiece();
    }

    private boolean pieceIsVulnarable(Piece potentialOpponent, String near) {
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

    // Returns the tile at a specified position
    public Tile tileAt(Position pos) {
        return state[pos.getX()][pos.getY()];
    }

    // removes a piece from a tile at a specified position
    private void removePieceAt(Position pos) {
        tileAt(pos).removePiece();
    }
}
