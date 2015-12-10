package com.foomoo.box.model.immutable;

import com.foomoo.box.Block;
import com.foomoo.box.Cell;
import com.google.common.collect.ImmutableSet;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * Tests for the BoardModelDiff class.
 */
@RunWith(JMockit.class)
public class BoardModelDiffTest {

    public static final Block TEST_BLOCK_1 = new Block("B1");
    public static final Block TEST_BLOCK_2 = new Block("B2");

    public static final Set<Block> TEST_BLOCK_SET_1 = Collections.singleton(TEST_BLOCK_1);
    public static final Set<Block> TEST_BLOCK_SET_2 = ImmutableSet.of(TEST_BLOCK_1, TEST_BLOCK_2);

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
        final List<Block> movedBlocks = boardModelDiff.getMovedBlocks();

        assertThat(movedBlocks, containsInAnyOrder(TEST_BLOCK_1));
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
        final List<Block> movedBlocks = boardModelDiff.getMovedBlocks();

        assertThat(movedBlocks, containsInAnyOrder(TEST_BLOCK_1, TEST_BLOCK_2));
    }

    private void withBlocks(final BoardModel boardModel, final Set<Block> blocks) {
        new Expectations() {{
            boardModel.getBlocks();
            result = blocks;
        }};
    }

    private void withBlockCell(final BoardModel boardModel, final Block block, final Cell cell) {
        new Expectations() {{
            boardModel.getBlockCell(block);
            result = Optional.of(cell);
        }};
    }

}
