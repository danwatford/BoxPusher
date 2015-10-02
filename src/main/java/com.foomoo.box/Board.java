package com.foomoo.box;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Defines the game layout and enforces movement rules.
 * <p>
 * Notifies a PieceMovedHandler when pieces are moved on the board. Intended for use by the UI.
 * <p>
 * Maintains a boolean property for each target which is true when the target is satisfied.
 * <p>
 * Maintains a game completed boolean property which is true when all targets have been satisfied.
 */
public class Board {

    PieceMovedHandler pieceMovedHandler;
    Piece playerPiece;
    Map<Piece, Point2D> piecesMap = new HashMap<>();
    Map<Target, TargetData> targetsMap = new HashMap<>();

    BooleanProperty complete = new SimpleBooleanProperty();

    final BoardDefinition definition;

    /**
     * Construct a Board using the given BoardDefinition to define the starting state.
     *
     * @param boardDefinition The definition to set the Board's start state.
     */
    public Board(final BoardDefinition boardDefinition) {
        definition = boardDefinition;

        definition.getPlayerCell().ifPresent(point -> {
            playerPiece = new Piece("@");
            piecesMap.put(playerPiece, point);
        });
    }

    /**
     * Set the handler to be notified when a piece is moved.
     *
     * @param pieceMovedHandler Handler to call.
     */
    public void setPieceMovedHandler(PieceMovedHandler pieceMovedHandler) {
        this.pieceMovedHandler = pieceMovedHandler;
    }

    /**
     * Gets the number of horizontal cells that are required to span the cells in play for the Board.
     *
     * @return The number of cells required to cover the width of the Board.
     */
    public int getCellColumns() {
        return definition.getWidth();
    }

    /**
     * Gets the number of vertical cells that are required to span the cells in play for the Board.
     *
     * @return The number of cells required to cover the height of the Board.
     */
    public int getCellRows() {
        return definition.getHeight();
    }

    /**
     * Is the given cell exist on the board, i.e. is it bounded by the walls of the game.
     *
     * @param point The cell to test.
     * @return True if the cell is on the board and bounded by the walls of the game.
     */
    public boolean isSpaceOnBoard(final Point2D point) {
        return (point.getX() < getCellColumns()) && (point.getY() < getCellRows()) && !definition.cellIsWall((int) point.getY(), (int) point.getX());
    }

    /**
     * Is the given cell available, i.e. not already occupied.
     *
     * @param point The cell to test.
     * @return True if the cell is in the game and not currently occupied by a Piece.
     */
    public boolean isSpaceFree(final Point2D point) {
        return isSpaceOnBoard(point) && !getPieceAtCell(point).isPresent();
    }

    /**
     * Add a new target to the game, located at the given point and associated with the given piece.
     *
     * @param target The new target.
     * @param piece  The piece associated with the target.
     * @param point  The location to place the target.
     * @return A boolean property used to track whether the target has been completed (i.e. the associated piece is
     * located at the same point as the target.)
     */
    public Property<Boolean> addTargetForPiece(final Target target, final Piece piece, final Point2D point) {
        BooleanProperty property = new SimpleBooleanProperty();

        TargetData targetData = new TargetData();
        targetData.point = point;
        targetData.piece = piece;
        targetData.complete = property;

        targetsMap.put(target, targetData);

        rebindCompletionProperty();

        return property;
    }

    /**
     * Rebinds the complete property to be dependant on all complete Property objects related to the Board's Targets.
     */
    private void rebindCompletionProperty() {
        complete.unbind();
        List<Property<Boolean>> targetProperties = targetsMap.values().stream().map(targetData -> targetData.complete).collect(Collectors.toList());
        complete.bind(Bindings.createBooleanBinding(() -> {
                    return targetProperties.stream().map(prop -> prop.getValue()).allMatch(Boolean::booleanValue);
                },
                targetProperties.toArray(new Property[1])));
    }

    /**
     * Gets the property used to track whether the game has completed.
     *
     * @return Property which is true when the game has completed, false otherwise.
     */
    public Property<Boolean> getCompleteProperty() {
        return complete;
    }

    /**
     * Get the target's data, throwing an exception if the target is not part of this board.
     *
     * @param target The target to get the data for.
     * @return The target's data.
     * @throws RuntimeException if the Target is not part of this Board.
     */
    private TargetData getTargetData(final Target target) {
        TargetData data = targetsMap.get(target);
        if (data == null) {
            throw new RuntimeException("Target does not exist on Board: " + target);
        }
        return data;
    }

    /**
     * Gets the completion property for the given target.
     *
     * @param target The target to get the property of.
     * @return The property.
     * @throws RuntimeException if the Target is not part of this Board.
     */
    public Property<Boolean> getPropertyForTarget(final Target target) {
        return getTargetData(target).complete;
    }

    /**
     * Gets the location for the given target.
     *
     * @param target The target to get the location of.
     * @return The location.
     * @throws RuntimeException if the Target is not part of this Board.
     */
    public Point2D getCellForTarget(final Target target) {
        return getTargetData(target).point;
    }

    /**
     * Gets the targets for the Board.
     *
     * @return A Stream of Targets
     */
    public Stream<Target> targets() {
        return targetsMap.keySet().stream();
    }

    /**
     * Add a new piece to the board.
     *
     * @param piece The Piece to add.
     * @param point The cell to add the piece to.
     * @throws RuntimeException If the piece cannot be added due to the cell not being free.
     */
    public void addPiece(final Piece piece, final Point2D point) {
        if (isSpaceFree(point)) {
            piecesMap.put(piece, point);
        } else {
            throw new RuntimeException("Cannot add piece at cell, location in use or not valid for board: " + point);
        }
    }

    /**
     * Get the cell for the given piece.
     *
     * @param piece The piece to find the cell for.
     * @return The cell.
     * @throws RuntimeException if Piece is not part of this Board.
     */
    public Point2D getCellForPiece(final Piece piece) {
        Point2D point = piecesMap.get(piece);
        if (point == null) {
            throw new RuntimeException("Piece not not part of board: " + piece);
        }
        return point;
    }

    /**
     * Find the Piece, if any, at the given Cell.
     *
     * @param point The cell to check for a Piece.
     * @return An Optional of the Piece at the requested Cell. Optional will be absent if no Piece was found.
     */
    public Optional<Piece> getPieceAtCell(final Point2D point) {
        return piecesMap.entrySet().stream().filter(entry -> entry.getValue().equals(point)).map(Map.Entry::getKey).findAny();
    }

    /**
     * Returns the player Piece, if a player has been defined.
     *
     * @return Optional of Piece if a player has been defined, else absent.
     */
    public Optional<Piece> getPlayerPiece() {
        return Optional.ofNullable(playerPiece);
    }

    /**
     * Get a stream of possible cell positions.
     * Note that this is a superset of the cells that are defined as on the board and playable.
     *
     * @return Stream of cell positions.
     */
    public Stream<Point2D> cellPositions() {
        Stream<Integer> xIndexStream = IntStream.range(0, getCellColumns()).boxed();

        return xIndexStream.flatMap(x -> {
            IntStream yIndexStream = IntStream.range(0, getCellRows());

            return yIndexStream.mapToObj(y -> new Point2D(x, y));
        });
    }

    /**
     * Get a stream of cell positions on the board.
     *
     * @return Stream of cell positions.
     */
    public Stream<Point2D> cellPositionsOnBoard() {
        return cellPositions().filter(this::isSpaceOnBoard);
    }

    /**
     * Move the given Piece to the given cell location.
     * The move will only be performed if it is valid, i.e. the space is on the board, is within one space
     * horizontally or vertically, and if the space is already occupied that the occupying piece can be pushed
     * out of the way.
     *
     * @param piece       The Piece to move.
     * @param targetPoint The location to move the piece to.
     */
    public void movePieceTo(final Piece piece, final Point2D targetPoint) {
        movePieceTo(piece, targetPoint, 1, false);
    }

    /**
     * Sets the location of the given Piece to the given Cell.
     *
     * @param piece The Piece to set to the new location.
     * @param point The Cell to place the Piece at.
     * @throws RuntimeException if the Piece is not part of this Board.
     */
    private void setPiecePosition(final Piece piece, final Point2D point) {
        Point2D currentPoint = getCellForPiece(piece);

        // Do nothing if there is no change in position requested for the piece.
        if (currentPoint.equals(point)) {
            return;
        }

        piecesMap.put(piece, point);
        if (pieceMovedHandler != null) {
            pieceMovedHandler.pieceMoved(piece, point);
        }

        // If there is a target for this piece, update its complete property.
        Optional<Map.Entry<Target, TargetData>> possibleTarget = targetsMap.entrySet().stream().filter(entry -> entry.getValue().piece.equals(piece)).findAny();
        possibleTarget.ifPresent(entry -> {
                    TargetData data = entry.getValue();
                    data.complete.set(data.point.equals(point));
                }
        );
    }

    /**
     * Tests whether the distance between the start and end location is permitted.
     *
     * @param startPoint The start point of the move.
     * @param endPoint   The end point of the move.
     * @return True if the move is permitted, false otherwise. Note this does not take into account whether the end location
     * is occupied and whether any occurpying block can be pushed out of the way.
     */
    private boolean moveVectorPermitted(final Point2D startPoint, final Point2D endPoint) {
        Point2D diff = endPoint.subtract(startPoint);
        return (Math.abs(diff.getX()) == 1) ^ (Math.abs(diff.getY()) == 1);
    }

    /**
     * If possible, moves the given Piece to the specified cell, pushing other pieces out of the way if needed.
     * <p>
     * The number of blocks which can be pushed is specified by <code>canPushBlockCount</code>.
     * <p>
     * If <code>dryRun</code> is true, don't perform the move, just report on whether it is possible.
     * <p>
     * Other pieces will only be pushed out of the way if they can move to another cell in the same direction as the requested piece.
     * If the piece to be moved/pushed is blocked by a wall, or another piece if the number of pieces that can be moved has been
     * exceeded, then the move cannot be completed.
     * <p>
     * If any pieces are moved the board's PieceMovedHandler will be notified.
     *
     * @param piece             The Piece to move.
     * @param targetPoint       The cell to move the Piece to.
     * @param canPushBlockCount The number of Pieces the given Piece can move if the target cell is occupied.
     * @param dryRun            If true, don't actually perform the move, just report on whether it is possible.
     * @return The move was succesfully performed.
     */
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

    /**
     * Tests whether all targets have been met.
     *
     * @return True if all targets met, false otherwise.
     */
    public boolean areTargetsMet() {

        return targetsMap.values().stream().map(targetData -> {
            Point2D piecePoint = getCellForPiece(targetData.piece);
            return piecePoint.equals(targetData.point);
        }).allMatch(Boolean::booleanValue);
    }

    /**
     * Interface to be implemented by the handler call when a Piece is moved.
     */
    @FunctionalInterface
    interface PieceMovedHandler {
        void pieceMoved(Piece piece, Point2D newCell);
    }

    /**
     * Storage class for data related to a target.
     */
    private static class TargetData {
        Point2D point;
        Piece piece;
        BooleanProperty complete;
    }
}
