package sudoku.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;

import sudoku.model.Board;

public class Generator {
    private final Solver solver;
    private final Random random;

    public Generator() {
        this.solver = new Solver();
        this.random = new Random();
    }

    public Board generate() {
        return generate(40);
    }

    public Board generate(int cellsToRemove) {
        if (cellsToRemove < 0 || cellsToRemove > 81) {
            throw new IllegalArgumentException("cellsToRemove must be between 0 and 81.");
        }

        Board board = new Board();
        solver.solve(board);
        randomizeSolvedBoard(board);
        removeCells(board, cellsToRemove);
        return board;
    }

    private void randomizeSolvedBoard(Board board) {
        applyRandomDigitMapping(board);
        shuffleRowsWithinBands(board);
        shuffleColumnsWithinStacks(board);
    }

    private void applyRandomDigitMapping(Board board) {
        List<Integer> digits = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(digits, random);
        Map<Integer, Integer> mapping = new HashMap<>();
        for (int i = 0; i < digits.size(); i++) {
            mapping.put(i + 1, digits.get(i));
        }

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = board.getCell(row, col).getValue();
                board.setValue(row, col, mapping.get(value));
            }
        }
    }

    private void shuffleRowsWithinBands(Board board) {
        for (int band = 0; band < 3; band++) {
            int baseRow = band * 3;
            int rowA = baseRow + random.nextInt(3);
            int rowB = baseRow + random.nextInt(3);
            swapRows(board, rowA, rowB);
        }
    }

    private void shuffleColumnsWithinStacks(Board board) {
        for (int stack = 0; stack < 3; stack++) {
            int baseCol = stack * 3;
            int colA = baseCol + random.nextInt(3);
            int colB = baseCol + random.nextInt(3);
            swapColumns(board, colA, colB);
        }
    }

    private void swapRows(Board board, int rowA, int rowB) {
        if (rowA == rowB) {
            return;
        }
        for (int col = 0; col < 9; col++) {
            int temp = board.getCell(rowA, col).getValue();
            board.setValue(rowA, col, board.getCell(rowB, col).getValue());
            board.setValue(rowB, col, temp);
        }
    }

    private void swapColumns(Board board, int colA, int colB) {
        if (colA == colB) {
            return;
        }
        for (int row = 0; row < 9; row++) {
            int temp = board.getCell(row, colA).getValue();
            board.setValue(row, colA, board.getCell(row, colB).getValue());
            board.setValue(row, colB, temp);
        }
    }

    private void removeCells(Board board, int cellsToRemove) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            positions.add(i);
        }
        Collections.shuffle(positions, random);

        for (int i = 0; i < cellsToRemove; i++) {
            int position = positions.get(i);
            int row = position / 9;
            int col = position % 9;
            board.setValue(row, col, 0);
            board.getCell(row, col).setFixed(false);
        }

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.getCell(row, col).getValue() != 0) {
                    board.getCell(row, col).setFixed(true);
                }
            }
        }
    }
}
