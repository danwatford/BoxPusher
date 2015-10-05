package com.foomoo.box;

/**
 * Represents a location on a Board.
 */
public class Cell {
    private int row;
    private int column;

    public Cell(int row, int column) {
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
     * Returns a cell with the specified coordinates subtracted from
     * the coordinates of this cell.
     *
     * @param cell the cell whose coordinates are to be subtracted
     * @return the cell with subtracted coordinates
     * @throws NullPointerException if the specified {@code cell} is null
     */
    public Cell subtract(Cell cell) {
        return new Cell(getRow() - cell.getRow(), getColumn() - cell.getColumn());
    }

    /**
     * Returns a cell with the coordinates of the specified cell added to the
     * coordinates of this cell.
     *
     * @param cell the cell whose coordinates are to be added
     * @return the cell with added coordinates
     * @throws NullPointerException if the specified {@code cell} is null
     */
    public Cell add(Cell cell) {
        return new Cell(getRow() + cell.getRow(), getColumn() + cell.getColumn());
    }

    public String toString() {
        return String.format("Cell(%d,%d)", row, column);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof Cell) {
            Cell cell = (Cell) obj;
            return getRow() == cell.getRow() && getColumn() == cell.getColumn();
        } else {
            return false;
        }
    }
}
