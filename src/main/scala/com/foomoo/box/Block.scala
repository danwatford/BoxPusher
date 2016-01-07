package com.foomoo.box

import com.foomoo.box.model.Vector
import org.apache.commons.lang3.builder.EqualsBuilder

/**
  * Represents a movable block for use on a Board.
  */
class Block(val text: String) {
  /**
    * Get the text associated with this block.
    *
    * @return The associated text.
    */
  def getText: String = {
    return text
  }

  override def toString: String = {
    return String.format("Block(%s)", text)
  }

  def translatePushVector(pushVector: Vector): Vector = {
    return pushVector
  }

  /**
    * Returns the level of effort (strength) required to move this block.
    * @return The effort to move.
    */
  def getEffortToMove: Int = {
    return 1
  }

  def canEqual(obj: Any) = obj.isInstanceOf[Block]

  override def equals(obj: Any): Boolean = obj match {
    case that: Block => (that canEqual this) && text == that.text
    case _ => false
  }

  override def hashCode(): Int = super.hashCode()
}