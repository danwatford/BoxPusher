package com.foomoo.box;

import com.foomoo.box.model.Vector;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.WritableValue;

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

    final BoardDefinition definition;
    PlayerJava player;
    final Map<Block, Cell> blocksMap = new HashMap<>();
    final Map<Target, TargetData> targetsDataMap = new HashMap<>();

    final BooleanProperty complete = new SimpleBooleanProperty();

    PieceMovedHandler pieceMovedHandler;

    /**
     * Construct a Board using the given BoardDefinition to define the starting state.
     *
     * @param boardDefinition The definition to set the Board's start state.
     */
    public Board(final BoardDefinition boardDefinition) {
        definition = boardDefinition;

        definition.getPlayerCell().ifPresent(cell -> {
            player = new PlayerJava("@");
            blocksMap.put(player, cell);
        });

        blocksMap.putAll(boardDefinition.getBlockCells());

        boardDefinition.getTargetCells().forEach((target, cell) -> {
            BooleanProperty property = new SimpleBooleanProperty();

            TargetData targetData = new TargetData();
            targetData.cell = cell;
            targetData.complete = property;

            targetsDataMap.put(target, targetData);

            rebindCompletionProperty();
        });

        boardDefinition.getBlockTargetMap().forEach((block, target) -> {
            TargetData data = targetsDataMap.computeIfAbsent(target, t -> {
                throw new RuntimeException("Target does not exist on Board: " + t);
            });

            data.block = block;
        });
    }

    /**
     * Set the handler to be notified when a block is moved.
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
     * @param cell The cell to test.
     * @return True if the cell is on the board and bounded by the walls of the game.
     */
    public boolean isSpaceOnBoard(final Cell cell) {
        return (cell.getColumn() < getCellColumns()) && (cell.getRow() < getCellRows()) && !definition.cellIsWall(cell.getRow(), cell.getColumn());
    }

    /**
     * Is the given cell available, i.e. not already occupied.
     *
     * @param cell The cell to test.
     * @return True if the cell is in the game and not currently occupied by a Block.
     */
    public boolean isSpaceFree(final Cell cell) {
        return isSpaceOnBoard(cell) && !getPieceAtCell(cell).isPresent();
    }

    /**
     * Rebinds the complete property to be dependant on all complete Property objects related to the Board's Targets.
     */
    private void rebindCompletionProperty() {
        complete.unbind();
        List<Property<Boolean>> targetProperties = targetsDataMap.values().stream().map(targetData -> targetData.complete).collect(Collectors.toList());
        complete.bind(Bindings.createBooleanBinding(() -> targetProperties.stream().map(WritableValue::getValue).allMatch(Boolean::booleanValue),
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
        return targetsDataMap.computeIfAbsent(target, t -> {
            throw new RuntimeException("Target does not exist on Board: " + t);
        });
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
    public Cell getCellForTarget(final Target target) {
        return getTargetData(target).cell;
    }

    /**
     * Gets the targets for the Board.
     *
     * @return A Stream of Targets
     */
    public Stream<Target> targets() {
        return targetsDataMap.keySet().stream();
    }

    /**
     * Get the cell for the given block.
     *
     * @param block The block to find the cell for.
     * @return The cell.
     * @throws RuntimeException if Block is not part of this Board.
     */
    public Cell getCellForPiece(final Block block) {
        Cell cell = blocksMap.get(block);
        if (cell == null) {
            throw new RuntimeException("Block not not part of board: " + block);
        }
        return cell;
    }

    /**
     * Find the Block, if any, at the given Cell.
     *
     * @param cell The cell to check for a Block.
     * @return An Optional of the Block at the requested Cell. Optional will be absent if no Block was found.
     */
    public Optional<Block> getPieceAtCell(final Cell cell) {
        return blocksMap.entrySet().stream().filter(entry -> entry.getValue().equals(cell)).map(Map.Entry::getKey).findAny();
    }

    /**
     * Returns the player Block, if a player has been defined.
     *
     * @return Optional of Block if a player has been defined, else absent.
     */
    public Optional<Block> getPlayer() {
        return Optional.ofNullable(player);
    }

    /**
     * Get a stream of possible cell positions. Note that this is a superset of the cells that are defined as on the
     * board and playable.
     *
     * @return Stream of cell positions.
     */
    public Stream<Cell> cellPositions() {
        Stream<Integer> xIndexStream = IntStream.range(0, getCellColumns()).boxed();

        return xIndexStream.flatMap(x -> {
            IntStream yIndexStream = IntStream.range(0, getCellRows());

            return yIndexStream.mapToObj(y -> new Cell(y, x));
        });
    }

    /**
     * Get a stream of cell positions on the board.
     *
     * @return Stream of cell positions.
     */
    public Stream<Cell> cellPositionsOnBoard() {
        return cellPositions().filter(this::isSpaceOnBoard);
    }

    /**
     * Move the given Block to the given cell location. The move will only be performed if it is valid, i.e. the space
     * is on the board, is within one space horizontally or vertically, and if the space is already occupied that the
     * occupying block can be pushed out of the way.
     *
     * @param block      The Block to move.
     * @param targetCell The location to move the block to.
     */
    public void movePieceTo(final Block block, final Cell targetCell) {
        movePieceTo(block, targetCell, 2, false);
    }

    /**
     * Sets the location of the given Block to the given Cell.
     *
     * @param block The Block to set to the new location.
     * @param cell  The Cell to place the Block at.
     * @throws RuntimeException if the Block is not part of this Board.
     */
    private void setPiecePosition(final Block block, final Cell cell) {
        Cell currentCell = getCellForPiece(block);

        // Do nothing if there is no change in position requested for the block.
        if (currentCell.equals(cell)) {
            return;
        }

        blocksMap.put(block, cell);
        if (pieceMovedHandler != null) {
            pieceMovedHandler.pieceMoved(block, cell);
        }

        // If there is a target for this block, update its complete property.
        Optional<Map.Entry<Target, TargetData>> possibleTarget;
        possibleTarget = targetsDataMap.entrySet().stream().filter(entry -> block.equals(entry.getValue().block)).findAny();
        possibleTarget.ifPresent(entry -> {
                    TargetData data = entry.getValue();
                    data.complete.set(data.cell.equals(cell));
                }
        );
    }

    /**
     * Tests whether the distance between the start and end location is permitted.
     *
     * @param startCell The start cell of the move.
     * @param endCell   The end cell of the move.
     * @return True if the move is permitted, false otherwise. Note this does not take into account whether the end
     * location is occupied and whether any occurpying block can be pushed out of the way.
     */
    private boolean moveVectorPermitted(final Cell startCell, final Cell endCell) {
        Vector diff = endCell.subtract(startCell);
        return (Math.abs(diff.getX()) == 1) ^ (Math.abs(diff.getY()) == 1);
    }

    /**
     * If possible, moves the given Block to the specified cell, pushing other pieces out of the way if needed.
     * <p>
     * The number of blocks which can be pushed is specified by <code>canPushBlockCount</code>.
     * <p>
     * If <code>dryRun</code> is true, don't perform the move, just report on whether it is possible.
     * <p>
     * Other pieces will only be pushed out of the way if they can move to another cell in the same direction as the
     * requested block. If the block to be moved/pushed is blocked by a wall, or another block if the number of pieces
     * that can be moved has been exceeded, then the move cannot be completed.
     * <p>
     * If any pieces are moved the board's PieceMovedHandler will be notified.
     *
     * @param block             The Block to move.
     * @param targetCell        The cell to move the Block to.
     * @param canPushBlockCount The number of Pieces the given Block can move if the target cell is occupied.
     * @param dryRun            If true, don't actually perform the move, just report on whether it is possible.
     * @return The move was succesfully performed.
     */
    private boolean movePieceTo(final Block block, final Cell targetCell, final int canPushBlockCount, final boolean dryRun) {
        Cell currentCell = blocksMap.get(block);
        if (!moveVectorPermitted(currentCell, targetCell)) {
            return false;
        }

        if (!isSpaceOnBoard(targetCell)) {
            return false;
        }

        if (isSpaceFree(targetCell)) {
            if (!dryRun) {
                setPiecePosition(block, targetCell);
            }
            return true;
        }

        if (canPushBlockCount <= 0) {
            return false;
        }

        Optional<Block> possiblePiece = getPieceAtCell(targetCell);
        if (!possiblePiece.isPresent()) {
            throw new RuntimeException("Block missing at cell but marked as occupied: " + targetCell);
        }

        Vector diff = targetCell.subtract(blocksMap.get(block));

        if (movePieceTo(possiblePiece.get(), targetCell.translate(diff), canPushBlockCount - 1, dryRun)) {
            if (!dryRun) {
                setPiecePosition(block, targetCell);
            }
            return true;
        }
        return false;
    }

    /**
     * Interface to be implemented by the handler call when a Block is moved.
     */
    @FunctionalInterface
    interface PieceMovedHandler {
        void pieceMoved(Block block, Cell newCell);
    }

    /**
     * Storage class for data related to a target.
     */
    private static class TargetData {
        Cell cell;
        Block block;
        BooleanProperty complete;
    }
}
