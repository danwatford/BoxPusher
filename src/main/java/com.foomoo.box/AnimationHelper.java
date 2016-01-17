package com.foomoo.box;

import javafx.animation.KeyValue;
import javafx.beans.property.DoubleProperty;

/**
 * Helper to allow creation of various animation objects which cannot be created directly from scala
 */
public class AnimationHelper {

    public static KeyValue createKeyValue(final DoubleProperty doubleProperty, final Double endValue) {
        return new KeyValue(doubleProperty, endValue);
    }

}
