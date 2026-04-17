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
        return solveBacktracking(board, 0);
    }

    /**
     * Implementa o núcleo recursivo do backtracking.
     *
     * @param board      tabuleiro em construção
     * @param startIndex índice linear (0-80) a partir do qual a busca por célula vazia começa
     * @return {@code true} se a recursão atingiu um estado totalmente preenchido e válido
     */
    private boolean solveBacktracking(Board board, int startIndex) {
        int[] emptyCell = findEmptyCell(board, startIndex);
        if (emptyCell == null) {
            return true;
        }

        int row         = emptyCell[0];
        int col         = emptyCell[1];
        int linearIndex = emptyCell[2];

        for (int value = 1; value <= 9; value++) {
            board.setValue(row, col, value);
            if (isPlacementValid(board, row, col) && solveBacktracking(board, linearIndex + 1)) {
                return true;
            }
        }

        board.setValue(row, col, 0);
        return false;
    }

    /**
     * Encontra a primeira célula vazia a partir de {@code startIndex} percorrendo o tabuleiro
     * linha a linha em ordem linear.
     *
     * @param board      tabuleiro a ser varrido
     * @param startIndex índice linear inicial (0 = linha 0, coluna 0; 80 = linha 8, coluna 8)
     * @return array {@code [row, col, linearIndex]} da primeira célula com valor {@code 0}
     *         encontrada a partir de {@code startIndex}, ou {@code null} se não houver nenhuma
     */
    private int[] findEmptyCell(Board board, int startIndex) {
        for (int i = startIndex; i <= 80; i++) {
            int row = i / 9;
            int col = i % 9;
            if (board.getCell(row, col).getValue() == 0) {
                return new int[] {row, col, i};
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
