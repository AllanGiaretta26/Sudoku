package sudoku.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import sudoku.model.Board;

public class FileManager {
    private static final int BOARD_SIZE = 9;

    public void saveGame(Board board, String filePath) throws IOException {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("filePath cannot be empty.");
        }

        Path path = Paths.get(filePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        StringBuilder content = new StringBuilder();
        content.append("SUDOKU_SAVE_V1").append(System.lineSeparator());

        for (int row = 0; row < BOARD_SIZE; row++) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < BOARD_SIZE; col++) {
                int value = board.getCell(row, col).getValue();
                int fixed = board.getCell(row, col).isFixed() ? 1 : 0;
                line.append(value).append(":").append(fixed);
                if (col < BOARD_SIZE - 1) {
                    line.append(",");
                }
            }
            content.append(line).append(System.lineSeparator());
        }

        Files.writeString(path, content.toString(), StandardCharsets.UTF_8);
    }

    public Board loadGame(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("filePath cannot be empty.");
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("Save file not found: " + filePath);
        }

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lines.size() != BOARD_SIZE + 1) {
            throw new IOException("Invalid save format: unexpected number of lines.");
        }

        if (!"SUDOKU_SAVE_V1".equals(lines.get(0).trim())) {
            throw new IOException("Invalid save format: unsupported header.");
        }

        Board board = new Board();
        for (int row = 0; row < BOARD_SIZE; row++) {
            String line = lines.get(row + 1).trim();
            String[] cells = line.split(",");
            if (cells.length != BOARD_SIZE) {
                throw new IOException("Invalid save format at row " + (row + 1) + ".");
            }

            for (int col = 0; col < BOARD_SIZE; col++) {
                String[] parts = cells[col].split(":");
                if (parts.length != 2) {
                    throw new IOException("Invalid save format at cell [" + row + "," + col + "].");
                }

                int value;
                int fixed;
                try {
                    value = Integer.parseInt(parts[0]);
                    fixed = Integer.parseInt(parts[1]);
                } catch (NumberFormatException exception) {
                    throw new IOException("Invalid number format at cell [" + row + "," + col + "].", exception);
                }

                if (value < 0 || value > 9 || (fixed != 0 && fixed != 1)) {
                    throw new IOException("Invalid cell values at [" + row + "," + col + "].");
                }

                board.setValue(row, col, value);
                board.getCell(row, col).setFixed(fixed == 1);
            }
        }

        return board;
    }
}
