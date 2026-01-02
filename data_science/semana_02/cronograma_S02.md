# 📘 Organização da Semana 2 – Pipeline, Feature Engineering e Baseline

Após a compreensão do comportamento dos dados na primeira etapa, a **Semana 2** será dedicada a transformar esses aprendizados em um **pipeline funcional**, permitindo o início das primeiras previsões.

Nesta fase, a equipe de **Data Science** trabalhará para:

- Unificar o pré-processamento, garantindo que todas as transformações sejam consistentes e reutilizáveis  
- Criar as primeiras features derivadas, com base nos padrões identificados na EDA  
- Treinar modelos **baseline**, que servirão como referência inicial de performance  
- Desenvolver uma **API em Python**, capaz de receber um JSON de entrada e retornar uma previsão simples via endpoint `/predict`  
- Validar a integração com o time de Dev  

Ao final desta etapa, teremos o **primeiro pipeline operacional da equipe de Data Science**, pronto para testes e integração.

Assim como na semana anterior, o trabalho será distribuído em **dimensões independentes**, permitindo que todos os membros contribuam simultaneamente.

---

## 🛠️ DS1 – Pré-Processamento Unificado  
**(Limpeza, Encoding e Normalização) | Responsável: Ana**

### Tarefas
- Reaplicar as correções identificadas no Data Quality da Semana 1  
- Criar funções reutilizáveis para:
  - Tratamento de valores nulos  
  - Padronização de tipos  
  - Normalização de variáveis numéricas (MinMaxScaler ou StandardScaler)  
  - Encoding de variáveis categóricas (OneHotEncoder ou OrdinalEncoder)  
- Garantir que todas as transformações funcionem para toda a base  
- Gerar:
  - Um notebook explicativo  
  - Um script contendo as funções de pré-processamento  

### Questão principal
> ❓ **Quais transformações são indispensáveis para que os modelos recebam dados consistentes e previsíveis?**

---

## 🧩 DS2 – Feature Engineering Inicial  
**(Criação de Novas Variáveis) | Responsável: Amélia**

### Tarefas
- Criar features derivadas de tempo:
  - Faixa de horário (manhã, tarde, noite)  
  - Transformações no atraso previsto (log, caps, etc.)  
- Criar features relacionadas a companhia e aeroporto:
  - Frequência de voos por companhia  
  - Rotas mais utilizadas  
- Testar rapidamente o impacto de cada feature (correlação, separabilidade)  
- Documentar todas as features criadas  

### Questão principal
> ❓ **Quais novas variáveis parecem adicionar mais sinal para a predição de atraso?**

### Observações Importantes (DS1 e DS2)

O foco desta etapa **não é apenas preparar os dados**, mas garantir que **todo o pré-processamento seja reproduzível em produção**.

Como o modelo será integrado a uma **API**, ele **não receberá um DataFrame pronto**, mas sim **novos dados via JSON**.  
Por isso:

- Todas as transformações **devem estar dentro de um pipeline reutilizável**
- Transformações feitas manualmente no notebook **não devem ser usadas pelo modelo**
- Se a transformação **não estiver no pipeline, ela não deve ser usada**

Exemplo:
> A média de atraso por companhia aérea não pode ser criada apenas com um `groupby`.  
> Essa lógica deve estar encapsulada em um transformador que **aprenda no treino** e **seja reaplicável em novos dados**.

O mesmo princípio vale para:
- Tratamento de nulos  
- Encoding de variáveis categóricas  
- Normalização de variáveis numéricas  

Para esta semana, não é necessário nada avançado:  
o uso **básico de pipelines do scikit-learn** é suficiente para garantir consistência.

---

## ⚖️ DS3 – Balanceamento e Preparação do Dataset  
**Responsável: Enoque (com suporte da Helena, se necessário)**

### Tarefas
- Avaliar o desbalanceamento da variável alvo  
- Testar técnicas simples de balanceamento:
  - Undersampling  
  - Oversampling  
- Criar função para separação consistente de treino e teste  
- Documentar qual estratégia apresentou melhor comportamento no baseline  

### Questão principal
> ❓ **Qual estratégia de balanceamento preserva melhor os padrões reais dos dados sem gerar distorções e por quê?**

### Direcionamento Técnico

Nesta etapa, o foco é **preparar os dados para a modelagem**, utilizando o dataset **já transformado pelo pipeline**.

- Não criar novas regras de transformação  
- Trabalhar apenas com os dados prontos para o modelo  
- Avaliar o impacto do desbalanceamento nas métricas  
- Garantir divisão treino/teste **reproduzível e consistente**

O objetivo **não é maximizar performance**, mas garantir que o **modelo baseline funcione corretamente**, com resultados confiáveis para integração com a API.

---

## 🧪 DS5 – Validação Técnica do Baseline e Testes do JSON  
**Responsável: Helena**

### Tarefas
- Validar se o modelo baseline funciona corretamente com o JSON definido  
- Criar função de predição a partir de JSON  
- Testar com:
  - Exemplos reais  
  - Exemplos inválidos ou incompletos  

### Questão principal
> ❓ **O modelo baseline e o JSON de entrada são suficientemente robustos para evitar erros na API?**

---

## 🧭 Organização do Trabalho

As atividades foram organizadas em **cinco frentes distintas**, permitindo que cada integrante escolha a dimensão com a qual mais se identifica.

Para evitar sobreposição de esforços:
- Cada membro deve comunicar à equipe e à liderança qual dimensão assumirá **antes de iniciar**
- Após a definição das responsabilidades, seguimos com o cronograma normalmente

---

## 📅 Cronograma da Semana 2 – Datas Importantes

### 📌 Segunda-feira — 22/12  
**Reunião de planejamento semanal**
- Alinhamento das responsabilidades  
- Revisão do pipeline definido  
- Dúvidas técnicas e checklist dos arquivos necessários  
- Ajustes no cronograma, se necessário  

### 📌 Quinta-feira — 25/12  
**Demonstração das entregas**
- Apresentação das partes do pipeline  
- Validação do pré-processamento  
- Demonstração do baseline em funcionamento  

### 📌 Sexta-feira — 26/12  
**Consolidação e documentação**
- Consolidação do pipeline inicial  
- Padronização dos scripts e da estrutura de pastas  
- Ajustes finais da API e testes adicionais  

---

## 🔴 Observações Finais

- Planejadas apenas as reuniões obrigatórias  
- Encontros adicionais podem ser marcados conforme necessidade técnica  

### ⏰ Prazos de Entrega
- **DS1 e DS2**: até **quinta-feira às 17:00 (horário do Brasil)**  
- **DS3**: até **quinta-feira às 16:00 (horário do Brasil)**  
