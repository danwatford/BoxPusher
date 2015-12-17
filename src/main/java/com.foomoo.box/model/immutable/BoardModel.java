package com.foomoo.box.model.immutable;

import com.foomoo.box.*;
import com.foomoo.box.model.Vector;
import com.foomoo.box.model.Wall;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.IntStream;

public final class BoardModel {

    private final Player player;
    private final Map<Block, Cell> blockCellMap;
    private final Map<Target, Cell> targetCellMap;
    private final Cell minCell;
    private final Cell maxCell;

    private BoardModel(final BoardModelBuilder builder) {
        this.player = builder.player;
        this.blockCellMap = new HashMap<>(builder.blockCellMap);
        this.targetCellMap = new HashMap<>(builder.targetCellMap);

        this.minCell = builder.minCell;
        this.maxCell = builder.maxCell;
    }

    /**
     * Move the player to the given cell, pushing other blocks out of the way if necessary.
     *
     * @param to The cell to move the player to.
     * @return An optional of BoardModel specifying the new model following the movement of the player and any other
     * necessary pieces. The optional will be empty if the move could not be performed.
     */
    public Optional<BoardModel> movePlayerPieceTo(final Cell to) {
        BoardModelBuilder builder = new BoardModelBuilder(this);
        return recursiveBlockMove(builder, player, to, player.getPushStrength());
    }

    /**
     * Gets the cell for the given block.
     *
     * @param block The block to get the cell for.
     * @return Optional of the Cell if the block is present in the model, empty otherwise.
     */
    public Optional<Cell> getBlockCell(final Block block) {
        return Optional.ofNullable(blockCellMap.get(block));
    }

    /**
     * Gets the block at the given cell, if any.
     *
     * @param cell The cell to get the block for.
     * @return Optional of the Block at the cell. Empty if no block present.
     */
    public Optional<Block> getBlockAtCell(final Cell cell) {
        return blockCellMap.entrySet().stream().filter(entry -> Objects.equals(cell, entry.getValue())).map(Entry::getKey).findAny();
    }

    /**
     * Gets all blocks known to the model.
     *
     * @return The Set of Blocks.
     */
    public Set<Block> getBlocks() {
        return blockCellMap.keySet();
    }

    /**
     * Get the cell for the given target.
     *
     * @param target The target to get the cell for.
     * @return Optional of the Cell if the block is present in the model, empty otherwise.
     */
    public Optional<Cell> getTargetCell(final Target target) {
        return Optional.ofNullable(targetCellMap.get(target));
    }

    /**
     * Gets the target at the given cell, if any.
     *
     * @param cell The cell to get the target for.
     * @return Optional of Target at the cell. Empty if no target present.
     */
    public Optional<Target> getTargetAtCell(final Cell cell) {
        return targetCellMap.entrySet().stream().filter(entry -> Objects.equals(cell, entry.getValue())).map(Entry::getKey).findAny();
    }

    /**
     * Gets all targets known to the model.
     *
     * @return The Set of Targets.
     */
    public Set<Target> getTargets() {
        return targetCellMap.keySet();
    }

    public Cell getMinCell() {
        return minCell;
    }

    public Cell getMaxCell() {
        return maxCell;
    }

    public int getRowCount() {
        return maxCell.getRow() - minCell.getRow() + 1;
    }

    public int getColumnCount() {
        return maxCell.getColumn() - minCell.getColumn() + 1;
    }

    private Optional<BoardModel> moveBlocksBetweenCell(final BoardModelBuilder builder, final Block block, final Cell from, final Cell to, final int pushStrength) {

        // Assume the move will succeed and apply the change to the builder.
        builder.blockCell(block, to);

        // Which block is currently at the target.
        final Optional<Block> targetCellBlockOptional = getBlockAtCell(to);

        if (targetCellBlockOptional.isPresent()) {
            return targetCellBlockOptional.flatMap(targetCellBlock -> {
                // Get the push vector which can be applied to all blocks as necessarily.
                final Vector pushVector = to.subtract(from);

                if (pushStrength >= targetCellBlock.getEffortToMove()) {
                    final Vector translatedPushVector = targetCellBlock.translatePushVector(pushVector);
                    final Cell nextTargetCell = to.translate(translatedPushVector);

                    return moveBlocksBetweenCell(builder, targetCellBlock, to, nextTargetCell, pushStrength - targetCellBlock.getEffortToMove());
                } else {
                    return Optional.empty();
                }
            });
        } else {
            return Optional.ofNullable(builder.build());
        }
    }

    /**
     * Move the requested block, applying the change to the given builder. Push other blocks out of the way if required
     * as long as the number of blocks that can be pushed is not breached.
     *
     * @param builder      The builder to apply moved blocks to.
     * @param block        The block to move.
     * @param targetCell   The cell to move the block to.
     * @param pushStrength The number of blocks that can be pushed in the direction of movement to allow the given block
     *                     to move.
     * @return A new BoardModel specifying the board following all the piece movements. The model will be absent if the
     * move could not be performed.
     */
    private Optional<BoardModel> recursiveBlockMove(final BoardModelBuilder builder, final Block block, final Cell targetCell, final int pushStrength) {
        // Get the current cell for the block. If the block doesn't exist don't change the model.
        return Optional.ofNullable(blockCellMap.get(block)).flatMap(currentCell -> moveBlocksBetweenCell(builder, block, currentCell, targetCell, pushStrength));
    }


    public static class BoardModelBuilder {
        private final Player player;
        private final Map<Block, Cell> blockCellMap = new HashMap<>();
        private final Map<Target, Cell> targetCellMap = new HashMap<>();
        private Error error;
        private Cell minCell;
        private Cell maxCell;

        public BoardModelBuilder(final BoardModel originalModel) {
            this.player = originalModel.player;
            this.blockCellMap.putAll(originalModel.blockCellMap);
            this.targetCellMap.putAll(originalModel.targetCellMap);
        }

        public BoardModelBuilder(final Player player, final Cell playerCell) {
            this.player = player;
            blockCell(player, playerCell);
        }

        public BoardModelBuilder blockCell(final Block block, final Cell cell) {
            blockCellMap.put(block, cell);
            return this;
        }

        public BoardModelBuilder targetCell(final Target target, final Cell cell) {
            targetCellMap.put(target, cell);
            return this;
        }

        public BoardModelBuilder wall(final Cell cell) {
            blockCellMap.put(new Wall(), cell);
            return this;
        }

        public BoardModelBuilder wall(final Cell from, final Cell to) throws BoardModelException {
            // Only accept line walls in rows or columns.
            if (from.getRow() == to.getRow()) {
                IntStream.rangeClosed(from.getColumn(), to.getColumn()).mapToObj(column -> new Cell(from.getRow(), column)).forEach(this::wall);
            } else if (from.getColumn() == to.getColumn()) {
                IntStream.rangeClosed(from.getRow(), to.getRow()).mapToObj(row -> new Cell(row, from.getColumn())).forEach(this::wall);
            } else {
                throw new BoardModelException(String.format("Cannot create non-horizonal or non-vertical walls. Wall request from: %s, to: %s", from, to));
            }

            return this;
        }

        public BoardModel build() {
            blockCellMap.values().stream().reduce(Cell::minimalCell).ifPresent(cell -> minCell = cell);
            blockCellMap.values().stream().reduce(Cell::maximalCell).ifPresent(cell -> maxCell = cell);

            return new BoardModel(this);
        }
    }

}
