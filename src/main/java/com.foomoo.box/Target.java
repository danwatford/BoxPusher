package com.foomoo.box;

/**
 * Represents a fixed space on a board intended to be the target location for a movable block.
 */
public class Target extends Block {
    /**
     * Constructs a Target with the given associated text.
     *
     * @param text The associated text.
     */
    public Target(String text) {
        super(text);
    }

    @Override
    public String toString() {
        return String.format("Target(%s)", getText());
    }

}
