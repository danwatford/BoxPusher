package com.foomoo.box

import org.scalatest._

/**
  * Base class for unit tests
  */
abstract class UnitSpec extends FlatSpec with Matchers with OptionValues with Inside with Inspectors
