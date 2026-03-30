# Sudoku em Java (Console)

## 1) Explicacao do jogo Sudoku

Sudoku e um jogo de logica com um tabuleiro `9x9`, dividido em 9 caixas `3x3`.
O objetivo e preencher as celulas vazias com numeros de `1` a `9`, sem repetir:

- na mesma linha;
- na mesma coluna;
- na mesma caixa `3x3`.

No projeto, o valor `0` representa celula vazia.

## 2) Tecnologias utilizadas

- **Java** (programacao orientada a objetos)
- **Gradle** (build e execucao de testes)
- **JUnit 5** (teste unitario basico)
- **Console/CLI** para interacao com o usuario
- **Java NIO (`java.nio.file`)** para salvar/carregar partidas em arquivo `.txt`

## 3) Estrutura do projeto 

```text
app/src/
├─ main/java/sudoku/
│  ├─ App.java
│  ├─ logic/
│  │  ├─ Validador.java
│  │  ├─ Solver.java
│  │  └─ Generator.java
│  ├─ model/
│  │  ├─ Cell.java
│  │  └─ Board.java
│  ├─ ui/
│  │  ├─ ConsoleUI.java
│  │  └─ GameController.java
│  └─ util/
│     └─ FileManager.java
└─ test/java/sudoku/
   └─ AppTest.java
```

## 4) Explicacao do codigo

- **Modelo (`model`)**
  - `Cell`: representa uma celula do tabuleiro (`value` e `fixed`).
  - `Board`: representa a matriz `9x9` e oferece acesso/atualizacao de celulas.

- **Logica (`logic`)**
  - `Validador`: valida linha, coluna e caixa `3x3` pelas regras do Sudoku.
  - `Solver`: resolve o Sudoku com backtracking recursivo.
  - `Generator`: gera um tabuleiro completo e remove celulas para formar o puzzle.

- **Interface e fluxo (`ui`)**
  - `ConsoleUI`: imprime tabuleiro e le comandos do usuario.
  - `GameController`: loop principal do jogo (entrada, validacao, atualizacao e vitoria).

- **Persistencia (`util`)**
  - `FileManager`: salva/carrega partida em arquivo `.txt` com valores e estado de celula fixa.

- **Entrada da aplicacao**
  - `App`: inicia o jogo chamando `GameController.run()`.

## 5) Funcionalidades do codigo

- Iniciar uma nova partida de Sudoku no console.
- Exibir tabuleiro formatado com separacao visual das caixas `3x3`.
- Inserir jogadas no formato: `linha coluna valor`.
- Bloquear edicao de celulas fixas.
- Validar jogadas conforme regras do Sudoku.
- Detectar vitoria quando o tabuleiro estiver completo e valido.
- Salvar partida com comando: `save arquivo.txt`.
- Carregar partida com comando: `load arquivo.txt`.
- Exibir ajuda com comando: `help`.
- Encerrar jogo com comando: `q`.

## Executar o projeto

No terminal, na raiz do projeto:

```bash
.\gradlew.bat run
```

Para executar testes:

```bash
.\gradlew.bat test
```
