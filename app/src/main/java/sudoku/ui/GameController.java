package sudoku.ui;

import java.io.IOException;

import sudoku.logic.Generator;
import sudoku.logic.Validador;
import sudoku.model.Board;
import sudoku.ui.ConsoleUI.Move;
import sudoku.util.FileManager;

public class GameController {
    private final ConsoleUI consoleUI;
    private final Generator generator;
    private final Validador validador;
    private final FileManager fileManager;

    public GameController() {
        this.consoleUI = new ConsoleUI();
        this.generator = new Generator();
        this.validador = new Validador();
        this.fileManager = new FileManager();
    }

    public void run() {
        Board board = generator.generate(40);
        consoleUI.printMessage("Sudoku iniciado. Digite 'help' para ver os comandos.");

        while (true) {
            consoleUI.printBoard(board);

            if (isVictory(board)) {
                consoleUI.printMessage("Parabens! Voce venceu o jogo.");
                return;
            }

            Move move = consoleUI.readMove();
            if (move.isQuit()) {
                consoleUI.printMessage("Jogo encerrado.");
                return;
            }

            if (move.isSave()) {
                try {
                    fileManager.saveGame(board, move.getFilePath());
                    consoleUI.printMessage("Partida salva em: " + move.getFilePath());
                } catch (IOException exception) {
                    consoleUI.printMessage("Falha ao salvar partida: " + exception.getMessage());
                }
                continue;
            }

            if (move.isLoad()) {
                try {
                    board = fileManager.loadGame(move.getFilePath());
                    consoleUI.printMessage("Partida carregada de: " + move.getFilePath());
                } catch (IOException exception) {
                    consoleUI.printMessage("Falha ao carregar partida: " + exception.getMessage());
                }
                continue;
            }

            if (move.isHelp()) {
                printHelp();
                continue;
            }

            int row = move.getRow();
            int col = move.getCol();
            int value = move.getValue();

            if (board.getCell(row, col).isFixed()) {
                consoleUI.printMessage("Essa celula e fixa e nao pode ser alterada.");
                continue;
            }

            int previousValue = board.getCell(row, col).getValue();
            board.setValue(row, col, value);

            if (!isMoveValid(board, row, col)) {
                board.setValue(row, col, previousValue);
                consoleUI.printMessage("Jogada invalida para as regras do Sudoku.");
                continue;
            }

            consoleUI.printMessage("Jogada aplicada.");
        }
    }

    private boolean isMoveValid(Board board, int row, int col) {
        int boxRow = row - (row % 3);
        int boxCol = col - (col % 3);

        return validador.isValidRow(board, row)
            && validador.isValidColumn(board, col)
            && validador.isValidBox(board, boxRow, boxCol);
    }

    private boolean isVictory(Board board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.getCell(row, col).getValue() == 0) {
                    return false;
                }
            }
        }

        for (int i = 0; i < 9; i++) {
            if (!validador.isValidRow(board, i) || !validador.isValidColumn(board, i)) {
                return false;
            }
        }

        for (int row = 0; row < 9; row += 3) {
            for (int col = 0; col < 9; col += 3) {
                if (!validador.isValidBox(board, row, col)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void printHelp() {
        consoleUI.printMessage("Comandos disponiveis:");
        consoleUI.printMessage("- linha coluna valor  -> ex: 1 3 9");
        consoleUI.printMessage("- save arquivo.txt    -> salva a partida");
        consoleUI.printMessage("- load arquivo.txt    -> carrega uma partida");
        consoleUI.printMessage("- help                -> mostra esta ajuda");
        consoleUI.printMessage("- q                   -> encerra o jogo");
    }
}
