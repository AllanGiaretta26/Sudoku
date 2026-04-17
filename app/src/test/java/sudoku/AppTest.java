package sudoku;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Teste de sanidade para a classe {@link App}.
 *
 * <p>Verifica apenas que a classe pode ser instanciada — a cobertura funcional
 * da aplicação é feita pelas suítes especializadas em cada camada.
 *
 * @author  Allan Giaretta
 * @version 1.0.0
 */
class AppTest {

    /**
     * Garante que a classe {@link App} pode ser construída sem erros.
     */
    @Test
    void appCanBeCreated() {
        App classUnderTest = new App();
        assertNotNull(classUnderTest, "app instance should be created");
    }
}
