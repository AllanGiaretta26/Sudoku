package sudoku.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sudoku.model.Board;

/**
 * Suíte de testes para {@link Validador}, cobrindo as três verificações de
 * unicidade do Sudoku (linha, coluna e caixa 3x3) bem como validação de argumentos.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
class ValidadorTest {
    private Validador validador;
    private Board board;

    @BeforeEach
    void setUp() {
        validador = new Validador();
        board = new Board();
    }

    @Test
    void isValidRowDetectsNoDuplicates() {
        for (int col = 0; col < 9; col++) {
            board.setValue(0, col, col + 1);
        }
        assertTrue(validador.isValidRow(board, 0));
    }

    @Test
    void isValidRowDetectsDuplicates() {
        board.setValue(0, 0, 5);
        board.setValue(0, 5, 5);
        assertFalse(validador.isValidRow(board, 0));
    }

    @Test
    void isValidRowAcceptsEmptyCells() {
        assertTrue(validador.isValidRow(board, 0));
    }

    @Test
    void isValidColumnDetectsDuplicates() {
        board.setValue(0, 3, 7);
        board.setValue(4, 3, 7);
        assertFalse(validador.isValidColumn(board, 3));
    }

    @Test
    void isValidColumnAcceptsValidValues() {
        for (int row = 0; row < 9; row++) {
            board.setValue(row, 2, row + 1);
        }
        assertTrue(validador.isValidColumn(board, 2));
    }

    @Test
    void isValidBoxDetectsDuplicates() {
        board.setValue(0, 0, 9);
        board.setValue(2, 2, 9);
        assertFalse(validador.isValidBox(board, 0, 0));
    }

    @Test
    void isValidBoxAcceptsEmptyBox() {
        assertTrue(validador.isValidBox(board, 0, 0));
    }

    @Test
    void isValidRowThrowsOnOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> validador.isValidRow(board, -1));
        assertThrows(IllegalArgumentException.class, () -> validador.isValidRow(board, 9));
    }

    @Test
    void isValidBoxThrowsOnOutOfRangeStart() {
        assertThrows(IllegalArgumentException.class, () -> validador.isValidBox(board, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> validador.isValidBox(board, 0, 7));
        assertThrows(IllegalArgumentException.class, () -> validador.isValidBox(board, 7, 0));
    }
}
