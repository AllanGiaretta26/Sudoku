# Camada util

A camada `util` reúne componentes auxiliares usados por outras camadas: persistência de partidas e enumeração de comandos.

## Classes

| Classe | Papel |
|---|---|
| `FileManager` | Salva e carrega o estado do tabuleiro em arquivo texto. |
| `CommandTypeEnum` | Lista os tipos de comando reconhecidos pela UI. |

## `FileManager`

`FileManager` persiste partidas em UTF-8 usando `java.nio.file.Files`.

### Formato `SUDOKU_SAVE_V1`

```text
SUDOKU_SAVE_V1
v:f,v:f,v:f,v:f,v:f,v:f,v:f,v:f,v:f
v:f,v:f,v:f,v:f,v:f,v:f,v:f,v:f,v:f
...
```

Regras:

- linha 1: cabeçalho obrigatório `SUDOKU_SAVE_V1`;
- linhas 2 a 10: 9 linhas do tabuleiro;
- cada linha: 9 células separadas por vírgula;
- cada célula: `valor:fixo`;
- `valor`: inteiro de `0` a `9`;
- `fixo`: `1` para célula fixa, `0` para célula editável.

### API pública

| Método | Contrato |
|---|---|
| `saveGame(Board board, String filePath)` | Rejeita `board == null`, caminho vazio e caminho contendo `..`; cria diretórios pais e grava UTF-8. |
| `loadGame(String filePath)` | Rejeita caminho vazio e caminho contendo `..`; exige arquivo existente e formato estrito. |

### Validações de leitura

`loadGame()` verifica:

- existência do arquivo;
- quantidade exata de 10 linhas;
- cabeçalho correto;
- 9 células por linha;
- separador `:` em cada célula;
- números parseáveis;
- `valor` entre `0..9`;
- `fixo` igual a `0` ou `1`.

Falhas de formato são reportadas por `IOException` com mensagem descritiva.

### Limitações de caminho

`validatePath()` apenas rejeita a substring `..`. Isso reduz path traversal simples, mas não implementa sandbox. Caminhos absolutos sem `..` continuam possíveis, dependendo do sistema operacional e permissões. Para uso normal, prefira caminhos relativos como `saves/partida.txt`.

## `CommandTypeEnum`

| Constante | Comando associado |
|---|---|
| `PLAY` | `linha coluna valor` |
| `SAVE` | `save arquivo.txt` |
| `LOAD` | `load arquivo.txt` |
| `NEW_BOARD` | `new` |
| `STATUS` | `status` |
| `COMPLETE` | `complete` |
| `REMOVE` | `remove linha coluna` |
| `CLEAR` | `clear` |
| `HELP` | `help` |
| `QUIT` | `q` |

Adicionar um comando novo exige alterar pelo menos três pontos: `CommandTypeEnum`, fábricas/predicados de `ConsoleUI.Move` e despacho em `GameController`.
