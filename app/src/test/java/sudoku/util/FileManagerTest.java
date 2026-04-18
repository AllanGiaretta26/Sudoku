package sudoku.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sudoku.model.Board;

class FileManagerTest {

    @TempDir
    Path tempDir;

    private FileManager fileManager;

    @BeforeEach
    void setUp() {
        fileManager = new FileManager();
    }

    @Test
    void saveAndLoadRoundtrip() throws IOException {
        Board original = new Board();
        original.setValue(0, 0, 5);
        original.getCell(0, 0).setFixed(true);
        original.setValue(1, 1, 3);

        String filePath = tempDir.resolve("partida.txt").toString();
        fileManager.saveGame(original, filePath);
        Board loaded = fileManager.loadGame(filePath);

        assertEquals(5, loaded.getValueAt(0, 0));
        assertTrue(loaded.getCell(0, 0).isFixed());
        assertEquals(3, loaded.getValueAt(1, 1));
        assertFalse(loaded.getCell(1, 1).isFixed());
        assertEquals(0, loaded.getValueAt(2, 2));
    }

    @Test
    void saveCreatesParentDirectories() throws IOException {
        Board board = new Board();
        String filePath = tempDir.resolve("sub/partida.txt").toString();
        assertDoesNotThrow(() -> fileManager.saveGame(board, filePath));
        assertTrue(Files.exists(Path.of(filePath)));
    }

    @Test
    void loadThrowsWhenFileNotFound() {
        String filePath = tempDir.resolve("naoexiste.txt").toString();
        assertThrows(IOException.class, () -> fileManager.loadGame(filePath));
    }

    @Test
    void loadThrowsOnInvalidHeader() throws IOException {
        Path file = tempDir.resolve("bad_header.txt");
        StringBuilder sb = new StringBuilder("WRONG_HEADER\n");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                sb.append("0:0");
                if (j < 8) sb.append(",");
            }
            sb.append("\n");
        }
        Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
        assertThrows(IOException.class, () -> fileManager.loadGame(file.toString()));
    }

    @Test
    void loadThrowsOnWrongLineCount() throws IOException {
        Path file = tempDir.resolve("short.txt");
        Files.writeString(file, "SUDOKU_SAVE_V1\n0:0,0:0\n", StandardCharsets.UTF_8);
        assertThrows(IOException.class, () -> fileManager.loadGame(file.toString()));
    }

    @Test
    void loadThrowsOnMalformedCell() throws IOException {
        Path file = tempDir.resolve("malformed.txt");
        StringBuilder sb = new StringBuilder("SUDOKU_SAVE_V1\n");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // row 0, col 0 uses bad format "5-1" instead of "5:1"
                if (i == 0 && j == 0) sb.append("5-1");
                else sb.append("0:0");
                if (j < 8) sb.append(",");
            }
            sb.append("\n");
        }
        Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
        assertThrows(IOException.class, () -> fileManager.loadGame(file.toString()));
    }

    @Test
    void loadThrowsOnValueOutOfRange() throws IOException {
        Path file = tempDir.resolve("out_of_range.txt");
        StringBuilder sb = new StringBuilder("SUDOKU_SAVE_V1\n");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (i == 0 && j == 0) sb.append("10:0");
                else sb.append("0:0");
                if (j < 8) sb.append(",");
            }
            sb.append("\n");
        }
        Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
        assertThrows(IOException.class, () -> fileManager.loadGame(file.toString()));
    }

    @Test
    void saveThrowsOnNullBoard() {
        String filePath = tempDir.resolve("x.txt").toString();
        assertThrows(IllegalArgumentException.class, () -> fileManager.saveGame(null, filePath));
    }

    @Test
    void saveThrowsOnNullPath() {
        Board board = new Board();
        assertThrows(IllegalArgumentException.class, () -> fileManager.saveGame(board, null));
    }

    @Test
    void loadThrowsOnEmptyPath() {
        assertThrows(IllegalArgumentException.class, () -> fileManager.loadGame(""));
    }
}
