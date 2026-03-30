package sudoku.ui;

import java.util.Scanner;

import sudoku.model.Board;

public class ConsoleUI {
    private final Scanner scanner;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
    }

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

    public Move readMove() {
        while (true) {
            System.out.print("Comando (linha coluna valor | save arquivo.txt | load arquivo.txt | help | q): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) {
                return Move.quit();
            }

            if (input.equalsIgnoreCase("help")) {
                return Move.help();
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

            String[] parts = input.split("\\s+");
            if (parts.length != 3) {
                printMessage("Entrada invalida. Use jogada, save, load, help ou q.");
                continue;
            }

            try {
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                int value = Integer.parseInt(parts[2]);

                if (row < 1 || row > 9 || col < 1 || col > 9 || value < 0 || value > 9) {
                    printMessage("Valores permitidos: linha 1-9, coluna 1-9, valor 0-9.");
                    continue;
                }

                return Move.play(row - 1, col - 1, value);
            } catch (NumberFormatException exception) {
                printMessage("Entrada invalida. Digite apenas numeros ou 'q'.");
            }
        }
    }

    public static class Move {
        private final CommandType type;
        private final boolean quit;
        private final int row;
        private final int col;
        private final int value;
        private final String filePath;

        private Move(CommandType type, boolean quit, int row, int col, int value, String filePath) {
            this.type = type;
            this.quit = quit;
            this.row = row;
            this.col = col;
            this.value = value;
            this.filePath = filePath;
        }

        public static Move quit() {
            return new Move(CommandType.QUIT, true, -1, -1, -1, null);
        }

        public static Move play(int row, int col, int value) {
            return new Move(CommandType.PLAY, false, row, col, value, null);
        }

        public static Move save(String filePath) {
            return new Move(CommandType.SAVE, false, -1, -1, -1, filePath);
        }

        public static Move load(String filePath) {
            return new Move(CommandType.LOAD, false, -1, -1, -1, filePath);
        }

        public static Move help() {
            return new Move(CommandType.HELP, false, -1, -1, -1, null);
        }

        public boolean isQuit() {
            return quit;
        }

        public boolean isSave() {
            return type == CommandType.SAVE;
        }

        public boolean isLoad() {
            return type == CommandType.LOAD;
        }

        public boolean isHelp() {
            return type == CommandType.HELP;
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

    private enum CommandType {
        PLAY,
        SAVE,
        LOAD,
        HELP,
        QUIT
    }
}
