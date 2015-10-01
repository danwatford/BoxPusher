package com.foomoo.box;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class BoardView extends Scene {
    private static final int CELL_WIDTH = 100;
    private static final int CELL_HEIGHT = 100;

    private final Board board;
    private final Group group;
    private final BoardCellClickedHandler cellClickedHandler;

    private final Map<Piece, Label> piecesLabelMap = new HashMap<>();

    private BoardView(Board board, BoardCellClickedHandler handler, Group group) {
        super(group, board.getCellColumns() * CELL_WIDTH, board.getCellRows() * CELL_HEIGHT, Color.WHITE);
        this.board = board;
        this.cellClickedHandler = handler;
        this.group = group;

        board.targets().forEach(target -> {
            Point2D cell = board.getCellForTarget(target);
            Label targetLabel = new Label(target.getText());
            targetLabel.setFont(new Font(CELL_HEIGHT * 3 / 4));
            targetLabel.setTranslateX(cell.getX() * CELL_WIDTH);
            targetLabel.setTranslateY(cell.getY() * CELL_HEIGHT);

            targetLabel.setStyle("-fx-border-color: lightskyblue; -fx-background-color: lightskyblue");

            group.getChildren().add(targetLabel);

            Property<Boolean> property = board.getPropertyForTarget(target);
            property.addListener((observable, oldValue, newValue) -> {

                if (newValue.booleanValue()) {
                    targetLabel.setStyle("-fx-border-color: greenyellow; -fx-background-color: greenyellow");
                } else {
                    targetLabel.setStyle("-fx-border-color: lightskyblue; -fx-background-color: lightskyblue");
                }
            });
        });

        board.cellPositionsOnBoard().forEach(cell -> {
            Rectangle r = new Rectangle(CELL_WIDTH * cell.getX(), CELL_HEIGHT * cell.getY(), CELL_WIDTH, CELL_HEIGHT);
            r.setFill(Color.rgb(0, 0, 0, 0));
            r.setStrokeType(StrokeType.INSIDE);
            r.setStroke(Color.BLACK);

            r.setOnMouseClicked((mouseEvent) -> {
                handler.cellClicked(cell);
            });

            group.getChildren().add(r);

            // Any pieces at this cell position?
            Optional<Piece> optionalPiece = board.getPieceAtCell(cell);
            if (optionalPiece.isPresent()) {
                Piece piece = optionalPiece.get();
                Label label = new Label(piece.getText());
                label.setFont(new Font(CELL_HEIGHT / 2));
                label.setTranslateX(cell.getX() * CELL_WIDTH);
                label.setTranslateY(cell.getY() * CELL_HEIGHT);

                group.getChildren().add(label);

                piecesLabelMap.put(piece, label);
            }
        });

        board.setPieceMovedHandler((piece, point) -> {
            Label label = piecesLabelMap.get(piece);
            if (label == null) {
                throw new RuntimeException("Label not found for Piece");
            } else {
                Timeline moving = new Timeline(Animation.INDEFINITE,
                        new KeyFrame(Duration.seconds(0.5), new KeyValue(label.translateXProperty(), point.getX() * CELL_WIDTH)),
                        new KeyFrame(Duration.seconds(0.5), new KeyValue(label.translateYProperty(), point.getY() * CELL_HEIGHT))
                );
                moving.play();
            }
        });

        board.getCompleteProperty().addListener(((observable1, oldValue1, newValue1) -> {
            if (newValue1.booleanValue()) {
                Label l = new Label("Finished");
                group.getChildren().add(l);
            }
        }));
    }

    public BoardView(Board board, BoardCellClickedHandler handler) {
        this(board, handler, new Group());
    }


    @FunctionalInterface
    interface BoardCellClickedHandler {
        void cellClicked(Point2D clickedCell);
    }
}
