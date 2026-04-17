package sudoku.logic;

import sudoku.model.Board;

/**
 * Centraliza a validação das regras do Sudoku.
 *
 * <p>Oferece três verificações independentes, cada uma aplicada a um eixo do puzzle:
 * <ul>
 *   <li>{@link #isValidRow(Board, int)}      — detecta duplicatas em uma linha;</li>
 *   <li>{@link #isValidColumn(Board, int)}   — detecta duplicatas em uma coluna;</li>
 *   <li>{@link #isValidBox(Board, int, int)} — detecta duplicatas em uma caixa 3x3.</li>
 * </ul>
 *
 * <p>Células vazias (valor {@code 0}) são ignoradas — um tabuleiro parcialmente
 * preenchido pode ser considerado "válido" desde que as células já preenchidas
 * não violem as regras do jogo.
 *
 * <p>Esta classe é stateless e thread-safe: pode ser compartilhada livremente
 * entre múltiplas instâncias de {@link Solver} ou {@link GameLogic}.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
public class Validador {

    /**
     * Verifica se uma linha do tabuleiro respeita a regra de unicidade do Sudoku.
     *
     * @param board tabuleiro a ser consultado
     * @param row   índice da linha (0–8)
     * @return {@code true} se não houver valores duplicados na linha
     * @throws IllegalArgumentException se {@code row} estiver fora do intervalo 0–8
     */
    public boolean isValidRow(Board board, int row) {
        if (row < 0 || row > 8) {
            throw new IllegalArgumentException("Row must be between 0 and 8.");
        }

        boolean[] seen = new boolean[10];
        for (int col = 0; col < 9; col++) {
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

    /**
     * Verifica se uma coluna do tabuleiro respeita a regra de unicidade do Sudoku.
     *
     * @param board  tabuleiro a ser consultado
     * @param column índice da coluna (0–8)
     * @return {@code true} se não houver valores duplicados na coluna
     * @throws IllegalArgumentException se {@code column} estiver fora do intervalo 0–8
     */
    public boolean isValidColumn(Board board, int column) {
        if (column < 0 || column > 8) {
            throw new IllegalArgumentException("Column must be between 0 and 8.");
        }

        boolean[] seen = new boolean[10];
        for (int row = 0; row < 9; row++) {
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

    /**
     * Verifica se uma caixa {@code 3x3} respeita a regra de unicidade do Sudoku.
     *
     * <p>O ponto de origem {@code (row, column)} é o canto superior esquerdo da caixa
     * e deve estar entre 0 e 6 (inclusive), já que a caixa se estende por 3 posições.
     *
     * @param board  tabuleiro a ser consultado
     * @param row    linha inicial da caixa (0–6)
     * @param column coluna inicial da caixa (0–6)
     * @return {@code true} se não houver valores duplicados na caixa
     * @throws IllegalArgumentException se {@code row} ou {@code column} estiverem fora de 0–6
     */
    public boolean isValidBox(Board board, int row, int column) {
        if (row < 0 || row > 6 || column < 0 || column > 6) {
            throw new IllegalArgumentException("Box start must be between 0 and 6.");
        }

        boolean[] seen = new boolean[10];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
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
