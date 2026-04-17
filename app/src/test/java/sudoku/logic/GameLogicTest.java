package sudoku.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sudoku.model.Board;

/**
 * Suíte de testes para {@link GameLogic}, cobrindo detecção de vitória,
 * cópia independente de tabuleiros e limpeza de jogadas do usuário.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
class GameLogicTest {
    private GameLogic gameLogic;
    private Solver solver;

    @BeforeEach
    void setUp() {
        Validador validador = new Validador();
        gameLogic = new GameLogic(validador);
        solver = new Solver(validador);
    }

    @Test
    void isVictoryReturnsFalseForBoardWithEmptyCells() {
        Board board = new Board();
        assertFalse(gameLogic.isVictory(board));
    }

    @Test
    void isVictoryReturnsFalseForNullBoard() {
        assertFalse(gameLogic.isVictory(null));
    }

    @Test
    void isVictoryReturnsTrueForSolvedBoard() {
        Board board = new Board();
        solver.solve(board);
        assertTrue(gameLogic.isVictory(board));
    }

    @Test
    void isVictoryReturnsFalseForFilledButInvalidBoard() {
        Board board = new Board();
        // preenche todas as celulas com 1 (invalido por duplicatas)
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                board.setValue(row, col, 1);
            }
        }
        assertFalse(gameLogic.isVictory(board));
    }

    @Test
    void copyBoardReturnsIndependentCopy() {
        Board board = new Board();
        board.setValue(0, 0, 5);
        Board copy = gameLogic.copyBoard(board);

        assertNotSame(board, copy);
        assertEquals(5, copy.getCell(0, 0).getValue());

        copy.setValue(0, 0, 9);
        assertEquals(5, board.getCell(0, 0).getValue(), "Mudanca na copia nao deve afetar original");
    }

    @Test
    void clearAllUserMovesPreservesFixedCells() {
        Board board = new Board();
        board.setValue(0, 0, 5);
        board.getCell(0, 0).setFixed(true);
        board.setValue(1, 1, 7); // nao fixa

        gameLogic.clearAllUserMoves(board);

        assertEquals(5, board.getCell(0, 0).getValue(), "Celula fixa deve ser preservada");
        assertEquals(0, board.getCell(1, 1).getValue(), "Celula do usuario deve ser zerada");
    }

    @Test
    void constructorRejectsNullValidador() {
        assertThrows(IllegalArgumentException.class, () -> new GameLogic(null));
    }

    @Test
    void copyBoardRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> gameLogic.copyBoard(null));
    }
}
