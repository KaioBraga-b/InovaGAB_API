#!/usr/bin/env bash
# ==============================================================================
# Script de Empacotamento do Projeto de Testes Automatizados (.ZIP)
# FIAP 2026 - Cidades ESG Inteligentes (InovaGAB API)
# ==============================================================================

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUTPUT_ZIP="${PROJECT_DIR}/InovaGAB_Testes_Automatizados.zip"

echo "=================================================================="
echo "📦 Empacotando Projeto de Testes Automatizados em arquivo .ZIP"
echo "Diretório do projeto: ${PROJECT_DIR}"
echo "Destino: ${OUTPUT_ZIP}"
echo "=================================================================="

cd "${PROJECT_DIR}"

# Remove zip anterior se existir
rm -f "${OUTPUT_ZIP}"

# Cria arquivo zip excluindo diretórios de build e arquivos temporários
zip -r "${OUTPUT_ZIP}" . \
  -x "target/*" \
  -x ".git/*" \
  -x ".DS_Store" \
  -x "*/.DS_Store" \
  -x ".idea/*" \
  -x "*.iml" \
  -x ".env" \
  -x "deploy/*/.env" \
  -x "*.zip"

echo "=================================================================="
echo "✅ Arquivo compactado com sucesso!"
ls -lh "${OUTPUT_ZIP}"
echo "=================================================================="
