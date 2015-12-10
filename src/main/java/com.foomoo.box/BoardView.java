package com.foomoo.box;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.Property;
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
import java.util.Optional;

/**
 * Provides a view of the Board, animating movement of blocks in response to notification of piece movement,
 * indicating when a block has reached its target, and calling a BoardCellClickedHandler in response to mouse clicks
 * on a cell.
 */
public class BoardView extends Scene {
    private static final int CELL_WIDTH = 100;
    private static final int CELL_HEIGHT = 100;

    // Map of each block to its associated Pane. Panes are moved around the view to represent the movement of
    // the blocks.
    private final Map<Block, Pane> piecesPaneMap = new HashMap<>();

    public BoardView(Board board, BoardCellClickedHandler handler) {
        this(board, handler, new Group());
    }

    private BoardView(final Board board, final BoardCellClickedHandler handler, final Group group) {
        super(group, board.getCellColumns() * CELL_WIDTH, board.getCellRows() * CELL_HEIGHT, Color.WHITE);

        board.targets().forEach(target -> {
            Cell cell = board.getCellForTarget(target);
            Label targetLabel = new Label(target.getText());
            targetLabel.setFont(new Font(CELL_HEIGHT / 2));

            StackPane stackPane = new StackPane(targetLabel);
            stackPane.setPrefWidth(CELL_WIDTH);
            stackPane.setPrefHeight(CELL_HEIGHT);
            stackPane.setTranslateX(cell.getColumn() * CELL_WIDTH);
            stackPane.setTranslateY(cell.getRow() * CELL_HEIGHT);
            stackPane.setStyle("-fx-border-color: lightskyblue; -fx-background-color: lightskyblue");

            group.getChildren().add(stackPane);

            Property<Boolean> property = board.getPropertyForTarget(target);
            property.addListener((observable, oldValue, newValue) -> {

                if (newValue) {
                    stackPane.setStyle("-fx-border-color: greenyellow; -fx-background-color: greenyellow");
                } else {
                    stackPane.setStyle("-fx-border-color: lightskyblue; -fx-background-color: lightskyblue");
                }
            });
        });

        board.cellPositionsOnBoard().forEach(cell -> {
            Rectangle r = new Rectangle(CELL_WIDTH * cell.getColumn(), CELL_HEIGHT * cell.getRow(), CELL_WIDTH, CELL_HEIGHT);
            r.setFill(Color.rgb(0, 0, 0, 0));
            r.setStrokeType(StrokeType.CENTERED);
            r.setStroke(Color.BLACK);

            r.setOnMouseClicked((mouseEvent) -> {
                handler.cellClicked(cell);
            });

            group.getChildren().add(r);

            // Any pieces at this cell position?
            Optional<Block> optionalPiece = board.getPieceAtCell(cell);
            if (optionalPiece.isPresent()) {
                Block block = optionalPiece.get();
                Label label = new Label(block.getText());
                label.setFont(new Font(CELL_HEIGHT / 2));

                StackPane stackPane = new StackPane(label);
                stackPane.setPrefWidth(CELL_WIDTH);
                stackPane.setPrefHeight(CELL_HEIGHT);

                stackPane.setTranslateX(cell.getColumn() * CELL_WIDTH);
                stackPane.setTranslateY(cell.getRow() * CELL_HEIGHT);

                StackPane.setAlignment(label, Pos.CENTER);

                group.getChildren().add(stackPane);

                piecesPaneMap.put(block, stackPane);
            }
        });

        board.setPieceMovedHandler((piece, point) -> {
            Pane pane = piecesPaneMap.get(piece);
            if (pane == null) {
                throw new RuntimeException("Label not found for Block");
            } else {
                Timeline moving = new Timeline(Animation.INDEFINITE,
                        new KeyFrame(Duration.seconds(0.5), new KeyValue(pane.translateXProperty(), point.getColumn() * CELL_WIDTH)),
                        new KeyFrame(Duration.seconds(0.5), new KeyValue(pane.translateYProperty(), point.getRow() * CELL_HEIGHT))
                );
                moving.play();
            }
        });

        board.getCompleteProperty().addListener(((observable1, oldValue1, newValue1) -> {
            if (newValue1) {
                Label l = new Label("Finished");
                group.getChildren().add(l);
            }
        }));
    }

    @FunctionalInterface
    interface BoardCellClickedHandler {
        void cellClicked(Cell clickedCell);
    }

}
