package sudoku.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sudoku.model.Board;

/**
 * Suíte de testes para {@link Generator}, cobrindo contagem de células vazias,
 * marcação correta de {@code fixed}, rejeição de parâmetros inválidos e
 * não-determinismo entre gerações consecutivas.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
class GeneratorTest {
    private Generator generator;

    @BeforeEach
    void setUp() {
        generator = new Generator();
    }

    @Test
    void generateProducesBoardWithExpectedEmptyCount() {
        Board board = generator.generate(40);
        int empty = countEmpty(board);
        assertEquals(40, empty);
    }

    @Test
    void generateMarksFilledCellsAsFixedAndEmptyAsNotFixed() {
        Board board = generator.generate(40);
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = board.getCell(row, col).getValue();
                boolean fixed = board.getCell(row, col).isFixed();
                if (value == 0) {
                    assertTrue(!fixed, "Celula vazia nao pode ser fixed");
                } else {
                    assertTrue(fixed, "Celula preenchida deve ser fixed");
                }
            }
        }
    }

    @Test
    void generateRejectsNegativeCount() {
        assertThrows(IllegalArgumentException.class, () -> generator.generate(-1));
    }

    @Test
    void generateRejectsCountAboveLimit() {
        assertThrows(IllegalArgumentException.class, () -> generator.generate(82));
    }

    @Test
    void twoGeneratedBoardsAreDifferent() {
        Board a = generator.generate(40);
        Board b = generator.generate(40);
        assertNotEquals(boardToString(a), boardToString(b),
                "Dois boards consecutivos devem ser diferentes");
    }

    @Test
    void generateDefaultOverloadWorks() {
        Board board = generator.generate();
        assertEquals(40, countEmpty(board));
    }

    private int countEmpty(Board board) {
        int empty = 0;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.getCell(row, col).getValue() == 0) {
                    empty++;
                }
            }
        }
        return empty;
    }

    private String boardToString(Board board) {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                sb.append(board.getCell(row, col).getValue());
            }
        }
        return sb.toString();
    }
}
