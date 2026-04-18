package sudoku.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BoardTest {

    @Test
    void defaultBoardIsAllZeroAndNotFixed() {
        Board board = new Board();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                assertEquals(0, board.getValueAt(row, col));
                assertFalse(board.getCell(row, col).isFixed());
            }
        }
    }

    @Test
    void setValueAndGetValueAt() {
        Board board = new Board();
        board.setValue(2, 3, 7);
        assertEquals(7, board.getValueAt(2, 3));
    }

    @Test
    void getCellReturnsMutableReference() {
        Board board = new Board();
        board.getCell(0, 0).setValue(5);
        assertEquals(5, board.getValueAt(0, 0));
    }

    @Test
    void copyConstructorClonesDeep() {
        Cell[][] cells = new Cell[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                cells[r][c] = new Cell(1, false);
            }
        }
        Board board = new Board(cells);
        cells[0][0].setValue(9);
        assertEquals(1, board.getValueAt(0, 0));
    }

    @Test
    void copyConstructorHandlesNullCells() {
        Cell[][] cells = new Cell[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                cells[r][c] = new Cell(3, false);
            }
        }
        cells[4][4] = null;
        Board board = new Board(cells);
        assertEquals(0, board.getValueAt(4, 4));
        assertFalse(board.getCell(4, 4).isFixed());
    }

    @Test
    void copyConstructorRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Board(null));
    }

    @Test
    void copyConstructorRejectsWrongSize() {
        Cell[][] cells = new Cell[8][9];
        assertThrows(IllegalArgumentException.class, () -> new Board(cells));
    }

    @Test
    void getValueAtRejectsInvalidRow() {
        Board board = new Board();
        assertThrows(IllegalArgumentException.class, () -> board.getValueAt(-1, 0));
    }

    @Test
    void getValueAtRejectsInvalidCol() {
        Board board = new Board();
        assertThrows(IllegalArgumentException.class, () -> board.getValueAt(0, 9));
    }

    @Test
    void setValueRejectsOutOfBounds() {
        Board board = new Board();
        assertThrows(IllegalArgumentException.class, () -> board.setValue(9, 9, 5));
    }

    @Test
    void printBoardContainsAllValues() {
        Board board = new Board();
        board.setValue(0, 0, 3);
        String output = board.printBoard();
        assertFalse(output.isEmpty());
        assertEquals(9, output.lines().count());
    }
}
