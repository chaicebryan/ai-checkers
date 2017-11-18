package main.java.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.java.impl.Move;
import main.java.impl.Position;
import main.java.impl.Take;
import main.java.utils.GameUtils;

public class Game extends Application {

    private Board board;
    private Piece currentlySelected;
    private Player player1;
    private Player player2;
    private Player currentPlayer;

    private List<Piece> blackPieces;
    private List<Piece> redPieces;

    public Game() {
        board = new Board();
        player1 = new Player(PieceType.BLACK, true, Side.BOTTOM);
        player2 = new Player(PieceType.RED, true, Side.TOP);
        currentPlayer = player1;

        blackPieces = new ArrayList<>();
        redPieces = new ArrayList<>();
    }

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
                board.getState()[x][y] = tile;
                pane.getChildren().add(tile);

                }
        }

        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {

                final Piece piece;
                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = new Piece(x, y, PieceType.RED, Side.TOP);
                    redPieces.add(piece);
                    board.getState()[x][y].setPiece(piece);
                } else if (y >= 5 && (x + y) % 2 != 0) {
                    piece = new Piece(x, y, PieceType.BLACK, Side.BOTTOM);
                    blackPieces.add(piece);
                    board.getState()[x][y].setPiece(piece);
                } else {
                    piece = null;
                }

                if (piece != null) {
                    piece.setOnMousePressed(event -> {
                        if (currentlySelected == null) {
                            piece.select();
                            currentlySelected = piece;
                        } else {
                            currentlySelected.deselect();
                            piece.select();
                            currentlySelected = piece;
                        }
                    });

                    piece.setOnMouseReleased((event) -> {
                        Position newPos = GameUtils.getInstance().convertToBoardPosition(
                                        piece.getLayoutX(),
                                        piece.getLayoutY()
                        );

                        boolean completedMove = board.attemptMove(currentPlayer, new Move(piece, newPos));

                        if (completedMove) {
                            System.out.println("completed");
                            endPlayerTurn(currentPlayer);
                        }
                    });
                    pane.getChildren().add(piece);
                }
            }
        }
        return pane;
    }

    private void endPlayerTurn(Player player) {
        if (player.equals(player1)) {
            System.out.println("Changing to player 2");
            currentPlayer = player2;
            System.out.println("Current player side: " + currentPlayer.getSide());
            redPieces.forEach((piece -> {
                HashSet<Move> takes = board.findForceTakes(piece);

                takes.forEach((take) -> {
                    ((Take)take).getPiece().select();
                });
            }));
        } else {
            System.out.println("Changing to player 1");
            currentPlayer = player1;
            System.out.println("Current player side: " + currentPlayer.getSide());
            blackPieces.forEach((piece -> {
                HashSet<Move> takes = board.findForceTakes(piece);

                takes.forEach((take) -> {
                    ((Take)take).getPiece().select();
                });
            }));
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{


        Text header = new Text("Checkers");
        header.setId("header");

        Text difficultyLabel = new Text("difficulty:");

        Slider difficulty = new Slider(1, 4, 1);
        difficulty.setShowTickLabels(true);
        difficulty.setShowTickMarks(true);
        difficulty.setBlockIncrement(1);
        difficulty.setMajorTickUnit(1);
        difficulty.setSnapToTicks(true);


        HBox buttons = new HBox();
        Button restart = new Button("restart");
        restart.setPrefSize(100, 40);
        Button start = new Button("start");
        start.setPrefSize(100, 40);
        buttons.getChildren().addAll(restart, start);

        VBox sideMenu = new VBox();
        sideMenu.setMinWidth(200);
        //sideMenu.setStyle("-fx-background-color: #000;");
        sideMenu.getStyleClass().add("hbox");
        sideMenu.setId("side-menu");
        sideMenu.setPadding(new Insets(20, 20, 20, 20));

        sideMenu.getChildren().add(header);
        sideMenu.getChildren().add(difficultyLabel);
        sideMenu.getChildren().add(difficulty);
        sideMenu.getChildren().add(buttons);

        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(sideMenu);
        borderPane.setCenter(createBoard());

        primaryStage.setTitle("AI Checkers");

        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add(Game.class.getResource("/side-menu.css")
                        .toExternalForm()
        );

        primaryStage.setScene(scene);
        primaryStage.setMinHeight(Board.HEIGHT * Tile.HEIGHT + 30);
        primaryStage.setMaxWidth(Board.WIDTH * Tile.WIDTH + 240);
        primaryStage.setMinWidth(Board.WIDTH * Tile.WIDTH + 240);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
