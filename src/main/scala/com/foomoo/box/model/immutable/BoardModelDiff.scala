package com.foomoo.box.model.immutable

import java.util.{Optional, Set}

import com.foomoo.box.{Block, Target}

import scala.collection.JavaConversions._

/**
  * Class to determine the difference between two {@link BoardModel} objects
  */
class BoardModelDiff(first: BoardModel, second: BoardModel) {

  /**
    * Get the blocks that have changed cell when moving from the first to the second BoardModel.
    *
    * @return The List of Blocks.
    */
  def getMovedBlocks: java.util.List[Block] = {
    getCommonBlocks.filter(block => first.getBlockCell(block).get() != second.getBlockCell(block).get()).toList
  }

  /**
    * Gets the targets that have been satisfied when moving from the first to the second BoardModel.
    *
    * @return The List of Targets.
    */
  def getNewCompletedTargets: java.util.List[Target] = (getCompletedTargets(second) &~ getCompletedTargets(first)).toList

  /**
    * Gets the targets that have been unsatisfied when moving from the first to the second BoardModel.
    *
    * @return The List of Targets.
    */
  def getNewUncompletedTargets: java.util.List[Target] = (getCompletedTargets(first) &~ getCompletedTargets(second)).toList

  /**
    * Get the Blocks that are present in both the first and second model.
    *
    * @return The List of Blocks.
    */
  private def getCommonBlocks: Set[Block] = first.getBlocks & second.getBlocks

  /**
    * Gets the completed targets for the given BoardModel.
    *
    * @param boardModel The BoardModel to get completed targets for.
    * @return The List of completed Targets.
    */
  private def getCompletedTargets(boardModel: BoardModel): Set[Target] = {
    boardModel.getTargets.filter(target => {
      toOption(boardModel.getTargetCell(target)) match {
        case None => false
        case Some(targetCell) => {
          toOption(boardModel.getBlockAtCell(targetCell)).map(target.isValidBlock(_)).isDefined
        }
      }
    }
    )
  }

  private def toOption[T](javaOp: Optional[T]): Option[T] = if (javaOp.isPresent) Some(javaOp.get()) else None
}