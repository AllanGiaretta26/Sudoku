# Sudoku em Java

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Build-Gradle-02303A?logo=gradle&logoColor=white)
![Tests](https://img.shields.io/badge/Tests-JUnit_5-25A162?logo=junit5&logoColor=white)
![Status](https://img.shields.io/badge/status-em%20manutenção-yellow)
![License](https://img.shields.io/badge/license-MIT-blue)

> Jogo de Sudoku no terminal, implementado em Java 21 com Gradle.

## Descrição

A aplicação gera um puzzle 9x9, aceita comandos via CLI, salva e carrega partidas em arquivo texto e consegue completar o tabuleiro usando um solver por backtracking. O projeto foi organizado em camadas (`model`, `logic`, `ui`, `util`) e conta com suíte de testes unitários e documentação técnica detalhada.

## Status do projeto

Projeto funcional, com 71 testes unitários distribuídos em 9 suítes.

## Tecnologias

- Java 21
- Gradle (com Gradle Wrapper, sem necessidade de instalação local)
- JUnit 5

## Como instalar e rodar

### Pré-requisitos

- Java 21 ou superior no `PATH`.
- Git para clonar o repositório.

### Clonar o repositório

```bash
git clone https://github.com/AllanGiaretta26/Sudoku.git
cd Sudoku
```

### Executar

Linux/macOS/Git Bash:

```bash
./gradlew run
```

Windows CMD/PowerShell:

```bat
.\gradlew.bat run
```

### Rodar os testes

Linux/macOS/Git Bash:

```bash
./gradlew test
```

Windows CMD/PowerShell:

```bat
.\gradlew.bat test
```

Cobertura atual por suíte:

| Suíte | Testes | Escopo |
|---|---:|---|
| `ValidadorTest` | 9 | Unicidade em linha, coluna e caixa; limites de índice |
| `ConsoleUICommandParserTest` | 12 | Parsing de comandos e recuperação de entradas inválidas |
| `GameLogicTest` | 8 | Vitória, cópia de tabuleiro e limpeza de jogadas |
| `GeneratorTest` | 6 | Remoção de células, marcação de fixos, limites e aleatoriedade |
| `SolverTest` | 5 | Resolução por backtracking e cenários sem solução |
| `CellTest` | 9 | Range de valor, getters/setters e estado fixo |
| `BoardTest` | 11 | Estado inicial, cópia profunda, referência mutável e limites |
| `FileManagerTest` | 10 | Roundtrip save/load, diretórios, formato inválido e path traversal |
| `AppTest` | 1 | Sanity check do ponto de entrada |

## Demonstração

```text
    1 2 3   4 5 6   7 8 9
  +-------+-------+-------+
1 | 5 3 . | . 7 . | . . . | 
2 | 6 . . | 1 9 5 | . . . | 
3 | . 9 8 | . . . | . 6 . | 
  +-------+-------+-------+
4 | 8 . . | . 6 . | . . 3 | 
5 | 4 . . | 8 . 3 | . . 1 | 
6 | 7 . . | . 2 . | . . 6 | 
  +-------+-------+-------+
7 | . 6 . | . . . | 2 8 . | 
8 | . . . | 4 1 9 | . . 5 | 
9 | . . . | . 8 . | . 7 9 | 
  +-------+-------+-------+

Comando (linha coluna valor | remove linha coluna | clear | save arquivo.txt | load arquivo.txt | new | status | complete | help | q): 1 3 4
Comando (...): status
INCOMPLETO
Comando (...): q
```

## Como jogar

Ao iniciar, o jogo imprime o tabuleiro e solicita comandos no terminal. Linhas e colunas digitadas pelo usuário usam índice de 1 a 9; internamente o código converte para 0 a 8.

| Comando | Efeito | Exemplo |
|---|---|---|
| `linha coluna valor` | Define o valor de uma célula não fixa. O parser aceita valores de 0 a 9; `0` esvazia a célula. | `3 5 7` |
| `remove linha coluna` | Remove o valor de uma célula não fixa. | `remove 3 5` |
| `clear` | Remove todos os valores digitados pelo usuário, mantendo células fixas. | `clear` |
| `new` | Gera um novo puzzle com 40 células vazias. | `new` |
| `complete` | Resolve uma cópia do tabuleiro atual e substitui o tabuleiro se houver solução. | `complete` |
| `status` | Exibe `COMPLETO` apenas se o tabuleiro estiver cheio e válido; caso contrário exibe `INCOMPLETO`. | `status` |
| `save arquivo.txt` | Salva a partida no formato `SUDOKU_SAVE_V1`. | `save saves/partida.txt` |
| `load arquivo.txt` | Carrega uma partida salva no formato `SUDOKU_SAVE_V1`. | `load saves/partida.txt` |
| `help` | Exibe a ajuda de comandos. | `help` |
| `q` | Encerra o jogo. | `q` |

Células fixas são as pistas originais do puzzle e não podem ser alteradas pelo jogador. O jogo detecta vitória quando o tabuleiro está completamente preenchido e todas as linhas, colunas e caixas 3x3 são válidas.

> **Atenção:** a aplicação não bloqueia conflitos de Sudoku a cada jogada. Uma jogada em célula não fixa é aplicada mesmo que crie duplicidade; a validação completa acontece apenas na verificação de vitória (`status`) e no solver (`complete`).

## Persistência

O `FileManager` salva arquivos texto em UTF-8 com este formato:

```text
SUDOKU_SAVE_V1
valor:fixo,valor:fixo,valor:fixo,valor:fixo,valor:fixo,valor:fixo,valor:fixo,valor:fixo,valor:fixo
...
```

Regras do formato:

- primeira linha obrigatória: `SUDOKU_SAVE_V1`;
- 9 linhas de tabuleiro após o cabeçalho;
- 9 células por linha;
- `valor` entre 0 e 9;
- `fixo` igual a `1` para célula fixa ou `0` para célula editável.

Ao salvar, diretórios pais são criados automaticamente. Caminhos contendo `..` são recusados para reduzir risco de path traversal. O código não limita salvamento a uma pasta específica; prefira caminhos relativos simples, como `saves/partida.txt`.

## Estrutura do projeto

```text
app/src/
├─ main/java/sudoku/
│  ├─ App.java                  # Ponto de entrada; monta dependências
│  ├─ logic/
│  │  ├─ GameLogic.java         # Vitória, cópia e limpeza de jogadas
│  │  ├─ Validador.java         # Validação de linha, coluna e caixa 3x3
│  │  ├─ Solver.java            # Solver por backtracking
│  │  └─ Generator.java         # Geração de puzzle e remoção de células
│  ├─ model/
│  │  ├─ Cell.java              # Valor e flag de célula fixa
│  │  └─ Board.java             # Matriz 9x9 de células
│  ├─ ui/
│  │  ├─ ConsoleUI.java         # Renderização e parser de comandos
│  │  └─ GameController.java    # Loop principal e orquestração
│  └─ util/
│     ├─ CommandTypeEnum.java   # Tipos de comando reconhecidos
│     └─ FileManager.java       # Persistência em arquivo texto
└─ test/java/sudoku/            # Testes unitários JUnit 5
```

## Documentação técnica

- [Índice da documentação](docs/README.md)
- [Arquitetura](docs/arquitetura/README.md)
- [Referência técnica por camada](docs/referencia/README.md)
- [Auditorias](docs/auditorias/README.md)

## Licença

Distribuído sob a licença [MIT](./LICENSE).

---
Desenvolvido por [Allan Giaretta](https://github.com/AllanGiaretta26).
