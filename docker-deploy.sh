#!/bin/bash
# ============================================================================
# Script de Deploy - FlightOnTime
# ============================================================================
# Automação de build, test e deploy da aplicação containerizada
# ============================================================================

set -e  # Exit on error

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Funções de output
info() { echo -e "${BLUE}ℹ️  $1${NC}"; }
success() { echo -e "${GREEN}✅ $1${NC}"; }
warning() { echo -e "${YELLOW}⚠️  $1${NC}"; }
error() { echo -e "${RED}❌ $1${NC}"; exit 1; }

# Banner
echo "════════════════════════════════════════════════"
echo "   FlightOnTime - Container Management"
echo "════════════════════════════════════════════════"
echo ""

# Verifica Docker
if ! command -v docker &> /dev/null; then
    error "Docker não está instalado!"
fi

if ! command -v docker-compose &> /dev/null; then
    error "Docker Compose não está instalado!"
fi

success "Docker encontrado: $(docker --version)"
success "Docker Compose encontrado: $(docker-compose --version)"
echo ""

# Menu
PS3='Escolha uma opção: '
options=(
    "Build e Start (completo)"
    "Start (containers existentes)"
    "Stop"
    "Restart"
    "Logs (todos)"
    "Logs (Java backend)"
    "Logs (Python API)"
    "Status"
    "Clean (remover tudo)"
    "Test (health checks)"
    "Sair"
)

select opt in "${options[@]}"
do
    case $opt in
        "Build e Start (completo)")
            info "Construindo imagens e iniciando containers..."
            docker-compose build --no-cache
            docker-compose up -d
            success "Aplicação iniciada!"
            info "Aguardando serviços ficarem prontos (30s)..."
            sleep 30
            docker-compose ps
            ;;
        "Start (containers existentes)")
            info "Iniciando containers..."
            docker-compose up -d
            success "Containers iniciados!"
            sleep 10
            docker-compose ps
            ;;
        "Stop")
            info "Parando containers..."
            docker-compose stop
            success "Containers parados!"
            ;;
        "Restart")
            info "Reiniciando containers..."
            docker-compose restart
            success "Containers reiniciados!"
            sleep 10
            docker-compose ps
            ;;
        "Logs (todos)")
            info "Mostrando logs de todos os serviços (Ctrl+C para sair)..."
            docker-compose logs -f
            ;;
        "Logs (Java backend)")
            info "Mostrando logs do backend Java (Ctrl+C para sair)..."
            docker-compose logs -f java-backend
            ;;
        "Logs (Python API)")
            info "Mostrando logs da API Python (Ctrl+C para sair)..."
            docker-compose logs -f python-api
            ;;
        "Status")
            info "Status dos containers:"
            docker-compose ps
            echo ""
            info "Uso de recursos:"
            docker stats --no-stream
            ;;
        "Clean (remover tudo)")
            warning "Isso vai remover todos os containers, volumes e imagens!"
            read -p "Tem certeza? (y/n) " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                info "Removendo containers..."
                docker-compose down -v
                info "Removendo imagens..."
                docker rmi flightontime-backend:latest 2>/dev/null || true
                docker rmi flightontime-python:latest 2>/dev/null || true
                success "Tudo removido!"
            fi
            ;;
        "Test (health checks)")
            info "Testando health checks..."
            echo ""
            
            echo "Backend Java:"
            if curl -f -s http://localhost:8080/api/health > /dev/null; then
                success "Backend Java: OK"
                curl -s http://localhost:8080/api/health | jq '.' 2>/dev/null || curl -s http://localhost:8080/api/health
            else
                error "Backend Java: FALHOU"
            fi
            echo ""
            
            echo "Python API:"
            if curl -f -s http://localhost:5000/health > /dev/null; then
                success "Python API: OK"
                curl -s http://localhost:5000/health | jq '.' 2>/dev/null || curl -s http://localhost:5000/health
            else
                error "Python API: FALHOU"
            fi
            echo ""
            
            info "Teste de predição..."
            curl -X POST http://localhost:8080/api/predict \
              -H "Content-Type: application/json" \
              -d '{
                "companhia": "AZ",
                "origem": "GIG",
                "destino": "GRU",
                "data_partida": "2025-12-25T14:30:00",
                "distancia_km": 350
              }' | jq '.' 2>/dev/null || echo "⚠️ jq não instalado (resposta raw acima)"
            ;;
        "Sair")
            info "Até logo!"
            break
            ;;
        *) 
            warning "Opção inválida $REPLY"
            ;;
    esac
    echo ""
done

# ============================================================================
# Como usar:
# ============================================================================
# chmod +x docker-deploy.sh
# ./docker-deploy.sh
# ============================================================================
