# 🚀 SEMANA 2: Integração Java ↔ Python

## 📋 Objetivo da Semana

Fazer a requisição completa: 
```
Postman → API Java → Microserviço Python → API Java → Postman
```

---

## 🎯 Squad A: Interface & Dados

**Responsáveis**: [Nome] e [Nome]

### ✅ Tarefas Prontas (já implementadas pelo Tech Lead)

#### 1. Mappers IATA → ICAO
Já criadas as classes:
- `AirportCodeMapper.java` - Converte aeroportos (GRU → SBGR)
- `AirlineCodeMapper.java` - Converte companhias (G3 → GLO)

**Localização**: `src/main/java/com/flightontime/api/mapper/`

**Como usar**:
```java
@Autowired
private AirportCodeMapper airportMapper;

String icao = airportMapper.toIcao("GRU"); // Retorna "SBGR"
```

#### 2. DTOs para Python
Já criados:
- `PythonPredictionRequest.java` - Envia para Python
- `PythonPredictionResponse.java` - Recebe do Python

**Localização**: `src/main/java/com/flightontime/api/dto/`

⚠️ **ATENÇÃO**: Esses DTOs devem estar EXATAMENTE iguais ao que o time DS espera!

### 🔨 O que vocês precisam fazer:

#### Tarefa 1: Validar Mappers
- Revisar os códigos IATA/ICAO nos mappers
- Adicionar mais aeroportos/companhias se necessário
- Criar testes unitários para os mappers

**Exemplo de teste**:
```java
@Test
void deveConverterGRUparaICAO() {
    String icao = airportMapper.toIcao("GRU");
    assertEquals("SBGR", icao);
}
```

#### Tarefa 2: Sincronizar com DS
- Confirmar com o time Python o formato EXATO do JSON
- Validar campos do `PythonPredictionRequest`
- Validar campos do `PythonPredictionResponse`

**JSON esperado (REQUEST)**:
```json
{
  "companhia_icao": "GLO",
  "origem_icao": "SBGR",
  "destino_icao": "SBGL",
  "data_partida": "2025-11-10T14:30:00",
  "distancia_km": 350
}
```

**JSON esperado (RESPONSE)**:
```json
{
  "previsao": "Atrasado",
  "probabilidade": 0.78,
  "modelo_versao": "v1.0"
}
```

---

## 🔗 Squad B: Integração & Core

**Responsáveis**: [Nome] e [Nome]

### ✅ Tarefas Prontas (já implementadas pelo Tech Lead)

#### 1. Client HTTP
Já criada a classe `PythonPredictionClient.java`
- Usa `RestTemplate` para fazer POST
- Trata erros HTTP
- Logs detalhados para debugging

**Localização**: `src/main/java/com/flightontime/api/client/`

#### 2. Configuração do RestTemplate
Já criado o Bean em `RestTemplateConfig.java`

**Localização**: `src/main/java/com/flightontime/api/config/`

#### 3. Variáveis de Ambiente
Já configurado no `application.properties`:
```properties
prediction.service.url=http://localhost:5000
prediction.service.use-mock=true
```

### 🔨 O que vocês precisam fazer:

#### Tarefa 1: Testar Client HTTP
- Quando o Python estiver pronto, mudar `use-mock=false`
- Testar chamada real ao microserviço
- Validar tratamento de erros (timeout, 404, 500, etc.)

**Como testar**:
1. Subir o serviço Python
2. Editar `application.properties`: `prediction.service.use-mock=false`
3. Fazer requisição via Postman
4. Verificar logs no console

#### Tarefa 2: Implementar Health Check
- Adicionar endpoint `/health` na API Java
- Verificar se o serviço Python está UP
- Útil para monitoramento

**Sugestão**:
```java
@GetMapping("/health")
public Map<String, Object> health() {
    boolean pythonUp = pythonClient.isHealthy();
    return Map.of(
        "status", "UP",
        "pythonService", pythonUp ? "UP" : "DOWN"
    );
}
```

#### Tarefa 3: Melhorar tratamento de erros
- Criar exceções customizadas (ex: `PythonServiceUnavailableException`)
- Retornar mensagens amigáveis para o usuário
- Implementar retry logic (opcional)

---

## 🎭 Estratégia de Transição: Mock vs Python

A API está configurada para funcionar em **dois modos**:

### Modo 1: MOCK (Semana 1)
```properties
prediction.service.use-mock=true
```
- Usa lógica local (heurísticas)
- Não depende do Python
- Ideal para desenvolvimento inicial

### Modo 2: PYTHON (Semana 2)
```properties
prediction.service.use-mock=false
```
- Chama o microserviço Python
- Previsão real com ML
- Se Python falhar → Fallback automático para MOCK

---

## ⚠️ Plano B: Python Atrasado?

**Não tem problema!** O Tech Lead vai subir um Mock Server Python simples:

```python
# mock_server.py
from flask import Flask, request, jsonify
app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    data = request.json
    return jsonify({
        "previsao": "Atrasado",
        "probabilidade": 0.75,
        "modelo_versao": "mock-v1.0"
    })

@app.route('/health', methods=['GET'])
def health():
    return jsonify({"status": "UP"})

if __name__ == '__main__':
    app.run(port=5000)
```

Rodar: `python mock_server.py`

---

## 🧪 Como Testar a Integração

### 1. Teste com MOCK (não precisa Python)
```bash
# application.properties
prediction.service.use-mock=true
```

**Requisição no Postman**:
```json
POST http://localhost:8080/api/flights/predict

{
  "companhia": "G3",
  "origem": "GRU",
  "destino": "GIG",
  "data_partida": "2025-12-25T14:30:00",
  "distancia_km": 350
}
```

### 2. Teste com Python Real
```bash
# 1. Subir Python (porta 5000)
cd python-service
python app.py

# 2. Configurar Java
prediction.service.use-mock=false

# 3. Testar via Postman
```

---

## 📊 Checklist de Entrega

### Squad A
- [ ] Validar todos os códigos IATA/ICAO
- [ ] Confirmar formato JSON com DS
- [ ] Testes unitários dos Mappers
- [ ] Documentação dos códigos suportados

### Squad B
- [ ] Testar chamada ao Python real
- [ ] Implementar health check
- [ ] Validar tratamento de erros
- [ ] Logs detalhados funcionando

### Integração Final
- [ ] Requisição completa: Postman → Java → Python → Java → Postman
- [ ] Status 200 OK funcionando
- [ ] Fallback para mock funcionando
- [ ] Logs claros em todo o fluxo

---

## 🆘 Dúvidas?

**Tech Lead**: [Seu Nome]
**Canal**: #squad-java-python

Vamos pra cima! 💪
