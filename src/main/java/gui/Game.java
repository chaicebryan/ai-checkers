package main.java.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.java.impl.Move;
import main.java.impl.Position;
import main.java.impl.Take;
import main.java.utils.GameUtils;

public class Game extends Application {

    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;

    ArrayList<Piece> pieces;
    private List<Piece> blackPieces;
    private List<Piece> redPieces;

    public Game() {
        board = new Board();
        player1 = new Player(PieceType.BLACK, true, Side.BOTTOM);
        player2 = new Player(PieceType.RED, true, Side.TOP);
        currentPlayer = player1;

        pieces = new ArrayList<>();
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
                    pane.getChildren().add(piece);
                }
            }
        }

        pieces.addAll(blackPieces);
        pieces.addAll(redPieces);

        setUpPieceLogic();

        return pane;
    }

    private void setUpPieceLogic() {
        pieces.forEach((piece -> {
            piece.setOnMouseReleased((event) -> {
                Position newPos = GameUtils.getInstance().convertToBoardPosition(
                        piece.getLayoutX(),
                        piece.getLayoutY()
                );

                Set<Take> takes;
                if (currentPlayer.getSide().equals(Side.BOTTOM)) {
                    takes = blackPieces.stream()
                            .map(board::findForceTakes)
                            .flatMap(Set::stream)
                            .collect(Collectors.toSet());
                } else {
                    takes = redPieces.stream()
                            .map(board::findForceTakes)
                            .flatMap(Set::stream)
                            .collect(Collectors.toSet());
                }

                boolean completedMove = false;
                if (!takes.isEmpty()) {
                    Optional<Take> takeMade = board.attemptMove(currentPlayer, new Move(piece, newPos), takes);
                    if (takeMade.isPresent()) {
                        Piece victim = takeMade.get().getTarget();
                        if (currentPlayer.getColor() == PieceType.RED) {
                            board.tileAt(victim.getPosition()).setPiece(null);
                            redPieces.remove(victim);
                            victim.setVisible(false);
                            victim = null;
                        } else {
                            board.tileAt(victim.getPosition()).setPiece(null);
                            blackPieces.remove(victim);
                            victim.setVisible(false);
                            victim = null;
                        }
                        completedMove = true;
                    }
                } else {
                    completedMove = board.attemptMove(currentPlayer, new Move(piece, newPos));
                }

                if (completedMove) {
                    System.out.println("completed");
                    endPlayerTurn(currentPlayer);
                    takes.forEach(this::unmarkForceTake);
                }
            });
        }));
    }

    private void endPlayerTurn(Player player) {
        if (player.getSide().equals(player1.getSide())) {
            System.out.println("Changing to player 2");
            currentPlayer = player2;
            System.out.println("Current player side: " + currentPlayer.getSide());

            redPieces.forEach((piece -> {
                HashSet<Take> takes = board.findForceTakes(piece);

                takes.forEach((take) -> {
                    markForceTake(take);
                });
            }));

        } else {
            System.out.println("Changing to player 1");
            currentPlayer = player1;
            System.out.println("Current player side: " + currentPlayer.getSide());
            blackPieces.forEach((piece -> {
                HashSet<Take> takes = board.findForceTakes(piece);

                takes.forEach((take) -> {
                    markForceTake(take);
                });
            }));
        }
    }

    private void markForceTake(Take take) {
        take.getPiece().setStroke(Color.BLUE);
        board.tileAt(take.getDest()).setFill(Color.RED);
    }

    private void unmarkForceTake(Take take) {
        Piece attacker = take.getPiece();
        attacker.setStroke(attacker.getDefaultStroke());

        board.tileAt(take.getDest()).setFill(Paint.valueOf("#d18b47"));
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

        Button testing = new Button("Show State");
        testing.setPrefSize(100, 40);
        testing.setOnAction((e) -> board.printContents());

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
        sideMenu.getChildren().add(testing);

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
