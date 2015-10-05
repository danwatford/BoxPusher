package com.foomoo.box;

/**
 * A movable block representing a player piece..
 */
public class Player extends Block {
    /**
     * Constructs a Player with the given associated text.
     *
     * @param text The associated text.
     */
    public Player(String text) {
        super(text);
    }

    public String toString() {
        return String.format("Player(%s)", getText());
    }
}
