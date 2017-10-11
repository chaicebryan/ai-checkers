package gui;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Game extends Application {

    private Board board;

    public Pane createBoard() {

        board = new Board();

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
                pane.getChildren().add(tile);
            }
        }
        return pane;
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("AI Checkers");
        primaryStage.setScene(new Scene(createBoard()));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
