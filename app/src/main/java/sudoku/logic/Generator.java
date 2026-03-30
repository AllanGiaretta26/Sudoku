package sudoku.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
        removeCells(board, cellsToRemove);
        return board;
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
