package sudoku.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Suíte de testes do parser de comandos do {@link ConsoleUI}.
 *
 * <p>Usa o construtor sobrecarregado {@code ConsoleUI(InputStream, PrintStream)}
 * para alimentar a UI com entradas simuladas via {@code ByteArrayInputStream},
 * permitindo validar o comportamento do parser de forma totalmente determinística.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
class ConsoleUICommandParserTest {

    private ConsoleUI uiFor(String input) {
        InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        PrintStream out = new PrintStream(new ByteArrayOutputStream());
        return new ConsoleUI(in, out);
    }

    @Test
    void parsesQuitCommand() {
        ConsoleUI ui = uiFor("q\n");
        assertTrue(ui.readMove().isQuit());
        ui.close();
    }

    @Test
    void parsesHelpCommand() {
        ConsoleUI ui = uiFor("help\n");
        assertTrue(ui.readMove().isHelp());
        ui.close();
    }

    @Test
    void parsesNewCommand() {
        ConsoleUI ui = uiFor("new\n");
        assertTrue(ui.readMove().isNewBoard());
        ui.close();
    }

    @Test
    void parsesStatusCommand() {
        ConsoleUI ui = uiFor("status\n");
        assertTrue(ui.readMove().isStatus());
        ui.close();
    }

    @Test
    void parsesClearCommand() {
        ConsoleUI ui = uiFor("clear\n");
        assertTrue(ui.readMove().isClear());
        ui.close();
    }

    @Test
    void parsesValidPlayMove() {
        ConsoleUI ui = uiFor("3 5 7\n");
        ConsoleUI.Move move = ui.readMove();
        assertEquals(2, move.getRow());
        assertEquals(4, move.getCol());
        assertEquals(7, move.getValue());
        ui.close();
    }

    @Test
    void parsesValidRemoveMove() {
        ConsoleUI ui = uiFor("remove 4 8\n");
        ConsoleUI.Move move = ui.readMove();
        assertTrue(move.isRemove());
        assertEquals(3, move.getRow());
        assertEquals(7, move.getCol());
        ui.close();
    }

    @Test
    void parsesSaveCommand() {
        ConsoleUI ui = uiFor("save partida.txt\n");
        ConsoleUI.Move move = ui.readMove();
        assertTrue(move.isSave());
        assertEquals("partida.txt", move.getFilePath());
        ui.close();
    }

    @Test
    void parsesLoadCommand() {
        ConsoleUI ui = uiFor("load partida.txt\n");
        ConsoleUI.Move move = ui.readMove();
        assertTrue(move.isLoad());
        assertEquals("partida.txt", move.getFilePath());
        ui.close();
    }

    @Test
    void rejectsOutOfRangeMoveThenAcceptsValid() {
        // Primeiro comando invalido (linha 10), segundo valido
        ConsoleUI ui = uiFor("10 5 7\n2 2 3\n");
        ConsoleUI.Move move = ui.readMove();
        assertEquals(1, move.getRow());
        assertEquals(1, move.getCol());
        assertEquals(3, move.getValue());
        ui.close();
    }

    @Test
    void rejectsNonNumericInputThenAcceptsValid() {
        ConsoleUI ui = uiFor("abc def ghi\n1 1 1\n");
        ConsoleUI.Move move = ui.readMove();
        assertEquals(0, move.getRow());
        assertEquals(0, move.getCol());
        assertEquals(1, move.getValue());
        ui.close();
    }

    @Test
    void rejectsSaveWithoutFileThenAcceptsValid() {
        ConsoleUI ui = uiFor("save \nsave ok.txt\n");
        ConsoleUI.Move move = ui.readMove();
        assertTrue(move.isSave());
        assertEquals("ok.txt", move.getFilePath());
        ui.close();
    }
}
