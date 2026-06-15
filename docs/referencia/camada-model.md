# Camada model

A camada `model` contém as estruturas de dados do Sudoku. Ela não executa regras de partida, não valida vitória e não protege células fixas contra alteração.

## Classes

| Classe | Papel |
|---|---|
| `Cell` | Representa uma célula individual, com valor e flag de fixação. |
| `Board` | Representa a matriz 9x9 de células e expõe operações de leitura/escrita. |

## `Cell`

### Estado

| Campo | Tipo | Regra |
|---|---|---|
| `value` | `int` | Aceita `0` a `9`; `0` significa célula vazia. |
| `fixed` | `boolean` | Indica se a célula veio do puzzle original. |

### API pública

| Método | Contrato |
|---|---|
| `Cell()` | Cria célula vazia e não fixa. |
| `Cell(int value, boolean fixed)` | Cria célula com valor e flag definidos; rejeita valor fora de `0..9`. |
| `getValue()` | Retorna o valor atual. |
| `setValue(int value)` | Atualiza o valor; rejeita valor fora de `0..9`. |
| `isFixed()` | Retorna se a célula é fixa. |
| `setFixed(boolean fixed)` | Atualiza a flag de fixação. |

### Atenções

- `setValue()` não consulta `fixed`. Uma célula fixa pode ser alterada diretamente se o chamador usar `Cell` ou `Board` sem passar pelo controller.
- A proteção de célula fixa é responsabilidade de `GameController`.

## `Board`

### Estado

`Board` encapsula `Cell[][]` com tamanho fixo 9x9. Todos os métodos públicos que recebem coordenadas validam linha e coluna no intervalo `0..8`.

### API pública

| Método | Contrato |
|---|---|
| `Board()` | Cria tabuleiro vazio com 81 células novas. |
| `Board(Cell[][] cell)` | Cria cópia profunda de uma matriz 9x9; posições `null` viram células vazias. |
| `getCell(int row, int col)` | Retorna a referência mutável da célula. |
| `getValueAt(int row, int col)` | Retorna apenas o valor da célula. |
| `setValue(int row, int col, int value)` | Atualiza o valor da célula; não verifica `fixed`. |
| `printBoard()` | Retorna string simples do tabuleiro, usada para debug/log. |

### Atenções

- Índices internos são 0-based. O usuário digita 1 a 9 e `ConsoleUI` converte.
- `getCell()` vaza mutabilidade por design. Use `getValueAt()` quando não precisar alterar estado.
- O construtor de cópia preserva valor e flag `fixed`, mas não reaproveita instâncias de `Cell`.
