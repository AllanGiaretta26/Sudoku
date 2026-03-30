package sudoku.logic;

import sudoku.model.Board;

public class Solver {
    private final Validador validador;

    public Solver() {
        this.validador = new Validador();
    }

    public Solver(Validador validador) {
        if (validador == null) {
            throw new IllegalArgumentException("Validador cannot be null.");
        }
        this.validador = validador;
    }

    public boolean solve(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }
        return solveBacktracking(board);
    }

    private boolean solveBacktracking(Board board) {
        int[] emptyCell = findEmptyCell(board);
        if (emptyCell == null) {
            return true;
        }

        int row = emptyCell[0];
        int col = emptyCell[1];

        for (int value = 1; value <= 9; value++) {
            board.setValue(row, col, value);
            if (isPlacementValid(board, row, col) && solveBacktracking(board)) {
                return true;
            }
        }

        board.setValue(row, col, 0);
        return false;
    }

    private int[] findEmptyCell(Board board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.getCell(row, col).getValue() == 0) {
                    return new int[] {row, col};
                }
            }
        }
        return null;
    }

    private boolean isPlacementValid(Board board, int row, int col) {
        int boxRow = row - (row % 3);
        int boxCol = col - (col % 3);
        return validador.isValidRow(board, row)
            && validador.isValidColumn(board, col)
            && validador.isValidBox(board, boxRow, boxCol);
    }
}
