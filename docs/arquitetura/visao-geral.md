# Visão geral da arquitetura

O projeto implementa um Sudoku de terminal em Java 21. A aplicação gera um puzzle 9x9, lê comandos do usuário, aplica alterações em células não fixas, salva/carrega partidas e consegue completar o tabuleiro via solver.

## Escopo funcional

| Funcionalidade | Implementação atual |
|---|---|
| Geração de puzzle | `Generator.generate(40)` cria um tabuleiro com 40 células vazias por padrão. |
| Entrada de comandos | `ConsoleUI.readMove()` interpreta comandos de texto e converte coordenadas 1-based para 0-based. |
| Jogadas | `GameController` aplica valores em células não fixas. Conflitos de Sudoku não são bloqueados no momento da jogada. |
| Remoção | `remove` limpa uma célula não fixa; `clear` limpa todas as células não fixas. |
| Vitória | `GameLogic.isVictory()` exige tabuleiro cheio e válido em linhas, colunas e caixas 3x3. |
| Completar automaticamente | `complete` resolve uma cópia do tabuleiro e substitui o estado atual se o solver retornar sucesso. |
| Persistência | `FileManager` grava e lê arquivos UTF-8 no formato `SUDOKU_SAVE_V1`. |

## Camadas

```text
App.java
  └─ GameController
      ├─ ConsoleUI
      ├─ Generator
      │   └─ Solver
      │       └─ Validador
      ├─ GameLogic
      │   └─ Validador
      ├─ FileManager
      └─ Board / Cell
```

| Camada | Classes | Responsabilidade |
|---|---|---|
| `model` | `Cell`, `Board` | Estado do tabuleiro e das células. Não protege células fixas nem valida regras de Sudoku. |
| `logic` | `Validador`, `Solver`, `Generator`, `GameLogic` | Regras de validação, resolução, geração e estado de vitória. |
| `ui` | `ConsoleUI`, `GameController` | Parser de comandos, saída no console e orquestração do loop de jogo. |
| `util` | `FileManager`, `CommandTypeEnum` | Persistência e classificação de comandos. |

## Fluxo principal

```text
Usuário digita comando
        ↓
ConsoleUI.readMove()
        ↓
GameController.run()
        ↓
GameLogic / Solver / Generator / FileManager
        ↓
Board / Cell
        ↓
ConsoleUI.printBoard() ou ConsoleUI.printMessage()
```

## Contratos importantes

- Coordenadas de usuário são 1-based; coordenadas internas são 0-based.
- `0` representa célula vazia em `Cell`, `Board`, solver e arquivo salvo.
- Células fixas são protegidas pelo `GameController`, não por `Board` ou `Cell`.
- `Solver.solve()` muta o `Board` recebido. Use `GameLogic.copyBoard()` antes se precisar preservar o estado original.
- `Generator` não garante solução única; ele gera uma solução válida, randomiza estrutura e remove células.
- `FileManager` rejeita caminhos contendo `..`, cria diretórios pais e não faz sandbox em uma pasta fixa.

## Como executar

Linux/macOS/Git Bash:

```bash
./gradlew run
./gradlew test
```

Windows CMD/PowerShell:

```bat
.\gradlew.bat run
.\gradlew.bat test
```

## Índice técnico

- [Índice da documentação](../README.md)
- [Referência técnica por camada](../referencia/README.md)
- [Camada model](../referencia/camada-model.md)
- [Camada logic](../referencia/camada-logic.md)
- [Camada UI](../referencia/camada-ui.md)
- [Camada util](../referencia/camada-util.md)
- [Auditoria da documentação](../auditorias/auditoria-documentacao.md)
