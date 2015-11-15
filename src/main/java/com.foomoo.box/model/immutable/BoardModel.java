package com.foomoo.box.model.immutable;

import com.foomoo.box.Block;
import com.foomoo.box.Cell;
import com.foomoo.box.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class BoardModel {

    private final Player player;
    final Map<Block, Cell> blockCellsMap;

    private BoardModel(final BoardModelBuilder builder) {
        this.player = builder.player;
        this.blockCellsMap = new HashMap(builder.blockCellsMap);
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
        return recursiveBlockMove(builder, player, to, 2);
    }

    /**
     * Determine whether the give block can be moved to the target cell based on the current board layout and the number
     * pieces that can be pushed out of the way.
     *
     * @param block        The block to move.
     * @param targetCell   The cell to move the block to.
     * @param canPushCount The number of other blocks that can be pushed along in order to allow this block to move.
     * @return
     */
    private boolean canBlockMoveTo(final Block block, final Cell targetCell, final int canPushCount) {
        // Is the space occupied.
        if (!blockCellsMap.containsValue(targetCell)) {
            return true;
        }

        return false;
    }

    /**
     * Move the requested block, applying the change to the given builder. Recursively move other blocks if required as
     * long as the number of blocks that can be pushed is not breached.
     *
     * @param builder      The builder to apply moved blocks to.
     * @param block        The block to move.
     * @param targetCell   The cell to move the block to.
     * @param canPushCount The number of blocks that can be pushed in the directory of movement to allow the given block
     *                     to move.
     * @return A new BoardModel specifying the board following all the piece movements. The model will be absent if the
     * move could not be performed.
     */
    private Optional<BoardModel> recursiveBlockMove(final BoardModelBuilder builder, final Block block, final Cell targetCell, final int canPushCount) {
        if (canBlockMoveTo(block, targetCell, canPushCount)) {
            builder.blockCell(block, targetCell);

            // Do any other blocks need to be moved?
            Optional<Block> optionalBlockAtTarget = blockCellsMap.entrySet().stream().filter(e -> e.getValue().equals(targetCell)).map(Entry::getKey).findAny();
            // TODO - need to transform targetCell in direction of pushing.
            optionalBlockAtTarget.flatMap(b -> recursiveBlockMove(builder, b, targetCell, canPushCount-1));
            if (blockCellsMap.containsValue(targetCell)) {
            } else {
                return Optional.of(builder.build());
            }
        } else {
            return Optional.empty();
        }
    }


    public static class BoardModelBuilder {
        private Player player;
        private Map<Block, Cell> blockCellsMap = new HashMap<>();

        public BoardModelBuilder(final BoardModel originalModel) {
            this.player = originalModel.player;
            this.blockCellsMap.putAll(originalModel.blockCellsMap);
        }

        public BoardModelBuilder(final Player player, final Cell playerCell) {
            this.player = player;
            blockCell(player, playerCell);
        }

        public BoardModelBuilder blockCell(final Block block, final Cell cell) {
            blockCellsMap.put(block, cell);
            return this;
        }

        public BoardModel build() {
            return new BoardModel(this);
        }
    }
}
