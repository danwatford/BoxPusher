package com.foomoo.box;

import java.util.*;

/**
 * Class to construct a Board model's initial state.
 */
public class BoardDefinition {

    private int width;
    private int height;
    private boolean[][] wallCells;
    private Cell playerCell;

    private Map<Block, Cell> blocks;
    private Map<Target, Cell> targets;
    private Map<Block, Target> blockTargetMap;

    private BoardDefinition(int width, int height, boolean[][] wallCells, Cell playerCell, Map<Block, Cell> blocks, Map<Target, Cell> targets, Map<Block, Target> blockTargetMap) {
        this.width = width;
        this.height = height;
        this.wallCells = wallCells;
        this.playerCell = playerCell;
        this.blocks = blocks;
        this.targets = targets;
        this.blockTargetMap = blockTargetMap;
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

    public Map<Block, Cell> getBlockCells() {
        return blocks;
    }

    public Map<Target, Cell> getTargetCells() {
        return targets;
    }

    public Map<Block, Target> getBlockTargetMap() {
        return blockTargetMap;
    }

    public static BoardDefinition fromString(final String boardDefinition) {
        if (boardDefinition.isEmpty()) {
            throw new RuntimeException("Board definition string cannot be empty.");
        }

        Cell playerCell = null;
        String rowStrings[] = boardDefinition.split("\n");
        int rowCount = rowStrings.length;
        int colCount = 0;

        boolean[][] wallCells = new boolean[rowStrings.length][];
        Map<Block, Cell> blockMap = new HashMap<>();
        Map<Target, Cell> targetMap = new HashMap<>();
        Map<Block, Target> blockTargetMap = new HashMap<>();

        for (int row = 0; row < rowStrings.length; row++) {
            String rowString = rowStrings[row];
            wallCells[row] = new boolean[rowString.length()];
            colCount = Math.max(colCount, rowString.length());

            for (int col = 0; col < rowString.length(); col++) {
                String textAtCell = rowString.substring(col, col + 1);

                if (textAtCell.equals("X")) {
                    wallCells[row][col] = true;
                } else if (textAtCell.equals("@")) {
                    playerCell = new Cell(row, col);
                } else if (Character.isUpperCase(textAtCell.codePointAt(0))) {
                    blockMap.put(new Block(textAtCell), new Cell(row, col));
                } else if (Character.isLowerCase(textAtCell.codePointAt(0))) {
                    targetMap.put(new Target(textAtCell), new Cell(row, col));
                }
            }
        }

        // We have our Blocks and Targets. Now try to find those that are associated with each other.
        blockMap.keySet().forEach(block -> {
            String possibleTargetText = block.getText().toLowerCase(Locale.ENGLISH);
            targetMap.keySet().stream().filter(target -> target.getText().equals(possibleTargetText))
                    .findFirst().ifPresent(target -> blockTargetMap.put(block, target));
        });

        return new BoardDefinition(colCount, rowCount, wallCells, playerCell, blockMap, targetMap, blockTargetMap);
    }
}
