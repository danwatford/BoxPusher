package com.foomoo.box;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 */
public class BoardDefinitionTest {

    private static final String DEF1 = "" +
            "X\n" +
            "X  @ X\n" +
            "XA a X\n" +
            "XX  X\n" +
            "XXXXXX";

    @Test
    public void testFromString() throws Exception {

        BoardDefinition result = BoardDefinition.fromString(DEF1);

        assertThat(result.getWidth(), equalTo(6));
        assertThat(result.getHeight(), equalTo(5));

        assertThat(result.cellIsWall(0, 0), equalTo(true));
        assertThat(result.cellIsWall(0, 1), equalTo(true));
        assertThat(result.cellIsWall(1, 1), equalTo(false));
        assertThat(result.cellIsWall(2, 2), equalTo(false));
        assertThat(result.cellIsWall(3, 3), equalTo(false));
        assertThat(result.cellIsWall(3, 4), equalTo(true));
        assertThat(result.cellIsWall(4, 4), equalTo(true));
    }
}