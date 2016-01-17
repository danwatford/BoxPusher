package com.foomoo.box

/**
  * Tests for the Cell class
  */
class CellSpec extends UnitSpec {
  private val MIN_CELL: Cell = new Cell(-5, -5)
  private val MAX_CELL: Cell = new Cell(3, 3)

  "A Cell Range" should "include minimum cell" in {
    val cellSeq = Cell.range(MIN_CELL, MAX_CELL)

    cellSeq should contain (MIN_CELL)
  }

  it should "include maximum cell" in {
    val cellSeq = Cell.range(MIN_CELL, MAX_CELL)

    cellSeq should contain (MAX_CELL)
  }

}
