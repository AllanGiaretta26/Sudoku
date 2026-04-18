# Camada Model — `Cell` e `Board`

## Visão geral

A camada **model** contém as estruturas de dados do jogo.  
Ela não tem lógica de regras nem de interface — só guarda e organiza os dados do tabuleiro.

São duas classes:
- **`Cell`** — representa uma célula individual do tabuleiro
- **`Board`** — representa o tabuleiro completo 9×9

---

## Cell.java

### O que faz

Armazena os dados de uma única célula. Cada célula tem:
- um **valor** de 0 a 9 (0 significa "vazia")
- um sinalizador **fixed** que indica se a célula veio do puzzle original e não pode ser apagada

### Explicação por seção

#### Atributos

```java
private int value = 0;      // número na célula; 0 = vazia
private boolean fixed = false; // true = célula do puzzle original (não pode ser alterada)
```

#### Construtores

```java
// Cria uma célula com valor e estado de fixação definidos
public Cell(int value, boolean fixed) { ... }

// Cria uma célula vazia e não-fixa
public Cell() { ... }
```

O construtor com parâmetros valida o valor. Se você passar um número fora de 0–9, ele lança um erro.

#### Métodos principais

| Método | O que faz |
|---|---|
| `getValue()` | Retorna o número guardado na célula |
| `setValue(int value)` | Muda o número; lança erro se estiver fora de 0–9 |
| `isFixed()` | Retorna `true` se a célula é do puzzle original |
| `setFixed(boolean fixed)` | Define se a célula é fixa ou não |

### Exemplo de uso

```java
Cell celula = new Cell();        // célula vazia, não-fixa
celula.setValue(5);              // insere o número 5
System.out.println(celula.getValue()); // imprime: 5

Cell celulaDoPuzzle = new Cell(7, true); // célula fixa com valor 7
System.out.println(celulaDoPuzzle.isFixed()); // imprime: true
```

### Pontos de atenção

- **Valor 0 significa "vazia"** — não é um número do puzzle, é só o placeholder para célula em branco.
- **`setValue()` lança `IllegalArgumentException`** se você passar um número negativo ou maior que 9.
- **`setFixed()` não protege a célula automaticamente** — é o `GameController` quem verifica se a célula é fixa antes de permitir alteração.

---

## Board.java

### O que faz

Representa o tabuleiro de Sudoku como uma grade 9×9 de objetos `Cell`.  
Fornece métodos para ler e escrever valores em posições específicas.

### Explicação por seção

#### Atributo principal

```java
private Cell[][] cell; // grade 9×9 de células
```

O índice sempre começa em 0. A linha 0 é a primeira linha visual, a coluna 0 é a primeira coluna visual.

#### Construtores

```java
// Cria um tabuleiro 9×9 completamente vazio
public Board() { ... }

// Cria um tabuleiro a partir de uma grade existente (cópia profunda)
public Board(Cell[][] cell) { ... }
```

O construtor que recebe `Cell[][]` faz uma **cópia profunda** — ele cria objetos `Cell` novos com os mesmos valores. Isso significa que alterar o array original não afeta o tabuleiro criado.

#### Métodos principais

| Método | O que faz |
|---|---|
| `getCell(row, col)` | Retorna a referência da célula (permite ler e alterar) |
| `getValueAt(row, col)` | Retorna só o número da célula, sem expor o objeto |
| `setValue(row, col, value)` | Muda o número em uma posição |
| `printBoard()` | Gera uma representação simples em texto (para debug) |

#### Validação de índices

Todos os métodos validam se `row` e `col` estão entre 0 e 8. Se não estiverem, lançam `IllegalArgumentException`.

### Exemplo de uso

```java
Board tabuleiro = new Board(); // tabuleiro vazio

tabuleiro.setValue(0, 0, 5);  // coloca 5 na linha 0, coluna 0
int valor = tabuleiro.getValueAt(0, 0); // retorna 5
System.out.println(valor); // imprime: 5

// Verificar se a célula é fixa
boolean ehFixa = tabuleiro.getCell(0, 0).isFixed(); // false (célula recém-criada)
```

### Pontos de atenção

- **Os índices começam em 0**, mas o usuário digita de 1 a 9. O `ConsoleUI` faz essa conversão automaticamente (subtrai 1 antes de chamar o Board).
- **`getCell()` retorna uma referência mutável** — qualquer alteração no `Cell` retornado afeta o tabuleiro diretamente. Use `getValueAt()` quando só precisar do número.
- **`setValue()` não verifica se a célula é fixa** — essa proteção fica no `GameController`. O `Board` só cuida dos dados.
- **O construtor de cópia `Board(Cell[][])` garante independência** — modificar o array original depois de passar para o construtor não altera o tabuleiro.
