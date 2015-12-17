package com.foomoo.box.model.immutable;

import com.foomoo.box.Block;
import com.foomoo.box.Cell;
import com.foomoo.box.Player;
import com.foomoo.box.PlayerJava;
import com.foomoo.box.model.immutable.BoardModel.BoardModelBuilder;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(JMockit.class)
public class BoardModelTest {

    @Mocked
    private static final Player TEST_PLAYER = new Player("P");

    @Mocked
    private static final Block TEST_BLOCK_1 = new Block("B1");

    @Mocked
    private static final Block TEST_BLOCK_2 = new Block("B2");

    private static final BoardModel SINGLE_PLAYER_NO_BLOCKS = new BoardModelBuilder(TEST_PLAYER, new Cell(1, 1)).build();

    private static final BoardModel SINGLE_PLAYER_ADJACENT_SINGLE_BLOCK = new BoardModelBuilder(TEST_PLAYER, new Cell(1, 1))
            .blockCell(TEST_BLOCK_1, new Cell(1, 2)).build();

    private static final BoardModel SINGLE_PLAYER_ADJACENT_TWO_BLOCK = new BoardModelBuilder(TEST_PLAYER, new Cell(1, 1))
            .blockCell(TEST_BLOCK_1, new Cell(1, 2)).blockCell(TEST_BLOCK_2, new Cell(1, 3)).build();

    @Test
    public void pieceMovesIfTargetSpaceFree() {
        Cell targetCell = new Cell(2, 2);
        Optional<BoardModel> modelOptional = SINGLE_PLAYER_NO_BLOCKS.movePlayerPieceTo(targetCell);
        assertThat(modelOptional.isPresent(), equalTo(true));
        BoardModel model = modelOptional.get();
        Optional<Cell> cellOptional = model.getBlockCell(TEST_PLAYER);
        assertThat(cellOptional.isPresent(), equalTo(true));
        assertThat(cellOptional.get(), equalTo(targetCell));
    }

    @Test
    public void pieceMovesIfCanPushSingleBlock(@Mocked final PlayerJava player) {

        new Expectations() {
            {
                player.getPushStrength();
                result = 1;
            }
        };

        Cell targetCell = new Cell(1, 2);
        Optional<BoardModel> modelOptional = SINGLE_PLAYER_ADJACENT_SINGLE_BLOCK.movePlayerPieceTo(targetCell);
        assertThat(modelOptional.isPresent(), equalTo(true));

        BoardModel model = modelOptional.get();
        Optional<Cell> cellOptional = model.getBlockCell(TEST_PLAYER);
        assertThat(cellOptional.get(), equalTo(targetCell));

        Optional<Cell> blockCellOptional = model.getBlockCell(TEST_BLOCK_1);
        assertThat(blockCellOptional.get(), equalTo(new Cell(1, 3)));
    }

    @Test
    public void pieceMovesIfCanPushMultipleBlocks(@Mocked final PlayerJava player) {
        new Expectations() {
            {
                player.getPushStrength();
                result = 2;
            }
        };

        Cell targetCell = new Cell(1, 2);
        Optional<BoardModel> modelOptional = SINGLE_PLAYER_ADJACENT_TWO_BLOCK.movePlayerPieceTo(targetCell);
        assertThat(modelOptional.isPresent(), equalTo(true));

        BoardModel model = modelOptional.get();
        Optional<Cell> playerCellOptional = model.getBlockCell(TEST_PLAYER);
        assertThat(playerCellOptional.get(), equalTo(targetCell));

        Optional<Cell> blockCell1Optional = model.getBlockCell(TEST_BLOCK_1);
        assertThat(blockCell1Optional.get(), equalTo(new Cell(1, 3)));

        Optional<Cell> blockCell2Optional = model.getBlockCell(TEST_BLOCK_2);
        assertThat(blockCell2Optional.get(), equalTo(new Cell(1, 4)));
    }

    @Test
    public void pieceDoesNotMoveIfCannotPushBlocks(@Mocked final PlayerJava player) {
        new Expectations() {
            {
                player.getPushStrength();
                result = 0;
            }
        };

        Cell targetCell = new Cell(1, 2);
        Optional<BoardModel> modelOptional = SINGLE_PLAYER_ADJACENT_SINGLE_BLOCK.movePlayerPieceTo(targetCell);
        assertThat(modelOptional.isPresent(), equalTo(false));
    }

    @Test
    public void pieceDoesNotMoveIfCannotPushMultipleBlocks(@Mocked final PlayerJava player) {
        new Expectations() {{
            player.getPushStrength();
            result = 1;
        }};

        Cell targetCell = new Cell(1, 2);
        Optional<BoardModel> modelOptional = SINGLE_PLAYER_ADJACENT_TWO_BLOCK.movePlayerPieceTo(targetCell);
        assertThat(modelOptional.isPresent(), equalTo(false));
    }

    @Test
    public void pieceCannotMoveBlockIfNotEnoughStrength(@Mocked final PlayerJava player) {
        new Expectations() {{
            player.getPushStrength();
            result = 1;
        }};

        new Expectations(TEST_BLOCK_1) {{
            TEST_BLOCK_1.getEffortToMove();
            result = 2;
        }};

        Cell targetCell = new Cell(1, 2);
        Optional<BoardModel> modelOptional = SINGLE_PLAYER_ADJACENT_SINGLE_BLOCK.movePlayerPieceTo(targetCell);
        assertThat(modelOptional.isPresent(), equalTo(false));
    }


    @Test(expected = BoardModelException.class)
    public void buildingInvalidWallGivesError() throws BoardModelException {
        Cell from = new Cell(0, 0);
        Cell to = new Cell(1, 1);

        new BoardModelBuilder(TEST_PLAYER, new Cell(1, 1)).wall(from, to).build();
    }

    @Test
    public void getsMinimumCell() {
        final BoardModel walledModel = getWalledModel();

        assertThat(walledModel.getMinCell(), equalTo(new Cell(0, 0)));
    }

    @Test
    public void getsMaximumCell() {
        final BoardModel walledModel = getWalledModel();

        assertThat(walledModel.getMaxCell(), equalTo(new Cell(10, 10)));
    }


    private BoardModel getWalledModel() {
        try {
            return new BoardModelBuilder(TEST_PLAYER, new Cell(2, 2))
                    .wall(new Cell(0, 0), new Cell(0, 10))
                    .wall(new Cell(10, 0), new Cell(10, 10))
                    .wall(new Cell(0, 0), new Cell(10, 0))
                    .wall(new Cell(0, 10), new Cell(10, 10))
                    .build();
        } catch (BoardModelException e) {
            throw new AssertionError("Error constructing test model with walls");
        }
    }

}