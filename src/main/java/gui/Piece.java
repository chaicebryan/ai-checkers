package main.java.gui;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class Piece extends Circle {

    public static int WIDTH = 70;
    public static int HEIGHT = 70;
    private double posX;
    private double posY;
    private PieceType pieceType;

    public Piece(double posX, double posY, PieceType pieceType) {
        super(posX, posY, 0.7*35.0);
        this.posX = posX;
        this.posY = posY;

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
    }
}
