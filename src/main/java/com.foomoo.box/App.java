package com.foomoo.box;

import com.sun.javafx.scene.traversal.Direction;
import javafx.animation.Timeline;
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


    private Board board = new Board();

    @Override
    public void start(Stage stage) throws Exception {
        Piece player = new Piece("@");
        board.addPiece(player, new Point2D(3, 4));

        Piece b1 = new Piece("A");
        board.addPiece(b1, new Point2D(3, 3));
        Piece b2 = new Piece("B");
        board.addPiece(b2, new Point2D(5, 5));

        Target t1 = new Target("a");
        Target t2 = new Target("b");
        board.addTargetForPiece(t1, b1, new Point2D(1, 7));
        board.addTargetForPiece(t2, b2, new Point2D(2, 2));

        BoardView view = new BoardView(board, (cell) -> {
            board.movePieceTo(player, cell);
        });

        stage.setTitle("Box Pusher");

        addKeyHandler(view, player);
        stage.setScene(view);
        stage.show();
    }

    private void addKeyHandler(Scene scene, Piece piece) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, ke -> {
            Optional<Point2D> possiblePoint = board.getCellForPiece(piece);
            if (!possiblePoint.isPresent()) {
                throw new RuntimeException("Cell for player not found.");
            }

            Point2D currentPoint = possiblePoint.get();

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
