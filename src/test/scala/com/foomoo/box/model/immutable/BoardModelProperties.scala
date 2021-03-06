package com.foomoo.box.model.immutable

import java.util.Optional

import com.foomoo.box.model.Wall
import com.foomoo.box.{Player, Cell}
import com.foomoo.box.model.immutable.BoardModel.BoardModelBuilder
import org.scalacheck.Prop.{classify, forAll, propBoolean}
import org.scalacheck.{Prop, Gen, Properties}

class BoardModelProperties extends Properties("BoardModel") {

  val boardDimension: Gen[Int] = Gen.choose(0, 5)

  val cells: Gen[Cell] = for {
    row <- boardDimension
    column <- boardDimension
  } yield new Cell(row, column)

  def isInRange(testVal: Int, x: Int, y: Int): Boolean = {
    val minVal = Math.min(x, y)
    val maxVal = Math.max(x, y)

    testVal >= minVal && testVal <= maxVal
  }

  def isPerimeterCell(testCell: Cell, corner1: Cell, corner2: Cell): Boolean =
    ((testCell.row == corner1.row || testCell.row == corner2.row) && isInRange(testCell.column, corner1.column, corner2.column)) ||
      ((testCell.column == corner1.column || testCell.column == corner2.column) && isInRange(testCell.row, corner1.row, corner2.row))

  def generateWalledBoardModel(wallCorner1: Cell, wallCorner2: Cell) = {
    val maxRow = Math.max(wallCorner1.getRow, wallCorner2.getRow)
    val maxColumn = Math.max(wallCorner1.getColumn, wallCorner2.getColumn)

    // Create a builder with the player placed outside of the wall.
    val builder = new BoardModelBuilder(new Player("P"), new Cell(maxRow + 1, maxColumn + 1))
    builder.wall(wallCorner1, wallCorner2).build
  }

  property("only generates wall blocks on rectangle perimeter") =
    forAll(cells, cells, cells)((c1, c2, testCell) =>
      classify(isPerimeterCell(testCell, c1, c2), "perimeter") {
        isPerimeterCell(testCell, c1, c2) == generateWalledBoardModel(c1, c2).getBlockAtCell(testCell).exists(_.isInstanceOf[Wall])
      }
    )

}
