package main.java.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Game extends Application {

    public Pane createBoard() {

        Pane pane = new Pane();
        pane.setPrefSize(Board.WIDTH * Tile.WIDTH, Board.HEIGHT * Tile.HEIGHT);

        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                Tile tile;
                if ((x + y) % 2 == 0) {
                    tile = new Tile(x, y, TileType.YELLOW);
                } else {
                    tile = new Tile(x, y, TileType.BROWN);
                }

                Piece piece = null;
                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = new Piece(x, y, PieceType.BLACK);
                } else if (y >= 5 && (x + y) % 2 != 0) {
                    piece = new Piece(x, y,PieceType.RED);
                }

                pane.getChildren().add(tile);
                if (piece != null) {
                    pane.getChildren().add(piece);
                }
            }
        }
        return pane;
    }
    @Override
    public void start(Stage primaryStage) throws Exception{


        Button b1 = new Button("Start");
        b1.setPrefSize(100, 40);
        VBox sideMenu = new VBox();
        sideMenu.setMinWidth(200);
        //sideMenu.setStyle("-fx-background-color: #000;");
        sideMenu.getStyleClass().add("hbox");
        sideMenu.setId("side-menu");
        sideMenu.setPadding(new Insets(20, 20, 20, 20));
        sideMenu.getChildren().add(b1);

        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(sideMenu);
        borderPane.setCenter(createBoard());

        primaryStage.setTitle("AI Checkers");

        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add(Game.class.getResource("/side-menu.css")
                        .toExternalForm()
        );

        primaryStage.setScene(scene);
        primaryStage.setMaxHeight(Board.HEIGHT * Tile.HEIGHT);
        primaryStage.setMinHeight(Board.HEIGHT * Tile.HEIGHT + 30);
        primaryStage.setMaxWidth(Board.WIDTH * Tile.WIDTH + 200);
        primaryStage.setMinWidth(Board.WIDTH * Tile.WIDTH + 200);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
