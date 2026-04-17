# 🎯 Sudoku em Java

![Status](https://img.shields.io/badge/status-concluído-brightgreen)
![Java](https://img.shields.io/badge/Java-21-orange)
![Gradle](https://img.shields.io/badge/Gradle-build-06A0D1?logo=gradle)
![JUnit5](https://img.shields.io/badge/JUnit-5-25A162)
![License](https://img.shields.io/badge/licença-MIT-lightgrey)

> Jogo de Sudoku completo no terminal — com geração automática de puzzles, validação em tempo real e suporte a salvar e carregar partidas.

---

## 📋 Descrição

**Sudoku em Java** é uma aplicação de console que implementa o clássico jogo de lógica. O tabuleiro `9x9` é dividido em 9 blocos `3x3`, e o objetivo é preencher as células vazias com números de `1` a `9` sem repetir nenhum na mesma linha, coluna ou bloco.

O puzzle é gerado automaticamente a cada partida usando um algoritmo de **backtracking recursivo** com randomização estrutural (permutação de dígitos e troca de linhas/colunas dentro de bandas). O projeto aplica **injeção de dependências**, separando claramente as responsabilidades em camadas de modelo, lógica, interface e persistência.

---

## 📌 Status do Projeto

![Status](https://img.shields.io/badge/status-concluído-brightgreen)

Todas as funcionalidades estão implementadas, documentadas e cobertas por testes.

---

## 🛠️ Tecnologias

![Java](https://img.shields.io/badge/Java-21-orange)
![Gradle](https://img.shields.io/badge/Gradle-06A0D1?logo=gradle)
![JUnit5](https://img.shields.io/badge/JUnit-5-25A162)

| Tecnologia | Uso |
|---|---|
| **Java 21** | Linguagem principal, programação orientada a objetos |
| **Gradle** | Build, execução da aplicação e dos testes |
| **JUnit 5** | Suíte de testes unitários (41 testes) |
| **Java NIO** (`java.nio.file`) | Leitura e escrita de partidas em arquivos `.txt` |
| **Console / CLI** | Interface de interação com o usuário |

---

## 🚀 Como Instalar e Rodar

### Pré-requisitos

- [Java 21+](https://adoptium.net/) instalado e configurado no PATH
- [Git](https://git-scm.com/) instalado

### Instalação

```bash
# Clone o repositório
git clone https://github.com/AllanGiaretta26/Sudoku.git

# Acesse a pasta do projeto
cd Sudoku
```

### Executar o jogo

```bash
.\gradlew.bat run
```

### Executar os testes

```bash
.\gradlew.bat test
```

---

## 🎮 Como Jogar

Após iniciar o jogo, o tabuleiro será exibido no terminal. Use os comandos abaixo para interagir:

| Comando | Descrição | Exemplo |
|---|---|---|
| `linha coluna valor` | Insere um número na célula indicada | `3 5 7` |
| `remove linha coluna` | Remove o número de uma célula não fixa | `remove 3 5` |
| `clear` | Remove todos os números inseridos pelo jogador | — |
| `new` | Gera um novo tabuleiro aleatório | — |
| `complete` | Completa o tabuleiro automaticamente | — |
| `status` | Exibe o status atual da partida | — |
| `save arquivo.txt` | Salva a partida atual em um arquivo | `save partida.txt` |
| `load arquivo.txt` | Carrega uma partida salva | `load partida.txt` |
| `help` | Exibe a lista de comandos disponíveis | — |
| `q` | Encerra o jogo | — |

> **Atenção:** células fixas (do puzzle original) não podem ser alteradas. O jogo detecta automaticamente a vitória quando o tabuleiro é preenchido corretamente.

---

## 📁 Estrutura do Projeto

```text
app/src/
├─ main/java/sudoku/
│  ├─ App.java                  ← Ponto de entrada — monta o grafo de dependências
│  ├─ logic/
│  │  ├─ GameLogic.java         ← Regras de partida (vitória, cópia, limpeza)
│  │  ├─ Validador.java         ← Valida linha, coluna e bloco 3x3
│  │  ├─ Solver.java            ← Resolve o puzzle via backtracking recursivo
│  │  └─ Generator.java         ← Gera tabuleiro completo e remove células
│  ├─ model/
│  │  ├─ Cell.java              ← Célula com valor (0–9) e flag de fixo
│  │  └─ Board.java             ← Matriz 9x9 com acesso e atualização
│  ├─ ui/
│  │  ├─ ConsoleUI.java         ← Renderiza o tabuleiro, lê e interpreta comandos
│  │  └─ GameController.java    ← Orquestra o loop principal do jogo (MVC controller)
│  └─ util/
│     ├─ CommandTypeEnum.java   ← Enum com todos os tipos de comando
│     └─ FileManager.java       ← Salva e carrega partidas em arquivo .txt
└─ test/java/sudoku/
   ├─ AppTest.java
   ├─ logic/
   │  ├─ GameLogicTest.java
   │  ├─ ValidadorTest.java
   │  ├─ SolverTest.java
   │  └─ GeneratorTest.java
   └─ ui/
      └─ ConsoleUICommandParserTest.java
```

---

## 🧪 Testes

O projeto conta com **41 testes unitários** distribuídos em 6 suítes:

| Suíte | Testes | O que cobre |
|---|---|---|
| `ValidadorTest` | 9 | Unicidade em linha, coluna e caixa; limites de índice |
| `ConsoleUICommandParserTest` | 12 | Parsing de todos os comandos; recuperação de entradas inválidas |
| `GameLogicTest` | 8 | Detecção de vitória; cópia de tabuleiro; limpeza de jogadas |
| `GeneratorTest` | 6 | Contagem de células; marcação de fixos; limites; não-determinismo |
| `SolverTest` | 5 | Resolução completa; boards parciais; board inconsistente |
| `AppTest` | 1 | Sanity check de instanciação |

---

## 📄 Licença

Distribuído sob a licença [MIT](./LICENSE).

---

Desenvolvido por **Allan Giaretta**.
