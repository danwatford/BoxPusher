package com.foomoo.box;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.Optional;

public class App extends Application {

    private static final Cell UP_CELL = new Cell(-1,0);
    private static final Cell DOWN_CELL = new Cell(1,0);
    private static final Cell LEFT_CELL = new Cell(0,-1);
    private static final Cell RIGHT_CELL = new Cell(0, 1);

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
/*        Block player = new Block("@");
        board.addPiece(player, new Point2D(3, 4));

        Block b1 = new Block("A");
        board.addPiece(b1, new Point2D(3, 3));
        Block b2 = new Block("B");
        board.addPiece(b2, new Point2D(5, 5));

        Target t1 = new Target("a");
        Target t2 = new Target("b");
        board.addTargetForPiece(t1, b1, new Point2D(1, 7));
        board.addTargetForPiece(t2, b2, new Point2D(2, 2));*/

        Optional<Block> optionalPlayer = board.getPlayerBlock();
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
                    board.movePieceTo(block, currentCell.add(UP_CELL));
                    break;
                case LEFT:
                    board.movePieceTo(block, currentCell.add(LEFT_CELL));
                    break;
                case DOWN:
                    board.movePieceTo(block, currentCell.add(DOWN_CELL));
                    break;
                case RIGHT:
                    board.movePieceTo(block, currentCell.add(RIGHT_CELL));
                    break;
                case ESCAPE:
                    Platform.exit();
            }
        });
    }
}
