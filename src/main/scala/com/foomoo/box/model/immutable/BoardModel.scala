package com.foomoo.box.model.immutable

import com.foomoo.box._
import com.foomoo.box.model.Wall
import com.foomoo.box.model.immutable.BoardModel.BoardModelBuilder

import scala.collection.JavaConversions._

object BoardModel {

  class BoardModelBuilder(val player: Player, playerCell: Cell) {
    val blockCellMap: scala.collection.mutable.Map[Block, Cell] = scala.collection.mutable.Map(player -> playerCell)
    val targetCellMap: scala.collection.mutable.Map[Target, Cell] = scala.collection.mutable.Map()

    var error: Error = null
    var minCell: Cell = null
    var maxCell: Cell = null

    def this(originalModel: BoardModel) {
      this(originalModel.player, originalModel.blockCellMap.get(originalModel.player).get)
      blockCellMap.putAll(originalModel.blockCellMap)
      targetCellMap.putAll(originalModel.targetCellMap)
    }

    def blockCell(block: Block, cell: Cell): BoardModel.BoardModelBuilder = {
      blockCellMap.put(block, cell)
      this
    }

    def targetCell(target: Target, cell: Cell): BoardModel.BoardModelBuilder = {
      targetCellMap.put(target, cell)
      this
    }

    def wall(cell: Cell): BoardModel.BoardModelBuilder = {
      blockCellMap.put(new Wall, cell)
      this
    }

    def wall(fromCell: Cell, toCell: Cell): BoardModel.BoardModelBuilder = {
      val topLeft = Cell.minimalCell(fromCell, toCell)
      val bottomRight = Cell.maximalCell(fromCell, toCell)

      for (column <- topLeft.column to bottomRight.column) {
        wall(new Cell(topLeft.row, column))
        wall(new Cell(bottomRight.row, column))
      }

      for (row <- topLeft.row to bottomRight.row) {
        wall(new Cell(row, topLeft.column))
        wall(new Cell(row, bottomRight.column))
      }

      this
    }

    private def getAllCells = blockCellMap.values ++ targetCellMap.values

    def build: BoardModel = {
      minCell = getAllCells.reduce(Cell.minimalCell)
      maxCell = getAllCells.reduce(Cell.maximalCell)
      new BoardModel(this)
    }
  }

}

final class BoardModel(builder: BoardModelBuilder) {
  val player: Player = builder.player
  val blockCellMap: scala.collection.mutable.Map[Block, Cell] = builder.blockCellMap
  val targetCellMap: scala.collection.mutable.Map[Target, Cell] = builder.targetCellMap
  val minCell = builder.minCell
  val maxCell = builder.maxCell

  /**
    * Move the player to the given cell, pushing other blocks out of the way if necessary.
    *
    * @param to The cell to move the player to.
    * @return An Option of BoardModel specifying the new model following the movement of the player and any other
    *         necessary pieces. The option will be None if the move could not be performed.
    */
  def movePlayerPieceTo(to: Cell): Option[BoardModel] = {
    val builder: BoardModel.BoardModelBuilder = new BoardModel.BoardModelBuilder(this)
    recursiveBlockMove(builder, player, to, player.getPushStrength)
  }

  /**
    * Gets the cell for the given block.
    *
    * @param block The block to get the cell for.
    * @return Option of the Cell if the block is present in the model, empty otherwise.
    */
  def getBlockCell(block: Block): Option[Cell] = blockCellMap.get(block)

  /**
    * Gets the block at the given cell, if any.
    *
    * @param cell The cell to get the block for.
    * @return Option of the Block at the cell. Empty if no block present.
    */
  def getBlockAtCell(cell: Cell): Option[Block] = blockCellMap.find(_._2 == cell).map(_._1)

  /**
    * Gets all blocks known to the model.
    *
    * @return The Set of Blocks.
    */
  def getBlocks: Set[Block] = {
    blockCellMap.keySet.toSet
  }

  /**
    * Get the cell for the given target.
    *
    * @param target The target to get the cell for.
    * @return Optional of the Cell if the block is present in the model, empty otherwise.
    */
  def getTargetCell(target: Target): Option[Cell] = targetCellMap.get(target)

  /**
    * Gets the target at the given cell, if any.
    *
    * @param cell The cell to get the target for.
    * @return Option of Target at the cell. Empty if no target present.
    */
  def getTargetAtCell(cell: Cell): Option[Target] = targetCellMap.find(_._2 == cell).map(_._1)

  /**
    * Gets all targets known to the model.
    *
    * @return The Set of Targets.
    */
  def getTargets: Set[Target] = {
    targetCellMap.keySet.toSet
  }

  def getMinCell: Cell = {
    minCell
  }

  def getMaxCell: Cell = {
    maxCell
  }

  def getRowCount: Int = {
    maxCell.getRow - minCell.getRow + 1
  }

  def getColumnCount: Int = {
    maxCell.getColumn - minCell.getColumn + 1
  }

  private def moveBlocksBetweenCell(builder: BoardModel.BoardModelBuilder, block: Block, from: Cell, to: Cell, pushStrength: Int): Option[BoardModel] = {
    builder.blockCell(block, to)
    val targetCellBlockOptional: Option[Block] = getBlockAtCell(to)
    targetCellBlockOptional match {
      case None => Some(builder.build)
      case Some(targetCellBlock) =>
        // Get the push vector which can be applied to all blocks as necessarily.
        val pushVector = to.subtract(from)

        if (pushStrength >= targetCellBlock.getEffortToMove) {
          val translatedPushVector = targetCellBlock.translatePushVector(pushVector)
          val nextTargetCell = to.translate(translatedPushVector)
          moveBlocksBetweenCell(builder, targetCellBlock, to, nextTargetCell, pushStrength - targetCellBlock.getEffortToMove)
        } else {
          None
        }
    }
  }

  /**
    * Move the requested block, applying the change to the given builder. Push other blocks out of the way if required
    * as long as the number of blocks that can be pushed is not breached.
    *
    * @param builder      The builder to apply moved blocks to.
    * @param block        The block to move.
    * @param targetCell   The cell to move the block to.
    * @param pushStrength The number of blocks that can be pushed in the direction of movement to allow the given block
    *                     to move.
    * @return An Optional of BoardModel specifying the board following all the piece movements. The model will be None if the
    *         move could not be performed.
    */
  private def recursiveBlockMove(builder: BoardModel.BoardModelBuilder, block: Block, targetCell: Cell, pushStrength: Int): Option[BoardModel] = {
    blockCellMap.lift(block).flatMap(currentCell => moveBlocksBetweenCell(builder, block, currentCell, targetCell, pushStrength))
  }

}