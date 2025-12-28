# Data Science

Este diretório contém todos os arquivos desenvolvidos pelo time de Data Science
para apoiar análises, experimentações e a integração com o backend/API.

O objetivo desta pasta é manter o trabalho de Data Science **organizado, isolado e
fácil de integrar**, sem impactar a estrutura principal da aplicação.

---

## 📂 Estrutura de Pastas

```text
data_science/
├── README.md
├── semana_01/
│   ├── notebooks/
│   ├── outputs/
│   └── cronograma.md
├── semana_02/
├── semana_03/
├── semana_04/
├── semana_05/
└── shared/
```

---

## 📁 Descrição das Pastas

### `semana_XX/`
Contém todas as entregas relacionadas a uma semana ou sprint específica do projeto.

Cada pasta de semana pode conter documentação própria, notebooks, scripts e
artefatos gerados durante o desenvolvimento.

---

### `notebooks/`
Notebooks Jupyter utilizados para:
- Análise exploratória de dados (EDA)
- Validações
- Testes e experimentações

Os notebooks devem ser utilizados prioritariamente para **exploração**, não para
integração direta com a API.

---

### `scripts/`
Scripts Python prontos para uso, contendo:
- Processamento de dados
- Feature engineering
- Funções utilitárias e reutilizáveis

Esta pasta é destinada a código **mais estável**, que pode ser reutilizado ou
integrado ao backend.

---

### `outputs/`
Resultados leves gerados durante as análises, como:
- Arquivos CSV
- Logs
- Artefatos intermediários

> ⚠️ Não incluir datasets grandes ou arquivos sensíveis.

---

### `shared/`
Contém funções, utilitários e componentes reutilizáveis entre diferentes semanas
ou sprints do projeto.

---

## 🔗 Integração com Backend / API

Todo artefato destinado à integração com o backend ou API deve:

- Estar claramente documentado
- Ser determinístico e reproduzível
- Estar localizado preferencialmente na pasta `scripts/`

Dependências ou requisitos específicos de integração devem ser documentados
no `README.md` da respectiva semana/sprint.

---

## 👥 Responsabilidade

Esta pasta é mantida pelo **time de Data Science**.

Todas as alterações devem seguir o mesmo fluxo de versionamento Git adotado
no projeto principal.
