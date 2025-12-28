# 📘 Semana 1 – EDA e Estruturação dos Dados

Para iniciarmos o projeto, será disponibilizado o dataset completo contendo os dados de **2020 a 2025** com os voos operados pela **ANAC**, já com a variável-alvo previamente definida.

A missão da equipe de **Data Science** nesta primeira semana será:

- Compreender o comportamento dos dados  
- Responder perguntas específicas  
- Consolidar um panorama estruturado para o início da modelagem  

Para garantir produtividade e independência dos membros, os dados foram divididos em **cinco frentes de análise**, cada uma correspondente a uma dimensão diferente do problema.

Cada integrante deverá:
- Criar um **notebook individual**
- Executar as tarefas definidas para a dimensão escolhida
- Produzir **ao menos um gráfico** e **um insight** que responda à pergunta central da dimensão

---

## 🛠️ DS1 – Qualidade e Estrutura dos Dados (Data Quality)

### Tarefas
- Verificar os tipos de dados de cada coluna  
- Identificar valores ausentes e calcular sua porcentagem  
- Detectar valores inválidos, inconsistentes e outliers  
- Criar uma tabela **Data Quality Report** contendo:
  - Nome da coluna  
  - Tipo  
  - % de valores nulos  
  - Problemas encontrados  
  - Possíveis correções  
- Descrever quais colunas exigirão limpeza futura  

### Questão principal
> ❓ **Quais problemas de qualidade podem comprometer o desempenho do modelo?**

---

## 📊 DS2 – Distribuições e Comportamento das Variáveis (Ana)

### Tarefas
- Identificar as variáveis numéricas  
- Criar histogramas para variáveis numéricas  
- Criar boxplots para análise de variação e outliers  
- Comparar valores de atraso vs. pontualidade  
- Observar padrões de concentração, assimetria e dispersão  

### Questão principal
> ❓ **Quais variáveis apresentam padrões que ajudam a diferenciar voos atrasados de pontuais?**

---

## 🧩 DS3 – Correlações e Relações Entre Variáveis (Ana)

### Tarefas
- Calcular correlação entre variáveis numéricas  
- Construir heatmap de correlação  
- Identificar variáveis redundantes (alta colinearidade)  
- Destacar variáveis com maior relação com atraso  

### Questão principal
> ❓ **Quais variáveis têm maior potencial para explicar os atrasos?**

---

## ⏱️ DS4 – Sazonalidade (Tempo: Mês, Dia, Horário) (Amélia)

### Tarefas
- Calcular atraso médio por mês do ano  
- Calcular atraso por dia da semana  
- Calcular atraso por horário do dia  
- Criar gráficos de linha para visualizar tendências temporais  
- Identificar picos de demanda e efeitos de sazonalidade  

### Questão principal
> ❓ **Em quais períodos do ano, meses ou horários os atrasos são mais frequentes e por quê?**

---

## ✈️ DS5 – Segmentação (Companhias e Aeroportos) (Amélia)

### Tarefas
- Criar ranking das companhias com maior taxa de atraso  
- Criar ranking dos aeroportos com mais atrasos (origem e destino)  
- Comparar desempenho entre diferentes regiões do país  
- Identificar gargalos e padrões específicos em companhias e aeroportos  

### Questão principal
> ❓ **Quais companhias e aeroportos mais contribuem para os atrasos? Eles apresentam padrões específicos?**

---

## 🧭 Organização do Trabalho

As atividades desta semana foram organizadas em **cinco dimensões distintas**, cada uma com tarefas bem definidas.

As frentes **não foram atribuídas previamente**, permitindo que cada integrante escolha aquela com a qual mais se identifica — seja por afinidade, curiosidade ou estratégia de análise.

Para evitar sobreposição de esforços:
- Cada membro deve **comunicar à equipe e à liderança** qual dimensão pretende assumir **antes de iniciar**
- Após a definição das escolhas, o cronograma segue normalmente, garantindo cobertura completa e colaboração efetiva

---

## 🗓️ Cronograma da Semana 1

### 📌 Segunda-feira — 15/12
**Reunião de alinhamento inicial**
- Escolha das dimensões e ajustes nas tarefas, se necessário  
- Definição dos critérios de padronização dos notebooks  
- Esclarecimento de dúvidas técnicas  

### 📌 Quinta-feira — 18/12
**Apresentação dos achados individuais**
- Apresentação dos notebooks por cada integrante  
- Discussão coletiva dos padrões encontrados  
- Identificação de features potenciais  

### 📌 Sexta-feira — 19/12
**Consolidação e documentação**
- Unificação dos insights  
- Padronização das variáveis relevantes  
- Criação da documentação formal para entrega na plataforma  

---

## 🔴 Observação

O planejamento contempla apenas as **sprints obrigatórias**.  
Reuniões ou atividades adicionais podem ser realizadas ao longo da semana, conforme a necessidade da equipe.
