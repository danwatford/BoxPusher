package com.foomoo.box;

/**
 * Represents a movable block for use on a Board.
 */
public class Block {
    // The text associated with this block.
    private String text;

    /**
     * Construct a block with the given associated text.
     *
     * @param text The text to associated with the block.
     */
    public Block(String text) {
        this.text = text;
    }

    /**
     * Get the text associated with this block.
     *
     * @return The associated text.
     */
    public String getText() {
        return text;
    }

    public String toString() {
        return String.format("Block(%s)", text);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof Block) {
            Block block = (Block) obj;
            return text.equals(block.getText());
        } else {
            return false;
        }
    }
}
