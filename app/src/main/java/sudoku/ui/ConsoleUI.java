package sudoku.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import sudoku.model.Board;
import sudoku.util.CommandTypeEnum;

/**
 * Camada de entrada e saída em modo console.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Imprimir o tabuleiro formatado com separadores visuais das caixas 3x3.</li>
 *   <li>Exibir mensagens de feedback ao usuário.</li>
 *   <li>Ler comandos digitados e convertê-los em objetos {@link Move} já validados
 *       estruturalmente (sintaxe + faixa de valores).</li>
 * </ul>
 *
 * <p>Implementa {@link AutoCloseable} para permitir uso com {@code try-with-resources}
 * e garantir o fechamento do {@link Scanner} interno.
 *
 * <p>O construtor sobrecarregado {@link #ConsoleUI(InputStream, PrintStream)} permite
 * injetar streams arbitrários, facilitando testes unitários do parser de comandos.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
public class ConsoleUI implements AutoCloseable {
    /** Scanner que lê os comandos do usuário. */
    private final Scanner scanner;

    /** Stream de saída onde o tabuleiro e mensagens são escritos. */
    private final PrintStream out;

    /** Mensagem padrão para comandos mal formados ou fora das regras do Sudoku. */
    private static final String INVALID_MOVE_MESSAGE = "Jogada errada para as regras Sudoku.";

    /**
     * Cria uma UI que lê de {@code System.in} e escreve em {@code System.out}.
     */
    public ConsoleUI() {
        this(System.in, System.out);
    }

    /**
     * Cria uma UI com streams injetados, útil para testes.
     *
     * @param in  fonte de entrada (geralmente {@code System.in} ou um {@code ByteArrayInputStream})
     * @param out destino da saída (geralmente {@code System.out} ou um {@code ByteArrayOutputStream})
     * @throws IllegalArgumentException se qualquer um dos streams for {@code null}
     */
    public ConsoleUI(InputStream in, PrintStream out) {
        if (in == null || out == null) {
            throw new IllegalArgumentException("InputStream and PrintStream cannot be null.");
        }
        this.scanner = new Scanner(in);
        this.out = out;
    }

    /**
     * Fecha o {@link Scanner} interno, liberando o recurso associado ao stream de entrada.
     */
    @Override
    public void close() {
        scanner.close();
    }

    /**
     * Imprime o tabuleiro formatado em {@code 9x9} com separadores visuais a cada 3 caixas.
     *
     * <p>Células vazias (valor {@code 0}) são representadas como {@code "."}.
     *
     * @param board tabuleiro a ser exibido
     */
    public void printBoard(Board board) {
        out.println();
        out.println("    1 2 3   4 5 6   7 8 9");
        out.println("  +-------+-------+-------+");
        for (int row = 0; row < 9; row++) {
            out.print((row + 1) + " | ");
            for (int col = 0; col < 9; col++) {
                int value = board.getCell(row, col).getValue();
                String symbol = value == 0 ? "." : Integer.toString(value);
                out.print(symbol + " ");
                if ((col + 1) % 3 == 0) {
                    out.print("| ");
                }
            }
            out.println();
            if ((row + 1) % 3 == 0) {
                out.println("  +-------+-------+-------+");
            }
        }
        out.println();
    }

    /**
     * Exibe uma mensagem arbitrária ao usuário.
     *
     * @param message texto a ser impresso
     */
    public void printMessage(String message) {
        out.println(message);
    }

    /**
     * Lê comandos do usuário em loop até receber um comando válido, retornando o
     * {@link Move} correspondente.
     *
     * <p>Entradas inválidas (ex.: número fora de 1–9, palavras em vez de dígitos,
     * argumentos ausentes) não encerram a leitura — o método exibe mensagem de erro
     * e solicita novamente.
     *
     * @return movimento interpretado pronto para ser aplicado pelo controller
     */
    public Move readMove() {
        while (true) {
            out.print("Comando (linha coluna valor | remove linha coluna | clear | save arquivo.txt | load arquivo.txt | new | status | complete | help | q): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) {
                return Move.quit();
            }

            if (input.equalsIgnoreCase("help")) {
                return Move.help();
            }

            if (input.equalsIgnoreCase("new")) {
                return Move.newBoard();
            }

            if (input.equalsIgnoreCase("status")) {
                return Move.status();
            }

            if (input.equalsIgnoreCase("complete")) {
                return Move.complete();
            }

            if (input.equalsIgnoreCase("clear")) {
                return Move.clearUserMoves();
            }

            if (input.toLowerCase().startsWith("save ")) {
                String filePath = input.substring(5).trim();
                if (filePath.isEmpty()) {
                    printMessage("Informe o nome do arquivo. Exemplo: save partida.txt");
                    continue;
                }
                return Move.save(filePath);
            }

            if (input.toLowerCase().startsWith("load ")) {
                String filePath = input.substring(5).trim();
                if (filePath.isEmpty()) {
                    printMessage("Informe o nome do arquivo. Exemplo: load partida.txt");
                    continue;
                }
                return Move.load(filePath);
            }

            if (input.toLowerCase().startsWith("remove ")) {
                String[] parts = input.split("\\s+");
                if (parts.length != 3) {
                    printMessage(INVALID_MOVE_MESSAGE);
                    continue;
                }
                try {
                    int row = Integer.parseInt(parts[1]);
                    int col = Integer.parseInt(parts[2]);
                    if (row < 1 || row > 9 || col < 1 || col > 9) {
                        printMessage(INVALID_MOVE_MESSAGE);
                        continue;
                    }
                    return Move.remove(row - 1, col - 1);
                } catch (NumberFormatException exception) {
                    printMessage(INVALID_MOVE_MESSAGE);
                    continue;
                }
            }

            String[] parts = input.split("\\s+");
            if (parts.length != 3) {
                printMessage(INVALID_MOVE_MESSAGE);
                continue;
            }

            try {
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                int value = Integer.parseInt(parts[2]);

                if (row < 1 || row > 9 || col < 1 || col > 9 || value < 0 || value > 9) {
                    printMessage(INVALID_MOVE_MESSAGE);
                    continue;
                }

                return Move.play(row - 1, col - 1, value);
            } catch (NumberFormatException exception) {
                printMessage(INVALID_MOVE_MESSAGE);
            }
        }
    }

    /**
     * Representa uma ação de jogo já interpretada pelo parser de comandos.
     *
     * <p>Imutável — cada instância é criada via métodos estáticos de fábrica que
     * correspondem a cada tipo de comando suportado. O controller consulta os
     * predicados {@code isXxx()} para decidir o fluxo apropriado.
     *
     * @author  Allan Giaretta
     * @version 1.0.0
     */
    public static class Move {
        /** Tipo do comando, classificado pelo enum {@link CommandTypeEnum}. */
        private final CommandTypeEnum type;

        /** Indica se o comando encerra o loop principal. */
        private final boolean quit;

        /** Linha da jogada (0-indexada) ou {@code -1} quando não aplicável. */
        private final int row;

        /** Coluna da jogada (0-indexada) ou {@code -1} quando não aplicável. */
        private final int col;

        /** Valor da jogada (0–9) ou {@code -1} quando não aplicável. */
        private final int value;

        /** Caminho de arquivo para comandos de persistência, ou {@code null}. */
        private final String filePath;

        /**
         * Construtor privado — use as fábricas estáticas {@link #play(int, int, int)},
         * {@link #save(String)}, etc.
         */
        private Move(CommandTypeEnum type, boolean quit, int row, int col, int value, String filePath) {
            this.type = type;
            this.quit = quit;
            this.row = row;
            this.col = col;
            this.value = value;
            this.filePath = filePath;
        }

        /** @return movimento que encerra o jogo. */
        public static Move quit() {
            return new Move(CommandTypeEnum.QUIT, true, -1, -1, -1, null);
        }

        /**
         * Cria uma jogada de preenchimento de célula.
         *
         * @param row   linha (0–8)
         * @param col   coluna (0–8)
         * @param value valor a ser inserido (0–9)
         * @return movimento do tipo PLAY
         */
        public static Move play(int row, int col, int value) {
            return new Move(CommandTypeEnum.PLAY, false, row, col, value, null);
        }

        /**
         * Cria um comando para salvar a partida em arquivo.
         *
         * @param filePath caminho do arquivo destino
         * @return movimento do tipo SAVE
         */
        public static Move save(String filePath) {
            return new Move(CommandTypeEnum.SAVE, false, -1, -1, -1, filePath);
        }

        /**
         * Cria um comando para carregar uma partida de arquivo.
         *
         * @param filePath caminho do arquivo origem
         * @return movimento do tipo LOAD
         */
        public static Move load(String filePath) {
            return new Move(CommandTypeEnum.LOAD, false, -1, -1, -1, filePath);
        }

        /** @return movimento que solicita a exibição da ajuda. */
        public static Move help() {
            return new Move(CommandTypeEnum.HELP, false, -1, -1, -1, null);
        }

        /** @return movimento que solicita a geração de um novo tabuleiro. */
        public static Move newBoard() {
            return new Move(CommandTypeEnum.NEW_BOARD, false, -1, -1, -1, null);
        }

        /** @return movimento que solicita o status atual da partida. */
        public static Move status() {
            return new Move(CommandTypeEnum.STATUS, false, -1, -1, -1, null);
        }

        /** @return movimento que solicita o preenchimento automático do tabuleiro. */
        public static Move complete() {
            return new Move(CommandTypeEnum.COMPLETE, false, -1, -1, -1, null);
        }

        /**
         * Cria um comando para remover o valor de uma célula.
         *
         * @param row linha (0–8)
         * @param col coluna (0–8)
         * @return movimento do tipo REMOVE
         */
        public static Move remove(int row, int col) {
            return new Move(CommandTypeEnum.REMOVE, false, row, col, 0, null);
        }

        /** @return movimento que limpa todas as jogadas do usuário. */
        public static Move clearUserMoves() {
            return new Move(CommandTypeEnum.CLEAR, false, -1, -1, -1, null);
        }

        /** @return {@code true} se o comando solicita encerrar o jogo. */
        public boolean isQuit() {
            return quit;
        }

        /** @return {@code true} se o comando é de salvar partida. */
        public boolean isSave() {
            return type == CommandTypeEnum.SAVE;
        }

        /** @return {@code true} se o comando é de carregar partida. */
        public boolean isLoad() {
            return type == CommandTypeEnum.LOAD;
        }

        /** @return {@code true} se o comando é de ajuda. */
        public boolean isHelp() {
            return type == CommandTypeEnum.HELP;
        }

        /** @return {@code true} se o comando solicita um novo tabuleiro. */
        public boolean isNewBoard() {
            return type == CommandTypeEnum.NEW_BOARD;
        }

        /** @return {@code true} se o comando solicita o status. */
        public boolean isStatus() {
            return type == CommandTypeEnum.STATUS;
        }

        /** @return {@code true} se o comando solicita resolução automática. */
        public boolean isComplete() {
            return type == CommandTypeEnum.COMPLETE;
        }

        /** @return {@code true} se o comando remove o valor de uma célula. */
        public boolean isRemove() {
            return type == CommandTypeEnum.REMOVE;
        }

        /** @return {@code true} se o comando limpa todas as jogadas do usuário. */
        public boolean isClear() {
            return type == CommandTypeEnum.CLEAR;
        }

        /** @return a linha associada ao comando (0-indexada). */
        public int getRow() {
            return row;
        }

        /** @return a coluna associada ao comando (0-indexada). */
        public int getCol() {
            return col;
        }

        /** @return o valor associado ao comando. */
        public int getValue() {
            return value;
        }

        /** @return o caminho do arquivo, para comandos save/load. */
        public String getFilePath() {
            return filePath;
        }
    }
}
