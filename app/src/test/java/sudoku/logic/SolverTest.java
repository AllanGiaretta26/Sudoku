package sudoku.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sudoku.model.Board;

/**
 * Suíte de testes para {@link Solver}, cobrindo resolução completa, boards
 * parcialmente preenchidos, boards inconsistentes e validação de argumentos.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
class SolverTest {
    private Solver solver;
    private Validador validador;

    @BeforeEach
    void setUp() {
        validador = new Validador();
        solver = new Solver(validador);
    }

    @Test
    void solveCompletesEmptyBoard() {
        Board board = new Board();
        assertTrue(solver.solve(board));
        assertTrue(isCompletelyFilled(board));
        assertTrue(isBoardValid(board));
    }

    @Test
    void solveHandlesBoardWithSomePreFilledCells() {
        Board board = new Board();
        board.setValue(0, 0, 5);
        board.setValue(0, 1, 3);
        board.setValue(1, 0, 6);

        assertTrue(solver.solve(board));
        assertEquals(5, board.getCell(0, 0).getValue());
        assertEquals(3, board.getCell(0, 1).getValue());
        assertEquals(6, board.getCell(1, 0).getValue());
        assertTrue(isCompletelyFilled(board));
    }

    @Test
    void solveReturnsFalseOnInconsistentBoard() {
        Board board = new Board();
        board.setValue(0, 0, 5);
        board.setValue(0, 1, 5); // duplicata ja na linha
        assertFalse(solver.solve(board));
    }

    @Test
    void solveThrowsOnNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> solver.solve(null));
    }

    @Test
    void constructorRejectsNullValidador() {
        assertThrows(IllegalArgumentException.class, () -> new Solver(null));
    }

    private boolean isCompletelyFilled(Board board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.getCell(row, col).getValue() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isBoardValid(Board board) {
        for (int i = 0; i < 9; i++) {
            if (!validador.isValidRow(board, i) || !validador.isValidColumn(board, i)) {
                return false;
            }
        }
        for (int row = 0; row < 9; row += 3) {
            for (int col = 0; col < 9; col += 3) {
                if (!validador.isValidBox(board, row, col)) {
                    return false;
                }
            }
        }
        return true;
    }
}
