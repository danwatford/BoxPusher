package com.foomoo.box.model.immutable;

import com.foomoo.box.Block;
import com.foomoo.box.Cell;
import com.foomoo.box.Target;
import com.google.common.collect.ImmutableSet;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Tests for the BoardModelDiff class.
 */
@RunWith(JMockit.class)
public class BoardModelDiffTest {

    public static final Block TEST_BLOCK_1 = new Block("B1");
    public static final Block TEST_BLOCK_2 = new Block("B2");

    public static final Target TEST_TARGET_1 = new Target("T1");

    public static final Set<Block> TEST_BLOCK_SET_1 = Collections.singleton(TEST_BLOCK_1);
    public static final Set<Block> TEST_BLOCK_SET_2 = ImmutableSet.of(TEST_BLOCK_1, TEST_BLOCK_2);

    public static final Set<Target> TEST_TARGET_SET_1 = Collections.singleton(TEST_TARGET_1);

    public static final Cell TEST_CELL_1 = new Cell(0, 0);
    public static final Cell TEST_CELL_2 = new Cell(0, 1);
    public static final Cell TEST_CELL_3 = new Cell(0, 2);

    @Mocked
    private BoardModel first;
    @Mocked
    private BoardModel second;

    @Test
    public void detectsSingleMovedBlock() {

        withBlocks(first, TEST_BLOCK_SET_1);
        withBlocks(second, TEST_BLOCK_SET_1);

        withBlockCell(first, TEST_BLOCK_1, TEST_CELL_1);
        withBlockCell(second, TEST_BLOCK_1, TEST_CELL_2);

        final BoardModelDiff boardModelDiff = new BoardModelDiff(first, second);
//        final List<Block> movedBlocks = boardModelDiff.getMovedBlocks();
//
//        assertThat(movedBlocks, containsInAnyOrder(TEST_BLOCK_1));
    }

    @Test
    public void detectsMultipleMovedBlocks() {

        withBlocks(first, TEST_BLOCK_SET_2);
        withBlocks(second, TEST_BLOCK_SET_2);

        withBlockCell(first, TEST_BLOCK_1, TEST_CELL_1);
        withBlockCell(first, TEST_BLOCK_2, TEST_CELL_2);
        withBlockCell(second, TEST_BLOCK_1, TEST_CELL_2);
        withBlockCell(second, TEST_BLOCK_2, TEST_CELL_3);

        final BoardModelDiff boardModelDiff = new BoardModelDiff(first, second);
//        final List<Block> movedBlocks = boardModelDiff.getMovedBlocks();
//
//        assertThat(movedBlocks, containsInAnyOrder(TEST_BLOCK_1, TEST_BLOCK_2));
    }

    /**
     * Tests that a new completed target can be detected between BoardModels.
     */
    @Test
    public void detectSingleNewCompletedTarget() {
        withBlocks(first, TEST_BLOCK_SET_1);
        withBlocks(second, TEST_BLOCK_SET_1);

        withBlockCell(first, TEST_BLOCK_1, TEST_CELL_1);
        withBlockCell(second, TEST_BLOCK_1, TEST_CELL_2);

        withTargets(first, TEST_TARGET_SET_1);
        withTargets(second, TEST_TARGET_SET_1);

        withTargetCell(first, TEST_TARGET_1, TEST_CELL_2);
        withTargetCell(second, TEST_TARGET_1, TEST_CELL_2);

        final BoardModelDiff boardModelDiff = new BoardModelDiff(first, second);
        final List<Target> newTargets = boardModelDiff.getNewCompletedTargets();

        assertThat(newTargets, contains(TEST_TARGET_1));
    }

    /**
     * Tests that a new uncompleted target can be detected between BoardModels.
     */
    @Test
    public void detectSingleNewUncompletedTarget() {
        withBlocks(first, TEST_BLOCK_SET_1);
        withBlocks(second, TEST_BLOCK_SET_1);

        withBlockCell(first, TEST_BLOCK_1, TEST_CELL_1);
        withBlockCell(second, TEST_BLOCK_1, TEST_CELL_2);

        withTargets(first, TEST_TARGET_SET_1);
        withTargets(second, TEST_TARGET_SET_1);

        withTargetCell(first, TEST_TARGET_1, TEST_CELL_1);
        withTargetCell(second, TEST_TARGET_1, TEST_CELL_1);

        final BoardModelDiff boardModelDiff = new BoardModelDiff(first, second);
        final List<Target> newUncompletedTargets = boardModelDiff.getNewUncompletedTargets();

        assertThat(newUncompletedTargets, contains(TEST_TARGET_1));
    }

    /**
     * Tests that a completed target which exists in both models is not detected as a new completed target.
     */
    @Test
    public void notDetectExistingCompletedTarget() {
        withBlocks(first, TEST_BLOCK_SET_2);
        withBlocks(second, TEST_BLOCK_SET_2);

        withBlockCell(first, TEST_BLOCK_1, TEST_CELL_1);
        withBlockCell(first, TEST_BLOCK_2, TEST_CELL_2);
        withBlockCell(second, TEST_BLOCK_1, TEST_CELL_1);
        withBlockCell(second, TEST_BLOCK_2, TEST_CELL_3);

        withTargets(first, TEST_TARGET_SET_1);
        withTargets(second, TEST_TARGET_SET_1);

        withTargetCell(first, TEST_TARGET_1, TEST_CELL_1);
        withTargetCell(second, TEST_TARGET_1, TEST_CELL_1);

        final BoardModelDiff boardModelDiff = new BoardModelDiff(first, second);
        final List<Target> newCompletedTargets = boardModelDiff.getNewCompletedTargets();

        assertThat(newCompletedTargets, not(contains(TEST_TARGET_1)));
    }

    /**
     * Sets an expectation on the given BoardModel to return the given blocks when requested.
     *
     * @param boardModel The BoardModel to return blocks on.
     * @param blocks     The Blocks to return.
     */
    private void withBlocks(final BoardModel boardModel, final Set<Block> blocks) {
        new NonStrictExpectations() {{
            boardModel.getBlocks();
            result = blocks;
        }};
    }

    /**
     * Sets an expectation on the given BoardModel to return the given Cell when requested for the given Block.
     *
     * @param boardModel The BoardModel to return the Cell on.
     * @param block      The Block to return the Cell for.
     * @param cell       The Cell to return.
     */
    private void withBlockCell(final BoardModel boardModel, final Block block, final Cell cell) {
        new NonStrictExpectations() {{
            boardModel.getBlockCell(block);
            result = Optional.of(cell);

            boardModel.getBlockAtCell(cell);
            result = Optional.of(block);
        }};
    }

    /**
     * Sets an expectation on the given BoardModel to return the given targets when requested.
     *
     * @param boardModel The BoardModel to return targets on.
     * @param targets    The Targets to return.
     */
    private void withTargets(final BoardModel boardModel, final Set<Target> targets) {
        new NonStrictExpectations() {{
            boardModel.getTargets();
            result = targets;
        }};
    }

    /**
     * Sets an expectation on the given BoardModel to return the given Cell when requested for the given Target.
     *
     * @param boardModel The BoardModel to return the Cell on.
     * @param target     The Target to return the Cell for.
     * @param cell       The Cell to return.
     */
    private void withTargetCell(final BoardModel boardModel, final Target target, final Cell cell) {
        new NonStrictExpectations() {{
            boardModel.getTargetCell(target);
            result = Optional.of(cell);
        }};
    }

}
