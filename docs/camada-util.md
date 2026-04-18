# Camada Util — Ferramentas Auxiliares

## Visão geral

A camada **util** contém ferramentas que não pertencem à lógica do jogo nem à interface — são componentes de apoio reutilizáveis.

São duas classes:

- **`FileManager`** — salva e carrega partidas em arquivos `.txt`
- **`CommandTypeEnum`** — lista de todos os tipos de comando reconhecidos pela UI

---

## FileManager.java

### O que faz

Persiste o estado do tabuleiro em um arquivo de texto e o restaura depois.  
O arquivo usa um formato próprio chamado `SUDOKU_SAVE_V1`.

### Explicação por seção

#### Formato do arquivo salvo

```
SUDOKU_SAVE_V1
5:1,3:1,0:0,0:0,7:1,0:0,0:0,0:0,0:0
6:1,0:0,0:0,1:1,9:1,5:1,0:0,0:0,0:0
...
```

- **Primeira linha:** cabeçalho de identificação `SUDOKU_SAVE_V1`
- **Linhas 2 a 10:** uma linha por linha do tabuleiro (9 no total)
- **Cada célula:** `valor:fixo` separado por vírgula
  - `valor` é o número de 0 a 9 (0 = vazia)
  - `fixo` é 1 (célula do puzzle original) ou 0 (jogada do usuário)

#### Método saveGame

```java
public void saveGame(Board board, String filePath) throws IOException
```

1. Monta o texto do arquivo linha por linha
2. Cria os diretórios necessários automaticamente (se `filePath` contiver pastas)
3. Escreve o arquivo em UTF-8

#### Método loadGame

```java
public Board loadGame(String filePath) throws IOException
```

1. Verifica se o arquivo existe
2. Lê todas as linhas
3. Valida o cabeçalho e o número de linhas
4. Para cada célula, faz parse do formato `valor:fixo`
5. Retorna um `Board` reconstruído com os valores e os flags de fixação corretos

#### Validações na leitura

O `loadGame` verifica:
- O arquivo existe
- Tem exatamente 10 linhas (1 cabeçalho + 9 do tabuleiro)
- O cabeçalho é `SUDOKU_SAVE_V1`
- Cada linha tem exatamente 9 células
- Cada célula tem o formato `v:f` com dois números
- `valor` está entre 0 e 9
- `fixo` é 0 ou 1

Se qualquer verificação falhar, lança `IOException` com uma mensagem descritiva.

### Exemplo de uso

```java
FileManager fm = new FileManager();

// Salvar a partida
try {
    fm.saveGame(board, "saves/minha_partida.txt");
    System.out.println("Partida salva com sucesso!");
} catch (IOException e) {
    System.out.println("Erro ao salvar: " + e.getMessage());
}

// Carregar a partida
try {
    Board carregado = fm.loadGame("saves/minha_partida.txt");
    System.out.println("Partida carregada!");
} catch (IOException e) {
    System.out.println("Erro ao carregar: " + e.getMessage());
}
```

#### Exemplo de arquivo gerado

```
SUDOKU_SAVE_V1
5:1,3:1,0:0,0:0,7:1,0:0,0:0,0:0,0:0
6:1,0:0,0:0,1:1,9:1,5:1,0:0,0:0,0:0
0:0,9:1,8:1,0:0,0:0,0:0,0:0,6:1,0:0
8:1,0:0,0:0,0:0,6:1,0:0,0:0,0:0,3:1
4:1,0:0,0:0,8:1,0:0,3:1,0:0,0:0,1:1
7:1,0:0,0:0,0:0,2:1,0:0,0:0,0:0,6:1
0:0,6:1,0:0,0:0,0:0,0:0,2:1,8:1,0:0
0:0,0:0,0:0,4:1,1:1,9:1,0:0,0:0,5:1
0:0,0:0,0:0,0:0,8:1,0:0,0:0,7:1,9:1
```

### Pontos de atenção

- **`filePath` vazio ou `null` lança `IllegalArgumentException`** — sempre passe um caminho válido.
- **`board` null lança `IllegalArgumentException`** no `saveGame`.
- **Se o arquivo não existir**, `loadGame` lança `IOException` com a mensagem `"Save file not found: ..."`.
- **O formato é estrito** — arquivos editados manualmente precisam manter o formato exato, incluindo o cabeçalho `SUDOKU_SAVE_V1`.
- **Diretórios são criados automaticamente** — `save("pasta/subpasta/arquivo.txt")` cria as pastas necessárias.
- **O `.trim()` é aplicado ao ler** — espaços extras antes/depois dos valores de uma célula são tolerados.

---

## CommandTypeEnum.java

### O que faz

Define os tipos de comando que o parser da `ConsoleUI` reconhece.  
É um **enum** — uma lista fixa de valores constantes em Java.

### Explicação por seção

#### O que é um enum?

Um enum é uma lista de constantes nomeadas. Em vez de usar números mágicos (0, 1, 2...) para representar o tipo do comando, usamos nomes legíveis:

```java
// Sem enum — difícil de entender
if (tipo == 1) { /* salvar */ }

// Com enum — muito mais claro
if (tipo == CommandTypeEnum.SAVE) { /* salvar */ }
```

#### Os valores disponíveis

| Constante | Quando é usado |
|---|---|
| `PLAY` | Jogada: `linha coluna valor` |
| `SAVE` | Salvar: `save arquivo.txt` |
| `LOAD` | Carregar: `load arquivo.txt` |
| `NEW_BOARD` | Novo tabuleiro: `new` |
| `STATUS` | Ver status: `status` |
| `COMPLETE` | Completar automaticamente: `complete` |
| `REMOVE` | Remover valor: `remove linha coluna` |
| `CLEAR` | Apagar todas as jogadas: `clear` |
| `HELP` | Ver ajuda: `help` |
| `QUIT` | Encerrar: `q` |

#### Como é usado na prática

O `ConsoleUI` cria objetos `Move` passando o enum correspondente:

```java
// Dentro de ConsoleUI.readMove()
if (input.equalsIgnoreCase("q")) {
    return Move.quit(); // internamente usa CommandTypeEnum.QUIT
}
```

O `GameController` verifica o tipo via os predicados do `Move`:

```java
// Dentro de GameController.run()
if (move.isSave()) { /* tipo == CommandTypeEnum.SAVE */ }
if (move.isLoad()) { /* tipo == CommandTypeEnum.LOAD */ }
```

### Exemplo de uso

```java
// Verificar o tipo de um Move diretamente (menos comum, mas possível)
CommandTypeEnum tipo = CommandTypeEnum.PLAY;

switch (tipo) {
    case PLAY    -> System.out.println("Jogada de preenchimento");
    case SAVE    -> System.out.println("Salvar partida");
    case QUIT    -> System.out.println("Encerrar jogo");
    default      -> System.out.println("Outro comando");
}
```

### Pontos de atenção

- **Enums em Java são seguros por tipo** — você não pode passar um valor inválido onde um `CommandTypeEnum` é esperado; o compilador rejeita.
- **Adicionar um novo comando** exige três mudanças: (1) adicionar a constante aqui, (2) criar o método de fábrica no `Move`, (3) tratar o caso no `GameController`.
- **Não use inteiros para representar comandos** — sempre use as constantes do enum para manter o código legível e sem "números mágicos".
