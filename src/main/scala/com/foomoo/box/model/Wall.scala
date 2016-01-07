package com.foomoo.box.model

import com.foomoo.box.Block
import org.apache.commons.lang3.builder.EqualsBuilder

/**
  * A non-movable block representing a wall.
  */
class Wall extends Block("#") {

  override lazy val toString: String = "#"

  override lazy val getEffortToMove: Int = Integer.MAX_VALUE

  override def equals(obj: Any): Boolean = {
    if (obj == null || !obj.isInstanceOf[Wall]) {
      return false
    }

    this eq obj.asInstanceOf[Wall]
  }

  override lazy val hashCode = System.identityHashCode(this)
}