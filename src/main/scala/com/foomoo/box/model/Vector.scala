package com.foomoo.box.model

import org.apache.commons.lang3.builder.{EqualsBuilder, HashCodeBuilder}

import scala.beans.BeanProperty

/**
  * Represents a vector
  */
class Vector(@BeanProperty val x: Int, @BeanProperty val y: Int) {
  /**
    * Returns the difference between this vector and another
    *
    * @param that the vector to be subtracted.
    * @return the difference vector.
    * @throws NullPointerException if the specified { @code Vector} is null
    */
  def subtract(that: Vector): Vector = new Vector(x - that.x, y - that.y)

  /**
    * Returns the sum of this and another vector.
    *
    * @param that the vector to be added.
    * @return the sum vector
    * @throws NullPointerException if the specified { @code Vector} is null
    */
  def add(that: Vector): Vector = new Vector(x + that.x, y + that.y)

  override lazy val toString: String = s"Vector($x,$y)"

  override def equals(obj: Any): Boolean = {
    if (obj == null || !obj.isInstanceOf[Vector]) {
      return false
    }

    val vector: Vector = obj.asInstanceOf[Vector]
    new EqualsBuilder().append(x, vector.x).append(y, vector.y).isEquals
  }

  override lazy val hashCode: Int = new HashCodeBuilder().append(x).append(y).toHashCode
}