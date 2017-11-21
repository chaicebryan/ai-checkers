package main.java.gui;

import java.util.HashSet;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import main.java.impl.Position;

// Piece represents a piece that exists within a tile on the board.
// It has an x. y position
// It has some metadata (WIDTH and HEIGHT in pixels) to enable drawing to a screen
public class Piece extends Circle implements Comparable<Piece> {

    // Width and height in pixels
    public static int WIDTH = 70;
    public static int HEIGHT = 70;

    // Is the piece the currently selected piece
    private boolean selected;

    // The colour of the piece
    private PieceType pieceType;

    // The position of the piece on the 8x8 board
    private Position boardPosition;

    // Is the piece a king or not
    private Boolean isKing;

    // All nearby pieces that are diagonal to this piece
    private HashSet<Piece> nearbyPieces;

    // All nearby opponents that are diagonal to this piece
    private HashSet<Piece> nearbyOpponents;

    // The side fo the board that this piece started from
    private Side side;

    private Color defaultStroke;


    public Piece(double posX, double posY, PieceType pieceType, Side side) {
        super(posX, posY, 0.7*35.0);
        selected = false;
        boardPosition = new Position((int)posX, (int)posY);
        this.pieceType = pieceType;
        isKing = false;
        nearbyPieces = new HashSet<>();
        nearbyOpponents = new HashSet<>();
        this.side = side;

        // Draw piece to its initial position on the UI
        relocate(posX * HEIGHT, posY * WIDTH);

        // Center the piece within the square
        setTranslateX(0.7*15.0);
        setTranslateY(0.7*15.0);

        // Assign pieces correct colours
        if (pieceType.equals(PieceType.BLACK)) {
            setFill(Paint.valueOf("#000"));
            setStroke(Color.WHITE);
            defaultStroke = Color.WHITE;
        } else {
            setFill(Paint.valueOf("#c40003"));
            setStroke(Paint.valueOf("#000"));
            defaultStroke = Color.BLACK;
        }

        // Enable click and drag for the piece
        setOnMouseDragged((e) -> {
            relocate(e.getSceneX() - 240.0 - 35.0, e.getSceneY() - 35.0);
        });

        setOnMousePressed((e) -> {
          System.out.println(this.toString());
          this.makeKing();
        });
    }

    // Add nearby piece to list of nearby pieces
    public void addNearbyPiece(Piece other) {
        nearbyPieces.add(other);
        other.getNearbyPieces().add(this);
    }

    // Add nearby opponent to list of nearby opponents
    public void addNearbyOpponent(Piece opponent) {
        nearbyOpponents.add(opponent);
        opponent.getNearbyPieces().add(this);
    }

    // remove piece from nearby pieces
    public void removeNearbyPiece(Piece other) {
        nearbyPieces.remove(other);
        other.getNearbyPieces().remove(this);
    }

    // remove opponent from nearby opponents
    public void removeNearbyOpponents(Piece opponent) {
        nearbyOpponents.remove(opponent);
        opponent.getNearbyPieces().remove(this);
    }

    // Change the position of this piece to a new specified position
    public void updatePositionTo(Position newPos) {
        boardPosition.changeTo(newPos.getX(), newPos.getY());
    }

    // Make this piece a king
    public void makeKing() {
        this.isKing = true;

        Image image = new Image("redking.png");

        ImagePattern imv = new ImagePattern(image);

        this.setFill(imv);
    }

    public Position getPosition() {
        return boardPosition;
    }

    public boolean isKing() {
        return isKing;
    }

    public HashSet<Piece> getNearbyPieces() {
        return nearbyPieces;
    }

    public HashSet<Piece> getNearbyOpponents() {
        return nearbyOpponents;
    }

    public Side getSide() {
        return side;
    }

    public Paint getDefaultStroke() {
        return defaultStroke;
    }

    @Override
    public int compareTo(Piece o) {
        return 0;
    }

    @Override
    public String toString() {
        return "[" + boardPosition.getX() + ", " + boardPosition.getY() + " ," + pieceType.toString() + " ," + side.toString() + "]" ;
    }
}
