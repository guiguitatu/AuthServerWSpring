# AuthServer sem Spring Boot

Este projeto demonstra um servidor HTTP simples em Java 17 utilizando apenas a API `com.sun.net.httpserver` para expor um CRUD básico de produtos. A aplicação mantém os dados em memória através de um repositório thread-safe e retorna/consome JSON utilizando Jackson.

## Como executar

1. Certifique-se de ter o Java 17+ e o Maven instalados.
2. Instale as dependências e execute os testes:
   ```bash
   mvn test
   ```
3. Inicie o servidor:
   ```bash
   mvn exec:java -Dexec.mainClass=com.example.authserver.AuthServerApplication
   ```
4. O servidor estará disponível em `http://localhost:8080`.

### Configuração do banco de dados

A aplicação seleciona o banco de dados a partir da variável de ambiente `DB`, que pode receber os valores `mysql` (padrão) ou `sqlite`.

- Para **MySQL**, os valores padrão são:
  - `MYSQL_URL` (ou `DB_URL`): `jdbc:mysql://localhost:3306/authserver`
  - `MYSQL_USER` (ou `DB_USER`): `root`
  - `MYSQL_PASSWORD` (ou `DB_PASSWORD`): `root`
- Para **SQLite**, os valores padrão são:
  - `SQLITE_URL` (ou `DB_URL`): `jdbc:sqlite:authserver.db`
  - `SQLITE_USER` (ou `DB_USER`): string vazia
  - `SQLITE_PASSWORD` (ou `DB_PASSWORD`): string vazia

Defina `DB=sqlite` no seu `.env` (ou nas variáveis de ambiente do sistema) para utilizar o driver SQLite; qualquer outro valor utiliza o MySQL.

## Endpoints

- `GET /products` – lista todos os produtos.
- `GET /products/{id}` – busca um produto pelo identificador.
- `POST /products` – cria um novo produto (JSON com `name`, `description`, `price`).
- `PUT /products/{id}` – atualiza um produto existente.
- `DELETE /products/{id}` – remove um produto.

### Exemplo de payload

```json
{
  "name": "Webcam",
  "description": "Webcam Full HD",
  "price": 399.90
}
```

## Testes

Os testes unitários cobrem o repositório em memória, garantindo a geração sequencial de IDs, busca por ID e remoção de produtos.
