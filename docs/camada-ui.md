# Camada UI — Interface com o Usuário

## Visão geral

A camada **ui** é responsável por tudo que o usuário vê e digita.  
São duas classes:

- **`ConsoleUI`** — imprime o tabuleiro na tela e lê os comandos do teclado
- **`GameController`** — coordena o loop principal do jogo, conectando a UI às regras

---

## ConsoleUI.java

### O que faz

Faz a ponte entre o terminal e o resto do código:
1. Imprime o tabuleiro formatado com separadores visuais
2. Exibe mensagens para o usuário
3. Lê o texto digitado e converte para um objeto `Move` (que o controller entende)

### Explicação por seção

#### Dois construtores

```java
// Uso normal: lê do teclado, imprime no terminal
public ConsoleUI()

// Uso em testes: injeta qualquer fonte de entrada e saída
public ConsoleUI(InputStream in, PrintStream out)
```

O segundo construtor permite testar o parser de comandos sem precisar de um teclado real. Você passa um `ByteArrayInputStream` com o texto de entrada e um `ByteArrayOutputStream` para capturar a saída.

#### AutoCloseable

`ConsoleUI` implementa `AutoCloseable`, o que permite usá-la com `try-with-resources`:

```java
try (ConsoleUI ui = new ConsoleUI()) {
    // usa a ui aqui
} // scanner.close() é chamado automaticamente aqui
```

Isso garante que o `Scanner` interno seja fechado corretamente ao final do programa.

#### printBoard

```java
public void printBoard(Board board)
```

Imprime o tabuleiro com separadores visuais a cada 3 colunas/linhas:

```
    1 2 3   4 5 6   7 8 9
  +-------+-------+-------+
1 | 5 3 . | . 7 . | . . . |
2 | 6 . . | 1 9 5 | . . . |
3 | . 9 8 | . . . | . 6 . |
  +-------+-------+-------+
...
```

Células vazias são exibidas como `.`.

#### readMove — o parser de comandos

```java
public Move readMove()
```

Lê uma linha do terminal e reconhece os seguintes formatos:

| Comando digitado | Resultado |
|---|---|
| `q` | `Move.quit()` |
| `help` | `Move.help()` |
| `new` | `Move.newBoard()` |
| `status` | `Move.status()` |
| `complete` | `Move.complete()` |
| `clear` | `Move.clearUserMoves()` |
| `save partida.txt` | `Move.save("partida.txt")` |
| `load partida.txt` | `Move.load("partida.txt")` |
| `remove 3 5` | `Move.remove(2, 4)` — converte para índice 0-based |
| `3 5 7` | `Move.play(2, 4, 7)` — converte para índice 0-based |

Entradas inválidas (ex.: letras onde esperava número, índice fora de 1–9) **não encerram o loop** — o método exibe mensagem de erro e pede o comando de novo.

#### A classe interna Move

`Move` é um objeto imutável que representa um comando já interpretado. O controller não precisa saber como o texto foi lido — só precisa perguntar ao `Move` o que fazer:

```java
if (move.isQuit())    { /* encerrar */ }
if (move.isSave())    { /* salvar arquivo move.getFilePath() */ }
if (move.isPlay())    { /* jogar move.getRow(), move.getCol(), move.getValue() */ }
```

Os objetos `Move` são criados pelos métodos estáticos de fábrica:

```java
Move.quit()
Move.play(row, col, value)
Move.save(filePath)
Move.load(filePath)
Move.remove(row, col)
Move.clearUserMoves()
Move.help()
Move.newBoard()
Move.status()
Move.complete()
```

### Exemplo de uso

```java
// Uso normal
try (ConsoleUI ui = new ConsoleUI()) {
    ui.printBoard(board);
    Move move = ui.readMove();

    if (move.isQuit()) {
        System.out.println("Saindo...");
    }
}

// Uso em testes (sem teclado)
String input = "3 5 7\n";
InputStream in = new ByteArrayInputStream(input.getBytes());
PrintStream out = new PrintStream(new ByteArrayOutputStream());

ConsoleUI ui = new ConsoleUI(in, out);
Move move = ui.readMove();

System.out.println(move.getRow());   // 2  (linha 3 - 1)
System.out.println(move.getCol());   // 4  (coluna 5 - 1)
System.out.println(move.getValue()); // 7
```

### Pontos de atenção

- **Os índices no `Move` são 0-based** — o parser subtrai 1 do que o usuário digitou. Então se o usuário digitar `3 5 7`, `getRow()` retorna 2 e `getCol()` retorna 4.
- **`readMove()` nunca retorna `null`** — se o comando for inválido, ele pede de novo até receber um válido.
- **`save` e `load` sem nome de arquivo** — o parser detecta e exibe mensagem pedindo o nome, sem travar o loop.
- **Maiúsculas e minúsculas** — `Q`, `HELP`, `NEW` funcionam igual a `q`, `help`, `new` (o parser usa `equalsIgnoreCase`).

---

## GameController.java

### O que faz

Coordena o loop principal do jogo. É o "maestro" que conecta todas as outras classes.  
Ele não contém lógica de Sudoku nem lida com a tela — delega tudo para os colaboradores certos.

### Explicação por seção

#### Injeção de dependências

```java
public GameController(ConsoleUI consoleUI, Generator generator, Solver solver,
                      FileManager fileManager, GameLogic gameLogic)
```

Todas as dependências são passadas pelo construtor. Isso torna o controller fácil de testar e de trocar partes sem modificar o código.

Também existe um construtor sem parâmetros para uso rápido:

```java
public GameController() // cria as dependências com valores padrão internamente
```

#### O método run()

```java
public void run()
```

Esse método contém o **loop principal** do jogo. A cada iteração:

1. Imprime o tabuleiro atual
2. Verifica se o jogador ganhou (encerra se sim)
3. Lê o próximo comando
4. Executa a ação correspondente

```
while (true) {
    printBoard → verificar vitória → readMove → executar ação → (repetir)
}
```

O diagrama abaixo mostra o fluxo de decisão para cada comando:

```
move.isQuit()      → encerra o loop
move.isSave()      → fileManager.saveGame()
move.isLoad()      → fileManager.loadGame()
move.isHelp()      → printHelp()
move.isNewBoard()  → generator.generate()
move.isStatus()    → gameLogic.isVictory()
move.isComplete()  → gameLogic.copyBoard() + solver.solve()
move.isRemove()    → board.setValue(row, col, 0)
move.isClear()     → gameLogic.clearAllUserMoves()
(default)          → board.setValue(row, col, value)
```

#### Proteção de células fixas

Antes de aplicar qualquer jogada (PLAY ou REMOVE), o controller verifica:

```java
if (board.getCell(row, col).isFixed()) {
    consoleUI.printMessage("Essa célula é fixa e não pode ser alterada.");
    continue;
}
```

#### O comando `complete`

Ao receber `complete`, o controller:
1. Faz uma **cópia** do tabuleiro atual (via `GameLogic.copyBoard`)
2. Tenta resolver a **cópia** com o `Solver`
3. Só substitui o tabuleiro real se a resolução foi bem-sucedida

Isso garante que o tabuleiro original não seja alterado se o puzzle não tiver solução.

### Exemplo de uso

```java
// Montagem manual das dependências (igual ao App.java)
Validador validador = new Validador();
Solver solver = new Solver(validador);
Generator generator = new Generator();
FileManager fileManager = new FileManager();
GameLogic gameLogic = new GameLogic(validador);

try (ConsoleUI ui = new ConsoleUI()) {
    GameController controller = new GameController(ui, generator, solver, fileManager, gameLogic);
    controller.run(); // inicia o loop do jogo
}

// Forma rápida (usando construtor de conveniência)
new GameController().run();
```

### Pontos de atenção

- **Nenhuma dependência pode ser `null`** — o construtor lança `IllegalArgumentException` se qualquer parâmetro for `null`.
- **O controller não valida regras do Sudoku** — ele só verifica se a célula é fixa. A validação (linha/coluna/bloco) fica no `GameLogic` e no `Validador`.
- **O loop só termina de duas formas:** o jogador digita `q` ou o tabuleiro é completado corretamente.
- **Erros de I/O no `save`/`load`** são capturados e exibidos como mensagem — o jogo continua normalmente.
