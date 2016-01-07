package com.foomoo.box.model.immutable;

import com.foomoo.box.Block;
import com.foomoo.box.Cell;
import com.foomoo.box.Player;
import com.foomoo.box.model.immutable.BoardModel.BoardModelBuilder;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import scala.Option;

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
        final Cell targetCell = new Cell(2, 2);
        final Option<BoardModel> modelOption = SINGLE_PLAYER_NO_BLOCKS.movePlayerPieceTo(targetCell);
        assertThat(modelOption.isDefined(), equalTo(true));
        final BoardModel model = modelOption.get();
        final Optional<Cell> cellOptional = model.getBlockCell(TEST_PLAYER);
        assertThat(cellOptional.isPresent(), equalTo(true));
        assertThat(cellOptional.get(), equalTo(targetCell));
    }

    @Test
    public void pieceMovesIfCanPushSingleBlock(@Mocked final Player player) {

        new Expectations() {
            {
                player.getPushStrength();
                result = 1;
            }
        };

        Cell targetCell = new Cell(1, 2);
        Option<BoardModel> modelOption = SINGLE_PLAYER_ADJACENT_SINGLE_BLOCK.movePlayerPieceTo(targetCell);
        assertThat(modelOption.isDefined(), equalTo(true));

        BoardModel model = modelOption.get();
        Optional<Cell> cellOptional = model.getBlockCell(TEST_PLAYER);
        assertThat(cellOptional.get(), equalTo(targetCell));

        Optional<Cell> blockCellOptional = model.getBlockCell(TEST_BLOCK_1);
        assertThat(blockCellOptional.get(), equalTo(new Cell(1, 3)));
    }

    @Test
    public void pieceMovesIfCanPushMultipleBlocks(@Mocked final Player player) {
        new Expectations() {
            {
                player.getPushStrength();
                result = 2;
            }
        };

        Cell targetCell = new Cell(1, 2);
        Option<BoardModel> modelOption = SINGLE_PLAYER_ADJACENT_TWO_BLOCK.movePlayerPieceTo(targetCell);
        assertThat(modelOption.isDefined(), equalTo(true));

        BoardModel model = modelOption.get();
        Optional<Cell> playerCellOptional = model.getBlockCell(TEST_PLAYER);
        assertThat(playerCellOptional.get(), equalTo(targetCell));

        Optional<Cell> blockCell1Optional = model.getBlockCell(TEST_BLOCK_1);
        assertThat(blockCell1Optional.get(), equalTo(new Cell(1, 3)));

        Optional<Cell> blockCell2Optional = model.getBlockCell(TEST_BLOCK_2);
        assertThat(blockCell2Optional.get(), equalTo(new Cell(1, 4)));
    }

    @Test
    public void pieceDoesNotMoveIfCannotPushBlocks(@Mocked final Player player) {
        new Expectations() {
            {
                player.getPushStrength();
                result = 0;
            }
        };

        Cell targetCell = new Cell(1, 2);
        Option<BoardModel> modelOption = SINGLE_PLAYER_ADJACENT_SINGLE_BLOCK.movePlayerPieceTo(targetCell);
        assertThat(modelOption.isDefined(), equalTo(false));
    }

    @Test
    public void pieceDoesNotMoveIfCannotPushMultipleBlocks(@Mocked final Player player) {
        new Expectations() {{
            player.getPushStrength();
            result = 1;
        }};

        Cell targetCell = new Cell(1, 2);
        Option<BoardModel> modelOption = SINGLE_PLAYER_ADJACENT_TWO_BLOCK.movePlayerPieceTo(targetCell);
        assertThat(modelOption.isDefined(), equalTo(false));
    }

    @Test
    public void pieceCannotMoveBlockIfNotEnoughStrength(@Mocked final Player player) {
        new Expectations() {{
            player.getPushStrength();
            result = 1;
        }};

        new Expectations(TEST_BLOCK_1) {{
            TEST_BLOCK_1.getEffortToMove();
            result = 2;
        }};

        Cell targetCell = new Cell(1, 2);
        Option<BoardModel> modelOption = SINGLE_PLAYER_ADJACENT_SINGLE_BLOCK.movePlayerPieceTo(targetCell);
        assertThat(modelOption.isDefined(), equalTo(false));
    }

}