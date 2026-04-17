package sudoku.model;

/**
 * Representa o tabuleiro completo de Sudoku como uma matriz {@code 9x9} de {@link Cell}.
 *
 * <p>O tabuleiro é organizado em:
 * <ul>
 *   <li>9 linhas indexadas de 0 a 8;</li>
 *   <li>9 colunas indexadas de 0 a 8;</li>
 *   <li>9 caixas {@code 3x3} cujos cantos superiores-esquerdos estão em
 *       (0,0), (0,3), (0,6), (3,0), ..., (6,6).</li>
 * </ul>
 *
 * <p>Por padrão, todas as células começam vazias ({@code value = 0},
 * {@code fixed = false}). O construtor de cópia realiza clonagem profunda
 * das células recebidas, garantindo independência do array original.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
public class Board {
    /** Tamanho do tabuleiro (9 linhas x 9 colunas). */
    private static final int SIZE = 9;

    /** Matriz interna de células. */
    private Cell[][] cell;

    /**
     * Constrói um tabuleiro a partir de uma matriz existente, aplicando cópia profunda.
     *
     * <p>Cada célula é clonada em uma nova instância, preservando valor e estado fixo.
     * Posições {@code null} dentro da matriz são substituídas por células vazias.
     *
     * @param cell matriz {@code 9x9} não nula
     * @throws IllegalArgumentException se a matriz for nula, não tiver dimensão 9x9
     *                                  ou contiver linha nula com tamanho inválido
     */
    public Board(Cell[][] cell) {
        if (cell == null || cell.length != SIZE) {
            throw new IllegalArgumentException("Board must be a 9x9 matrix.");
        }

        this.cell = new Cell[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            if (cell[row] == null || cell[row].length != SIZE) {
                throw new IllegalArgumentException("Board must be a 9x9 matrix.");
            }

            for (int col = 0; col < SIZE; col++) {
                Cell source = cell[row][col];
                if (source == null) {
                    this.cell[row][col] = new Cell();
                } else {
                    this.cell[row][col] = new Cell(source.getValue(), source.isFixed());
                }
            }
        }
    }

    /**
     * Constrói um tabuleiro {@code 9x9} totalmente vazio.
     */
    public Board() {
        this.cell = new Cell[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                this.cell[row][col] = new Cell();
            }
        }
    }

    /**
     * Retorna a referência da célula localizada em {@code (row, col)}.
     *
     * @param row índice da linha (0–8)
     * @param col índice da coluna (0–8)
     * @return a célula naquela posição
     * @throws IllegalArgumentException se os índices estiverem fora do intervalo válido
     */
    public Cell getCell(int row, int col) {
        validateIndices(row, col);
        return this.cell[row][col];
    }

    /**
     * Atualiza o valor da célula em {@code (row, col)}.
     *
     * <p>Este método não verifica se a célula é fixa — essa validação é
     * responsabilidade das camadas superiores (controller ou lógica de regras).
     *
     * @param row   índice da linha (0–8)
     * @param col   índice da coluna (0–8)
     * @param value novo valor (0 para vazia, 1–9 para preenchida)
     * @throws IllegalArgumentException se os índices estiverem fora do intervalo válido
     */
    public void setValue(int row, int col, int value) {
        validateIndices(row, col);
        this.cell[row][col].setValue(value);
    }

    /**
     * Gera uma representação textual simples do tabuleiro, sem separadores visuais.
     *
     * <p>Cada linha contém os 9 valores separados por espaço. Útil para debug e logs.
     *
     * @return string multilinha com os valores de todas as células
     */
    public String printBoard() {
        StringBuilder board = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board.append(this.cell[i][j].getValue()).append(" ");
            }
            board.append("\n");
        }
        return board.toString();
    }

    /**
     * Garante que os índices passados estejam dentro dos limites do tabuleiro.
     *
     * @param row índice da linha a validar
     * @param col índice da coluna a validar
     * @throws IllegalArgumentException se qualquer índice estiver fora de 0–8
     */
    private void validateIndices(int row, int col) {
        if (row < 0 || row > 8 || col < 0 || col > 8) {
            throw new IllegalArgumentException("Row and column must be between 0 and 8.");
        }
    }
}
