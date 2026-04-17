package sudoku.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sudoku.model.Board;

/**
 * Gera tabuleiros de Sudoku jogáveis a partir de uma solução aleatória.
 *
 * <p>O processo de geração é composto por três etapas sequenciais:
 * <ol>
 *   <li><b>Resolver um tabuleiro vazio</b> via {@link Solver} para obter uma solução válida.</li>
 *   <li><b>Aplicar randomização estrutural</b> preservando a validade do Sudoku:
 *       <ul>
 *         <li>Permutação aleatória dos dígitos 1–9 (mapeamento bijetor).</li>
 *         <li>Troca de duas linhas distintas dentro de cada banda horizontal.</li>
 *         <li>Troca de duas colunas distintas dentro de cada banda vertical.</li>
 *       </ul>
 *   </li>
 *   <li><b>Remover células</b> aleatoriamente para formar o puzzle inicial,
 *       marcando as células remanescentes como fixas.</li>
 * </ol>
 *
 * <p>Observação: este gerador não verifica unicidade da solução — em níveis muito
 * altos de remoção, o puzzle pode admitir múltiplas soluções.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
public class Generator {
    /** Número padrão de células removidas quando {@link #generate()} é chamado. */
    private static final int DEFAULT_CELLS_TO_REMOVE = 40;

    /** Solver usado para produzir a solução base do tabuleiro. */
    private final Solver solver;

    /** Fonte de aleatoriedade para permutações e remoções. */
    private final Random random;

    /**
     * Cria um gerador com solver padrão e fonte de aleatoriedade não determinística.
     */
    public Generator() {
        this.solver = new Solver();
        this.random = new Random();
    }

    /**
     * Gera um tabuleiro padrão com 40 células vazias.
     *
     * @return tabuleiro pronto para jogar
     */
    public Board generate() {
        return generate(DEFAULT_CELLS_TO_REMOVE);
    }

    /**
     * Gera um tabuleiro resolvido, aplica randomização estrutural
     * e remove a quantidade solicitada de células.
     *
     * @param cellsToRemove quantidade de células a serem zeradas (0–81)
     * @return tabuleiro com células preenchidas marcadas como fixas
     * @throws IllegalArgumentException se {@code cellsToRemove} estiver fora de 0–81
     */
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

    /**
     * Aplica as três transformações que preservam a validade do Sudoku.
     *
     * @param board tabuleiro já resolvido a ser randomizado
     */
    private void randomizeSolvedBoard(Board board) {
        applyRandomDigitMapping(board);
        shuffleRowsWithinBands(board);
        shuffleColumnsWithinStacks(board);
    }

    /**
     * Substitui cada dígito 1–9 por outro através de um mapeamento bijetor aleatório.
     *
     * <p>Como a operação é uma permutação de símbolos, todas as restrições do Sudoku
     * continuam válidas após a aplicação.
     *
     * @param board tabuleiro a ser transformado
     */
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

    /**
     * Troca duas linhas <em>distintas</em> dentro de cada banda horizontal (3 bandas de 3 linhas).
     *
     * <p>A troca usa um offset aleatório de 1 ou 2, garantindo que os dois índices
     * sorteados nunca sejam iguais — evitando swaps nulos.
     *
     * @param board tabuleiro a ser transformado
     */
    private void shuffleRowsWithinBands(Board board) {
        for (int band = 0; band < 3; band++) {
            int baseRow = band * 3;
            int localA = random.nextInt(3);
            int offset = 1 + random.nextInt(2); // 1 ou 2, garante localA != localB
            int localB = (localA + offset) % 3;
            swapRows(board, baseRow + localA, baseRow + localB);
        }
    }

    /**
     * Troca duas colunas <em>distintas</em> dentro de cada banda vertical (3 bandas de 3 colunas).
     *
     * @param board tabuleiro a ser transformado
     */
    private void shuffleColumnsWithinStacks(Board board) {
        for (int stack = 0; stack < 3; stack++) {
            int baseCol = stack * 3;
            int localA = random.nextInt(3);
            int offset = 1 + random.nextInt(2); // 1 ou 2, garante localA != localB
            int localB = (localA + offset) % 3;
            swapColumns(board, baseCol + localA, baseCol + localB);
        }
    }

    /**
     * Troca o conteúdo de duas linhas inteiras.
     *
     * @param board tabuleiro a ser modificado
     * @param rowA  primeira linha
     * @param rowB  segunda linha
     */
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

    /**
     * Troca o conteúdo de duas colunas inteiras.
     *
     * @param board tabuleiro a ser modificado
     * @param colA  primeira coluna
     * @param colB  segunda coluna
     */
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

    /**
     * Remove aleatoriamente {@code cellsToRemove} células, zerando seus valores,
     * e marca as células remanescentes como fixas.
     *
     * @param board          tabuleiro resolvido
     * @param cellsToRemove  quantidade de células a zerar
     */
    private void removeCells(Board board, int cellsToRemove) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            positions.add(i);
        }
        Collections.shuffle(positions, random);

        // Primeiro marca tudo como fixo; as células removidas serão desmarcadas a seguir.
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                board.getCell(row, col).setFixed(true);
            }
        }

        // Remove as células sorteadas e desmarca seu estado fixo.
        for (int i = 0; i < cellsToRemove; i++) {
            int position = positions.get(i);
            int row = position / 9;
            int col = position % 9;
            board.setValue(row, col, 0);
            board.getCell(row, col).setFixed(false);
        }
    }
}
