package sudoku.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import sudoku.model.Board;

/**
 * Responsável pela persistência de partidas de Sudoku em arquivo texto.
 *
 * <p>Formato do arquivo (versão {@code SUDOKU_SAVE_V1}):
 * <pre>
 * SUDOKU_SAVE_V1
 * v:f,v:f,v:f,v:f,v:f,v:f,v:f,v:f,v:f   (linha 0)
 * ...
 * v:f,v:f,v:f,v:f,v:f,v:f,v:f,v:f,v:f   (linha 8)
 * </pre>
 * Onde {@code v} é o valor da célula (0–9) e {@code f} é 0 ou 1 indicando se
 * a célula é fixa. O arquivo é gravado em UTF-8.
 *
 * <p>Operações de I/O usam {@code java.nio.file.Files}, que fecha recursos
 * automaticamente. Validação é rigorosa: cabeçalho, número de linhas, formato de
 * cada célula e faixa de valores são todos verificados na leitura.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
public class FileManager {
    /** Dimensão do tabuleiro (9 linhas x 9 colunas). */
    private static final int BOARD_SIZE = 9;

    /**
     * Salva o estado atual do tabuleiro em um arquivo texto.
     *
     * <p>Diretórios pai ausentes são criados automaticamente.
     *
     * @param board    tabuleiro a ser persistido
     * @param filePath caminho de destino (não pode ser vazio)
     * @throws IllegalArgumentException se {@code board} for {@code null} ou {@code filePath} vazio
     * @throws IOException              em caso de falha ao gravar o arquivo
     */
    public void saveGame(Board board, String filePath) throws IOException {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("filePath cannot be empty.");
        }
        validatePath(filePath);

        Path path = Paths.get(filePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        StringBuilder content = new StringBuilder();
        content.append("SUDOKU_SAVE_V1").append(System.lineSeparator());

        for (int row = 0; row < BOARD_SIZE; row++) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < BOARD_SIZE; col++) {
                int value = board.getCell(row, col).getValue();
                int fixed = board.getCell(row, col).isFixed() ? 1 : 0;
                line.append(value).append(":").append(fixed);
                if (col < BOARD_SIZE - 1) {
                    line.append(",");
                }
            }
            content.append(line).append(System.lineSeparator());
        }

        Files.writeString(path, content.toString(), StandardCharsets.UTF_8);
    }

    /**
     * Carrega um tabuleiro a partir de um arquivo salvo anteriormente.
     *
     * <p>Verifica:
     * <ul>
     *   <li>Existência do arquivo;</li>
     *   <li>Número exato de 10 linhas (1 header + 9 linhas do tabuleiro);</li>
     *   <li>Cabeçalho {@code SUDOKU_SAVE_V1};</li>
     *   <li>9 células por linha, cada uma no formato {@code v:f};</li>
     *   <li>Valores dentro das faixas válidas.</li>
     * </ul>
     *
     * @param filePath caminho do arquivo a ser lido
     * @return tabuleiro reconstruído
     * @throws IllegalArgumentException se {@code filePath} for nulo ou vazio
     * @throws IOException              se o arquivo não existir ou tiver formato inválido
     */
    public Board loadGame(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("filePath cannot be empty.");
        }
        validatePath(filePath);

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("Save file not found: " + filePath);
        }

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lines.size() != BOARD_SIZE + 1) {
            throw new IOException("Invalid save format: unexpected number of lines.");
        }

        if (!"SUDOKU_SAVE_V1".equals(lines.get(0).trim())) {
            throw new IOException("Invalid save format: unsupported header.");
        }

        Board board = new Board();
        for (int row = 0; row < BOARD_SIZE; row++) {
            String line = lines.get(row + 1).trim();
            String[] cells = line.split(",");
            if (cells.length != BOARD_SIZE) {
                throw new IOException("Invalid save format at row " + (row + 1) + ".");
            }

            for (int col = 0; col < BOARD_SIZE; col++) {
                String[] parts = cells[col].split(":");
                if (parts.length != 2) {
                    throw new IOException("Invalid save format at cell [" + row + "," + col + "].");
                }

                int value;
                int fixed;
                try {
                    value = Integer.parseInt(parts[0].trim());
                    fixed = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException exception) {
                    throw new IOException("Invalid number format at cell [" + row + "," + col + "].", exception);
                }

                if (value < 0 || value > 9 || (fixed != 0 && fixed != 1)) {
                    throw new IOException("Invalid cell values at [" + row + "," + col + "].");
                }

                board.setValue(row, col, value);
                board.getCell(row, col).setFixed(fixed == 1);
            }
        }

        return board;
    }

    /**
     * Valida que o caminho fornecido não contém sequências de path traversal
     * (ex.: {@code ../../../etc/passwd}).
     *
     * @param filePath caminho a ser validado
     * @throws IOException se o caminho contiver {@code ..}
     */
    private static void validatePath(String filePath) throws IOException {
        if (filePath.contains("..")) {
            throw new IOException("Caminho de arquivo inválido: não são permitidos caminhos com '..'.");
        }
    }
}
