package com.foomoo.box;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Board {

    PieceMovedHandler pieceMovedHandler;
    Map<Piece, Point2D> piecesMap = new HashMap<>();

    Map<Target, Point2D> targetsMap = new HashMap<>();

    Map<Target, Piece> targetPieceMap = new HashMap<>();

    Map<Target, BooleanProperty> targetsPropertyMap = new HashMap<>();

    public void setPieceMovedHandler(PieceMovedHandler pieceMovedHandler) {
        this.pieceMovedHandler = pieceMovedHandler;
    }

    public boolean isSpaceOnBoard(final Point2D point) {
        return (point.getY() >= 1 && point.getY() < getCellRows() - 1 && point.getX() >= 1 && point.getX() < getCellColumns() - 1);
    }

    public boolean isSpaceFree(final Point2D point) {
        return isSpaceOnBoard(point) && !getPieceAtCell(point).isPresent();
    }

    public boolean addPiece(final Piece piece, final Point2D point) {
        if (isSpaceFree(point)) {
            piecesMap.put(piece, point);
            return true;
        }
        return false;
    }

    public Property<Boolean> addTargetForPiece(final Target target, final Piece piece, final Point2D point) {
        targetsMap.put(target, point);
        targetPieceMap.put(target, piece);

        BooleanProperty property = new SimpleBooleanProperty();
        targetsPropertyMap.put(target, property);

        return property;
    }

    public BooleanProperty getPropertyForTarget(final Target target) {
        return targetsPropertyMap.get(target);
    }

    public Optional<Point2D> getCellForTarget(final Target target) {
        return Optional.ofNullable(targetsMap.get(target));
    }

    public Stream<Target> targets() {
        return targetsMap.keySet().stream();
    }

    public Optional<Point2D> getCellForPiece(final Piece piece) {
        return Optional.ofNullable(piecesMap.get(piece));
    }

    public Optional<Piece> getPieceAtCell(final Point2D point) {
        return piecesMap.entrySet().stream().filter(entry -> entry.getValue().equals(point)).map(entry -> entry.getKey()).findAny();
    }

    public void setPiecePosition(final Piece piece, final Point2D point) {
        Point2D currentPoint = piecesMap.get(piece);
        if (currentPoint != null) {
            if (currentPoint.equals(point)) {
                return;
            }
        }
        piecesMap.put(piece, point);

        if (pieceMovedHandler != null) {
            pieceMovedHandler.pieceMoved(piece, point);
        }

        Optional<Target> possibleTarget = targetPieceMap.entrySet().stream().filter(entry -> entry.getValue().equals(piece)).map(entry -> entry.getKey()).findAny();
        possibleTarget.ifPresent(target -> {
                    Point2D targetPoint = targetsMap.get(target);
                    BooleanProperty property = targetsPropertyMap.get(target);
                    property.set(targetPoint.equals(point));

                }
        );
    }

    public int getCellColumns() {
        return 9;
    }

    public int getCellRows() {
        return 16;
    }

    /* Return a stream of possible cell positions on the board.
        Note that this is a superset of the cells that are defined as on the board and playable.
     */
    public Stream<Point2D> cellPositions() {
        Stream<Integer> xIndexStream = IntStream.range(0, getCellColumns()).boxed();

        return xIndexStream.flatMap(x -> {
            IntStream yIndexStream = IntStream.range(0, getCellRows());

            return yIndexStream.mapToObj(y -> new Point2D(x, y));
        });
    }

    public Stream<Point2D> cellPositionsOnBoard() {
        return cellPositions().filter(this::isSpaceOnBoard);
    }

    public boolean pieceCanMove(final Piece piece, final Point2D targetPoint) {
        return movePieceTo(piece, targetPoint, 1, true);
    }

    private boolean moveVectorPermitted(final Point2D startPoint, final Point2D endPoint) {
        Point2D diff = endPoint.subtract(startPoint);
        return (Math.abs(diff.getX()) == 1) ^ (Math.abs(diff.getY()) == 1);
    }

    private boolean movePieceTo(final Piece piece, final Point2D targetPoint, final int canPushBlockCount, final boolean dryRun) {
        Point2D currentPoint = piecesMap.get(piece);
        if (!moveVectorPermitted(currentPoint, targetPoint)) {
            return false;
        }

        if (!isSpaceOnBoard(targetPoint)) {
            return false;
        }

        if (isSpaceFree(targetPoint)) {
            if (!dryRun) {
                setPiecePosition(piece, targetPoint);
            }
            return true;
        }

        if (canPushBlockCount <= 0) {
            return false;
        }

        Optional<Piece> possiblePiece = getPieceAtCell(targetPoint);
        if (!possiblePiece.isPresent()) {
            throw new RuntimeException("Piece missing at point but marked as occupied: " + targetPoint);
        }

        Point2D diff = targetPoint.subtract(piecesMap.get(piece));

        if (movePieceTo(possiblePiece.get(), targetPoint.add(diff), canPushBlockCount - 1, dryRun)) {
            if (!dryRun) {
                setPiecePosition(piece, targetPoint);
            }
            return true;
        }
        return false;
    }

    public void movePieceTo(final Piece piece, final Point2D targetPoint) {
        movePieceTo(piece, targetPoint, 1, false);
    }

    public boolean areTargetsMet() {
        return targets().map(target -> {
            Point2D targetPoint = getCellForTarget(target).orElseThrow(RuntimeException::new);
            Piece targetPiece = Optional.ofNullable(targetPieceMap.get(target)).orElseThrow(RuntimeException::new);
            Point2D piecePoint2D = getCellForPiece(targetPiece).orElseThrow(RuntimeException::new);

            return piecePoint2D.equals(targetPoint);
        }).allMatch(val -> val);
    }

    @FunctionalInterface
    interface PieceMovedHandler {
        void pieceMoved(Piece piece, Point2D newCell);
    }
}
