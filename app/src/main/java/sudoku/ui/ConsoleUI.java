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
 * @version 1.0.1
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
     * <p>Entradas inválidas não encerram a leitura — o método exibe mensagem de erro
     * e solicita novamente.
     *
     * @return movimento interpretado pronto para ser aplicado pelo controller
     */
    public Move readMove() {
        while (true) {
            out.print("Comando (linha coluna valor | remove linha coluna | clear | save arquivo.txt | load arquivo.txt | new | status | complete | help | q): ");
            String input = scanner.nextLine().trim();
            Move move = parseInput(input);
            if (move != null) {
                return move;
            }
        }
    }

    /**
     * Despacha a entrada para o parser correto conforme o prefixo do comando.
     * Retorna {@code null} quando a entrada não corresponde a nenhum comando válido
     * (o loop de {@link #readMove()} continua nesse caso).
     */
    private Move parseInput(String input) {
        String lower = input.toLowerCase();
        if (lower.equals("q"))        return Move.quit();
        if (lower.equals("help"))     return Move.help();
        if (lower.equals("new"))      return Move.newBoard();
        if (lower.equals("status"))   return Move.status();
        if (lower.equals("complete")) return Move.complete();
        if (lower.equals("clear"))    return Move.clearUserMoves();
        if (lower.startsWith("save ")) return parseSave(input);
        if (lower.startsWith("load ")) return parseLoad(input);
        if (lower.startsWith("remove ")) return parseRemove(input);
        return parsePlay(input);
    }

    private Move parseSave(String input) {
        String filePath = input.substring(5).trim();
        if (filePath.isEmpty()) {
            printMessage("Informe o nome do arquivo. Exemplo: save partida.txt");
            return null;
        }
        return Move.save(filePath);
    }

    private Move parseLoad(String input) {
        String filePath = input.substring(5).trim();
        if (filePath.isEmpty()) {
            printMessage("Informe o nome do arquivo. Exemplo: load partida.txt");
            return null;
        }
        return Move.load(filePath);
    }

    private Move parseRemove(String input) {
        String[] parts = input.split("\\s+");
        if (parts.length != 3) {
            printMessage(INVALID_MOVE_MESSAGE);
            return null;
        }
        try {
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            if (row < 1 || row > 9 || col < 1 || col > 9) {
                printMessage(INVALID_MOVE_MESSAGE);
                return null;
            }
            return Move.remove(row - 1, col - 1);
        } catch (NumberFormatException exception) {
            printMessage(INVALID_MOVE_MESSAGE);
            return null;
        }
    }

    private Move parsePlay(String input) {
        String[] parts = input.split("\\s+");
        if (parts.length != 3) {
            printMessage(INVALID_MOVE_MESSAGE);
            return null;
        }
        try {
            int row   = Integer.parseInt(parts[0]);
            int col   = Integer.parseInt(parts[1]);
            int value = Integer.parseInt(parts[2]);
            if (row < 1 || row > 9 || col < 1 || col > 9 || value < 0 || value > 9) {
                printMessage(INVALID_MOVE_MESSAGE);
                return null;
            }
            return Move.play(row - 1, col - 1, value);
        } catch (NumberFormatException exception) {
            printMessage(INVALID_MOVE_MESSAGE);
            return null;
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
     * @version 1.0.1
     */
    public static class Move {
        /** Tipo do comando, classificado pelo enum {@link CommandTypeEnum}. */
        private final CommandTypeEnum type;

        private final boolean quit;
        private final int row;
        private final int col;
        private final int value;
        private final String filePath;

        private Move(CommandTypeEnum type, boolean quit, int row, int col, int value, String filePath) {
            this.type = type;
            this.quit = quit;
            this.row = row;
            this.col = col;
            this.value = value;
            this.filePath = filePath;
        }

        /** Cria um movimento do tipo QUIT. */
        public static Move quit() {
            return new Move(CommandTypeEnum.QUIT, true, -1, -1, -1, null);
        }

        /**
         * Cria uma jogada de preenchimento de célula.
         *
         * @param row   linha (0–8)
         * @param col   coluna (0–8)
         * @param value valor a ser inserido (0–9)
         */
        public static Move play(int row, int col, int value) {
            return new Move(CommandTypeEnum.PLAY, false, row, col, value, null);
        }

        /**
         * Cria um comando para salvar a partida em arquivo.
         *
         * @param filePath caminho do arquivo destino
         */
        public static Move save(String filePath) {
            return new Move(CommandTypeEnum.SAVE, false, -1, -1, -1, filePath);
        }

        /**
         * Cria um comando para carregar uma partida de arquivo.
         *
         * @param filePath caminho do arquivo origem
         */
        public static Move load(String filePath) {
            return new Move(CommandTypeEnum.LOAD, false, -1, -1, -1, filePath);
        }

        /** Cria um movimento do tipo HELP. */
        public static Move help() {
            return new Move(CommandTypeEnum.HELP, false, -1, -1, -1, null);
        }

        /** Cria um movimento do tipo NEW_BOARD. */
        public static Move newBoard() {
            return new Move(CommandTypeEnum.NEW_BOARD, false, -1, -1, -1, null);
        }

        /** Cria um movimento do tipo STATUS. */
        public static Move status() {
            return new Move(CommandTypeEnum.STATUS, false, -1, -1, -1, null);
        }

        /** Cria um movimento do tipo COMPLETE. */
        public static Move complete() {
            return new Move(CommandTypeEnum.COMPLETE, false, -1, -1, -1, null);
        }

        /**
         * Cria um comando para remover o valor de uma célula.
         *
         * @param row linha (0–8)
         * @param col coluna (0–8)
         */
        public static Move remove(int row, int col) {
            return new Move(CommandTypeEnum.REMOVE, false, row, col, 0, null);
        }

        /** Cria um movimento do tipo CLEAR. */
        public static Move clearUserMoves() {
            return new Move(CommandTypeEnum.CLEAR, false, -1, -1, -1, null);
        }

        public boolean isQuit() {
            return quit;
        }

        public boolean isSave() {
            return type == CommandTypeEnum.SAVE;
        }

        public boolean isLoad() {
            return type == CommandTypeEnum.LOAD;
        }

        public boolean isHelp() {
            return type == CommandTypeEnum.HELP;
        }

        public boolean isNewBoard() {
            return type == CommandTypeEnum.NEW_BOARD;
        }

        public boolean isStatus() {
            return type == CommandTypeEnum.STATUS;
        }

        public boolean isComplete() {
            return type == CommandTypeEnum.COMPLETE;
        }

        public boolean isRemove() {
            return type == CommandTypeEnum.REMOVE;
        }

        public boolean isClear() {
            return type == CommandTypeEnum.CLEAR;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public int getValue() {
            return value;
        }

        public String getFilePath() {
            return filePath;
        }
    }
}
