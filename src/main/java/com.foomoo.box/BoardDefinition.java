package com.foomoo.box;

import javafx.geometry.Point2D;

import java.util.*;

/**
 * Class to construct a Board model's initial state.
 */
public class BoardDefinition {

    private int width;
    private int height;
    private boolean[][] wallCells;
    private Cell playerCell;

    public static BoardDefinition fromString(final String boardDefinition) {
        if (boardDefinition.isEmpty()) {
            throw new RuntimeException("Board definition string cannot be empty.");
        }

        Cell playerCell = null;
        String rowStrings[] = boardDefinition.split("\n");
        int rowCount = rowStrings.length;
        int colCount = 0;
        List<PieceData> pieces = new ArrayList<>();

        boolean[][] wallCells = new boolean[rowStrings.length][];
        Map<Character, Cell> blockMap = new HashMap<>();
        Map<Character, Cell> targetMap = new HashMap<>();

        for (int row = 0; row < rowStrings.length; row++) {
            String rowString = rowStrings[row];
            wallCells[row] = new boolean[rowString.length()];
            colCount = Math.max(colCount, rowString.length());

            for (int col = 0; col < rowString.length(); col++) {
                int charAtCell = rowString.charAt(col);

                // Check for walls.
                if (charAtCell == 'X') {
                    wallCells[row][col] = true;
                }

                // Check for the player.
                if (charAtCell == '@') {
                    playerCell = new Cell(row, col);
                }

                // Look for any other movable pieces.
                if (charAtCell >= 'A' && charAtCell <= 'G') {
                    blockMap.put((char) charAtCell, new Cell(row, col));
                }

                // Look for any targets.
                if (charAtCell >= 'a' && charAtCell <= 'g') {
                    targetMap.put((char) charAtCell, new Cell(row, col));
                }
            }
        }

        //TODO get targets and blocks into the definition***********************
        blockMap.forEach((pieceChar, piecePoint) -> {
            PieceData data = new PieceData();
            data.pieceText = String.valueOf(pieceChar);
            data.pieceCell = piecePoint;

            char possibleTargetChar = data.pieceText.toLowerCase(Locale.ENGLISH).charAt(0);
            Cell targetCell = targetMap.get(possibleTargetChar);
            if (targetCell != null) {
                data.targetText = data.pieceText.toLowerCase(Locale.ENGLISH);
                targetMap.remove(possibleTargetChar);
            }

            pieces.add(data);
        });

        // Any targets remaining?
        targetMap.forEach((targetChar, targetPoint) -> {
            PieceData data = new PieceData();
            data.targetText = String.valueOf(targetChar);
            data.targetCell = targetPoint;
            pieces.add(data);
        });

        return new BoardDefinition(colCount, rowCount, wallCells, playerCell, pieces);
    }

    private BoardDefinition(int width, int height, boolean[][] wallCells, Cell playerCell, List<PieceData> pieces) {
        this.width = width;
        this.height = height;
        this.wallCells = wallCells;
        this.playerCell = playerCell;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean cellIsWall(int row, int column) {
        boolean[] rowWalls = wallCells[row];
        return (rowWalls.length <= column) || rowWalls[column];
    }

    public Optional<Cell> getPlayerCell() {
        return Optional.ofNullable(playerCell);
    }

    public static class PieceData {
        String pieceText;
        String targetText;
        Cell pieceCell;
        Cell targetCell;
    }
}
