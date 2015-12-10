package com.foomoo.box.model.immutable;

import com.foomoo.box.Block;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class to determine the difference between two {@link BoardModel} objects
 */
public class BoardModelDiff {

    private final BoardModel first;
    private final BoardModel second;

    public BoardModelDiff(final BoardModel first, final BoardModel second) {
        this.first = first;
        this.second = second;
    }

    public List<Block> getMovedBlocks() {

        final List<Block> commonBlocks = getCommonBlocks();

        return commonBlocks.stream().filter(block -> !first.getBlockCell(block).equals(second.getBlockCell(block))).collect(Collectors.toList());
    }


    private List<Block> getCommonBlocks() {
        final Set<Block> firstBlocks = first.getBlocks();
        final Set<Block> secondBlocks = second.getBlocks();

        return firstBlocks.stream().filter(secondBlocks::contains).collect(Collectors.toList());
    }
}
