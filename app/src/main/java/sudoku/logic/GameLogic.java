package sudoku.logic;

import sudoku.model.Board;
import sudoku.model.Cell;

/**
 * Concentra as regras de partida do Sudoku que não são responsabilidade direta
 * do {@link Validador} nem do {@link Solver}.
 *
 * <p>Foi extraída do controller para permitir reuso e testes isolados. Oferece:
 * <ul>
 *   <li>{@link #isVictory(Board)}        — detecção de tabuleiro completo e válido;</li>
 *   <li>{@link #copyBoard(Board)}        — cópia profunda de tabuleiro para simulações;</li>
 *   <li>{@link #clearAllUserMoves(Board)}— limpa jogadas do usuário preservando células fixas.</li>
 * </ul>
 *
 * <p>Internamente delega as validações estruturais ao {@link Validador} recebido
 * via construtor (injeção de dependência).
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
public class GameLogic {
    /** Tamanho do tabuleiro (9 linhas x 9 colunas). */
    private static final int SIZE = 9;

    /** Validador utilizado nas verificações de linha, coluna e caixa. */
    private final Validador validador;

    /**
     * Cria uma instância com um {@link Validador} padrão.
     */
    public GameLogic() {
        this(new Validador());
    }

    /**
     * Cria uma instância injetando um validador específico.
     *
     * @param validador instância de validador a ser usada
     * @throws IllegalArgumentException se {@code validador} for {@code null}
     */
    public GameLogic(Validador validador) {
        if (validador == null) {
            throw new IllegalArgumentException("Validador cannot be null.");
        }
        this.validador = validador;
    }

    /**
     * Retorna {@code true} se o tabuleiro está totalmente preenchido e respeita
     * todas as regras do Sudoku (linhas, colunas e caixas 3x3 sem duplicatas).
     *
     * @param board tabuleiro a ser avaliado; {@code null} retorna {@code false}
     * @return {@code true} se o tabuleiro representa uma solução completa e válida
     */
    public boolean isVictory(Board board) {
        if (board == null) {
            return false;
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board.getCell(row, col).getValue() == 0) {
                    return false;
                }
            }
        }

        for (int i = 0; i < SIZE; i++) {
            if (!validador.isValidRow(board, i) || !validador.isValidColumn(board, i)) {
                return false;
            }
        }

        for (int row = 0; row < SIZE; row += 3) {
            for (int col = 0; col < SIZE; col += 3) {
                if (!validador.isValidBox(board, row, col)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Cria uma cópia profunda do tabuleiro, com novas instâncias de {@link Cell}
     * preservando valor e estado fixo.
     *
     * <p>Usado, por exemplo, para simular a conclusão automática do puzzle sem
     * alterar o estado atual da partida.
     *
     * @param board tabuleiro original
     * @return novo tabuleiro independente
     * @throws IllegalArgumentException se {@code board} for {@code null}
     */
    public Board copyBoard(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }

        Cell[][] cells = new Cell[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell source = board.getCell(row, col);
                cells[row][col] = new Cell(source.getValue(), source.isFixed());
            }
        }
        return new Board(cells);
    }

    /**
     * Zera o valor de todas as células não-fixas, mantendo intactas as células
     * pré-preenchidas do puzzle original.
     *
     * @param board tabuleiro a ser limpo
     * @throws IllegalArgumentException se {@code board} for {@code null}
     */
    public void clearAllUserMoves(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (!board.getCell(row, col).isFixed()) {
                    board.setValue(row, col, 0);
                }
            }
        }
    }
}
