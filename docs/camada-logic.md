# Camada Logic — Regras do Jogo

## Visão geral

A camada **logic** contém toda a inteligência do jogo.  
Ela não faz nenhuma leitura de teclado nem imprime nada na tela — apenas recebe um tabuleiro, aplica uma lógica e retorna um resultado.

São quatro classes:

| Classe | Responsabilidade |
|---|---|
| `Validador` | Verifica se linha, coluna ou bloco 3×3 tem números repetidos |
| `Solver` | Resolve um tabuleiro automaticamente via backtracking |
| `Generator` | Gera tabuleiros aleatórios válidos para começar um jogo |
| `GameLogic` | Regras da partida (vitória, cópia do tabuleiro, limpeza de jogadas) |

---

## Validador.java

### O que faz

Verifica se o tabuleiro obedece às três regras do Sudoku:
1. Nenhum número repetido na mesma linha
2. Nenhum número repetido na mesma coluna
3. Nenhum número repetido no mesmo bloco 3×3

> **Importante:** células vazias (valor 0) são **ignoradas** na validação. Um tabuleiro parcialmente preenchido pode estar "válido" mesmo com espaços em branco.

### Explicação por seção

#### Métodos públicos

```java
// Verifica uma linha inteira (row = 0 a 8)
public boolean isValidRow(Board board, int row)

// Verifica uma coluna inteira (column = 0 a 8)
public boolean isValidColumn(Board board, int column)

// Verifica um bloco 3×3 a partir do canto superior esquerdo
// row e column devem ser 0, 3 ou 6 (origem do bloco)
public boolean isValidBox(Board board, int row, int column)
```

#### Como funciona internamente

Os três métodos coletam os 9 valores do eixo em um array e chamam o método privado `checkUnique`:

```java
private boolean checkUnique(int[] values) {
    boolean[] seen = new boolean[10]; // índices 1–9
    for (int value : values) {
        if (value == 0) continue;           // ignora vazias
        if (value < 1 || value > 9 || seen[value]) return false; // duplicata
        seen[value] = true;
    }
    return true;
}
```

O array `seen` age como uma lista de presença: se o número já apareceu, `seen[número]` é `true`. Na segunda vez que aparece, retorna `false` (inválido).

### Exemplo de uso

```java
Board board = new Board();
board.setValue(0, 0, 5);
board.setValue(0, 1, 3);

Validador v = new Validador();
System.out.println(v.isValidRow(board, 0));    // true — sem repetição
System.out.println(v.isValidColumn(board, 0)); // true
System.out.println(v.isValidBox(board, 0, 0)); // true

board.setValue(0, 2, 5); // insere 5 de novo na mesma linha
System.out.println(v.isValidRow(board, 0));    // false — 5 repetido
```

### Pontos de atenção

- **`isValidBox` aceita `row` e `column` de 0 a 6**, não apenas múltiplos de 3. Mas na prática, use sempre 0, 3 ou 6 para apontar para o canto de um bloco válido.
- **Não lança exceção se o tabuleiro tiver conflito** — apenas retorna `false`. Quem decide o que fazer com isso é o chamador.
- **A classe não tem estado** — pode ser criada uma vez e usada em vários lugares ao mesmo tempo sem problema.

---

## Solver.java

### O que faz

Resolve um tabuleiro de Sudoku automaticamente usando o algoritmo **backtracking** (tentativa e erro recursivo). 

### Explicação por seção

#### Como o backtracking funciona (passo a passo)

1. Encontra a primeira célula vazia
2. Tenta colocar o número 1
3. Verifica se está válido (sem conflito na linha, coluna e bloco)
4. Se sim → avança para a próxima célula vazia e repete
5. Se não → tenta o número 2, 3, ... até 9
6. Se nenhum número funcionou → **volta para a célula anterior** e tenta o próximo número lá
7. Se chegou até o fim sem célula vazia → tabuleiro resolvido!

#### Método público

```java
// Tenta resolver o tabuleiro; modifica o Board diretamente
// Retorna true se conseguiu, false se o puzzle não tem solução
public boolean solve(Board board)
```

#### Otimização de desempenho

Em vez de começar sempre do zero ao procurar a próxima célula vazia, o solver passa a posição atual (`startIndex`) para a próxima chamada. Isso evita re-percorrer células que já foram preenchidas:

```java
private boolean solveBacktracking(Board board, int startIndex) {
    int[] emptyCell = findEmptyCell(board, startIndex); // busca a partir de startIndex
    if (emptyCell == null) return true; // tabuleiro completo!

    int row = emptyCell[0], col = emptyCell[1], linearIndex = emptyCell[2];

    for (int value = 1; value <= 9; value++) {
        board.setValue(row, col, value);
        if (isPlacementValid(board, row, col) && solveBacktracking(board, linearIndex + 1)) {
            return true;
        }
    }

    board.setValue(row, col, 0); // desfaz e retorna false
    return false;
}
```

### Exemplo de uso

```java
Board board = new Board(); // tabuleiro vazio
Solver solver = new Solver();

boolean resolvido = solver.solve(board);

if (resolvido) {
    System.out.println("Solução encontrada!");
    System.out.print(board.printBoard());
} else {
    System.out.println("Sem solução.");
}
```

### Pontos de atenção

- **O `solve()` modifica o tabuleiro passado** — se quiser preservar o original, faça uma cópia antes com `GameLogic.copyBoard()`.
- **Se retornar `false`, o tabuleiro fica com as células zeradas** — o backtracking desfaz tudo automaticamente.
- **Passado um tabuleiro já completo** — retorna `true` imediatamente sem modificar nada.

---

## Generator.java

### O que faz

Cria um tabuleiro de Sudoku pronto para jogar. O processo tem três etapas:

1. **Resolve** um tabuleiro vazio (via `Solver`) para ter uma solução válida
2. **Embaralha** a solução aplicando transformações que não quebram as regras
3. **Remove** células aleatoriamente para criar o puzzle (deixando as restantes como "fixas")

### Explicação por seção

#### Etapa 1 — Resolver um tabuleiro vazio

O `Solver` preenche um `Board` vazio. Sempre sai a mesma solução base, por isso a etapa 2 é necessária.

#### Etapa 2 — Embaralhar sem quebrar as regras

Três transformações são aplicadas:

**a) Permutação de dígitos:** troca todos os 1s por outro número, todos os 2s por outro, etc. O mapeamento é aleatório mas consistente — todos os números são permutados sem repetição.

```
Antes: 1 2 3 → Depois: 7 4 9  (mapeamento: 1→7, 2→4, 3→9 ...)
```

**b) Troca de linhas dentro de bandas:** o tabuleiro tem 3 bandas horizontais de 3 linhas cada (linhas 0-2, 3-5, 6-8). Dentro de cada banda, duas linhas são trocadas.

**c) Troca de colunas dentro de pilhas:** igual, mas vertical.

Essas trocas preservam as regras do Sudoku porque não movem números entre bandas/pilhas diferentes.

#### Etapa 3 — Remover células

```java
public Board generate(int cellsToRemove) // padrão: 40 células removidas
```

Células removidas têm valor 0 e `fixed=false`.  
Células mantidas têm `fixed=true` — o jogador não pode apagá-las.

#### Como o shuffle garante índices distintos

```java
int localA = random.nextInt(3);         // 0, 1 ou 2
int offset = 1 + random.nextInt(2);     // 1 ou 2 (nunca 0)
int localB = (localA + offset) % 3;    // sempre diferente de localA
```

Isso garante que as duas linhas/colunas trocadas sejam sempre diferentes.

### Exemplo de uso

```java
Generator generator = new Generator();

// Gera tabuleiro com 40 células removidas (padrão)
Board puzzle = generator.generate(40);

// Gera tabuleiro mais fácil (menos células removidas)
Board facil = generator.generate(20);

// Gera tabuleiro mais difícil
Board dificil = generator.generate(55);
```

### Pontos de atenção

- **`generate(0)` retorna o tabuleiro completo** sem nenhuma célula vazia — útil para testes.
- **`generate(81)` remove tudo** — o tabuleiro ficará completamente vazio (sem células fixas).
- **Passar um número fora de 0–81 lança `IllegalArgumentException`**.
- **O gerador não garante solução única** — com muitas células removidas, pode haver múltiplas soluções válidas.

---

## GameLogic.java

### O que faz

Concentra as regras de partida que não são responsabilidade do `Validador` nem do `Solver`:

- **`isVictory`** — verifica se o jogador venceu
- **`copyBoard`** — cria uma cópia independente do tabuleiro
- **`clearAllUserMoves`** — apaga as jogadas do usuário, mantendo as células originais

### Explicação por seção

#### isVictory

```java
public boolean isVictory(Board board)
```

Verifica três condições em sequência:
1. Todas as células têm valor diferente de 0 (nenhuma vazia)
2. Todas as 9 linhas são válidas
3. Todas as 9 colunas e todos os 9 blocos 3×3 são válidos

Se qualquer condição falhar, retorna `false` imediatamente.

#### copyBoard

```java
public Board copyBoard(Board board)
```

Cria um `Board` completamente independente com as mesmas células. Útil para "simular" a conclusão automática sem alterar o tabuleiro real do jogador.

#### clearAllUserMoves

```java
public void clearAllUserMoves(Board board)
```

Percorre todas as células e zera as que têm `fixed=false`. As células originais do puzzle (`fixed=true`) ficam intactas.

### Exemplo de uso

```java
GameLogic logic = new GameLogic();

// Verificar se ganhou
if (logic.isVictory(board)) {
    System.out.println("Parabéns, você venceu!");
}

// Simular solução sem alterar o tabuleiro original
Board copia = logic.copyBoard(board);
solver.solve(copia); // resolve a cópia
// board original continua intacto

// Apagar todas as jogadas do usuário
logic.clearAllUserMoves(board);
```

### Pontos de atenção

- **`isVictory(null)` retorna `false`** — não lança exceção.
- **`copyBoard(null)` e `clearAllUserMoves(null)` lançam `IllegalArgumentException`**.
- **`isVictory` valida as regras inteiras** — um tabuleiro cheio mas com conflito (ex.: dois 5s na mesma linha) retorna `false`.
