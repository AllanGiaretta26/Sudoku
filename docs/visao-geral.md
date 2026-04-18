# Visão Geral — Sudoku em Java

## O que esse projeto faz

Este projeto implementa o jogo de Sudoku completo no terminal (linha de comando).  
Ao iniciar, o programa gera um tabuleiro 9×9 automaticamente e aguarda os comandos do jogador. O objetivo é preencher todas as células vazias com números de 1 a 9 sem repetir nenhum na mesma linha, coluna ou bloco 3×3.

---

## Funcionalidades principais

| Funcionalidade | Descrição |
|---|---|
| Geração automática | Cria um tabuleiro diferente a cada partida |
| Validação em tempo real | Detecta automaticamente quando o jogador venceu |
| Salvar e carregar | Guarda a partida em um arquivo `.txt` e a recarrega depois |
| Completar automático | Resolve o tabuleiro inteiro via algoritmo |
| Desfazer jogadas | Remove valores inseridos pelo jogador |

---

## Como o código está organizado

O projeto é dividido em quatro camadas. Cada camada tem uma responsabilidade clara:

```
App.java              ← Ponto de entrada: inicia o jogo
│
├── model/            ← Os dados do jogo
│   ├── Cell.java     ← Uma célula do tabuleiro (valor + se é fixa)
│   └── Board.java    ← O tabuleiro completo 9×9
│
├── logic/            ← As regras do jogo
│   ├── Validador.java  ← Verifica se linha, coluna ou bloco tem repetição
│   ├── Solver.java     ← Resolve o puzzle automaticamente (backtracking)
│   ├── Generator.java  ← Gera um novo tabuleiro aleatório
│   └── GameLogic.java  ← Regras da partida (vitória, cópia, limpeza)
│
├── ui/               ← Interação com o usuário
│   ├── ConsoleUI.java      ← Lê comandos do teclado, imprime o tabuleiro
│   └── GameController.java ← Orquestra o loop principal do jogo
│
└── util/             ← Ferramentas auxiliares
    ├── FileManager.java     ← Salva e carrega partidas em arquivo
    └── CommandTypeEnum.java ← Lista de todos os tipos de comando
```

---

## Como as camadas se comunicam

```
Usuário digita comando
       ↓
  ConsoleUI.readMove()         ← lê e interpreta o texto
       ↓
  GameController.run()         ← decide o que fazer com o comando
       ↓
  GameLogic / Solver / Generator / FileManager  ← executa a ação
       ↓
  Board / Cell                 ← atualiza os dados do tabuleiro
       ↓
  ConsoleUI.printBoard()       ← mostra o resultado para o usuário
```

---

## Como executar

```bash
# Rodar o jogo
.\gradlew.bat run

# Rodar os testes
.\gradlew.bat test
```

---

## Índice da documentação

| Documento | O que cobre |
|---|---|
| [camada-model.md](camada-model.md) | `Cell` e `Board` — os dados do tabuleiro |
| [camada-logic.md](camada-logic.md) | `Validador`, `Solver`, `Generator`, `GameLogic` |
| [camada-ui.md](camada-ui.md) | `ConsoleUI` e `GameController` |
| [camada-util.md](camada-util.md) | `FileManager` e `CommandTypeEnum` |
