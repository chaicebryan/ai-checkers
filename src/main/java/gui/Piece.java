package main.java.gui;

import java.util.HashSet;

import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import main.java.impl.Board;
import main.java.impl.PieceType;
import main.java.impl.Position;
import main.java.impl.Side;

// Piece represents a piece that exists within a tile on the board.
// It has an x. y position
// It has some metadata (WIDTH and HEIGHT in pixels) to enable drawing to a screen
@SuppressWarnings("WeakerAccess")
public class Piece extends Circle {

    // Width and height in pixels
    public static final int WIDTH = 70;
    public static final int HEIGHT = 70;

    // The colour of the piece
    private final PieceType pieceType;

    // The position of the piece on the 8x8 board
    private Position boardPosition;

    // Is the piece a king or not
    private Boolean isKing;

    // The side fo the board that this piece started from
    private final Side side;

    // Used to reset pieces to default colour
    private final Color defaultStroke;


    public Piece(double posX, double posY, PieceType pieceType, Side side) {
        super(posX, posY, 0.7*35.0);
        boardPosition = new Position((int)posX, (int)posY);
        this.pieceType = pieceType;
        isKing = false;
        this.side = side;


        // Draw piece to its initial position on the UI
        relocate(posX * HEIGHT, posY * WIDTH);

        // Center the piece within the square
        setTranslateX(0.7*15.0);
        setTranslateY(0.7*15.0);

        // Assign pieces correct colours
        if (pieceType.equals(PieceType.BLACK)) {
            setFill(Paint.valueOf("#000"));
            setStroke(Color.BLACK);
        } else {
            setFill(Paint.valueOf("#FF0000"));
            setStroke(Paint.valueOf("#000"));
        }
        defaultStroke = Color.BLACK;

        // Enable click and drag for the piece
        setOnMouseDragged((e) -> {
            relocate(e.getSceneX() - 240.0 - 35.0, e.getSceneY() - 35.0);
        });

        setOnMousePressed((e) -> {
          System.out.println(this.toString());
        });

        Bloom bloom = new Bloom();
        bloom.setThreshold(1.0);
        setEffect(bloom);
    }

    // Change the position of this piece to a new specified position
    public void updatePositionTo(Position newPos) {
        if ((side == Side.BOTTOM && newPos.getY() == 0) || (side == Side.TOP && newPos.getY() == Board.HEIGHT-1)) {
            this.makeKing();
            boardPosition = newPos;
        } else {
            boardPosition = newPos;
        }
    }

    // Make this piece a king
    public void makeKing() {
        this.isKing = true;
    }

    // remove king status
    public void demote() {
        this.isKing = false;
    }

    // draw crown on piece to be used when
    // converting to king
    public void animateKingConversion() {
        Image image;
        if (pieceType == PieceType.RED) {
            image = new Image("redking.png");
        } else {
            image = new Image("blackking.png");
        }
        ImagePattern imv = new ImagePattern(image);
        this.setFill(imv);
    }

    // returns position of the piece
    public Position getPosition() {
        return boardPosition;
    }

    // returns true if this piece is a king, false otherwise
    public boolean isKing() {
        return isKing;
    }

    // returns what side of the board that this piece is affiliated with
    public Side getSide() {
        return side;
    }

    // return the default stroke (around the edge of each piece)
    public Paint getDefaultStroke() {
        return defaultStroke;
    }

    // animate the movement of the piece
    public void moveTo(Position newPos) {
        relocate(newPos.getX() * WIDTH, newPos.getY() * HEIGHT);
    }

    @Override
    public String toString() {
        return "[" + boardPosition.getX() + ", " + boardPosition.getY() + " ," + pieceType.toString() + " ," + side.toString() + "]" ;
    }
}
