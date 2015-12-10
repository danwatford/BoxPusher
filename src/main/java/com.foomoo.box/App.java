package com.foomoo.box;

import com.foomoo.box.model.Vector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.Optional;

public class App extends Application {

    private static final Vector UP_CELL = new Vector(-1, 0);
    private static final Vector DOWN_CELL = new Vector(1, 0);
    private static final Vector LEFT_CELL = new Vector(0, -1);
    private static final Vector RIGHT_CELL = new Vector(0, 1);

    private static final String BOARD_DEF = "" +
            "XXXXXXXX\n" +
            "XX     X\n" +
            "X  a     cX\n" +
            "X  ABC  X\n" +
            "X         X\n" +
            "X       X\n" +
            "X     @  bX\n" +
            "X      X\n" +
            "X        XXX\n" +
            "XXX";

    private final Board board = new Board(BoardDefinition.fromString(BOARD_DEF));

    @Override
    public void start(Stage stage) throws Exception {
        Optional<Block> optionalPlayer = board.getPlayer();
        BoardView view = new BoardView(board, (cell) -> {
            optionalPlayer.ifPresent(player -> board.movePieceTo(player, cell));
        });

        stage.setTitle("Box Pusher");

        optionalPlayer.ifPresent(player -> addKeyHandler(view, player));
        stage.setScene(view);
        stage.show();
    }

    private void addKeyHandler(Scene scene, Block block) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, ke -> {
            Cell currentCell = board.getCellForPiece(block);

            KeyCode keyCode = ke.getCode();
            switch (keyCode) {
                case UP:
                    board.movePieceTo(block, currentCell.translate(UP_CELL));
                    break;
                case LEFT:
                    board.movePieceTo(block, currentCell.translate(LEFT_CELL));
                    break;
                case DOWN:
                    board.movePieceTo(block, currentCell.translate(DOWN_CELL));
                    break;
                case RIGHT:
                    board.movePieceTo(block, currentCell.translate(RIGHT_CELL));
                    break;
                case ESCAPE:
                    Platform.exit();
            }
        });
    }
}
