package sudoku.logic;

import sudoku.model.Board;

public class Validador {
    
    public boolean isValidRow(Board board, int row){
        if (row < 0 || row > 8) {
            throw new IllegalArgumentException("Row must be between 0 and 8.");
        }

        boolean[] seen = new boolean[10];
        for(int col = 0; col < 9; col++){
            int value = board.getCell(row, col).getValue();
            if (value == 0) {
                continue;
            }

            if (value < 1 || value > 9 || seen[value]) {
                return false;
            }
            seen[value] = true;
        }
        return true;
    }

    public boolean isValidColumn(Board board, int column){
        if (column < 0 || column > 8) {
            throw new IllegalArgumentException("Column must be between 0 and 8.");
        }

        boolean[] seen = new boolean[10];
        for(int row = 0; row < 9; row++){
            int value = board.getCell(row, column).getValue();
            if (value == 0) {
                continue;
            }

            if (value < 1 || value > 9 || seen[value]) {
                return false;
            }
            seen[value] = true;
        }
        return true;
    }
    
    public boolean isValidBox(Board board, int row, int column){
        if (row < 0 || row > 6 || column < 0 || column > 6) {
            throw new IllegalArgumentException("Box start must be between 0 and 6.");
        }

        boolean[] seen = new boolean[10];
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                int value = board.getCell(row + i, column + j).getValue();
                if (value == 0) {
                    continue;
                }

                if (value < 1 || value > 9 || seen[value]) {
                    return false;
                }
                seen[value] = true;
            }
        }
        return true;
    }

}
