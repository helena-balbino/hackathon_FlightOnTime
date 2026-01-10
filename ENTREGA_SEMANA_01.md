# 📘 Entrega Semana 1 – Análise Exploratória de Dados (EDA)

## 📌 Resumo Executivo

Na primeira semana do projeto **FlightOnTime**, focamos na compreensão profunda dos dados históricos de voos da ANAC (2020-2025) através de uma Análise Exploratória de Dados (EDA) abrangente. O objetivo foi identificar padrões, detectar problemas de qualidade e gerar insights que fundamentarão as decisões de modelagem nas próximas fases.

---

## 🎯 Objetivos da Semana

1. **Compreender o comportamento dos dados** de voos históricos
2. **Identificar problemas de qualidade** que possam impactar a modelagem
3. **Descobrir padrões e correlações** relacionados a atrasos
4. **Analisar sazonalidades** temporais (mês, dia, horário)
5. **Segmentar dados** por companhias aéreas e aeroportos
6. **Preparar uma base sólida** para a fase de modelagem

---

## 📊 Dataset Utilizado

### Características Gerais
- **Período**: 2020 a 2025
- **Fonte**: ANAC (Agência Nacional de Aviação Civil)
- **Tipo**: Dados históricos de voos operados no Brasil
- **Variável Alvo**: Status do voo (Pontual/Atrasado) - já pré-definida

### Principais Variáveis Analisadas
- **Temporais**: Data/hora de partida, dia da semana, mês, horário
- **Operacionais**: Companhia aérea, aeroporto de origem/destino
- **Físicas**: Distância percorrida, tipo de aeronave
- **Alvo**: Indicador de atraso (variável binária)

---

## 🛠️ Metodologia de Trabalho

### Divisão em Frentes de Análise

O trabalho foi organizado em **cinco dimensões independentes**, permitindo que a equipe trabalhasse em paralelo:

#### **DS1 – Qualidade e Estrutura dos Dados (Data Quality)**
- Verificação de tipos de dados
- Identificação de valores ausentes e cálculo de percentuais
- Detecção de outliers e inconsistências
- Criação de Data Quality Report

#### **DS2 – Distribuições e Comportamento das Variáveis**
- Análise de variáveis numéricas via histogramas
- Criação de boxplots para outliers
- Comparação entre voos atrasados vs. pontuais
- Identificação de padrões de concentração e dispersão

#### **DS3 – Correlações e Relações Entre Variáveis**
- Cálculo de matriz de correlação
- Construção de heatmap
- Identificação de variáveis redundantes
- Destaque de variáveis com maior relação com atraso

#### **DS4 – Sazonalidade (Tempo: Mês, Dia, Horário)**
- Análise de atraso médio por mês
- Análise de atraso por dia da semana
- Análise de atraso por horário do dia
- Identificação de picos de demanda

#### **DS5 – Segmentação (Companhias e Aeroportos)**
- Ranking de companhias com maior taxa de atraso
- Ranking de aeroportos problemáticos
- Comparação regional
- Identificação de gargalos operacionais

---

## 📈 Principais Descobertas e Insights

### 1. Qualidade dos Dados

**Problemas Identificados:**
- Valores nulos em colunas específicas (percentuais documentados)
- Inconsistências em códigos de aeroportos
- Outliers em variáveis de tempo e distância
- Tipos de dados inadequados em algumas colunas

**Ações Recomendadas:**
- Tratamento de valores ausentes por imputação ou remoção
- Padronização de códigos IATA
- Normalização de variáveis numéricas
- Encoding adequado de variáveis categóricas

### 2. Distribuição das Variáveis

**Padrões Observados:**
- Distribuição assimétrica em variáveis de distância e atraso
- Concentração de voos em horários comerciais
- Presença de outliers significativos em tempos de atraso
- Diferenças claras entre distribuições de voos pontuais vs. atrasados

### 3. Correlações Relevantes

**Variáveis com Maior Correlação com Atraso:**
- Horário de partida (voos noturnos mais propensos)
- Distância (voos longos apresentam mais variabilidade)
- Companhia aérea (diferenças operacionais)
- Aeroporto de origem (infraestrutura impacta)

**Variáveis Redundantes:**
- Identificadas variáveis com alta colinearidade
- Recomendação para remoção ou combinação na fase de feature engineering

### 4. Sazonalidade Temporal

**Por Mês:**
- Picos de atraso em meses de alta temporada (dezembro, janeiro, julho)
- Menor taxa de atraso em meses de baixa demanda

**Por Dia da Semana:**
- Segundas e sextas-feiras apresentam mais atrasos
- Fins de semana com melhor pontualidade

**Por Horário:**
- Voos matutinos (6h-9h) mais pontuais
- Voos noturnos (após 20h) com maior taxa de atraso
- Efeito cascata ao longo do dia

### 5. Segmentação Operacional

**Companhias Aéreas:**
- Identificação das 3 companhias com maior taxa de atraso
- Diferenças significativas entre operadores low-cost e tradicionais
- Padrões específicos por tamanho de frota

**Aeroportos:**
- Aeroportos de hub apresentam mais congestionamento
- Aeroportos regionais com melhor pontualidade
- Diferenças entre aeroportos de origem e destino

**Análise Regional:**
- Regiões metropolitanas com mais problemas
- Influência de condições climáticas regionais

---

## 📁 Entregáveis

### Documentação
- ✅ Cronograma detalhado da Semana 1
- ✅ Notebooks individuais por dimensão de análise
- ✅ Data Quality Report consolidado
- ✅ Relatório de insights e recomendações

### Artefatos Técnicos
- ✅ Notebook consolidado de EDA (`S01_Consolidado_ETL_EDA.ipynb`)
- ✅ Gráficos e visualizações por dimensão
- ✅ Estatísticas descritivas completas
- ✅ Matriz de correlação

### Insights Documentados
- ✅ Perguntas respondidas por dimensão
- ✅ Recomendações para pré-processamento
- ✅ Sugestões de features para criação
- ✅ Identificação de variáveis críticas

---

## 🎓 Aprendizados e Conclusões

### Principais Conclusões

1. **Dataset é viável** para modelagem preditiva de atrasos
2. **Qualidade dos dados** requer tratamento específico antes da modelagem
3. **Padrões claros** foram identificados em múltiplas dimensões
4. **Sazonalidade temporal** é forte indicador de atrasos
5. **Companhias e aeroportos** têm impacto significativo

### Próximos Passos (Semana 2)

1. Implementar pipeline de pré-processamento baseado nos achados
2. Criar features derivadas a partir dos insights descobertos
3. Aplicar técnicas de balanceamento para a variável alvo
4. Treinar modelos baseline para estabelecer referência
5. Desenvolver API Python para integração

---

## 🔗 Referências e Recursos

### Notebooks Desenvolvidos
- `data_science/semana_01/notebooks/S01_Consolidado_ETL_EDA.ipynb`

### Documentação Relacionada
- `data_science/semana_01/cronograma_S01.md` - Planejamento detalhado
- `data_science/README.md` - Estrutura geral do projeto DS

### Ferramentas Utilizadas
- **Python 3.x** - Linguagem de programação
- **Pandas** - Manipulação de dados
- **NumPy** - Operações numéricas
- **Matplotlib/Seaborn** - Visualizações
- **Scikit-learn** - Estatísticas e análises

---

## 👥 Equipe Data Science

**Organização do Trabalho:**
- Trabalho distribuído em 5 dimensões independentes
- Cada membro escolheu sua frente de análise
- Colaboração através de notebooks individuais
- Consolidação final dos resultados

**Responsabilidades:**
- DS1 - Qualidade de Dados
- DS2 - Distribuições (Ana)
- DS3 - Correlações (Ana)
- DS4 - Sazonalidade (Amélia)
- DS5 - Segmentação (Amélia)

---

## 📊 Métricas de Entrega

| Métrica | Status | Observação |
|---------|--------|------------|
| Notebooks Individuais | ✅ Completo | 5 dimensões cobertas |
| Data Quality Report | ✅ Completo | Problemas identificados |
| Gráficos e Visualizações | ✅ Completo | Mínimo 1 por dimensão |
| Insights Documentados | ✅ Completo | Perguntas respondidas |
| Recomendações para S02 | ✅ Completo | Pipeline definido |

---

## 📅 Cronograma Cumprido

- [x] Divisão de tarefas por dimensão
- [x] Análise exploratória completa
- [x] Identificação de problemas de qualidade
- [x] Geração de visualizações
- [x] Documentação de insights
- [x] Consolidação de resultados
- [x] Preparação para Semana 2

---

**Data de Conclusão**: Semana 1 do Projeto  
**Status**: ✅ **CONCLUÍDO**  
**Próxima Etapa**: Semana 2 - Pipeline e Feature Engineering
