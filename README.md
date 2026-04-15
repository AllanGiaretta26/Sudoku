# 🎯 Sudoku em Java

![Status](https://img.shields.io/badge/status-concluído-brightgreen)
![Java](https://img.shields.io/badge/Java-17+-orange)
![Gradle](https://img.shields.io/badge/Gradle-build-06A0D1?logo=gradle)
![JUnit5](https://img.shields.io/badge/JUnit-5-25A162)
![License](https://img.shields.io/badge/licença-MIT-lightgrey)

> Jogo de Sudoku completo no terminal — com geração automática de puzzles, validação em tempo real e suporte a salvar e carregar partidas.

---

## 📋 Descrição

**Sudoku em Java** é uma aplicação de console que implementa o clássico jogo de lógica. O tabuleiro `9x9` é dividido em 9 blocos `3x3`, e o objetivo é preencher as células vazias com números de `1` a `9` sem repetir nenhum na mesma linha, coluna ou bloco.

O puzzle é gerado automaticamente a cada partida usando um algoritmo de **backtracking recursivo**. O projeto foi desenvolvido com foco em **orientação a objetos**, separando claramente as responsabilidades em camadas de modelo, lógica, interface e persistência.

Internamente, o valor `0` representa uma célula vazia.

---

## 📌 Status do Projeto

![Status](https://img.shields.io/badge/status-concluído-brightgreen)

Todas as funcionalidades estão implementadas e os testes passando.

---

## 🛠️ Tecnologias

![Java](https://img.shields.io/badge/Java-17+-orange)
![Gradle](https://img.shields.io/badge/Gradle-06A0D1?logo=gradle)
![JUnit5](https://img.shields.io/badge/JUnit-5-25A162)

| Tecnologia | Uso |
|---|---|
| **Java 17+** | Linguagem principal, programação orientada a objetos |
| **Gradle** | Build, execução da aplicação e dos testes |
| **JUnit 5** | Testes unitários |
| **Java NIO** (`java.nio.file`) | Leitura e escrita de partidas em arquivos `.txt` |
| **Console / CLI** | Interface de interação com o usuário |

---

## 🚀 Como Instalar e Rodar

### Pré-requisitos

- [Java 17+](https://adoptium.net/) instalado e configurado no PATH
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
| `save arquivo.txt` | Salva a partida atual em um arquivo | `save minha_partida.txt` |
| `load arquivo.txt` | Carrega uma partida salva | `load minha_partida.txt` |
| `help` | Exibe a lista de comandos disponíveis | — |
| `q` | Encerra o jogo | — |

> **Atenção:** células fixas (do puzzle original) não podem ser alteradas. O jogo detecta automaticamente a vitória quando o tabuleiro é preenchido corretamente.

---

## 📁 Estrutura do Projeto

```text
app/src/
├─ main/java/sudoku/
│  ├─ App.java                  ← Ponto de entrada — inicia o GameController
│  ├─ logic/
│  │  ├─ Validador.java         ← Valida linha, coluna e bloco 3x3
│  │  ├─ Solver.java            ← Resolve o puzzle via backtracking recursivo
│  │  └─ Generator.java         ← Gera tabuleiro completo e remove células
│  ├─ model/
│  │  ├─ Cell.java              ← Célula com valor e flag de fixo
│  │  └─ Board.java             ← Matriz 9x9 com acesso e atualização
│  ├─ ui/
│  │  ├─ ConsoleUI.java         ← Renderiza o tabuleiro e lê comandos
│  │  └─ GameController.java    ← Loop principal: entrada, validação e vitória
│  └─ util/
│     └─ FileManager.java       ← Salva e carrega partidas em arquivo .txt
└─ test/java/sudoku/
   └─ AppTest.java
```

---

## 📄 Licença

Distribuído sob a licença [MIT](./LICENSE).

---

Desenvolvido por **Allan Giaretta**.
