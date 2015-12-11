package com.foomoo.box;

import com.foomoo.box.model.Vector;
import com.foomoo.box.model.immutable.BoardModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.Optional;

public class AppWithImmutableModel extends Application {

    private static final Vector UP_CELL = new Vector(-1, 0);
    private static final Vector DOWN_CELL = new Vector(1, 0);
    private static final Vector LEFT_CELL = new Vector(0, -1);
    private static final Vector RIGHT_CELL = new Vector(0, 1);

    private static final Player player = new Player("P1");

    private BoardViewImmutableModel view;
    private BoardModel model;

    @Override
    public void start(Stage stage) throws Exception {

        model = new BoardModel.BoardModelBuilder(player, new Cell(2, 2))
                .wall(new Cell(0, 0), new Cell(0, 10))
                .wall(new Cell(10, 0), new Cell(10, 10))
                .wall(new Cell(0, 0), new Cell(10, 0))
                .wall(new Cell(0, 10), new Cell(10, 10))
                .blockCell(new Block("B1"), new Cell(4, 4))
                .blockCell(new Block("B2"), new Cell(6, 6))
                .blockCell(new Block("B3"), new Cell(8, 2))
                .build();

        view = new BoardViewImmutableModel(model);

        stage.setTitle("Box Pusher");

        addKeyHandler(view);
        stage.setScene(view);
        stage.show();
    }

    private void addKeyHandler(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, ke -> {

            final Optional<Cell> optionalPlayerCell = model.getBlockCell(player);
            optionalPlayerCell.ifPresent(currentCell -> {
                KeyCode keyCode = ke.getCode();
                Optional<BoardModel> nextModelOptional = Optional.empty();
                switch (keyCode) {
                    case UP:
                        nextModelOptional = model.movePlayerPieceTo(currentCell.translate(UP_CELL));
                        break;
                    case LEFT:
                        nextModelOptional = model.movePlayerPieceTo(currentCell.translate(LEFT_CELL));
                        break;
                    case DOWN:
                        nextModelOptional = model.movePlayerPieceTo(currentCell.translate(DOWN_CELL));
                        break;
                    case RIGHT:
                        nextModelOptional = model.movePlayerPieceTo(currentCell.translate(RIGHT_CELL));
                        break;
                    case ESCAPE:
                        Platform.exit();
                }

                nextModelOptional.ifPresent(nextModel -> {
                    model = nextModel;
                    view.setNextBoardModel(model);
                });
            });
        });
    }

}
