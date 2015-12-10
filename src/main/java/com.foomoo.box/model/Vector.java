package com.foomoo.box.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a vector
 */
public class Vector {
    private final int x;
    private final int y;

    public Vector(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Returns the difference between this vector and another
     *
     * @param that the vector to be subtracted.
     * @return the difference vector.
     * @throws NullPointerException if the specified {@code Vector} is null
     */
    public Vector subtract(Vector that) {
        return new Vector(x - that.getX(), y - that.getY());
    }

    /**
     * Returns the sum of this and another vector.
     *
     * @param that the vector to be added.
     * @return the sum vector
     * @throws NullPointerException if the specified {@code Vector} is null
     */
    public Vector add(Vector that) {
        return new Vector(x + that.getX(), y + that.getY());
    }

    @Override
    public String toString() {
        return String.format("Vector(%d,%d)", x, y);
    }


    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Vector)) {
            return false;
        }

        Vector vector = (Vector) obj;

        return new EqualsBuilder()
                .append(x, vector.x)
                .append(y, vector.y)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(x)
                .append(y)
                .toHashCode();
    }
}
