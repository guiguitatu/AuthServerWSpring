# Script de teste do servidor
Write-Host "=== Testando Servidor AuthServer ===" -ForegroundColor Cyan

# Iniciar servidor em background
Write-Host "`n[1/5] Iniciando servidor..." -ForegroundColor Yellow
$env:DB = "sqlite"
$serverJob = Start-Job -ScriptBlock {
    Set-Location $using:PWD
    mvn exec:java -Dexec.mainClass=com.example.authserver.AuthServerApplication 2>&1
}

# Aguardar servidor iniciar
Write-Host "[2/5] Aguardando servidor iniciar (5 segundos)..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# Testar endpoints
Write-Host "`n[3/5] Testando endpoints..." -ForegroundColor Yellow

try {
    # Teste 1: GET /products - Listar produtos
    Write-Host "`n  Teste 1: GET /products" -ForegroundColor Green
    $response1 = Invoke-WebRequest -Uri "http://localhost:8080/products" -Method GET -UseBasicParsing
    Write-Host "    Status: $($response1.StatusCode)" -ForegroundColor White
    Write-Host "    Resposta: $($response1.Content)" -ForegroundColor Gray
    
    # Teste 2: POST /products - Criar produto
    Write-Host "`n  Teste 2: POST /products" -ForegroundColor Green
    $body = @{
        name = "Webcam"
        description = "Webcam Full HD"
        price = 399.90
    } | ConvertTo-Json
    $response2 = Invoke-WebRequest -Uri "http://localhost:8080/products" -Method POST -Body $body -ContentType "application/json" -UseBasicParsing
    Write-Host "    Status: $($response2.StatusCode)" -ForegroundColor White
    Write-Host "    Resposta: $($response2.Content)" -ForegroundColor Gray
    $createdProduct = $response2.Content | ConvertFrom-Json
    
    # Teste 3: GET /products/{id} - Buscar produto específico
    if ($createdProduct.id) {
        Write-Host "`n  Teste 3: GET /products/$($createdProduct.id)" -ForegroundColor Green
        $response3 = Invoke-WebRequest -Uri "http://localhost:8080/products/$($createdProduct.id)" -Method GET -UseBasicParsing
        Write-Host "    Status: $($response3.StatusCode)" -ForegroundColor White
        Write-Host "    Resposta: $($response3.Content)" -ForegroundColor Gray
    }
    
    # Teste 4: PUT /products/{id} - Atualizar produto
    if ($createdProduct.id) {
        Write-Host "`n  Teste 4: PUT /products/$($createdProduct.id)" -ForegroundColor Green
        $updateBody = @{
            name = "Webcam Atualizada"
            description = "Webcam Full HD 4K"
            price = 499.90
        } | ConvertTo-Json
        $response4 = Invoke-WebRequest -Uri "http://localhost:8080/products/$($createdProduct.id)" -Method PUT -Body $updateBody -ContentType "application/json" -UseBasicParsing
        Write-Host "    Status: $($response4.StatusCode)" -ForegroundColor White
        Write-Host "    Resposta: $($response4.Content)" -ForegroundColor Gray
    }
    
    # Teste 5: DELETE /products/{id} - Deletar produto
    if ($createdProduct.id) {
        Write-Host "`n  Teste 5: DELETE /products/$($createdProduct.id)" -ForegroundColor Green
        $response5 = Invoke-WebRequest -Uri "http://localhost:8080/products/$($createdProduct.id)" -Method DELETE -UseBasicParsing
        Write-Host "    Status: $($response5.StatusCode)" -ForegroundColor White
    }
    
    # Teste 6: GET /products novamente para verificar lista
    Write-Host "`n  Teste 6: GET /products (verificar lista final)" -ForegroundColor Green
    $response6 = Invoke-WebRequest -Uri "http://localhost:8080/products" -Method GET -UseBasicParsing
    Write-Host "    Status: $($response6.StatusCode)" -ForegroundColor White
    Write-Host "    Resposta: $($response6.Content)" -ForegroundColor Gray
    
    Write-Host "`n[4/5] Todos os testes passaram!" -ForegroundColor Green
    
} catch {
    Write-Host "`n  ERRO: $_" -ForegroundColor Red
    Write-Host "    Detalhes: $($_.Exception.Message)" -ForegroundColor Red
}

# Parar servidor
Write-Host "`n[5/5] Parando servidor..." -ForegroundColor Yellow
Stop-Job $serverJob
Remove-Job $serverJob

Write-Host "`n=== Teste concluído ===" -ForegroundColor Cyan

