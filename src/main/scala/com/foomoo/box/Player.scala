package com.foomoo.box

/**
  * A movable block representing a player piece..
  */
class Player(text: String) extends Block(text) {
  override def toString = s"PlayerJava $text"
  def getPushStrength = 2
}