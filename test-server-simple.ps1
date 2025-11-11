# Script de teste simplificado
Write-Host "=== Testando Servidor AuthServer ===" -ForegroundColor Cyan

# Verificar se servidor já está rodando
$portCheck = netstat -ano | findstr :8080
if ($portCheck) {
    Write-Host "Servidor já está rodando na porta 8080!" -ForegroundColor Green
} else {
    Write-Host "Servidor não está rodando. Por favor, inicie manualmente com:" -ForegroundColor Yellow
    Write-Host "  mvn exec:java -Dexec.mainClass=com.example.authserver.AuthServerApplication" -ForegroundColor White
    Write-Host "`nOu execute o AuthServerApplication.java diretamente no seu IDE." -ForegroundColor White
    Write-Host "`nAguardando 10 segundos para você iniciar o servidor..." -ForegroundColor Yellow
    Start-Sleep -Seconds 10
}

# Testar endpoints
Write-Host "`n=== Testando Endpoints ===" -ForegroundColor Cyan

try {
    # Teste 1: GET /products
    Write-Host "`n[1] GET /products - Listar produtos" -ForegroundColor Green
    $response1 = Invoke-WebRequest -Uri "http://localhost:8080/products" -Method GET -UseBasicParsing
    Write-Host "    Status: $($response1.StatusCode)" -ForegroundColor White
    Write-Host "    Resposta: $($response1.Content)" -ForegroundColor Gray
    
    # Teste 2: POST /products
    Write-Host "`n[2] POST /products - Criar produto" -ForegroundColor Green
    $body = '{"name":"Webcam","description":"Webcam Full HD","price":399.90}'
    $response2 = Invoke-WebRequest -Uri "http://localhost:8080/products" -Method POST -Body $body -ContentType "application/json" -UseBasicParsing
    Write-Host "    Status: $($response2.StatusCode)" -ForegroundColor White
    Write-Host "    Resposta: $($response2.Content)" -ForegroundColor Gray
    $createdProduct = $response2.Content | ConvertFrom-Json
    
    # Teste 3: GET /products/{id}
    if ($createdProduct.id) {
        Write-Host "`n[3] GET /products/$($createdProduct.id) - Buscar produto" -ForegroundColor Green
        $response3 = Invoke-WebRequest -Uri "http://localhost:8080/products/$($createdProduct.id)" -Method GET -UseBasicParsing
        Write-Host "    Status: $($response3.StatusCode)" -ForegroundColor White
        Write-Host "    Resposta: $($response3.Content)" -ForegroundColor Gray
    }
    
    # Teste 4: PUT /products/{id}
    if ($createdProduct.id) {
        Write-Host "`n[4] PUT /products/$($createdProduct.id) - Atualizar produto" -ForegroundColor Green
        $updateBody = '{"name":"Webcam Atualizada","description":"Webcam Full HD 4K","price":499.90}'
        $response4 = Invoke-WebRequest -Uri "http://localhost:8080/products/$($createdProduct.id)" -Method PUT -Body $updateBody -ContentType "application/json" -UseBasicParsing
        Write-Host "    Status: $($response4.StatusCode)" -ForegroundColor White
        Write-Host "    Resposta: $($response4.Content)" -ForegroundColor Gray
    }
    
    # Teste 5: DELETE /products/{id}
    if ($createdProduct.id) {
        Write-Host "`n[5] DELETE /products/$($createdProduct.id) - Deletar produto" -ForegroundColor Green
        $response5 = Invoke-WebRequest -Uri "http://localhost:8080/products/$($createdProduct.id)" -Method DELETE -UseBasicParsing
        Write-Host "    Status: $($response5.StatusCode)" -ForegroundColor White
    }
    
    # Teste 6: GET /products novamente
    Write-Host "`n[6] GET /products - Lista final" -ForegroundColor Green
    $response6 = Invoke-WebRequest -Uri "http://localhost:8080/products" -Method GET -UseBasicParsing
    Write-Host "    Status: $($response6.StatusCode)" -ForegroundColor White
    Write-Host "    Resposta: $($response6.Content)" -ForegroundColor Gray
    
    Write-Host "`n=== Todos os testes passaram! ===" -ForegroundColor Green
    
} catch {
    Write-Host "`nERRO ao testar: $_" -ForegroundColor Red
    if ($_.Exception.Response) {
        $statusCode = [int]$_.Exception.Response.StatusCode
        Write-Host "    Status Code: $statusCode" -ForegroundColor Red
    }
    Write-Host "    Detalhes: $($_.Exception.Message)" -ForegroundColor Red
}
