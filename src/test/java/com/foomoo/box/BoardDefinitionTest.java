package com.foomoo.box;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 */
public class BoardDefinitionTest {

    private static final String DEF1 = "" +
            "X\n" +
            "X  @cX\n" +
            "XA a X\n" +
            "XXB X\n" +
            "XXXXXX";

    private final BoardDefinition definition1 = BoardDefinition.fromString(DEF1);

    @Test
    public void definitionHasWalls() throws Exception {
        assertThat(definition1.getWidth(), equalTo(6));
        assertThat(definition1.getHeight(), equalTo(5));

        assertThat(definition1.cellIsWall(0, 0), equalTo(true));
        assertThat(definition1.cellIsWall(0, 1), equalTo(true));
        assertThat(definition1.cellIsWall(1, 1), equalTo(false));
        assertThat(definition1.cellIsWall(2, 2), equalTo(false));
        assertThat(definition1.cellIsWall(3, 3), equalTo(false));
        assertThat(definition1.cellIsWall(3, 4), equalTo(true));
        assertThat(definition1.cellIsWall(4, 4), equalTo(true));
    }

    @Test
    public void definitionContainsAllBlocks() {
        Map<Block, Cell> blockCellMap = definition1.getBlockCells();

        assertThat(blockCellMap, Matchers.hasEntry(new Block("A"), new Cell(2, 1)));
        assertThat(blockCellMap, Matchers.hasEntry(new Block("B"), new Cell(3, 2)));
    }

    @Test
    public void definitionContainsAllTargets() {
        Map<Target, Cell> targetCellMap = definition1.getTargetCells();

        assertThat(targetCellMap, Matchers.hasEntry(new Target("a"), new Cell(2, 3)));
        assertThat(targetCellMap, Matchers.hasEntry(new Target("c"), new Cell(1, 4)));
    }

    @Test
    public void definitionContainsLinkedBlocksAndTargets() {
        Map<Block, Target> blockTargetMap = definition1.getBlockTargetMap();

        assertThat(blockTargetMap, Matchers.hasEntry(new Block("A"), new Target("a")));
    }
}