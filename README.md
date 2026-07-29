<p align="center">
  <img src="money-pilot-logo.png" alt="Money Pilot" width="200"/>
</p>

<h1 align="center">Money Pilot</h1>

<p align="center">
  API REST para controle de finanças pessoais: categorias, parceiros (pessoa física e jurídica), contas bancárias, receitas, despesas, pagamentos e recebimentos.
</p>

## 📌 Status

Projeto em desenvolvimento. Hoje o CRUD completo (controller, service, DTOs, exceptions e testes) está implementado para os recursos **Categoria**, **Parceiro** (pessoa física e jurídica), **Receita** e **Despesa**. As demais entidades do domínio (ContaBancaria, Banco, Endereco, Pagamento, Recebimento) já existem como models e possuem migrations e repositories, mas ainda não têm service/controller expostos.

## 🚀 Começando

### 📋 Pré-requisitos

* Java 21
* Docker (para subir o MySQL via `docker-compose`)
* Não é necessário ter o Maven instalado — o projeto usa o Maven Wrapper (`mvnw`)

### 🔧 Instalação

1. Clone o repositório:
   ```
   git clone https://github.com/guh99998/money-pilot.git
   cd money-pilot
   ```

2. Copie o arquivo de variáveis de ambiente e preencha os valores:
   ```
   cp .env.example .env
   ```

3. Suba o banco de dados MySQL:
   ```
   docker-compose up -d
   ```

4. Rode a aplicação (as migrations do Flyway são aplicadas automaticamente na inicialização):
   ```
   ./mvnw spring-boot:run
   ```

A API sobe em `http://localhost:8080`.

## 📚 Endpoints

### Categoria

| Método | Rota              | Descrição                          |
|--------|-------------------|-------------------------------------|
| GET    | `/categorias`     | Lista categorias (paginado)         |
| GET    | `/categorias/{id}`| Busca uma categoria por id          |
| POST   | `/categorias`     | Cria uma nova categoria             |
| PUT    | `/categorias/{id}`| Atualiza uma categoria existente    |
| DELETE | `/categorias/{id}`| Remove uma categoria                |

### Parceiro

| Método | Rota              | Descrição                          |
|--------|-------------------|-------------------------------------|
| GET    | `/parceiros`      | Lista parceiros (paginado)          |
| GET    | `/parceiros/{id}` | Busca um parceiro por id            |
| POST   | `/parceiros`      | Cria um novo parceiro (PF ou PJ)    |
| PUT    | `/parceiros/{id}` | Atualiza um parceiro existente      |
| DELETE | `/parceiros/{id}` | Remove um parceiro                  |

### Receita

| Método | Rota              | Descrição                          |
|--------|-------------------|-------------------------------------|
| GET    | `/receitas`       | Lista receitas (paginado)           |
| GET    | `/receitas/{id}`  | Busca uma receita por id            |
| POST   | `/receitas`       | Cria uma nova receita               |
| PUT    | `/receitas/{id}`  | Atualiza uma receita existente      |
| DELETE | `/receitas/{id}`  | Remove uma receita                  |

### Despesa

| Método | Rota              | Descrição                          |
|--------|-------------------|-------------------------------------|
| GET    | `/despesas`       | Lista despesas (paginado)           |
| GET    | `/despesas/{id}`  | Busca uma despesa por id            |
| POST   | `/despesas`       | Cria uma nova despesa               |
| PUT    | `/despesas/{id}`  | Atualiza uma despesa existente      |
| DELETE | `/despesas/{id}`  | Remove uma despesa                  |

## ⚙️ Executando os testes

```
./mvnw test
```

A suíte cobre os recursos Categoria, Parceiro, Receita e Despesa em diferentes camadas:

* **Service** (`CategoriaServiceTest`, `ParceiroServiceTest`, `ReceitaServiceTest`, `DespesaServiceTest`) — regras de negócio com o repository mockado (Mockito)
* **Controller** (`CategoriaControllerTest`, `ParceiroControllerTest`, `ReceitaControllerTest`, `DespesaControllerTest`) — fatia web (`@WebMvcTest` + MockMvc), validando status HTTP e corpo de resposta
* **DTO** (`CategoriaRequestDTOTest`, `ParceiroRequestDTOTest`, `ReceitaRequestDTOTest`, `DespesaRequestDTOTest`) — validação de Bean Validation (campos obrigatórios, formatos e regras de negócio)
* **Model** (`ReceitaTest`, `DespesaTest`) — validação de Bean Validation ao nível de entidade, `equals`/`hashCode` e `toString`
* **Exception Handler** (`ApiExceptionHandlerTest`) — mapeamento de exceções para as respostas de erro padronizadas

## 🛠️ Construído com

* [Java 21](https://openjdk.org/projects/jdk/21/)
* [Spring Boot](https://spring.io/projects/spring-boot) — Web, Data JPA, Security, Validation
* [MySQL](https://www.mysql.com/) — Banco de dados relacional
* [Flyway](https://flywaydb.org/) — Versionamento e migração de schema
* [Lombok](https://projectlombok.org/) — Redução de boilerplate nos models
* [JUnit 5](https://junit.org/junit5/), [Mockito](https://site.mockito.org/) e [AssertJ](https://assertj.github.io/doc/) — Testes
* [Maven](https://maven.apache.org/) — Gerenciador de dependências

## ✒️ Autor

* **Gustavo Lopes** — [guh99998](https://github.com/guh99998)
