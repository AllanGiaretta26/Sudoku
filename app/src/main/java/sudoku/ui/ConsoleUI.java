package sudoku.ui;

import java.util.Scanner;

import sudoku.model.Board;
import sudoku.util.CommandTypeEnum;

/**
 * Camada de entrada e saida em console.
 * Converte texto digitado em comandos estruturados para o controlador.
 */
public class ConsoleUI {
    private final Scanner scanner;

    private static final String INVALID_MOVE_MESSAGE = "Jogada errada para as regras Sudoku.";

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Exibe o tabuleiro no formato 9x9 com separadores visuais.
     */
    public void printBoard(Board board) {
        System.out.println();
        System.out.println("    1 2 3   4 5 6   7 8 9");
        System.out.println("  +-------+-------+-------+");
        for (int row = 0; row < 9; row++) {
            System.out.print((row + 1) + " | ");
            for (int col = 0; col < 9; col++) {
                int value = board.getCell(row, col).getValue();
                String symbol = value == 0 ? "." : Integer.toString(value);
                System.out.print(symbol + " ");
                if ((col + 1) % 3 == 0) {
                    System.out.print("| ");
                }
            }
            System.out.println();
            if ((row + 1) % 3 == 0) {
                System.out.println("  +-------+-------+-------+");
            }
        }
        System.out.println();
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    /**
     * Le o comando do usuario e retorna um objeto de movimento/acao validado.
     */
    public Move readMove() {
        while (true) {
            System.out.print("Comando (linha coluna valor | remove linha coluna | clear | save arquivo.txt | load arquivo.txt | new | status | complete | help | q): ");
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
     * Representa uma acao de jogo ja interpretada pelo parser de comandos.
     */
    public static class Move {
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

        public static Move quit() {
            return new Move(CommandTypeEnum.QUIT, true, -1, -1, -1, null);
        }

        public static Move play(int row, int col, int value) {
            return new Move(CommandTypeEnum.PLAY, false, row, col, value, null);
        }

        public static Move save(String filePath) {
            return new Move(CommandTypeEnum.SAVE, false, -1, -1, -1, filePath);
        }

        public static Move load(String filePath) {
            return new Move(CommandTypeEnum.LOAD, false, -1, -1, -1, filePath);
        }

        public static Move help() {
            return new Move(CommandTypeEnum.HELP, false, -1, -1, -1, null);
        }

        public static Move newBoard() {
            return new Move(CommandTypeEnum.NEW_BOARD, false, -1, -1, -1, null);
        }

        public static Move status() {
            return new Move(CommandTypeEnum.STATUS, false, -1, -1, -1, null);
        }

        public static Move complete() {
            return new Move(CommandTypeEnum.COMPLETE, false, -1, -1, -1, null);
        }

        public static Move remove(int row, int col) {
            return new Move(CommandTypeEnum.REMOVE, false, row, col, 0, null);
        }

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
