package sudoku;

import sudoku.logic.GameLogic;
import sudoku.logic.Generator;
import sudoku.logic.Solver;
import sudoku.logic.Validador;
import sudoku.ui.ConsoleUI;
import sudoku.ui.GameController;
import sudoku.util.FileManager;

/**
 * Ponto de entrada da aplicação de Sudoku em modo console.
 *
 * <p>Responsável por construir o grafo de dependências do jogo e delegar a
 * execução ao {@link GameController}. O {@link ConsoleUI} é gerenciado por
 * {@code try-with-resources} para garantir que o {@code Scanner} interno seja
 * fechado corretamente ao final da partida.
 *
 * <p>Uso:
 * <pre>
 *   ./gradlew.bat run
 * </pre>
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
public class App {

    /**
     * Inicia o jogo montando o grafo de dependências e executando o loop principal.
     *
     * @param args argumentos de linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        Validador validador = new Validador();
        Solver solver = new Solver(validador);
        Generator generator = new Generator();
        FileManager fileManager = new FileManager();
        GameLogic gameLogic = new GameLogic(validador);

        try (ConsoleUI ui = new ConsoleUI()) {
            new GameController(ui, generator, solver, fileManager, gameLogic).run();
        }
    }
}
