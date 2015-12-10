package com.foomoo.box.model;

import com.foomoo.box.Block;

/**
 * A non-movable block representing a wall.
 */
public class Wall extends Block {
    public Wall() {
        super("#");
    }

    public String toString() {
        return "#";
    }

    @Override
    public int getEffortToMove() {
        return Integer.MAX_VALUE;
    }
}
