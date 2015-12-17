package com.foomoo.box;

/**
 * A movable block representing a player piece..
 */
public class PlayerJava extends Block {
    /**
     * Constructs a PlayerJava with the given associated text.
     *
     * @param text The associated text.
     */
    public PlayerJava(String text) {
        super(text);
    }

    public String toString() {
        return String.format("PlayerJava(%s)", getText());
    }

    public int getPushStrength() {
        return 2;
    }
}
