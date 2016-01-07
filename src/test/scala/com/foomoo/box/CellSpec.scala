package com.foomoo.box

import java.util.stream.Collectors

import scala.collection.JavaConversions._

/**
  * Tests for the Cell class
  */
class CellSpec extends UnitSpec {
  private val MIN_CELL: Cell = new Cell(-5, -5)
  private val MAX_CELL: Cell = new Cell(3, 3)

  "A Cell Range" should "include minimum cell" in {
    val cellStream: java.util.stream.Stream[Cell] = Cell.range(MIN_CELL, MAX_CELL)
    val cellList: java.util.List[Cell] = cellStream.collect(Collectors.toList())

    cellList should contain (MIN_CELL)
  }

  it should "include maximum cell" in {
    val cellStream: java.util.stream.Stream[Cell] = Cell.range(MIN_CELL, MAX_CELL)
    val cellList: java.util.List[Cell] = cellStream.collect(Collectors.toList())

    cellList should contain (MAX_CELL)
  }

}
