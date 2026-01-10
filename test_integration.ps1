# Script de Teste - Integracao Java <-> Python

Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "TESTE DE INTEGRACAO COMPLETA" -ForegroundColor Cyan
Write-Host "============================================`n" -ForegroundColor Cyan

# Teste 1: Health Check - Java API
Write-Host "Teste 1: Java API Health Check" -ForegroundColor Yellow
try {
    $javaHealth = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method GET
    Write-Host "OK Java API UP na porta 8080" -ForegroundColor Green
} catch {
    Write-Host "ERRO Java API nao responde" -ForegroundColor Red
    exit 1
}

# Teste 2: Health Check - Python API
Write-Host "`nTeste 2: Python API Health Check" -ForegroundColor Yellow
try {
    $pythonHealth = Invoke-RestMethod -Uri "http://localhost:5000/health" -Method GET
    if ($pythonHealth.modelo_carregado) {
        Write-Host "OK Python API UP com modelo (v$($pythonHealth.version))" -ForegroundColor Green
    } else {
        Write-Host "OK Python API UP em modo MOCK (v$($pythonHealth.version))" -ForegroundColor Green
    }
} catch {
    Write-Host "ERRO Python API nao responde" -ForegroundColor Red
    exit 1
}

# Teste 3: Voo Diurno (esperado: Pontual)
Write-Host "`nTeste 3: Previsao Voo Diurno GRU->GIG 10:30" -ForegroundColor Yellow
$request1 = @{
    companhia_icao = "GLO"
    origem_icao = "SBGR"
    destino_icao = "SBGL"
    data_partida = "2025-12-25T10:30:00"
    distancia_km = 350
} | ConvertTo-Json

try {
    $response1 = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/flights/predict" -Method POST -Body $request1 -ContentType "application/json"
    Write-Host "Previsao: $($response1.previsao) ($([math]::Round($response1.probabilidade * 100, 0))%) - $($response1.modelo_versao)" -ForegroundColor White
} catch {
    Write-Host "ERRO: $($_.Exception.Message)" -ForegroundColor Red
}

# Teste 4: Voo Noturno (esperado: Atrasado)
Write-Host "`nTeste 4: Previsao Voo Noturno CGH->BSB 19:30" -ForegroundColor Yellow
$request2 = @{
    companhia_icao = "AZU"
    origem_icao = "SBSP"
    destino_icao = "SBBR"
    data_partida = "2025-12-25T19:30:00"
    distancia_km = 870
} | ConvertTo-Json

try {
    $response2 = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/flights/predict" -Method POST -Body $request2 -ContentType "application/json"
    Write-Host "Previsao: $($response2.previsao) ($([math]::Round($response2.probabilidade * 100, 0))%) - $($response2.modelo_versao)" -ForegroundColor White
} catch {
    Write-Host "ERRO: $($_.Exception.Message)" -ForegroundColor Red
}

# Teste 5: Voo Internacional
Write-Host "`nTeste 5: Previsao Voo Longa Distancia GRU->MIA 23:45" -ForegroundColor Yellow
$request3 = @{
    companhia_icao = "GLO"
    origem_icao = "SBGR"
    destino_icao = "KMIA"
    data_partida = "2025-12-25T23:45:00"
    distancia_km = 6580
} | ConvertTo-Json

try {
    $response3 = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/flights/predict" -Method POST -Body $request3 -ContentType "application/json"
    Write-Host "Previsao: $($response3.previsao) ($([math]::Round($response3.probabilidade * 100, 0))%) - $($response3.modelo_versao)" -ForegroundColor White
} catch {
    Write-Host "ERRO: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "TESTES CONCLUIDOS!" -ForegroundColor Green
Write-Host "Fluxo: Postman -> Java (8080) -> Python (5000) -> Java -> Postman" -ForegroundColor Yellow
Write-Host "============================================`n" -ForegroundColor Cyan
