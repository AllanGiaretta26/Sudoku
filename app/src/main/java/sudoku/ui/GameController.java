package sudoku.ui;

import java.io.IOException;

import sudoku.logic.Generator;
import sudoku.logic.Solver;
import sudoku.logic.Validador;
import sudoku.model.Board;
import sudoku.model.Cell;
import sudoku.ui.ConsoleUI.Move;
import sudoku.util.FileManager;

public class GameController {
    private final ConsoleUI consoleUI;
    private final Generator generator;
    private final Solver solver;
    private final Validador validador;
    private final FileManager fileManager;

    public GameController() {
        this.consoleUI = new ConsoleUI();
        this.generator = new Generator();
        this.solver = new Solver();
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

            if (move.isNewBoard()) {
                board = generator.generate(40);
                consoleUI.printMessage("Novo tabuleiro gerado.");
                continue;
            }

            if (move.isStatus()) {
                consoleUI.printMessage("Status da partida: " + getGameStatus(board));
                continue;
            }

            if (move.isComplete()) {
                Board solvedCandidate = copyBoard(board);
                if (solver.solve(solvedCandidate)) {
                    board = solvedCandidate;
                    consoleUI.printMessage("Tabuleiro completado automaticamente.");
                } else {
                    consoleUI.printMessage("Nao foi possivel completar o tabuleiro atual.");
                }
                continue;
            }

            if (move.isRemove()) {
                int row = move.getRow();
                int col = move.getCol();
                if (board.getCell(row, col).isFixed()) {
                    consoleUI.printMessage("Jogada errada para as regras Sudoku.");
                    continue;
                }
                board.setValue(row, col, 0);
                consoleUI.printMessage("Numero removido da posicao.");
                continue;
            }

            if (move.isClear()) {
                clearAllUserMoves(board);
                consoleUI.printMessage("Todos os numeros digitados pelo usuario foram removidos.");
                continue;
            }

            int row = move.getRow();
            int col = move.getCol();
            int value = move.getValue();

            if (board.getCell(row, col).isFixed()) {
                consoleUI.printMessage("Essa celula e fixa e nao pode ser alterada.");
                continue;
            }

            board.setValue(row, col, value);
            consoleUI.printMessage("Jogada aplicada.");
        }
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

    private String getGameStatus(Board board) {
        if (board == null) {
            return "NAO_INICIADO";
        }
        return isVictory(board) ? "COMPLETO" : "INCOMPLETO";
    }

    private Board copyBoard(Board board) {
        Cell[][] cells = new Cell[9][9];
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                Cell source = board.getCell(row, col);
                cells[row][col] = new Cell(source.getValue(), source.isFixed());
            }
        }
        return new Board(cells);
    }

    private void clearAllUserMoves(Board board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (!board.getCell(row, col).isFixed()) {
                    board.setValue(row, col, 0);
                }
            }
        }
    }

    private void printHelp() {
        consoleUI.printMessage("Comandos disponiveis:");
        consoleUI.printMessage("- linha coluna valor  -> ex: 1 3 9");
        consoleUI.printMessage("- remove linha coluna -> remove numero da posicao (se nao fixa)");
        consoleUI.printMessage("- clear               -> remove todos os numeros digitados pelo usuario");
        consoleUI.printMessage("- save arquivo.txt    -> salva a partida");
        consoleUI.printMessage("- load arquivo.txt    -> carrega uma partida");
        consoleUI.printMessage("- new                 -> gera um novo tabuleiro aleatorio");
        consoleUI.printMessage("- status              -> mostra o status da partida");
        consoleUI.printMessage("- complete            -> completa automaticamente o tabuleiro");
        consoleUI.printMessage("- help                -> mostra esta ajuda");
        consoleUI.printMessage("- q                   -> encerra o jogo");
    }
}
