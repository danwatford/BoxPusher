package com.foomoo.box.model.immutable;

import com.foomoo.box.Block;
import com.foomoo.box.Cell;
import com.foomoo.box.Target;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Class to determine the difference between two {@link BoardModel} objects
 */
public class BoardModelDiff {

    private final BoardModel first;
    private final BoardModel second;

    /**
     * Construct a BoardModelDiff to determine differences between the given BoardModels.
     *
     * @param first  The first model.
     * @param second The second model.
     */
    public BoardModelDiff(final BoardModel first, final BoardModel second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Get the blocks that have changed cell when moving from the first to the second BoardModel.
     *
     * @return The List of Blocks.
     */
    public List<Block> getMovedBlocks() {

        final List<Block> commonBlocks = getCommonBlocks();

        return commonBlocks.stream().filter(block -> !first.getBlockCell(block).equals(second.getBlockCell(block))).collect(toList());
    }

    /**
     * Gets the targets that have been satisfied when moving from the first to the second BoardModel.
     *
     * @return The List of Targets.
     */
    public List<Target> getNewCompletedTargets() {
        final List<Target> firstCompletedTargets = getCompletedTargets(first);
        final List<Target> secondCompletedTargets = getCompletedTargets(second);

        return secondCompletedTargets.stream().filter(secondTarget -> !firstCompletedTargets.contains(secondTarget)).collect(toList());
    }

    /**
     * Gets the targets that have been unsatisfied when moving from the first to the second BoardModel.
     *
     * @return The List of Targets.
     */
    public List<Target> getNewUncompletedTargets() {
        final List<Target> firstCompletedTargets = getCompletedTargets(first);
        final List<Target> secondCompletedTargets = getCompletedTargets(second);

        return firstCompletedTargets.stream().filter(firstTarget -> !secondCompletedTargets.contains(firstTarget)).collect(toList());
    }

    /**
     * Get the Blocks that are present in both the first and second model.
     *
     * @return The List of Blocks.
     */
    private List<Block> getCommonBlocks() {
        final Set<Block> firstBlocks = first.getBlocks();
        final Set<Block> secondBlocks = second.getBlocks();

        return firstBlocks.stream().filter(secondBlocks::contains).collect(toList());
    }

    /**
     * Gets the completed targets for the given BoardModel.
     *
     * @param boardModel The BoardModel to get completed targets for.
     * @return The List of completed Targets.
     */
    private List<Target> getCompletedTargets(final BoardModel boardModel) {
        return boardModel.getTargets().stream().filter(target -> {
            final Optional<Cell> targetCellOptional = boardModel.getTargetCell(target);

            return targetCellOptional.flatMap(targetCell -> boardModel.getBlockAtCell(targetCell).filter(target::isValidBlock)).isPresent();

        }).collect(toList());
    }

}
