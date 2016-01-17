package com.foomoo.box

import java.lang.Math.{max, min}
import java.util.Spliterator._
import java.util.stream.{Stream, StreamSupport}
import java.util.{NoSuchElementException, Spliterators}

import com.foomoo.box.model.Vector
import org.apache.commons.lang3.builder.{EqualsBuilder, HashCodeBuilder}

/**
  * Represents a location on a Board.
  */
object Cell {
  /**
    * Returns a Cell which is based on the minimum row and minimum column of the two given cells.
    *
    * @param cell1 First cell.
    * @param cell2 Second cell.
    * @return Cell representing minimal row and column.
    */
  def minimalCell(cell1: Cell, cell2: Cell): Cell = new Cell(min(cell1.row, cell2.row), min(cell1.column, cell2.column))

  /**
    * Returns a Cell which is based on the maximal row and maximal column of the two given cells.
    *
    * @param cell1 First cell.
    * @param cell2 Second cell.
    * @return Cell representing minimal row and column.
    */
  def maximalCell(cell1: Cell, cell2: Cell): Cell = new Cell(max(cell1.row, cell2.row), max(cell1.column, cell2.column))

  /**
    * Provides a sequence of Cells representing all cell positions bounded by the two corner cells.
    * @param corner1 The first corner cell
    * @param corner2 The second corner cell
    * @return The stream of Cells.
    */
  def range(corner1: Cell, corner2: Cell): Seq[Cell] = {
    val minCell: Cell = Cell.minimalCell(corner1, corner2)
    val maxCell: Cell = Cell.maximalCell(corner1, corner2)

    for (row <- minCell.row to maxCell.row; column <- minCell.column to maxCell.column) yield new Cell(row, column)
  }

  var EMPTY: Cell = new Cell(-9999, -9999)
}

class Cell(val row: Int, val column: Int) {
  def getRow: Int = {
    return row
  }

  def getColumn: Int = {
    return column
  }

  /**
    * Returns a vector representing the difference between this and another Cell.
    *
    * @param cell the cell whose coordinates are to be subtracted
    * @return the vector representing the difference between the cells.
    * @throws NullPointerException if the specified { @code cell} is null
    */
  def subtract(cell: Cell): Vector = new Vector(row - cell.row, column - cell.column)

  /**
    * Returns a cell based on this one with the given vector transform applied.
    *
    * @param vector to apply to this cell.
    * @return the cell resulting from the transform.
    * @throws NullPointerException if the specified { @code cell} is null
    */
  def translate(vector: Vector): Cell = new Cell(row + vector.x, column + vector.y)

  override def equals(obj: Any): Boolean = {
    if (obj == null || !obj.isInstanceOf[Cell]) {
      false
    } else {
      val cell: Cell = obj.asInstanceOf[Cell]
      new EqualsBuilder().append(row, cell.row).append(column, cell.column).isEquals
    }
  }

  override lazy val hashCode: Int = new HashCodeBuilder().append(row).append(column).toHashCode

  override lazy val toString: String = s"Cell($row,$column)"
}