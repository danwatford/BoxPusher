package com.foomoo.box;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a fixed space on a board intended to be the target location for a movable block.
 */
public class Target {

    private final String text;

    /**
     * Constructs a Target with the given associated text.
     *
     * @param text The associated text.
     */
    public Target(String text) {
        this.text = text;
    }

    /**
     * Gets the text associated with this target.
     *
     * @return The text.
     */
    public String getText() {
        return text;
    }

    /**
     * Indicates whether the given block can be used to satisfy this target. Such a block will complete this target if
     * it is at the same cell as the target.
     *
     * @param block The block to test.
     * @return True if the block can be used to satisfy this target, false otherwise.
     */
    public boolean isValidBlock(final Block block) {
        return true;
    }

    @Override
    public String toString() {
        return String.format("Target(%s)", text);
    }

    @Override
    public boolean equals(final Object obj) {
        if (! (obj instanceof Target)) {
            return false;
        }

        final Target target = (Target) obj;

        return new EqualsBuilder()
                .append(text, target.text)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(text)
                .toHashCode();
    }
}
