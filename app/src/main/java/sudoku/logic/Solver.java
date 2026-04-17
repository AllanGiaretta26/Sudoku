package sudoku.logic;

import sudoku.model.Board;

/**
 * Resolve tabuleiros de Sudoku usando o algoritmo de <em>backtracking</em> recursivo.
 *
 * <p>Estratégia: para cada célula vazia, testa os valores de 1 a 9; ao encontrar um
 * valor que respeita as regras (validado via {@link Validador}), avança para a próxima
 * célula. Se nenhum valor funcionar, desfaz a tentativa e retorna ao passo anterior.
 *
 * <p>Complexidade teórica de pior caso: {@code O(9^m)}, onde {@code m} é o número de
 * células vazias. Na prática, a poda por validação reduz drasticamente o espaço de busca.
 *
 * <p>A instância é reutilizável — o solver não mantém estado entre chamadas de
 * {@link #solve(Board)}; a mutação ocorre no próprio {@link Board} passado como parâmetro.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
public class Solver {
    /** Validador de regras injetado, usado em cada tentativa de preenchimento. */
    private final Validador validador;

    /**
     * Cria um solver com um {@link Validador} padrão.
     */
    public Solver() {
        this.validador = new Validador();
    }

    /**
     * Cria um solver usando um validador externo (útil para testes e injeção de dependência).
     *
     * @param validador instância de validador a ser usada
     * @throws IllegalArgumentException se {@code validador} for {@code null}
     */
    public Solver(Validador validador) {
        if (validador == null) {
            throw new IllegalArgumentException("Validador cannot be null.");
        }
        this.validador = validador;
    }

    /**
     * Tenta resolver o tabuleiro passado, preenchendo todas as células vazias.
     *
     * <p>O método modifica o tabuleiro recebido. Se nenhuma solução for encontrada,
     * todas as modificações são desfeitas (rollback via backtracking).
     *
     * @param board tabuleiro a ser resolvido
     * @return {@code true} se uma solução válida foi encontrada, {@code false} caso contrário
     * @throws IllegalArgumentException se {@code board} for {@code null}
     */
    public boolean solve(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }
        return solveBacktracking(board);
    }

    /**
     * Implementa o núcleo recursivo do backtracking.
     *
     * @param board tabuleiro em construção
     * @return {@code true} se a recursão atingiu um estado totalmente preenchido e válido
     */
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

    /**
     * Encontra a primeira célula vazia percorrendo o tabuleiro linha a linha.
     *
     * @param board tabuleiro a ser varrido
     * @return array {@code [row, col]} da primeira célula com valor {@code 0},
     *         ou {@code null} se o tabuleiro estiver completamente preenchido
     */
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

    /**
     * Valida uma jogada recém-efetuada verificando linha, coluna e caixa afetadas.
     *
     * @param board tabuleiro em análise
     * @param row   linha da célula modificada
     * @param col   coluna da célula modificada
     * @return {@code true} se os três eixos continuam válidos após a jogada
     */
    private boolean isPlacementValid(Board board, int row, int col) {
        int boxRow = row - (row % 3);
        int boxCol = col - (col % 3);
        return validador.isValidRow(board, row)
            && validador.isValidColumn(board, col)
            && validador.isValidBox(board, boxRow, boxCol);
    }
}
