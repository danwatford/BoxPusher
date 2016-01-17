package com.foomoo.box;

import com.foomoo.box.model.Vector;
import com.foomoo.box.model.immutable.BoardModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import scala.Option;

public class AppWithImmutableModel extends Application {

    private static final Vector UP_CELL = new Vector(-1, 0);
    private static final Vector DOWN_CELL = new Vector(1, 0);
    private static final Vector LEFT_CELL = new Vector(0, -1);
    private static final Vector RIGHT_CELL = new Vector(0, 1);

    private static final Player player = new Player("P1");

    private BoardModel model;

    @Override
    public void start(Stage stage) throws Exception {

        model = new BoardModel.BoardModelBuilder(player, new Cell(2, 2))
                .wall(new Cell(0,0), new Cell(10, 10))
                .blockCell(new Block("B1"), new Cell(4, 4))
                .blockCell(new Block("B2"), new Cell(6, 6))
                .blockCell(new Block("B3"), new Cell(8, 2))
                .targetCell(new Target("T1"), new Cell(3, 3))
                .targetCell(new Target("T2"), new Cell(5, 5))
                .build();

        final BoardViewImmutableModel2 view = new BoardViewImmutableModel2(model);

        stage.setTitle("Box Pusher");

        addKeyHandler(view);
        stage.setScene(view.getScene());
        stage.show();
    }

    private void addKeyHandler(final BoardViewImmutableModel2 view) {
        view.getScene().addEventHandler(KeyEvent.KEY_PRESSED, ke -> {

            final Option<Cell> playerCellOption = model.getBlockCell(player);
            if (playerCellOption.isDefined()) {
                final Cell currentCell = playerCellOption.get();

                final KeyCode keyCode = ke.getCode();
                Option<BoardModel> nextModelOption = scala.Option.apply(null);
                switch (keyCode) {
                    case UP:
                        nextModelOption = model.movePlayerPieceTo(currentCell.translate(UP_CELL));
                        break;
                    case LEFT:
                        nextModelOption = model.movePlayerPieceTo(currentCell.translate(LEFT_CELL));
                        break;
                    case DOWN:
                        nextModelOption = model.movePlayerPieceTo(currentCell.translate(DOWN_CELL));
                        break;
                    case RIGHT:
                        nextModelOption = model.movePlayerPieceTo(currentCell.translate(RIGHT_CELL));
                        break;
                    case ESCAPE:
                        Platform.exit();
                }

                if (nextModelOption.isDefined()) {
                    final BoardModel nextModel = nextModelOption.get();
                    model = nextModel;
                    view.setNextBoardModel(nextModel);
                }
            }
        });
    }

}
