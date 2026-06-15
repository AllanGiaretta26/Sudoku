# Referência técnica

Contratos técnicos por camada e classe. Esta pasta é referência, não tutorial.

## Documentos

| Documento | Escopo |
|---|---|
| [camada-model.md](camada-model.md) | `Cell`, `Board`, estado e mutabilidade. |
| [camada-logic.md](camada-logic.md) | `Validador`, `Solver`, `Generator`, `GameLogic`. |
| [camada-ui.md](camada-ui.md) | `ConsoleUI`, `ConsoleUI.Move`, `GameController`. |
| [camada-util.md](camada-util.md) | `FileManager`, `CommandTypeEnum`, formato de persistência. |

## Regras

- Documente contratos reais do código, incluindo limitações.
- Preserve nomes de classes, métodos, comandos e status exatamente como aparecem no código.
- Se o comportamento público mudar, atualize também o [`../../README.md`](../../README.md) quando afetar uso do projeto.
