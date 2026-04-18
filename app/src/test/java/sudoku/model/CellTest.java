package sudoku.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CellTest {

    @Test
    void defaultCellIsEmptyAndNotFixed() {
        Cell cell = new Cell();
        assertEquals(0, cell.getValue());
        assertFalse(cell.isFixed());
    }

    @Test
    void constructorSetsValueAndFixed() {
        Cell cell = new Cell(5, true);
        assertEquals(5, cell.getValue());
        assertTrue(cell.isFixed());
    }

    @Test
    void constructorRejectsNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new Cell(-1, false));
    }

    @Test
    void constructorRejectsValueAbove9() {
        assertThrows(IllegalArgumentException.class, () -> new Cell(10, false));
    }

    @Test
    void setValueUpdatesCorrectly() {
        Cell cell = new Cell();
        cell.setValue(7);
        assertEquals(7, cell.getValue());
    }

    @Test
    void setValueRejectsNegative() {
        Cell cell = new Cell();
        assertThrows(IllegalArgumentException.class, () -> cell.setValue(-1));
    }

    @Test
    void setValueRejectsAbove9() {
        Cell cell = new Cell();
        assertThrows(IllegalArgumentException.class, () -> cell.setValue(10));
    }

    @Test
    void setValueAcceptsZero() {
        Cell cell = new Cell(5, false);
        assertDoesNotThrow(() -> cell.setValue(0));
        assertEquals(0, cell.getValue());
    }

    @Test
    void setFixedToggles() {
        Cell cell = new Cell();
        cell.setFixed(true);
        assertTrue(cell.isFixed());
        cell.setFixed(false);
        assertFalse(cell.isFixed());
    }
}
