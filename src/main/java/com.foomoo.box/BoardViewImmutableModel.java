package com.foomoo.box;

import com.foomoo.box.model.immutable.BoardModel;
import com.foomoo.box.model.immutable.BoardModelDiff;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a view of the Board, animating movement of blocks in response to changing to a new BoardModel and calling a
 * BoardCellClickedHandler in response to mouse clicks on a cell.
 */
public class BoardViewImmutableModel extends Scene {
    private static final int CELL_WIDTH = 50;
    private static final int CELL_HEIGHT = 50;

    private BoardModel boardModel;

    // Map of each block to its associated Pane. Panes are moved around the view to represent the movement of
    // the blocks.
    private final Map<Block, Pane> blockPaneMap = new HashMap<>();

    // Map of each target to its associated Pane.
    private final Map<Target, Pane> targetPaneMap = new HashMap<>();

    public BoardViewImmutableModel(final BoardModel boardModel) {
        this(boardModel, new Group());
    }

    private BoardViewImmutableModel(final BoardModel board, final Group group) {
        super(group, board.getColumnCount() * CELL_WIDTH, board.getRowCount() * CELL_HEIGHT, Color.WHITE);

        this.boardModel = board;

        Cell.range(board.getMinCell(), board.getMaxCell()).forEach(cell -> {
            final Rectangle r = new Rectangle(CELL_WIDTH * cell.getColumn(), CELL_HEIGHT * cell.getRow(), CELL_WIDTH, CELL_HEIGHT);
            r.setFill(Color.rgb(0, 0, 0, 0));
            r.setStrokeType(StrokeType.CENTERED);
            r.setStroke(Color.BLACK);

            group.getChildren().add(r);

            // Any targets at this cell position?
            board.getTargetAtCell(cell).ifPresent(target -> {
                final StackPane stackPane = createStackPane(target.getText(), cell);
                group.getChildren().add(stackPane);
                targetPaneMap.put(target, stackPane);
            });

            // Any pieces at this cell position?
            board.getBlockAtCell(cell).ifPresent(block -> {
                final StackPane stackPane = createStackPane(block.getText(), cell);
                group.getChildren().add(stackPane);
                blockPaneMap.put(block, stackPane);
            });
        });
    }

    private StackPane createStackPane(final String text, final Cell cell) {
        final Label label = new Label(text);
        label.setFont(new Font(CELL_HEIGHT / 2));

        final StackPane stackPane = new StackPane(label);
        stackPane.setPrefWidth(CELL_WIDTH);
        stackPane.setPrefHeight(CELL_HEIGHT);

        stackPane.setTranslateX(cell.getColumn() * CELL_WIDTH);
        stackPane.setTranslateY(cell.getRow() * CELL_HEIGHT);

        StackPane.setAlignment(label, Pos.CENTER);

        return stackPane;
    }

    public void setNextBoardModel(final BoardModel nextBoardModel) {

        final BoardModelDiff diff = new BoardModelDiff(boardModel, nextBoardModel);

        diff.getMovedBlocks().forEach(block -> nextBoardModel.getBlockCell(block).ifPresent(cell -> {
            final Pane pane = blockPaneMap.get(block);
            if (pane == null) {
                throw new RuntimeException("Pane not found for Block: " + block);
            } else {
                final Timeline moving = new Timeline(Animation.INDEFINITE,
                        new KeyFrame(Duration.seconds(0.5), new KeyValue(pane.translateXProperty(), cell.getColumn() * CELL_WIDTH)),
                        new KeyFrame(Duration.seconds(0.5), new KeyValue(pane.translateYProperty(), cell.getRow() * CELL_HEIGHT))
                );
                moving.play();
            }
        }));

        boardModel = nextBoardModel;
    }

}
