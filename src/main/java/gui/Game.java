package main.java.gui;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import main.java.impl.Board;
import main.java.impl.Move;
import main.java.impl.MoveAndScore;
import main.java.impl.PieceType;
import main.java.impl.Player;
import main.java.impl.Position;
import main.java.impl.Side;
import main.java.impl.Take;
import main.java.impl.TileType;
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
    private static TextArea updates;
    private List<MoveAndScore> successorEvaluations;
    private int depthLimit = 10;

    public Game() {
        board = new Board();
        player1 = new Player("P1", true, Side.BOTTOM);
        player2 = new Player("P2", true, Side.TOP);
        currentPlayer = player1;

        pieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        redPieces = new ArrayList<>();
        availableMoves = new ArrayList<>();
        availableTakes = new ArrayList<>();
        gameInProgress = false;
        successorEvaluations = new ArrayList<>();
    }

    // This creates all of the tiles and places them on within a pane before returning the pane
    private Pane createBoard() {
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

                // Populate underlying board stile with the tiles
                board.getState()[x][y] = tile;

                // Add the tile to the pane so that it is visible
                pane.getChildren().add(tile);
            }
        }
        return pane;
    }

    // Create pieces, populate the underlying board tiles with them
    // Also display them on screen
    private void setUpPieces() {
        // Maintain lists of red pieces and black pieces
        redPieces = new ArrayList<>();
        blackPieces = new ArrayList<>();

        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                final Piece piece;

                // Place pieces on brown squares only, add them to their respective list and
                // add them to the underlying board
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

    // Add event listeners to each piece
    private void setUpPieceLogic() {
        pieces.forEach((piece -> piece.setOnMouseReleased((event) -> {

            // When a user releases mouse on a piece we record the pixel coordinates
            // and convert them into board co-ordinates to determine validity
            // and to move them
            Position userMove = GameUtils.getInstance().convertToBoardPosition(
                    piece.getLayoutX(),
                    piece.getLayoutY()
            );
            // See if move/take exists
            tryUserMove(new Move(piece, userMove));
        })));
    }

    // start game by finding available moves
    private void startNewTurn() {
        availableMoves = board.findValidMoves(currentPlayer, getPiecesForPlayer(currentPlayer));
        availableTakes = board.findForceTakes(getPiecesForPlayer(currentPlayer));
        printState();
    }

    // When two AIs play against each other we run a loop
    // with some threading logic to make moves visible
    private void runAIGame() {
        while (true) {
            new Thread(() -> {
                makeAIMove();
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (Thread.currentThread().isInterrupted()) {
                System.out.println(true);
                break;
            }
        }
    }

    // If the human is playing against an AI then we check if it is the AIs
    // turn (i.e. they player 1) else we pass control over to the user
    private void startNormalGame() {
        if (!currentPlayer.isHuman()) {
            makeAIMove();
        }
    }

    // This method contains the main logic behind human moves
    // If a take is available the user must make it. If more takes exist
    // they must continue making them until none are left.
    // Otherwise check if it is in the list of available moves and animate it
    // if it is.
    // Else the move is not valid so we snap it back to its original position
    private void tryUserMove(Move move) {

        // If take does not exist then check if this move is in the list of available
        // moves, otherwise check if it is in available takes. If not snap back to original
        // position
        if (availableTakes.isEmpty() && availableMoves.contains(move)) {
            // Update board state
            makeMove(move);
            // Animate the move
            animateMove(move);

            updates.appendText("(" + currentPlayer.getName() +  ") Move: " + move.toString() + "\n");

            // Pass control to other player
            nextMove();
        } else  if (!availableTakes.isEmpty() && availableTakes.contains(move)) {
            makeTake(move);
            animateTake(move);
            updates.appendText("(" + currentPlayer.getName() +  ") Take: " + move.toString() + "\n");

            // Check if more takes are available once we have made a successful take
            unmarkForceTakes(availableTakes);
            availableTakes = findAdditionalTakes(move.getPiece());
            markForceTakes(availableTakes);
            if (availableTakes.isEmpty()) {
                nextMove();
            }
        } else {
            move.getPiece().moveTo(move.getOrigin());

            switch (board.returnFailureCode(currentPlayer, move)) {
                case 1: updates.appendText("Opponent must move first\n");
                    break;
                case 2: updates.appendText("Out Of Bounds\n");
                    break;
                case 3: updates.appendText("Can't move that far\n");
                    break;
                case 4: updates.appendText("Only diagonal moves allowed\n");
                    break;
                case 5: updates.appendText("Tile already occupied\n");
                    break;
                case 6: updates.appendText("Wrong direction\n");
                    break;
            }
        }

        // If other player is an AI then get then make the AI's next move
        if (!currentPlayer.isHuman()) {
            makeAIMove();
        }
    }

    // The given player has won if the opposing player has no pieces left
    // or can make no more valid moves
    private boolean playerHasWon(Player player) {
        return getPiecesForPlayer(otherPlayer(player)).isEmpty() || board.findValidMoves(otherPlayer(player), getPiecesForPlayer(otherPlayer(player))).isEmpty();
    }

    // This makes a move by making changes to the underlying board state
    // without animation
    // This is what is used during game simulation in minimax
    private void makeMove(Move move) {
        // If move destination at opposing sides edge then convert to king
        if ((move.getPiece().getSide() == Side.BOTTOM && move.getDest().getY() == 0) ||
                (move.getPiece().getSide() == Side.TOP && move.getDest().getY() == Board.HEIGHT-1)) {
            move.getPiece().makeKing();
        }

        // Update board state
        board.acceptMove(move);

        // Changing position state of piece
        move.getPiece().updatePositionTo(move.getDest());
    }

    // This makes a take by making changes to the underlying board state
    // without Animation
    // This is what is used during game simulation in minimax
    private void makeTake(Move move) {
        Take take = availableTakes.get(availableTakes.indexOf(move));

        Piece attacker = take.getPiece();
        Piece target = take.getTarget();

        // If the move led to a king conversion then convert
        // the taking piece to a king
        if (take.moveCausedKing()) {
            attacker.makeKing();
        }

        // Remove the taken piece from the opposing players list of pieces
        getPiecesForPlayer(otherPlayer(currentPlayer)).remove(target);

        // Update board state
        board.acceptMove(take);

        // Changing position state of piece
        take.getPiece().updatePositionTo(take.getDest());
    }

    // Completely reverses a move by changing board state to state
    // that existed before the given move was made
    // Used in minimax simulation
    private void undoMove(Move move) {
        Move reverse = new Move(move.getPiece(), move.getOrigin());

        // We convert the moving piece back to a normal piece
        // if the move led to a king conversion
        if (move.moveCausedKing()) {
            move.getPiece().demote();
        }

        board.acceptMove(reverse);
        move.getPiece().updatePositionTo(move.getOrigin());
    }

    // Completely reverses a take by changing board state to state
    // that existed before the given take was made
    // Used in minimax simulation
    private void undoTake(Take take) {
        Move reverse = new Move(take.getPiece(), take.getOrigin());
        board.acceptMove(reverse);
        board.tileAt(take.getTarget().getPosition()).setPiece(take.getTarget());
        take.getPiece().updatePositionTo(take.getOrigin());

        if (take.moveCausedKing()) {
            take.getPiece().demote();
        }
        if (take.getTarget().getSide() == Side.BOTTOM) {
            blackPieces.add(take.getTarget());
        } else {
            redPieces.add(take.getTarget());
        }
    }

    // Runs simulation of the game from the current board state
    // and the AI makes the best possible move based on this
    private void makeAIMove() {
        startSimulation();

        Move aiMove = getBestMove();
        if (availableTakes.contains(aiMove)) {
            makeTake(aiMove);
            animateTake(aiMove);
        } else {
            makeMove(aiMove);
            animateMove(aiMove);
        }
        nextMove();
    }

    // Runs minimax starting from a depth of zero
    private void startSimulation() {
        successorEvaluations = new ArrayList<>();
        minimax(0, player2);
    }

    // From a list of successor moves attain the best one for the current player
    private Move getBestMove() {
        return currentPlayer == player1 ? Collections.max(successorEvaluations).getMove() : Collections.min(successorEvaluations).getMove();
    }

    /*
    This is an implementation of minimax. To compute our successors we run board.findValidMoves
    and board.findForceTakes

    We follow the same pattern of forcing takes to be made if there are any otherwise make an available move

    Once we reach a depth of 0 re run evaluateBoardState which returns the current score of the board
     */
    private int minimax(int depth, Player player) {
        currentPlayer = player;
        List<Move> movesAvailable = board.findValidMoves(player, getPiecesForPlayer(player));
        List<Take> takesAvailable = board.findForceTakes(getPiecesForPlayer(player));

        availableMoves = movesAvailable;
        availableTakes = takesAvailable;

        if (player == player1 && playerHasWon(player)) {
            return 1000;
        } else if (player == player2 && playerHasWon(player)) {
            return -1000;
        }

        if (depth <= depthLimit) {
            if (player == player1) {
                int bestScore = Integer.MAX_VALUE;
                if (! takesAvailable.isEmpty()) {
                    for (Take take : takesAvailable) {
                        makeTake(take);
                        int eval = minimax(depth + 1, otherPlayer(player));
                        currentPlayer = player;
                        bestScore = Math.max(bestScore, eval);
                        undoTake(take);
                        if (depth == 0) {
                            successorEvaluations.add(new MoveAndScore(take, bestScore));
                        }
                        availableTakes = takesAvailable;
                    }
                    return bestScore;
                } else if (! movesAvailable.isEmpty()) {
                    for (Move move : availableMoves) {
                        makeMove(move);
                        int eval = minimax(depth + 1, otherPlayer(player));
                        currentPlayer = player;
                        bestScore = Math.max(bestScore, eval);
                        undoMove(move);
                        if (depth == 0) {
                            successorEvaluations.add(new MoveAndScore(move, bestScore));
                        }
                        availableMoves = movesAvailable;
                    }
                    return bestScore;
                }
            } else if (player == player2) {
                int bestScore = Integer.MIN_VALUE;
                if (! takesAvailable.isEmpty()) {
                    for (Take take : takesAvailable) {
                        makeTake(take);
                        int eval = minimax(depth + 1, otherPlayer(player));
                        currentPlayer = player;
                        bestScore = Math.min(bestScore, eval);
                        undoTake(take);
                        if (depth == 0) {
                            successorEvaluations.add(new MoveAndScore(take, bestScore));
                        }
                        availableTakes = takesAvailable;
                    }
                    return bestScore;

                } else if (! movesAvailable.isEmpty()) {
                    for (Move move : availableMoves) {
                        makeMove(move);
                        int eval = minimax(depth + 1, otherPlayer(player));
                        currentPlayer = player;
                        bestScore = Math.min(bestScore, eval);
                        undoMove(move);
                        if (depth == 0) {
                            System.out.println("Adding move");
                            successorEvaluations.add(new MoveAndScore(move, bestScore));
                        }
                        availableMoves = movesAvailable;
                    }
                    return bestScore;
                }
            }
        }
        return evaluateState(player);
    }

    // This method contains the game heuristic that is used in minimax
    private int evaluateState(Player player) {
        int takes = 5 * (12 - getPiecesForPlayer(otherPlayer(player)).size());
        long kingPiecesAttained = getPiecesForPlayer(player).stream()
                .filter(Piece::isKing)
                .count();

        return takes + (int) kingPiecesAttained;
    }


    // This method simply runs the animation of moves
    // and makes them appear in different locations
    private void animateMove(Move move) {
        move.getPiece().moveTo(move.getDest());
        if (move.moveCausedKing()) {
            move.getPiece().animateKingConversion();
        }
    }

    // This method runs the animation of takes and
    // removes target pieces from the board and updates
    // the position of the taking piece
    private void animateTake(Move move) {
        Take take = availableTakes.get(availableTakes.indexOf(move));
        take.getPiece().moveTo(take.getDest());
        take.getTarget().setVisible(false);

        // If the piece being taken is a king piece then convert the taking
        // piece to a king
        if (take.moveCausedKing()) {
            take.getPiece().animateKingConversion();
        }
    }

    // Prepare for the next players move by unmarking an available moves/takes
    // Change to the other player
    // Find available takes/moves for the next player
    // Mark any force takes
    private void nextMove() {
        unMarkValidMoves(availableMoves);
        unmarkForceTakes(availableTakes);

        changePlayer();

        availableMoves = board.findValidMoves(currentPlayer, getPiecesForPlayer(currentPlayer));
        availableTakes = board.findForceTakes(getPiecesForPlayer(currentPlayer));
        markForceTakes(availableTakes);

        printState();
    }

    // Change the current player to the opposing player
    private void changePlayer() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }

    // Return the opposing player to the player given
    private Player otherPlayer(Player player) {
        if (player == player1) {
            return player2;
        } else {
            return player1;
        }
    }

    // Return the list of pieces that belong to the player given
    private List<Piece> getPiecesForPlayer(Player player) {
        return player == player1 ? blackPieces : redPieces;
    }

    // Highlight the valid moves on the screen
    private void markValidMoves(List<Move> moves) {
        moves.forEach((move -> {
            move.getPiece().setStroke(Color.GREEN);
            move.getPiece().setStrokeWidth(4);
            board.tileAt(move.getDest()).setFill(Paint.valueOf("#a07e5d"));
        }));
    }

    // Remove highlights of valid moves on the screen
    private void unMarkValidMoves(List<Move> moves) {
        moves.forEach((move -> {
            move.getPiece().setStroke(move.getPiece().getDefaultStroke());
            move.getPiece().setStrokeWidth(1);
            board.tileAt(move.getDest()).setFill(Paint.valueOf("#d18b47"));
        }));

    }

    // Highlight force takes on the screen
    private void markForceTakes(List<Take> takes) {
        takes.forEach((take -> {
            take.getPiece().setStroke(Color.GREEN);
            take.getPiece().setStrokeWidth(4);
            board.tileAt(take.getDest()).setFill(Paint.valueOf("#c4513c"));
        }));
    }

    // Remove highlights for force takes from the screen
    private void unmarkForceTakes(List<Take> takes) {
        takes.forEach((take -> {
            Piece attacker = take.getPiece();
            attacker.setStroke(attacker.getDefaultStroke());
            attacker.setStrokeWidth(1);
            board.tileAt(take.getDest()).setFill(Paint.valueOf("#d18b47"));
        }));
    }

    // Simply runs the same method which finds normal takes
    // Exists for clarity
    private ArrayList<Take> findAdditionalTakes(Piece piece) {
        return board.findForceTakes(Collections.singletonList(piece));
    }

    // Sets up and displays all GUI components
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

    // Create and return side menu
    private VBox createSideMenu() {
        Label header = new Label("Checkers");
        header.setTextFill(Paint.valueOf("#aeb6ba"));
        header.setId("header");

        VBox playerSettings = createPlayerSettings();

        VBox difficultyBox = createDifficultyPane();

        VBox buttons = createGameButtons();

        updates = new TextArea();
        VBox updatesBox = new VBox(updates);
        updatesBox.setMaxWidth(200);
        updatesBox.setPadding(new Insets(10, 0, 0, 0));
        updates.setText("Welcome!\n");


        VBox sideMenu = new VBox();
        sideMenu.setMinWidth(200);
        sideMenu.getStyleClass().add("hbox");
        sideMenu.setId("side-menu");
        sideMenu.setPadding(new Insets(20, 20, 20, 20));

        sideMenu.getChildren().addAll(header, playerSettings, difficultyBox, buttons, updatesBox);

        return sideMenu;
    }

    // create and return difficulty slider
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


    // Create and return start, stop, instructions and hint button
    private VBox createGameButtons() {

        Button start = new Button("start");
        start.setPrefSize(100, 40);
        start.setOnAction((e) -> {
            if (!gameInProgress) {
                gameInProgress = true;
                setUpPieces();
                boardPane.getChildren().addAll(redPieces);
                boardPane.getChildren().addAll(blackPieces);
                startNewTurn();

                if (!player1.isHuman() && !player2.isHuman()) {
                    Thread AImatch = new Thread(() -> runAIGame());
                    AImatch.start();
                } else {
                    Thread regularMatch = new Thread(() -> startNormalGame());
                    regularMatch.start();
                }
            } else {
                System.out.println("Game already in progress");
            }
        });

        Button stop = new Button("stop");
        stop.setOnAction((event -> {
            resetGame();
        }));
        stop.setPrefSize(100, 40);

        Button instructions = new Button("Instructions");
        instructions.setPrefSize(100, 40);
        instructions.setOnAction((e) -> {
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
        hBox1.getChildren().addAll(stop, start);
        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(instructions, hint);

        VBox buttons = new VBox();
        buttons.getChildren().addAll(hBox1, hBox2);
        return buttons;
    }

    // Is run upon pressing 'stop'
    private void resetGame() {
        // Game no longer in progress
        // Stop button now does nothing
        gameInProgress = false;

        // Game has stopped. Hint button should show nothing if pressed
        // Therefore availableMoves and availableTakes must be reset
        unmarkForceTakes(availableTakes);
        unMarkValidMoves(availableMoves);
        availableTakes = new ArrayList<>();
        availableMoves = new ArrayList<>();

        // Remove piece graphics from board
        boardPane.getChildren().removeAll(redPieces);
        boardPane.getChildren().removeAll(blackPieces);

        // Wipe board state
        removePiecesFromBoard();

        // To start again all pieces must be reset
        redPieces = new ArrayList<>();
        blackPieces = new ArrayList<>();

        // First player is always player 1
        currentPlayer = player1;

    }

    // Remove every piece from the underlying board array
    private void removePiecesFromBoard() {
        for (int i = 0; i < Board.HEIGHT; i++) {
            for (int j = 0; j < Board.WIDTH; j++) {
                board.getState()[i][j].setPiece(null);
            }
        }
    }

    // Create player selection section with combo boxes
    private VBox createPlayerSettings() {
        HBox player1Options = new HBox();
        player1Options.getChildren().add(new Label("Player 1:"));

        ComboBox player1Class = new ComboBox();
        player1Class.getItems().addAll("Human", "AI");
        player1Class.getSelectionModel().selectFirst();
        player1Class.setOnAction((event -> {
            if (player1Class.getSelectionModel().getSelectedIndex() == 0) {
                player1.setIsHuman(true);
            } else {
                player1.setIsHuman(false);
            }
        }));
        player1Options.getChildren().add(player1Class);

        HBox player2Options = new HBox();
        player2Options.getChildren().add(new Label("Player 2:"));

        ComboBox player2Class = new ComboBox();
        player2Class.getItems().addAll("Human", "AI");
        player2Class.getSelectionModel().select("Human");
        player2Class.setOnAction((event -> {
            if (player2Class.getSelectionModel().getSelectedIndex() == 0) {
                player2.setIsHuman(true);
            } else {
                player2.setIsHuman(false);
            }
        }));
        player2Options.getChildren().add(player2Class);

        VBox settings = new VBox();
        settings.getChildren().addAll(player1Options, player2Options);
        return settings;
    }

    private void printState() {
        System.out.println("------------------------------------------------");
        System.out.println("Current Player: " + currentPlayer.getSide());
        System.out.println("Available Moves: " + availableMoves);
        System.out.println("Available Takes : " + availableTakes);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
