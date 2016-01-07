package com.foomoo.box

import org.apache.commons.lang3.builder.{EqualsBuilder, HashCodeBuilder}

/**
  * Represents a fixed space on a board intended to be the target location for a movable block.
  */
class Target(val text: String) {

  /**
    * Gets the text associated with this target.
    *
    * @return The text.
    */
  def getText: String = {
    return text
  }

  /**
    * Indicates whether the given block can be used to satisfy this target. Such a block will complete this target if
    * it is at the same cell as the target.
    *
    * @param block The block to test.
    * @return True if the block can be used to satisfy this target, false otherwise.
    */
  def isValidBlock(block: Block): Boolean = true;

  override lazy val toString: String = s"Target($text)"

  override def equals(obj: Any): Boolean = {
    if (obj == null || !(obj.isInstanceOf[Target])) {
      false
    } else {
      val target: Target = obj.asInstanceOf[Target]
      new EqualsBuilder().append(text, target.text).isEquals
    }
  }

  override lazy val hashCode: Int = new HashCodeBuilder().append(text).toHashCode
}