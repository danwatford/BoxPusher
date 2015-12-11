package com.foomoo.box.model;

import com.foomoo.box.Cell;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

/**
 * Tests for the {@link com.foomoo.box.Cell} class.
 */
public class CellTest {

    private static final Cell MIN_CELL = new Cell(-5, -5);
    private static final Cell MAX_CELL = new Cell(3, 3);

    @Test
    public void rangeIncludesMinCell() {
        final Stream<Cell> cellStream = Cell.range(MIN_CELL, MAX_CELL);

        final List<Cell> cellList = cellStream.collect(toList());
        assertThat(cellList, hasItem(MIN_CELL));
    }

    @Test
    public void rangeIncludesMaxCell() {
        final Stream<Cell> cellStream = Cell.range(MIN_CELL, MAX_CELL);

        final List<Cell> cellList = cellStream.collect(toList());
        assertThat(cellList, hasItem(MAX_CELL));
    }

}
