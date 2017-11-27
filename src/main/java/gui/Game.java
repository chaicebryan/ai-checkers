package main.java.gui;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import main.java.impl.Move;
import main.java.impl.Position;
import main.java.impl.Take;
import main.java.utils.GameUtils;

public class Game extends Application {

    private Pane boardPane;
    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private ArrayList<Piece> pieces;
    private List<Piece> blackPieces;
    private List<Piece> redPieces;
    private List<Move> availableMoves;
    private List<Take> availableTakes;
    private boolean gameInProgress;

    public Game() {
        board = new Board();
        player1 = new Player(PieceType.BLACK, true, Side.BOTTOM);
        player2 = new Player(PieceType.RED, true, Side.TOP);
        currentPlayer = player1;

        pieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        redPieces = new ArrayList<>();
        availableMoves = new ArrayList<>();
        availableTakes = new ArrayList<>();
        gameInProgress = false;
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


        return pane;
    }

    private void setUpPieces() {

        redPieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
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
            }
        }

        pieces.addAll(blackPieces);
        pieces.addAll(redPieces);
        setUpPieceLogic();
    }

    private void setUpPieceLogic() {
        pieces.forEach((piece -> piece.setOnMouseReleased((event) -> {
            Position newPos = GameUtils.getInstance().convertToBoardPosition(
                    piece.getLayoutX(),
                    piece.getLayoutY()
            );
            processMove(new Move(piece, newPos));
        })));
    }

    private void processMove(Move move) {
        if (takesExist()) {
            Optional<Take> takeMade = board.attemptMove(move, availableTakes);
            if (takeMade.isPresent()) {
                processTake(takeMade.get());

                unmarkForceTakes(availableTakes);
                availableTakes = board.findForceTakes(Collections.singletonList(move.getPiece()));

                if (!takesExist()) {
                    finishMove();
                } else {
                    markForceTakes(availableTakes);
                }
            }
        } else if (board.attemptMove(currentPlayer, move)){
            finishMove();
        }
    }

    private void finishMove() {
        System.out.println("completed");
        this.unmarkForceTakes(availableTakes);
        this.unMarkValidMoves(availableMoves);
        availableTakes = new ArrayList<>();
        availableMoves = new ArrayList<>();
        endPlayerTurn(currentPlayer);
    }

    private void processTake(Take takeMade) {
        if (takeMade.getTarget().isKing()) {
            takeMade.getPiece().makeKing();
        }
        if (currentPlayer.getColor() == PieceType.RED) {
            blackPieces.remove(takeMade.getTarget());
            takeMade.getTarget().setVisible(false);
        } else {
            redPieces.remove(takeMade.getTarget());
            takeMade.getTarget().setVisible(false);
        }
    }

    private void endPlayerTurn(Player player) {
        if (player.getSide() == Side.BOTTOM) {
            currentPlayer = player2;
            System.out.println("Changed to player: " + currentPlayer.getSide());

            availableMoves = board.findValidMoves(currentPlayer, redPieces);
            availableTakes = board.findForceTakes(redPieces);
            if (availableTakes.isEmpty()) {
            //    markValidMoves(availableMoves);
            } {
                markForceTakes(availableTakes);
            }
        } else {
            currentPlayer = player1;
            System.out.println("Changed to player: " + currentPlayer.getSide());
            availableMoves = board.findValidMoves(currentPlayer, blackPieces);
            availableTakes = board.findForceTakes(blackPieces);
            if (availableTakes.isEmpty()) {
            //    markValidMoves(availableMoves);
            } {
                markForceTakes(availableTakes);
            }
        }
    }

    private void markValidMoves(List<Move> moves) {
        moves.forEach((move -> {
            move.getPiece().setStroke(Color.GREEN);
            move.getPiece().setStrokeWidth(4);
            board.tileAt(move.getDest()).setFill(Paint.valueOf("#a07e5d"));
        }));
    }

    private void unMarkValidMoves(List<Move> moves) {
        moves.forEach((move -> {
            move.getPiece().setStroke(move.getPiece().getDefaultStroke());
            move.getPiece().setStrokeWidth(1);
            board.tileAt(move.getDest()).setFill(Paint.valueOf("#d18b47"));
        }));

    }

    private void markForceTakes(List<Take> takes) {
        takes.forEach((take -> {
            System.out.println("Marking: " + take.toString());
            take.getPiece().setStroke(Color.GREEN);
            take.getPiece().setStrokeWidth(4);
            board.tileAt(take.getDest()).setFill(Paint.valueOf("#c4513c"));
        }));
    }

    private void unmarkForceTakes(List<Take> takes) {
        takes.forEach((take -> {
            System.out.println("unmarking");
            Piece attacker = take.getPiece();
            attacker.setStroke(attacker.getDefaultStroke());
            attacker.setStrokeWidth(1);
            board.tileAt(take.getDest()).setFill(Paint.valueOf("#d18b47"));
        }));
    }

    private boolean takesExist() {
        return !availableTakes.isEmpty();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(createSideMenu());

        boardPane = createBoard();
        borderPane.setCenter(boardPane);

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

    private VBox createSideMenu() {
        Label header = new Label("Checkers");
        header.setTextFill(Paint.valueOf("#aeb6ba"));
        header.setId("header");

        VBox playerSettings = createPlayerSettings();

        VBox difficultyBox = createDifficultyPane();

        VBox buttons = createGameButtons();

        TextArea updates = new TextArea();
        VBox updatesBox = new VBox(updates);
        updatesBox.setMaxWidth(200);
        updatesBox.setPadding(new Insets(10, 0, 0, 0));
        updates.setText("Welcome!");


        VBox sideMenu = new VBox();
        sideMenu.setMinWidth(200);
        sideMenu.getStyleClass().add("hbox");
        sideMenu.setId("side-menu");
        sideMenu.setPadding(new Insets(20, 20, 20, 20));

        sideMenu.getChildren().addAll(header, playerSettings, difficultyBox, buttons, updatesBox);

        return sideMenu;
    }

    private VBox createDifficultyPane() {
        VBox difficultyBox = new VBox();
        difficultyBox.setPadding(new Insets(10, 0, 10, 0));
        Label difficultyLabel = new Label("difficulty:");
        Slider difficulty = new Slider(1, 4, 1);
        difficulty.setShowTickLabels(true);
        difficulty.setShowTickMarks(true);
        difficulty.setBlockIncrement(1);
        difficulty.setMajorTickUnit(1);
        difficulty.setSnapToTicks(true);
        difficultyBox.getChildren().addAll(difficultyLabel, difficulty);
        return difficultyBox;
    }


    private VBox createGameButtons() {
        Button restart = new Button("stop");
        restart.setOnAction((event -> {
            gameInProgress = false;
            boardPane.getChildren().removeAll(redPieces);
            boardPane.getChildren().removeAll(blackPieces);
            redPieces = new ArrayList<>();
            blackPieces = new ArrayList<>();
            unmarkForceTakes(availableTakes);
            unMarkValidMoves(availableMoves);
        }));
        restart.setPrefSize(100, 40);
        Button start = new Button("start");
        start.setPrefSize(100, 40);
        start.setOnAction((e) -> {
            if (!gameInProgress) {
                gameInProgress = true;
                setUpPieces();
                boardPane.getChildren().addAll(redPieces);
                boardPane.getChildren().addAll(blackPieces);
            } else {
                System.out.println("Game already in progress");
            }
        });

        Button instructions = new Button("Instructions");
        instructions.setPrefSize(100, 40);
        instructions.setOnAction((e) -> {
            board.printContents();

            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI("http://www.indepthinfo.com/checkers/play.shtml"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
        });

        Button hint = new Button("Hint");
        hint.setPrefSize(100, 40);
        hint.setOnAction((event -> markValidMoves(availableMoves)));

        HBox hBox1= new HBox();
        hBox1.getChildren().addAll(restart, start);
        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(instructions, hint);

        VBox buttons = new VBox();
        buttons.getChildren().addAll(hBox1, hBox2);
        return buttons;
    }

    private VBox createPlayerSettings() {
        HBox player1Options = new HBox();
        player1Options.getChildren().add(new Label("Player 1:"));

        ComboBox player1Class = new ComboBox();
        player1Class.getItems().addAll("Human", "AI");
        player1Class.getSelectionModel().selectFirst();
        player1Class.setOnAction((event -> {
            if (player1Class.getSelectionModel().getSelectedIndex() == 0) {
                player1.setClass(true);
            } else {
                player1.setClass(false);
            }
        }));
        player1Options.getChildren().add(player1Class);

        HBox player2Options = new HBox();
        player2Options.getChildren().add(new Label("Player 2:"));

        ComboBox player2Class = new ComboBox();
        player2Class.getItems().addAll("Human", "AI");
        player2Class.getSelectionModel().select("AI");
        player2Class.setOnAction((event -> {
            if (player2Class.getSelectionModel().getSelectedIndex() == 0) {
                player2.setClass(true);
            } else {
                player2.setClass(false);
            }
        }));
        player2Options.getChildren().add(player2Class);

        VBox settings = new VBox();
        settings.getChildren().addAll(player1Options, player2Options);
        return settings;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
