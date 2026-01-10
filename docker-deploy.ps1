# ============================================================================
# Script de Deploy - FlightOnTime (Windows PowerShell)
# ============================================================================
# Automação de build, test e deploy da aplicação containerizada
# ============================================================================

$ErrorActionPreference = "Stop"

# Funções de output colorido
function Write-Info { Write-Host "ℹ️  $args" -ForegroundColor Blue }
function Write-Success { Write-Host "✅ $args" -ForegroundColor Green }
function Write-Warning { Write-Host "⚠️  $args" -ForegroundColor Yellow }
function Write-Error { Write-Host "❌ $args" -ForegroundColor Red }

# Banner
Write-Host "════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "   FlightOnTime - Container Management" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# Verifica Docker
try {
    $dockerVersion = docker --version
    $composeVersion = docker-compose --version
    Write-Success "Docker encontrado: $dockerVersion"
    Write-Success "Docker Compose encontrado: $composeVersion"
} catch {
    Write-Error "Docker ou Docker Compose não estão instalados!"
    exit 1
}

Write-Host ""

# Menu
do {
    Write-Host "════════════════════════════════════════════════" -ForegroundColor Cyan
    Write-Host "1.  Build e Start (completo)"
    Write-Host "2.  Start (containers existentes)"
    Write-Host "3.  Stop"
    Write-Host "4.  Restart"
    Write-Host "5.  Logs (todos)"
    Write-Host "6.  Logs (Java backend)"
    Write-Host "7.  Logs (Python API)"
    Write-Host "8.  Status"
    Write-Host "9.  Clean (remover tudo)"
    Write-Host "10. Test (health checks)"
    Write-Host "0.  Sair"
    Write-Host "════════════════════════════════════════════════" -ForegroundColor Cyan
    
    $choice = Read-Host "Escolha uma opção"
    
    switch ($choice) {
        "1" {
            Write-Info "Construindo imagens e iniciando containers..."
            docker-compose build --no-cache
            docker-compose up -d
            Write-Success "Aplicação iniciada!"
            Write-Info "Aguardando serviços ficarem prontos (30s)..."
            Start-Sleep -Seconds 30
            docker-compose ps
        }
        "2" {
            Write-Info "Iniciando containers..."
            docker-compose up -d
            Write-Success "Containers iniciados!"
            Start-Sleep -Seconds 10
            docker-compose ps
        }
        "3" {
            Write-Info "Parando containers..."
            docker-compose stop
            Write-Success "Containers parados!"
        }
        "4" {
            Write-Info "Reiniciando containers..."
            docker-compose restart
            Write-Success "Containers reiniciados!"
            Start-Sleep -Seconds 10
            docker-compose ps
        }
        "5" {
            Write-Info "Mostrando logs de todos os serviços (Ctrl+C para sair)..."
            docker-compose logs -f
        }
        "6" {
            Write-Info "Mostrando logs do backend Java (Ctrl+C para sair)..."
            docker-compose logs -f java-backend
        }
        "7" {
            Write-Info "Mostrando logs da API Python (Ctrl+C para sair)..."
            docker-compose logs -f python-api
        }
        "8" {
            Write-Info "Status dos containers:"
            docker-compose ps
            Write-Host ""
            Write-Info "Uso de recursos:"
            docker stats --no-stream
        }
        "9" {
            Write-Warning "Isso vai remover todos os containers, volumes e imagens!"
            $confirm = Read-Host "Tem certeza? (s/n)"
            if ($confirm -eq "s" -or $confirm -eq "S") {
                Write-Info "Removendo containers..."
                docker-compose down -v
                Write-Info "Removendo imagens..."
                docker rmi flightontime-backend:latest 2>$null
                docker rmi flightontime-python:latest 2>$null
                Write-Success "Tudo removido!"
            }
        }
        "10" {
            Write-Info "Testando health checks..."
            Write-Host ""
            
            Write-Host "Backend Java:" -ForegroundColor Yellow
            try {
                $response = Invoke-WebRequest -Uri "http://localhost:8080/api/health" -UseBasicParsing
                Write-Success "Backend Java: OK"
                $response.Content
            } catch {
                Write-Error "Backend Java: FALHOU"
            }
            Write-Host ""
            
            Write-Host "Python API:" -ForegroundColor Yellow
            try {
                $response = Invoke-WebRequest -Uri "http://localhost:5000/health" -UseBasicParsing
                Write-Success "Python API: OK"
                $response.Content
            } catch {
                Write-Error "Python API: FALHOU"
            }
            Write-Host ""
            
            Write-Info "Teste de predição..."
            $body = @{
                companhia = "AZ"
                origem = "GIG"
                destino = "GRU"
                data_partida = "2025-12-25T14:30:00"
                distancia_km = 350
            } | ConvertTo-Json
            
            try {
                $response = Invoke-RestMethod -Uri "http://localhost:8080/api/predict" `
                    -Method Post `
                    -ContentType "application/json" `
                    -Body $body
                $response | ConvertTo-Json -Depth 10
            } catch {
                Write-Error "Teste de predição falhou: $_"
            }
        }
        "0" {
            Write-Info "Até logo!"
            break
        }
        default {
            Write-Warning "Opção inválida!"
        }
    }
    
    Write-Host ""
    
} while ($choice -ne "0")

# ============================================================================
# Como usar:
# ============================================================================
# .\docker-deploy.ps1
# ============================================================================
