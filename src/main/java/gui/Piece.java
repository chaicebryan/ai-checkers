package main.java.gui;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import main.java.impl.Position;
import main.java.utils.GameUtils;

public class Piece extends Circle {

    public static int WIDTH = 70;
    public static int HEIGHT = 70;
    private boolean selected;
    private PieceType pieceType;
    private Position boardPosition;
    private Boolean isKing;

    public Piece(double posX, double posY, PieceType pieceType) {
        super(posX, posY, 0.7*35.0);
        selected = false;
        boardPosition = new Position((int)posX, (int)posY);
        this.pieceType = pieceType;
        isKing = false;

        relocate(posX * HEIGHT, posY * WIDTH);

        setTranslateX(0.7*15.0);
        setTranslateY(0.7*15.0);

        if (pieceType.equals(PieceType.BLACK)) {
            setFill(Paint.valueOf("#000"));
            setStroke(Color.WHITE);
        } else {
            setFill(Paint.valueOf("#c40003"));
            setStroke(Paint.valueOf("#000"));
        }

        setOnMouseDragged((e) -> {
            relocate(e.getSceneX() - 240.0 - 35.0, e.getSceneY() - 35.0);
        });
    }

    public void select() {
        selected = true;
        setStroke(Color.YELLOW);
        setStrokeWidth(5);
    }

    public void deselect() {
        if (pieceType == PieceType.BLACK) {
            selected = false;
            setStroke(Color.WHITE);
            setStrokeWidth(1);
        } else {
            selected = false;
            setStroke(Color.BLACK);
            setStrokeWidth(1);
        }
    }

    public void updatePositionTo(Position newPos) {
        boardPosition.changeTo(newPos.getX(), newPos.getY());
    }

    public void makeKing() {
        this.isKing = true;
    }

    public Position getPosition() {
        return boardPosition;
    }

    public boolean isKing() {
        return isKing;
    }
}
