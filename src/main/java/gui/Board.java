package main.java.gui;

import main.java.impl.Position;
import main.java.utils.GameUtils;

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
        if (moveIsValied(piece, newPos, player)) {
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
            System.out.println(piece.getPosition().getX());
            System.out.println(piece.getPosition().getY());
            piece.relocate(piece.getPosition().getX() * Piece.WIDTH, piece.getPosition().getY() * Piece.HEIGHT);
        }
    }

    private boolean moveIsValied(Piece piece, Position newPos, Player player) {
        System.out.println("Old position: " + piece.getPosition().getX() + ", " + piece.getPosition().getY());
        System.out.println("New position: " + newPos.getX() + ", " + newPos.getY());

        if (player.isHuman()) {
            System.out.println("Player is human");
            if (newPos.getY() >= piece.getPosition().getY()) {
                return false;
            }

            if (newPos.getX() == piece.getPosition().getX()) {
                return false;
            }

            if (newPos.getY() == piece.getPosition().getY()) {
                return false;
            }

            if (newPos.getX() > WIDTH-1) {
                return false;
            }

            if (this.tileAt(newPos).hasPiece()) {
                return false;
            }
        }

        return true;
    }

    private Tile tileAt(Position pos) {
        return state[pos.getX()][pos.getY()];
    }

    private void removePieceAt(Position pos) {
        tileAt(pos).removePiece();
    }
}
