# Script para testar endpoints do servidor
# Certifique-se de que o servidor está rodando antes de executar este script

Write-Host "=== Testando Endpoints do Servidor ===" -ForegroundColor Cyan
Write-Host "Certifique-se de que o servidor está rodando na porta 8080`n" -ForegroundColor Yellow

$baseUrl = "http://localhost:8080/products"

# Teste 1: GET /products
Write-Host "[1] GET /products" -ForegroundColor Green
try {
    $r1 = Invoke-WebRequest -Uri $baseUrl -Method GET -UseBasicParsing
    Write-Host "    Status: $($r1.StatusCode) OK" -ForegroundColor White
    Write-Host "    Resposta: $($r1.Content)" -ForegroundColor Gray
} catch {
    Write-Host "    ERRO: $_" -ForegroundColor Red
    exit
}

# Teste 2: POST /products
Write-Host "`n[2] POST /products" -ForegroundColor Green
try {
    $body = '{"name":"Webcam","description":"Webcam Full HD","price":399.90}'
    $r2 = Invoke-WebRequest -Uri $baseUrl -Method POST -Body $body -ContentType "application/json" -UseBasicParsing
    Write-Host "    Status: $($r2.StatusCode) Created" -ForegroundColor White
    Write-Host "    Resposta: $($r2.Content)" -ForegroundColor Gray
    $product = $r2.Content | ConvertFrom-Json
    $productId = $product.id
} catch {
    Write-Host "    ERRO: $_" -ForegroundColor Red
    exit
}

# Teste 3: GET /products/{id}
if ($productId) {
    Write-Host "`n[3] GET /products/$productId" -ForegroundColor Green
    try {
        $r3 = Invoke-WebRequest -Uri "$baseUrl/$productId" -Method GET -UseBasicParsing
        Write-Host "    Status: $($r3.StatusCode) OK" -ForegroundColor White
        Write-Host "    Resposta: $($r3.Content)" -ForegroundColor Gray
    } catch {
        Write-Host "    ERRO: $_" -ForegroundColor Red
    }
}

# Teste 4: PUT /products/{id}
if ($productId) {
    Write-Host "`n[4] PUT /products/$productId" -ForegroundColor Green
    try {
        $updateBody = '{"name":"Webcam Atualizada","description":"Webcam Full HD 4K","price":499.90}'
        $r4 = Invoke-WebRequest -Uri "$baseUrl/$productId" -Method PUT -Body $updateBody -ContentType "application/json" -UseBasicParsing
        Write-Host "    Status: $($r4.StatusCode) OK" -ForegroundColor White
        Write-Host "    Resposta: $($r4.Content)" -ForegroundColor Gray
    } catch {
        Write-Host "    ERRO: $_" -ForegroundColor Red
    }
}

# Teste 5: DELETE /products/{id}
if ($productId) {
    Write-Host "`n[5] DELETE /products/$productId" -ForegroundColor Green
    try {
        $r5 = Invoke-WebRequest -Uri "$baseUrl/$productId" -Method DELETE -UseBasicParsing
        Write-Host "    Status: $($r5.StatusCode) No Content" -ForegroundColor White
    } catch {
        Write-Host "    ERRO: $_" -ForegroundColor Red
    }
}

# Teste 6: GET /products (final)
Write-Host "`n[6] GET /products (verificação final)" -ForegroundColor Green
try {
    $r6 = Invoke-WebRequest -Uri $baseUrl -Method GET -UseBasicParsing
    Write-Host "    Status: $($r6.StatusCode) OK" -ForegroundColor White
    Write-Host "    Resposta: $($r6.Content)" -ForegroundColor Gray
} catch {
    Write-Host "    ERRO: $_" -ForegroundColor Red
}

Write-Host "`n=== Teste concluído ===" -ForegroundColor Cyan

