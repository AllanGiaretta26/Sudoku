package sudoku.model;

/**
 * Representa o estado completo do tabuleiro 9x9.
 */
public class Board {
    private Cell[][] cell;

    public Board(Cell[][] cell){
        if (cell == null || cell.length != 9) {
            throw new IllegalArgumentException("Board must be a 9x9 matrix.");
        }

        this.cell = new Cell[9][9];
        for (int row = 0; row < 9; row++) {
            if (cell[row] == null || cell[row].length != 9) {
                throw new IllegalArgumentException("Board must be a 9x9 matrix.");
            }

            for (int col = 0; col < 9; col++) {
                Cell source = cell[row][col];
                if (source == null) {
                    this.cell[row][col] = new Cell();
                } else {
                    this.cell[row][col] = new Cell(source.getValue(), source.isFixed());
                }
            }
        }
    }

    public Board(){
        this.cell = new Cell[9][9];
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                this.cell[row][col] = new Cell();
            }
        }
    }

    public Cell getCell(int row, int col){
        validateIndices(row, col);
        return this.cell[row][col];
    }

    public void setValue(int row, int col, int value){
        validateIndices(row, col);
        this.cell[row][col].setValue(value);
    }

    public String printBoard(){
        StringBuilder board = new StringBuilder();
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                board.append(this.cell[i][j].getValue()).append(" ");
            }
            board.append("\n");
        }
        return board.toString();
    }

    private void validateIndices(int row, int col) {
        if (row < 0 || row > 8 || col < 0 || col > 8) {
            throw new IllegalArgumentException("Row and column must be between 0 and 8.");
        }
    }
}
