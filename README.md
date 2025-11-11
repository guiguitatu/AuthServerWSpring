# AuthServer sem Spring Boot

Este projeto demonstra um servidor HTTP simples em Java 17 utilizando apenas a API `com.sun.net.httpserver` para expor um CRUD básico de produtos. A aplicação mantém os dados em memória através de um repositório thread-safe e retorna/consome JSON utilizando Jackson.

## Estrutura do Projeto

```
src/main/java/com/example/authserver/
├── AuthServerApplication.java          # Classe principal da aplicação
├── domain/
│   └── Product.java                    # Entidade de domínio
├── http/
│   ├── ProductHttpHandler.java         # Handler principal HTTP
│   ├── filter/
│   │   └── RequestLoggingFilter.java   # Filtro de logging (Chain of Responsibility)
│   ├── handler/
│   │   ├── HandlerFactory.java         # Factory Method
│   │   ├── RouteHandler.java           # Interface dos handlers
│   │   ├── StaticResponseHandler.java  # Handler para respostas estáticas
│   │   └── product/
│   │       ├── AbstractProductHandler.java  # Template Method
│   │       ├── CreateProductHandler.java
│   │       ├── DeleteProductHandler.java
│   │       ├── GetProductHandler.java
│   │       ├── ListProductsHandler.java
│   │       └── UpdateProductHandler.java
│   └── util/
│       └── ResponseWriter.java         # Utilitário de escrita JSON (Singleton)
├── repository/
│   ├── DatabaseDialect.java            # Strategy para diferentes SGBDs
│   └── ProductRepository.java         # Repositório de produtos
└── validation/
    ├── ProductValidator.java           # Interface Strategy
    └── DefaultProductValidator.java    # Implementação Strategy
```

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

## Padrões de Design GOF (Gang of Four)

Este projeto implementa vários padrões de design clássicos do livro "Design Patterns: Elements of Reusable Object-Oriented Software" (Gang of Four). Os padrões GOF são soluções reutilizáveis para problemas comuns de design de software.

### Padrões Implementados

#### 1. Factory Method
**O que é:** Padrão criacional que fornece uma interface para criar objetos sem especificar suas classes concretas. Delega a criação de objetos para subclasses ou classes especializadas.

**Arquivo:** `src/main/java/com/example/authserver/http/handler/HandlerFactory.java`

**Como é usado:** A classe `HandlerFactory` encapsula a lógica de criação de handlers HTTP baseado no método HTTP (GET, POST, PUT, DELETE) e no caminho da requisição. Cada rota retorna o handler apropriado (`ListProductsHandler`, `CreateProductHandler`, `GetProductHandler`, etc.) sem expor a lógica de criação ao cliente.

#### 2. Template Method
**O que é:** Padrão comportamental que define o esqueleto de um algoritmo em uma classe base, permitindo que subclasses sobrescrevam etapas específicas sem alterar a estrutura geral do algoritmo.

**Arquivo:** `src/main/java/com/example/authserver/http/handler/product/AbstractProductHandler.java`

**Como é usado:** A classe abstrata `AbstractProductHandler` define o método `handle()` que contém a estrutura comum (tratamento de exceções, escrita de resposta), enquanto o método abstrato `doHandle()` é implementado pelas subclasses (`CreateProductHandler`, `GetProductHandler`, `UpdateProductHandler`, `DeleteProductHandler`, `ListProductsHandler`) para implementar a lógica específica de cada operação.

#### 3. Strategy
**O que é:** Padrão comportamental que define uma família de algoritmos, encapsula cada um deles e os torna intercambiáveis. Permite que o algoritmo varie independentemente dos clientes que o utilizam.

**Arquivos:**
- `src/main/java/com/example/authserver/validation/ProductValidator.java` (interface)
- `src/main/java/com/example/authserver/validation/DefaultProductValidator.java` (implementação)
- `src/main/java/com/example/authserver/repository/DatabaseDialect.java` (enum com estratégias)

**Como é usado:**
- **Validação:** A interface `ProductValidator` define a estratégia de validação de produtos. `DefaultProductValidator` implementa a validação padrão (nome não vazio, preço positivo), mas outras estratégias podem ser facilmente adicionadas e injetadas nos handlers.
- **Banco de dados:** O enum `DatabaseDialect` encapsula diferentes estratégias SQL para diferentes bancos de dados (MySQL, SQLite), permitindo que o `ProductRepository` funcione com múltiplos SGBDs sem alterar sua lógica principal.

#### 4. Chain of Responsibility
**O que é:** Padrão comportamental que evita o acoplamento do remetente de uma requisição ao seu receptor, dando a mais de um objeto a oportunidade de tratar a requisição. Encadeia os objetos receptores e passa a requisição ao longo da cadeia até que um objeto a trate.

**Arquivo:** `src/main/java/com/example/authserver/http/filter/RequestLoggingFilter.java`

**Como é usado:** O `RequestLoggingFilter` estende a classe `Filter` do `com.sun.net.httpserver` e implementa o padrão Chain of Responsibility através do método `chain.doFilter(exchange)`. O filtro intercepta todas as requisições HTTP antes que cheguem ao handler, registra o tempo de execução e passa a requisição para o próximo elemento da cadeia. Múltiplos filtros podem ser adicionados sequencialmente, cada um processando a requisição antes de passá-la adiante.

#### 5. Singleton
**O que é:** Padrão criacional que garante que uma classe tenha apenas uma instância e fornece um ponto de acesso global a essa instância.

**Arquivo:** `src/main/java/com/example/authserver/http/util/ResponseWriter.java`

**Como é usado:** `ResponseWriter` é implementado como um enum singleton (`INSTANCE`), garantindo uma única instância do `ObjectMapper` do Jackson em toda a aplicação. Isso é eficiente em termos de memória e thread-safe por padrão em Java.