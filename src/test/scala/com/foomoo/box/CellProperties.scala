package com.foomoo.box

import java.lang.Math.abs

import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck.{Gen, Properties}

/**
  * Property tests for com.foomoo.box.Cell
  */
object CellProperties extends Properties("Cell Generation") {

  val cells: Gen[Cell] = for {
    row <- Gen.posNum[Int]
    column <- Gen.posNum[Int]
  } yield new Cell(row, column)

  property("generates a rectangular number of cells") =
    forAll(cells, cells) { (c1, c2) =>

      val rectWidth = abs(c2.getColumn - c1.getColumn) + 1
      val rectHeight = abs(c2.getRow - c1.getRow) + 1

      val expectedCellCount = rectHeight * rectWidth

      Cell.range(c1, c2).size == expectedCellCount
    }

}
