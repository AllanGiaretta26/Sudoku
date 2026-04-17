package sudoku.model;

/**
 * Representa uma célula individual do tabuleiro de Sudoku.
 *
 * <p>Cada célula armazena dois atributos:
 * <ul>
 *   <li>{@code value} — o número contido na célula (1–9), onde {@code 0} indica célula vazia.</li>
 *   <li>{@code fixed} — indica se a célula faz parte do puzzle original e, portanto,
 *       não pode ser alterada pelo jogador.</li>
 * </ul>
 *
 * <p>Esta classe é uma entidade mutável por design, permitindo que o {@link Board}
 * atualize valores durante o jogo ou o processo de resolução por backtracking.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
public class Cell {
    /** Valor armazenado na célula. {@code 0} representa célula vazia. */
    private int value = 0;

    /** Indica se a célula é parte do puzzle original e não pode ser alterada. */
    private boolean fixed = false;

    /**
     * Cria uma célula com valor e estado de fixação definidos.
     *
     * @param value valor inicial (0 para vazia, 1–9 para preenchida)
     * @param fixed {@code true} se a célula não pode ser alterada pelo usuário
     */
    public Cell(int value, boolean fixed) {
        this.value = value;
        this.fixed = fixed;
    }

    /**
     * Cria uma célula vazia e não fixa (valor {@code 0}, {@code fixed=false}).
     */
    public Cell() {
    }

    /**
     * Retorna o valor atual da célula.
     *
     * @return valor entre 0 e 9, onde 0 indica célula vazia
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Define um novo valor para a célula.
     *
     * @param value novo valor (0 para limpar, 1–9 para preencher)
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Indica se a célula é fixa (parte do puzzle original).
     *
     * @return {@code true} se a célula não pode ser alterada pelo jogador
     */
    public boolean isFixed() {
        return this.fixed;
    }

    /**
     * Marca ou desmarca a célula como fixa.
     *
     * @param fixed {@code true} para proteger a célula contra alterações do usuário
     */
    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
}
