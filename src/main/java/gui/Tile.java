package main.java.gui;


import java.util.HashMap;
import java.util.Optional;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import main.java.impl.Board;
import main.java.impl.Position;
import main.java.impl.Side;
import main.java.impl.TileType;

// This represents a tile on the game board,
// A tile can either hold a piece or hold nothing
// A tile has a position
public class Tile extends Rectangle {

    public static final int HEIGHT = 70;
    public static final int WIDTH = 70;

    public static final String TL = "tl";
    public static final String TR = "tr";
    public static final String BL = "bl";
    public static final String BR = "br";

    private final int posX;
    private final int posY;
    private final HashMap<String, Optional<Position>> surrounding;

    // Represents the colour of the tile
    private final TileType type;

    // Reference to piece if this tile has one
    private Piece piece;

    public Tile(int posX, int posY, TileType type) {
        this.posX = posX;
        this.posY = posY;
        this.type = type;
        piece = null;

        surrounding = new HashMap<>();

        // This section populates the 'surrounding' HashMap with pointers
        // to surrounding tiles so that every tile has information about
        // its surrounding tiles

        // This is used when finding valid moves and takes
        if (posX!=0 && posY!= 0) {
            surrounding.put(TL, Optional.of(new Position(posX-1, posY-1)));
        } else {
            surrounding.put(TL, Optional.empty());
        }

        if (posX!= Board.HEIGHT-1 && posY!=0) {
            surrounding.put(TR, Optional.of(new Position(posX+1, posY-1)));
        } else {
            surrounding.put(TR, Optional.empty());
        }

        if (posX!= 0 && posY!=Board.HEIGHT-1) {
            surrounding.put(BL, Optional.of(new Position(posX-1, posY+1)));
        } else {
            surrounding.put(BL, Optional.empty());
        }

        if (posX != Board.WIDTH-1 && posY!=Board.HEIGHT-1) {
            surrounding.put(BR, Optional.of(new Position(posX+1, posY+1)));
        } else {
            surrounding.put(BR, Optional.empty());
        }

        // The size of each rectangle
        setHeight(HEIGHT);
        setWidth(WIDTH);

        // Where the rectangle is on the board.
        // Multiply WIDTH and HEIGHT by x or y to get actual pixel value
        relocate(posX * HEIGHT, posY * WIDTH);

        // Choose colour
        if (type.equals(TileType.BROWN)) {
            setFill(Paint.valueOf("#d18b47"));
        } else {
            setFill(Paint.valueOf("#ffce9e"));
        }

        setStroke(Color.BLACK);
    }

    // Return the TileType of the tile (BROWN | YELLOW)
    public TileType getType() {
        return type;
    }

    // Assign the tile a piece to occupy it
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    // get the piece that is occupying this tile
    public Piece getPiece() {
        return piece;
    }

    // Return true if this tile is occupied by a piece
    // false otherwise
    public boolean hasPiece() {
        return piece != null;
    }

    // Remove the piece that is occupying this tile
    public void removePiece() {
        this.piece = null;
    }

    // return the surrounding tiles for the tile
    public HashMap<String, Optional<Position>> getSurrounding() {
        return surrounding;
    }

//   @Override
//   public String toString() {
//       return "Tile [" + posX + ", " + posY + ", " + surrounding.toString() + ", " + hasPiece() + ", " + piece + "]";
//   }

    @Override
    public String toString() {
        if (hasPiece() && piece.getSide() == Side.TOP) {
            return "X";
        } else if (hasPiece() && piece.getSide() == Side.BOTTOM) {
            return "O";
        } else {
            return " ";
        }
    }
}
