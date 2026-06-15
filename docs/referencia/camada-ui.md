# Camada UI

A camada `ui` faz a fronteira entre o usuário e o restante da aplicação. Ela interpreta texto digitado, imprime tabuleiro/mensagens e coordena o loop do jogo.

## Classes

| Classe | Papel |
|---|---|
| `ConsoleUI` | Renderiza o tabuleiro, imprime mensagens e converte entrada textual em `Move`. |
| `GameController` | Mantém o loop principal e despacha comandos para lógica, solver, gerador e persistência. |

## `ConsoleUI`

### Construtores

| Construtor | Uso |
|---|---|
| `ConsoleUI()` | Usa `System.in` e `System.out`. |
| `ConsoleUI(InputStream in, PrintStream out)` | Injeta streams para testes automatizados. |

A classe implementa `AutoCloseable` para fechar o `Scanner` interno via `try-with-resources`.

### Renderização

`printBoard(Board board)` imprime o tabuleiro com cabeçalho de colunas `1..9`, linhas `1..9`, separadores de caixas 3x3 e `.` para células vazias.

### Parser de comandos

`readMove()` repete a leitura até conseguir retornar um `Move` válido. Entradas mal formadas imprimem mensagem de erro e não encerram o jogo.

| Entrada | `Move` gerado | Observação |
|---|---|---|
| `q` | `Move.quit()` | Encerra o jogo. |
| `help` | `Move.help()` | Mostra ajuda. |
| `new` | `Move.newBoard()` | Gera novo puzzle. |
| `status` | `Move.status()` | Consulta status de vitória. |
| `complete` | `Move.complete()` | Solicita solução automática. |
| `clear` | `Move.clearUserMoves()` | Limpa células não fixas. |
| `save partida.txt` | `Move.save("partida.txt")` | Caminho fica em `filePath`. |
| `load partida.txt` | `Move.load("partida.txt")` | Caminho fica em `filePath`. |
| `remove 3 5` | `Move.remove(2, 4)` | Converte coordenadas para 0-based. |
| `3 5 7` | `Move.play(2, 4, 7)` | Converte coordenadas para 0-based. |

O parser aceita comandos textuais sem diferenciar maiúsculas/minúsculas. Para jogadas, aceita valor `0..9`; `0` esvazia a célula se o controller permitir alteração.

## `ConsoleUI.Move`

`Move` é um objeto imutável criado por métodos estáticos de fábrica. O controller consulta predicados como `isSave()`, `isLoad()` e `isRemove()` para decidir o fluxo.

Campos principais:

| Campo | Uso |
|---|---|
| `type` | Tipo lógico definido por `CommandTypeEnum`. |
| `quit` | Atalho booleano para encerramento. |
| `row`, `col`, `value` | Dados de jogada já convertidos para índice interno. |
| `filePath` | Caminho usado por `save` e `load`. |

## `GameController`

`run()` inicia um tabuleiro com `generator.generate(40)` e entra no loop:

```text
imprimir tabuleiro
verificar vitória
ler comando
executar comando
repetir
```

Despacho de comandos:

| Comando | Ação |
|---|---|
| `q` | Imprime `Jogo encerrado.` e retorna. |
| `save` | Chama `FileManager.saveGame()`. |
| `load` | Chama `FileManager.loadGame()` e substitui o board se carregar com sucesso. |
| `help` | Imprime a lista de comandos. |
| `new` | Gera novo board com 40 células removidas. |
| `status` | Imprime `COMPLETO` ou `INCOMPLETO` conforme `GameLogic.isVictory()`. |
| `complete` | Resolve uma cópia e substitui o board se `Solver.solve()` retornar `true`. |
| `remove` | Zera a célula se ela não for fixa. |
| `clear` | Chama `GameLogic.clearAllUserMoves()`. |
| jogada | Define o valor se a célula não for fixa. |

### Limitações de validação

O controller bloqueia alteração de células fixas, mas não bloqueia uma jogada que cause duplicidade em linha, coluna ou caixa. Isso é comportamento atual do código, não uma regra ideal de Sudoku. A validação completa fica em `GameLogic.isVictory()` e no processo do `Solver`.

### Mensagens literais

Algumas mensagens impressas pelo código não têm acentuação, por exemplo `Parabens! Voce venceu o jogo.` e `Comandos disponiveis:`. Ao documentar saída exata, preserve o literal como está no código.
