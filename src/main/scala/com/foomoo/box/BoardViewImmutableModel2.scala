package com.foomoo.box

import javafx.animation.{Animation, KeyFrame, Timeline}
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.{Pane, StackPane}
import javafx.scene.paint.Color
import javafx.scene.shape.{Rectangle, StrokeType}
import javafx.scene.text.Font
import javafx.scene.{Group, Scene}
import javafx.util.Duration

import com.foomoo.box.model.immutable.{BoardModel, BoardModelDiff}

/**
  * Provides a view of the Board, animating movement of blocks in response to changing to a new BoardModel and calling a
  * BoardCellClickedHandler in response to mouse clicks on a cell.
  */
object BoardViewImmutableModel2 {
}

class BoardViewImmutableModel2(var boardModel: BoardModel) {

  private val CELL_WIDTH: Int = 50
  private val CELL_HEIGHT: Int = 50

  private val blockPaneMap: scala.collection.mutable.Map[Block, Pane] = scala.collection.mutable.Map()
  private val targetPaneMap: scala.collection.mutable.Map[Target, Pane] = scala.collection.mutable.Map()

  private val group: Group = new Group()
  private val scene = new Scene(group, boardModel.getColumnCount * CELL_WIDTH, boardModel.getRowCount * CELL_HEIGHT, Color.WHITE)

  Cell.range(boardModel.getMinCell, boardModel.getMaxCell).foreach(cell => {
    val r = new Rectangle(CELL_WIDTH * cell.column, CELL_HEIGHT * cell.row, CELL_WIDTH, CELL_HEIGHT)
    r.setFill(Color.rgb(0, 0, 0, 0))
    r.setStrokeType(StrokeType.CENTERED)
    r.setStroke(Color.BLACK)

    group.getChildren().add(r)

    // Any targets at this cell position?
    boardModel.getTargetAtCell(cell).foreach((target:Target) => {
      val stackPane = createStackPane(target.text, cell)
      group.getChildren().add(stackPane)
      targetPaneMap.put(target, stackPane)
    })

    // Any pieces at this cell position?
    boardModel.getBlockAtCell(cell).foreach(block => {
      val stackPane = createStackPane(block.text, cell)
      group.getChildren().add(stackPane)
      blockPaneMap.put(block, stackPane)
    })
  })

  def getScene: Scene = scene

  private def createStackPane(text: String, cell: Cell): StackPane = {
    val label: Label = new Label(text)
    label.setFont(new Font(CELL_HEIGHT / 2))
    val stackPane: StackPane = new StackPane(label)
    stackPane.setPrefWidth(CELL_WIDTH)
    stackPane.setPrefHeight(CELL_HEIGHT)
    stackPane.setTranslateX(cell.getColumn * CELL_WIDTH)
    stackPane.setTranslateY(cell.getRow * CELL_HEIGHT)
    StackPane.setAlignment(label, Pos.CENTER)
    stackPane
  }

  def setNextBoardModel(nextBoardModel: BoardModel): Unit = {
    val diff: BoardModelDiff = new BoardModelDiff(boardModel, nextBoardModel)
    diff.getMovedBlocks.foreach(block =>
      nextBoardModel.getBlockCell(block).foreach(cell => {
        val paneOption = blockPaneMap.get(block)
        if (paneOption.isEmpty) {
          throw new RuntimeException("Pane not found for Block: " + block)
        } else {
          paneOption.foreach(pane => {
            val moving: Timeline = new Timeline(Animation.INDEFINITE,
              new KeyFrame(Duration.seconds(0.5), AnimationHelper.createKeyValue(pane.translateXProperty, cell.column * CELL_WIDTH)),
              new KeyFrame(Duration.seconds(0.5), AnimationHelper.createKeyValue(pane.translateYProperty, cell.row * CELL_HEIGHT))
            )
            moving.play()
          })
        }
      }))

    boardModel = nextBoardModel
  }

}

