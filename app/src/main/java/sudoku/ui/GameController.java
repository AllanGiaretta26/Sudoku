package sudoku.ui;

import java.io.IOException;

import sudoku.logic.GameLogic;
import sudoku.logic.Generator;
import sudoku.logic.Solver;
import sudoku.logic.Validador;
import sudoku.model.Board;
import sudoku.ui.ConsoleUI.Move;
import sudoku.util.FileManager;

/**
 * Orquestra o ciclo principal do jogo, conectando UI, geração, validação,
 * persistência e regras de partida.
 *
 * <p>Atua como <em>controller</em> no padrão MVC: não contém lógica de Sudoku nem
 * responsabilidades de I/O diretas; delega a cada colaborador o que compete a ele
 * e se concentra em coordenar o fluxo.
 *
 * <p>Dependências são injetadas via construtor — há um construtor de conveniência
 * que monta um grafo de dependências padrão para uso produtivo pela {@code App}.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
public class GameController {
    /** Padrão de células vazias por novo jogo. */
    private static final int DEFAULT_CELLS_TO_REMOVE = 40;

    private final ConsoleUI consoleUI;
    private final Generator generator;
    private final Solver solver;
    private final FileManager fileManager;
    private final GameLogic gameLogic;

    /**
     * Construtor de conveniência: instancia o grafo de dependências padrão
     * (útil para inicialização rápida em {@code main}).
     */
    public GameController() {
        this(new ConsoleUI(), new Generator(), new Solver(), new FileManager(),
                new GameLogic(new Validador()));
    }

    /**
     * Construtor com injeção de dependências, usado em testes e pela {@code App}.
     *
     * @param consoleUI   camada de entrada e saída
     * @param generator   gerador de tabuleiros
     * @param solver      resolvedor por backtracking
     * @param fileManager persistência em arquivo
     * @param gameLogic   regras de partida (vitória, cópia, limpeza)
     * @throws IllegalArgumentException se qualquer dependência for {@code null}
     */
    public GameController(ConsoleUI consoleUI, Generator generator, Solver solver,
                          FileManager fileManager, GameLogic gameLogic) {
        if (consoleUI == null || generator == null || solver == null
                || fileManager == null || gameLogic == null) {
            throw new IllegalArgumentException("Dependencies cannot be null.");
        }
        this.consoleUI = consoleUI;
        this.generator = generator;
        this.solver = solver;
        this.fileManager = fileManager;
        this.gameLogic = gameLogic;
    }

    /**
     * Executa o loop principal da aplicação em modo console.
     *
     * <p>A cada iteração: exibe o tabuleiro, verifica vitória, lê um comando
     * e o aplica conforme o tipo. O método retorna quando o jogador encerra
     * o jogo ({@code q}) ou completa o puzzle.
     */
    public void run() {
        Board board = generator.generate(DEFAULT_CELLS_TO_REMOVE);
        consoleUI.printMessage("Sudoku iniciado. Digite 'help' para ver os comandos.");

        while (true) {
            consoleUI.printBoard(board);

            if (gameLogic.isVictory(board)) {
                consoleUI.printMessage("Parabens! Voce venceu o jogo.");
                return;
            }

            Move move = consoleUI.readMove();
            if (move.isQuit())     { consoleUI.printMessage("Jogo encerrado."); return; }
            if (move.isSave())     { handleSave(move, board); continue; }
            if (move.isLoad())     { board = handleLoad(move, board); continue; }
            if (move.isHelp())     { printHelp(); continue; }
            if (move.isNewBoard()) { board = generator.generate(DEFAULT_CELLS_TO_REMOVE); consoleUI.printMessage("Novo tabuleiro gerado."); continue; }
            if (move.isStatus())   { consoleUI.printMessage("Status da partida: " + getGameStatus(board)); continue; }
            if (move.isComplete()) { board = handleComplete(board); continue; }
            if (move.isRemove())   { handleRemove(move, board); continue; }
            if (move.isClear())    { gameLogic.clearAllUserMoves(board); consoleUI.printMessage("Todos os numeros digitados pelo usuario foram removidos."); continue; }
            handlePlay(move, board);
        }
    }

    private void handleSave(Move move, Board board) {
        try {
            fileManager.saveGame(board, move.getFilePath());
            consoleUI.printMessage("Partida salva em: " + move.getFilePath());
        } catch (IOException exception) {
            consoleUI.printMessage("Falha ao salvar partida: " + exception.getMessage());
        }
    }

    private Board handleLoad(Move move, Board board) {
        try {
            Board loaded = fileManager.loadGame(move.getFilePath());
            consoleUI.printMessage("Partida carregada de: " + move.getFilePath());
            return loaded;
        } catch (IOException exception) {
            consoleUI.printMessage("Falha ao carregar partida: " + exception.getMessage());
            return board;
        }
    }

    private Board handleComplete(Board board) {
        Board candidate = gameLogic.copyBoard(board);
        if (solver.solve(candidate)) {
            consoleUI.printMessage("Tabuleiro completado automaticamente.");
            return candidate;
        }
        consoleUI.printMessage("Nao foi possivel completar o tabuleiro atual.");
        return board;
    }

    private void handleRemove(Move move, Board board) {
        int row = move.getRow();
        int col = move.getCol();
        if (board.getCell(row, col).isFixed()) {
            consoleUI.printMessage("Jogada errada para as regras Sudoku.");
            return;
        }
        board.setValue(row, col, 0);
        consoleUI.printMessage("Numero removido da posicao.");
    }

    private void handlePlay(Move move, Board board) {
        int row   = move.getRow();
        int col   = move.getCol();
        int value = move.getValue();
        if (board.getCell(row, col).isFixed()) {
            consoleUI.printMessage("Essa celula e fixa e nao pode ser alterada.");
            return;
        }
        board.setValue(row, col, value);
        consoleUI.printMessage("Jogada aplicada.");
    }

    /**
     * Retorna o status textual da partida, usado pelo comando {@code status}.
     *
     * @param board tabuleiro atual
     * @return {@code "NAO_INICIADO"}, {@code "COMPLETO"} ou {@code "INCOMPLETO"}
     */
    private String getGameStatus(Board board) {
        if (board == null) {
            return "NAO_INICIADO";
        }
        return gameLogic.isVictory(board) ? "COMPLETO" : "INCOMPLETO";
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
