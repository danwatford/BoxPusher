package com.foomoo.box;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.Optional;

public class App extends Application {

    private static final Point2D UP_POINT = new Point2D(0, -1);
    private static final Point2D DOWN_POINT = new Point2D(0, 1);
    private static final Point2D LEFT_POINT = new Point2D(-1, 0);
    private static final Point2D RIGHT_POINT = new Point2D(1, 0);

    private static final String BOARD_DEF = "" +
            "XXXXXX\n" +
            "XX   X\n" +
            "X    X\n" +
            "X   @X\n" +
            "X  XXX\n" +
            "XXX";

    private Board board = new Board(BoardDefinition.fromString(BOARD_DEF));

    @Override
    public void start(Stage stage) throws Exception {
/*        Piece player = new Piece("@");
        board.addPiece(player, new Point2D(3, 4));

        Piece b1 = new Piece("A");
        board.addPiece(b1, new Point2D(3, 3));
        Piece b2 = new Piece("B");
        board.addPiece(b2, new Point2D(5, 5));

        Target t1 = new Target("a");
        Target t2 = new Target("b");
        board.addTargetForPiece(t1, b1, new Point2D(1, 7));
        board.addTargetForPiece(t2, b2, new Point2D(2, 2));*/

        Optional<Piece> optionalPlayer = board.getPlayerPiece();
        BoardView view = new BoardView(board, (cell) -> {
            optionalPlayer.ifPresent(player -> board.movePieceTo(player, cell));
        });

        stage.setTitle("Box Pusher");

        optionalPlayer.ifPresent(player -> addKeyHandler(view, player));
        stage.setScene(view);
        stage.show();
    }

    private void addKeyHandler(Scene scene, Piece piece) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, ke -> {
            Point2D currentPoint = board.getCellForPiece(piece);

            KeyCode keyCode = ke.getCode();
            switch (keyCode) {
                case UP:
                    board.movePieceTo(piece, currentPoint.add(UP_POINT));
                    break;
                case LEFT:
                    board.movePieceTo(piece, currentPoint.add(LEFT_POINT));
                    break;
                case DOWN:
                    board.movePieceTo(piece, currentPoint.add(DOWN_POINT));
                    break;
                case RIGHT:
                    board.movePieceTo(piece, currentPoint.add(RIGHT_POINT));
                    break;
                case ESCAPE:
                    Platform.exit();
            }
        });
    }
}
