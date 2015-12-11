package com.foomoo.box;

import com.foomoo.box.model.Vector;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Spliterator.*;

/**
 * Represents a location on a Board.
 */
public class Cell {
    private final int row;
    private final int column;

    /**
     * Returns a Cell which is based on the minimum row and minimum column of the two given cells.
     *
     * @param cell1 First cell.
     * @param cell2 Second cell.
     * @return Cell representing minimal row and column.
     */
    public static Cell minimalCell(final Cell cell1, final Cell cell2) {
        return new Cell(min(cell1.row, cell2.row), min(cell1.column, cell2.column));
    }

    /**
     * Returns a Cell which is based on the maximal row and maximal column of the two given cells.
     *
     * @param cell1 First cell.
     * @param cell2 Second cell.
     * @return Cell representing minimal row and column.
     */
    public static Cell maximalCell(final Cell cell1, final Cell cell2) {
        return new Cell(max(cell1.row, cell2.row), max(cell1.column, cell2.column));
    }

    /**
     * Provides a stream of Cells representing all cell positions bounded by the two corner cells.
     * @param corner1 The first corner cell
     * @param corner2 The second corner cell
     * @return The stream of Cells.
     */
    public static Stream<Cell> range(final Cell corner1, final Cell corner2) {
        final Vector distanceVector = corner2.subtract(corner1);
        final int maxX = distanceVector.getX();
        final int maxY = distanceVector.getY();

        final int size = (maxX + 1) * (maxY + 1);

        final Iterator<Cell> iterator = new Iterator<Cell>() {

            int i = 0;
            int currentY = 0;
            int currentX = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public Cell next() {
                if (i < size) {
                    i++;
                    final Cell retCell = corner1.translate(new Vector(currentX, currentY));

                    currentX++;
                    if (currentX > maxX) {
                        currentX = 0;
                        currentY++;
                    }

                    return retCell;
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
        return StreamSupport.stream(
                Spliterators.spliterator(iterator, size, ORDERED | IMMUTABLE | DISTINCT | NONNULL | SIZED),
                false);
    }

    public Cell(final int row, final int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    /**
     * Returns a vector representing the difference between this and another Cell.
     *
     * @param cell the cell whose coordinates are to be subtracted
     * @return the vector representing the difference between the cells.
     * @throws NullPointerException if the specified {@code cell} is null
     */
    public Vector subtract(final Cell cell) {
        return new Vector(getRow() - cell.getRow(), getColumn() - cell.getColumn());
    }

    /**
     * Returns a cell based on this one with the given vector transform applied.
     *
     * @param vector to apply to this cell.
     * @return the cell resulting from the transform.
     * @throws NullPointerException if the specified {@code cell} is null
     */
    public Cell translate(final Vector vector) {
        return new Cell(row + vector.getX(), column + vector.getY());
    }

    @Override
    public String toString() {
        return String.format("Cell(%d,%d)", row, column);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cell)) {
            return false;
        }

        Cell cell = (Cell) obj;

        return new EqualsBuilder()
                .append(row, cell.row)
                .append(column, cell.column)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(row)
                .append(column)
                .toHashCode();
    }

}
