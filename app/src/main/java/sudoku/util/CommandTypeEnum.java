package sudoku.util;

/**
 * Enumera todos os tipos de comandos que o parser da UI reconhece.
 *
 * <p>Cada valor corresponde a uma ação possível do usuário no loop principal
 * do jogo. Usado internamente pela classe {@code ConsoleUI.Move} para classificar
 * o comando interpretado.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
public enum CommandTypeEnum {
    /** Jogada de preenchimento: {@code linha coluna valor}. */
    PLAY,

    /** Salvar a partida atual em arquivo. */
    SAVE,

    /** Carregar uma partida previamente salva. */
    LOAD,

    /** Gerar um novo tabuleiro aleatório. */
    NEW_BOARD,

    /** Consultar o status atual da partida. */
    STATUS,

    /** Solicitar o preenchimento automático do tabuleiro. */
    COMPLETE,

    /** Remover o valor de uma célula não-fixa. */
    REMOVE,

    /** Limpar todas as jogadas do usuário, preservando as células fixas. */
    CLEAR,

    /** Exibir a lista de comandos disponíveis. */
    HELP,

    /** Encerrar o jogo. */
    QUIT
}
