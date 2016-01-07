package com.foomoo.box.model.immutable

import com.foomoo.box.model.immutable.BoardModel.BoardModelBuilder
import com.foomoo.box.{Cell, Player, UnitSpec}

object  BoardModelSpec {

  //  @Mocked private val TEST_BLOCK_1: Block = new Block("B1")
  //  @Mocked private val TEST_BLOCK_2: Block = new Block("B2")
  //  private val SINGLE_PLAYER_NO_BLOCKS: BoardModel = new BoardModel.BoardModelBuilder(TEST_PLAYER, new Cell(1, 1)).build
  //  private val SINGLE_PLAYER_ADJACENT_SINGLE_BLOCK: BoardModel = new BoardModel.BoardModelBuilder(TEST_PLAYER, new Cell(1, 1)).blockCell(TEST_BLOCK_1, new Cell(1, 2)).build
  //  private val SINGLE_PLAYER_ADJACENT_TWO_BLOCK: BoardModel = new BoardModel.BoardModelBuilder(TEST_PLAYER, new Cell(1, 1)).blockCell(TEST_BLOCK_1, new Cell(1, 2)).blockCell(TEST_BLOCK_2, new Cell(1, 3)).build
}

class BoardModelSpec extends UnitSpec {

  private val TEST_PLAYER: Player = new Player("P")

  "BoardModel" should "return Minimal Cell" in {
    val walledModel: BoardModel = getWalledModel
    assertResult(new Cell(0, 0)) {
      walledModel.getMinCell
    }
  }

  it should "return Maximal Cell" in {
    val walledModel: BoardModel = getWalledModel
    assertResult(new Cell(10, 10)) {
      walledModel.getMaxCell
    }
  }

  it should "generate walls on rectangle perimeter" in {
    val corner1 = new Cell(2, 7)
    val corner2 = new Cell(7, 2)
    val testCell = new Cell(6, 2)


    val model = generateWalledBoardModel(corner1, corner2);

    val blockOptional = model.getBlockAtCell(testCell);


  }

  def generateWalledBoardModel(wallCorner1: Cell, wallCorner2: Cell) = {
    val maxRow = Math.max(wallCorner1.getRow, wallCorner2.getRow)
    val maxColumn = Math.max(wallCorner1.getColumn, wallCorner2.getColumn)

    // Create a builder with the player placed outside of the wall.
    val builder = new BoardModelBuilder(new Player("P"), new Cell(maxRow + 1, maxColumn + 1))
    builder.wall(wallCorner1, wallCorner2).build
  }

  private val getWalledModel: BoardModel = new BoardModel.BoardModelBuilder(TEST_PLAYER, new Cell(2, 2)).wall(new Cell(0, 0), new Cell(10, 10)).build

}