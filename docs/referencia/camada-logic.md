# Camada logic

A camada `logic` concentra regras de Sudoku, resolução, geração de puzzles e regras de partida. Ela não lê teclado, não imprime no terminal e não persiste arquivos.

## Classes

| Classe | Responsabilidade |
|---|---|
| `Validador` | Verifica unicidade em linha, coluna e caixa 3x3. |
| `Solver` | Preenche células vazias por backtracking. |
| `Generator` | Cria tabuleiros jogáveis a partir de uma solução gerada. |
| `GameLogic` | Verifica vitória, copia tabuleiro e limpa jogadas do usuário. |

## `Validador`

`Validador` é stateless. Os métodos ignoram valores `0`, porque `0` representa célula vazia.

| Método | Contrato |
|---|---|
| `isValidRow(Board board, int row)` | Rejeita `row` fora de `0..8`; retorna `false` se houver duplicata não zero. |
| `isValidColumn(Board board, int column)` | Rejeita `column` fora de `0..8`; retorna `false` se houver duplicata não zero. |
| `isValidBox(Board board, int row, int column)` | Rejeita origem fora de `0..6`; verifica a janela 3x3 iniciada em `(row, column)`. |

Uso correto para caixas reais de Sudoku: passe origens normalizadas `0`, `3` ou `6`. O método aceita qualquer origem de `0` a `6`, então `(1, 1)` é tecnicamente válido para o método, mas não representa uma caixa padrão do Sudoku.

## `Solver`

`Solver.solve(Board board)` procura a primeira célula vazia e testa valores de `1` a `9` recursivamente. A validação de cada tentativa usa `Validador` na linha, coluna e caixa afetadas.

Contratos:

- rejeita `board == null` com `IllegalArgumentException`;
- muta o tabuleiro recebido;
- desfaz tentativas quando um ramo falha;
- retorna `true` quando não há mais células vazias.

Limitação importante: se o tabuleiro já estiver cheio, o solver retorna `true` sem revalidar o tabuleiro inteiro. Para verificar solução final, use `GameLogic.isVictory()`.

## `Generator`

Fluxo de `generate(int cellsToRemove)`:

1. cria um `Board` vazio;
2. resolve o tabuleiro com `Solver`;
3. aplica randomização estrutural;
4. remove `cellsToRemove` posições;
5. marca células remanescentes como fixas e removidas como não fixas.

Randomizações aplicadas:

- permutação bijetora dos dígitos `1..9`;
- troca de linhas dentro de cada banda horizontal;
- troca de colunas dentro de cada pilha vertical.

Contratos:

| Método | Contrato |
|---|---|
| `generate()` | Usa o padrão de 40 células removidas. |
| `generate(int cellsToRemove)` | Aceita `0..81`; rejeita valores fora desse intervalo. |

Limitação: o gerador não garante solução única. Remover muitas células pode gerar puzzles com múltiplas soluções.

## `GameLogic`

| Método | Contrato |
|---|---|
| `isVictory(Board board)` | Retorna `false` para `null`; exige todas as células preenchidas e regras válidas. |
| `copyBoard(Board board)` | Rejeita `null`; cria cópia profunda preservando valores e flags `fixed`. |
| `clearAllUserMoves(Board board)` | Rejeita `null`; zera todas as células com `fixed == false`. |

`GameLogic` não decide se uma jogada individual pode ser aplicada. Essa decisão fica em `GameController`, que só bloqueia alteração de célula fixa.
