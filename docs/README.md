# Documentação do projeto

Este diretório contém a documentação técnica ativa do Sudoku em Java.

## Ordem de autoridade

Quando houver conflito, use esta ordem:

1. código e testes em `app/src/`;
2. documentação de camada em `docs/referencia/`;
3. visão geral em `docs/arquitetura/`;
4. `README.md`, que é guia de uso e entrada do projeto;
5. relatórios em `docs/auditorias/`, que registram diagnóstico e manutenção documental.

## Organização

```text
docs/
├─ README.md                       # índice e convenções
├─ arquitetura/                    # visão explicativa do desenho do sistema
├─ referencia/                     # contratos técnicos por camada/classe
└─ auditorias/                     # relatórios de auditoria e manutenção documental
```

## Mapa dos documentos

| Documento | Papel | Leitor principal |
|---|---|---|
| [../README.md](../README.md) | Guia de instalação, execução, comandos e links técnicos. | Usuário e avaliador do projeto |
| [arquitetura/README.md](arquitetura/README.md) | Índice da documentação de arquitetura. | Desenvolvedor novo no código |
| [arquitetura/visao-geral.md](arquitetura/visao-geral.md) | Explicação da arquitetura e fluxo entre camadas. | Desenvolvedor novo no código |
| [referencia/README.md](referencia/README.md) | Índice da referência técnica por camada. | Desenvolvedor |
| [referencia/camada-model.md](referencia/camada-model.md) | Referência das entidades `Cell` e `Board`. | Desenvolvedor |
| [referencia/camada-logic.md](referencia/camada-logic.md) | Referência das regras, solver e geração de puzzles. | Desenvolvedor |
| [referencia/camada-ui.md](referencia/camada-ui.md) | Referência da interface de console e do controller. | Desenvolvedor |
| [referencia/camada-util.md](referencia/camada-util.md) | Referência de persistência e tipos de comando. | Desenvolvedor |
| [auditorias/README.md](auditorias/README.md) | Índice dos relatórios de auditoria. | Mantenedor |
| [auditorias/auditoria-documentacao.md](auditorias/auditoria-documentacao.md) | Registro da auditoria documental e pendências. | Mantenedor |

## Convenções de manutenção

- Não documente comportamento desejado como se já existisse. Se o código não faz, marque como limitação.
- Preserve literais do código quando citar comandos, status e formatos (`SUDOKU_SAVE_V1`, `COMPLETO`, `INCOMPLETO`).
- Use português técnico com acentos no texto explicativo; mantenha sem acentos apenas mensagens exatamente como o programa imprime.
- Não duplique blocos longos de código-fonte. Cite classes, métodos e regras de contrato.
- Atualize README, índice da subpasta e documento de camada na mesma alteração quando um comando, formato ou contrato público mudar.
