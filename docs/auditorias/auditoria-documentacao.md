# Auditoria da documentação

## Escopo

Auditoria e refatoração da documentação ativa do projeto Sudoku em Java:

- `README.md`;
- `docs/README.md`;
- `docs/arquitetura/visao-geral.md`;
- `docs/referencia/camada-model.md`;
- `docs/referencia/camada-logic.md`;
- `docs/referencia/camada-ui.md`;
- `docs/referencia/camada-util.md`.

Fontes verificadas: código em `app/src/main/java`, testes em `app/src/test/java`, `settings.gradle.kts`, `app/build.gradle.kts` e execução de testes Gradle.

## Achados

| Severidade | Achado | Correção aplicada |
|---|---|---|
| Alta | O README dizia “validação em tempo real”, mas o controller não bloqueia conflitos de Sudoku por jogada. | README e docs de UI passaram a declarar que conflitos só são avaliados por vitória/solver. |
| Média | A documentação era Windows-cêntrica ao mostrar só `.\gradlew.bat`, apesar de existir `./gradlew`. | README e visão geral passaram a mostrar comandos para Linux/macOS/Git Bash e Windows. |
| Média | Não havia índice técnico nem ordem de autoridade documental em `docs/`. | Criado `docs/README.md` com mapa, autoridade e convenções de manutenção. |
| Média | Os documentos técnicos estavam todos soltos em `docs/`, misturando arquitetura, referência e auditoria. | Reorganizado em `docs/arquitetura/`, `docs/referencia/` e `docs/auditorias/`, com índices por subpasta. |
| Média | `remove` e `clear` estavam descritos como “desfazer jogadas”, termo que sugere pilha de undo inexistente. | Termos substituídos por remoção/limpeza de células não fixas. |
| Média | O valor `0` aceito pelo parser em jogadas não estava documentado. | README e docs de UI passaram a declarar que `0` esvazia célula não fixa. |
| Média | Limitações do `Solver` e `Generator` estavam dispersas ou suaves demais. | Docs de logic agora registram mutação do board, ausência de revalidação de board cheio e ausência de garantia de solução única. |
| Baixa | Persistência documentava o formato, mas não deixava claro que não há sandbox de diretório. | README e docs de util agora diferenciam rejeição de `..` de sandbox real. |

## Refatoração aplicada

- README reestruturado como guia de uso: requisitos, instalação, execução, testes, comandos, persistência, estrutura e links técnicos.
- Documentação técnica reorganizada por papel: índice (`docs/README.md`), arquitetura em `docs/arquitetura/`, referência em `docs/referencia/` e auditorias em `docs/auditorias/`.
- Contratos e limitações foram explicitados onde o comportamento do código é menos óbvio.
- A linguagem foi padronizada em português técnico, preservando literais de comandos e formatos.

## Verificação

Resultados registrados após a refatoração:

- `./gradlew test --console=plain`: `BUILD SUCCESSFUL in 557ms`.
- Checks de Markdown/H1/links após reorganização em subpastas: `checked 11 markdown files`; sem links quebrados e com exatamente um H1 por arquivo.
- `git diff --check`: passou sem saída.

## Pendências recomendadas

- Decidir se o comportamento de aceitar `0` em jogada comum é intencional. Se não for, ajuste código e testes; se for, mantenha documentado.
- Decidir se jogadas conflitantes devem ser bloqueadas imediatamente. Hoje o código aceita e só impede vitória.
- Avaliar sandbox real para arquivos salvos, se o projeto evoluir além de aplicação local de estudo.
