# Contas a Pagar API

Uma aplicação RESTful desenvolvida com Spring Boot para gerenciamento de contas a pagar.

## Funcionalidades

### Contas

CRUD para contas a pagar, importação via CSV e cálculo do total pago por período.

#### Endpoints:

- **POST** `/api/v1/contas` – Cria conta (usa usuário autenticado ou informado para ADM).
- **GET** `/api/v1/contas` – Lista contas (filtra pelo usuário autenticado quando não ADM).
- **GET** `/api/v1/contas/{id}` – Consulta conta por ID.
- **PUT** `/api/v1/contas/{id}` – Atualização completa.
- **PATCH** `/api/v1/contas/{id}` – Atualização parcial.
- **PATCH** `/api/v1/contas/{id}/situacao` – Atualiza a situação da conta.
- **DELETE** `/api/v1/contas/{id}` – Exclui conta.
- **GET** `/api/v1/contas/total-pago` – Soma dos valores pagos.
- **POST** `/api/v1/contas/import-csv` – Importação de contas via arquivo CSV.

### Usuários

CRUD para usuários com validação e restrição de acesso.

#### Endpoints:

- **GET** `/api/v1/usuarios` – Lista usuários (apenas ADM).
- **GET** `/api/v1/usuarios/{id}` – Consulta usuário por ID.
- **PUT** `/api/v1/usuarios/{id}` – Atualização completa.
- **PATCH** `/api/v1/usuarios/{id}` – Atualização parcial.
- **DELETE** `/api/v1/usuarios/{id}` – Exclui usuário.
- **POST** `/api/v1/auth/register` – Registro (conforme política de acesso).

### Autenticação JWT

- **POST** `/api/v1/auth/login` – Gera token JWT para acesso seguro.

## Estrutura do Projeto

### Controller

- Endpoints REST com validação (`@Valid` e grupos) e controle de acesso via `@PreAuthorize`.

### Service

- Regras de negócio, persistência e lógica de operações, como importação CSV e cálculo de total.

### Repository

- Acesso ao banco de dados usando Spring Data JPA e Specifications para consultas dinâmicas.

### DTOs

- Objetos de transferência com validação (ex.: `UsuarioDTO`, `ContaDTO`).

### Tratamento de Exceções

- `@ControllerAdvice` centraliza exceções customizadas (ex.: `UserNotFoundException`, `ContaNotFoundException`, `CsvProcessException`, `TokenException`) e erros de validação, retornando JSON padronizado.

## Execução

### Clone o repositório:

```bash
git clone https://github.com/seu-usuario/contas-pagar-api.git
cd contas-pagar-api
```

### Gere uma imagem docker do projeto

```bash
./mvnw clean install
docker build . -t app-contas-pagar
```

### Inicialize os containers

```bash
docker-compose up
```

Após inicializar os containers, a API estará disponível em [http://localhost:8080](http://localhost:8080/).

### Acessando o banco de dados (PgAdmin)

Você pode acessar o PgAdmin através de [**http://localhost:15432**](http://localhost:15432) (email: admin@admin.com senha: admin) e conectar-se ao banco de dados **db-contas-pagar** usando as seguintes credenciais:

- **Host:** `db-contas-pagar`
- **Usuário:** `admin`
- **Senha:** `secret-pass!`
- **Banco de dados:** `contas-pagar`

## Acesso Inicial

Para acessar a API, um usuário ADMIN deve ser criado manualmente no banco de dados, pois apenas administradores podem cadastrar novos usuários.

### Dados para criar um usuário ADMIN inicial:

```json
{
    "nome": "Admin",
    "email": "admin@admin.com",
    "senha": "$2a$10$yRy9LlDu5BWUick6lb/htuW4QlN3zt1pCUm1PTbfMSek0mQ6fFPQ2",
    "cargo": "ADMIN"
}
```

🔑 **Observação:** A senha acima é o hash da senha `senha123`.

## Importação de Contas via CSV

Para importar contas através de um arquivo CSV, ele deve seguir o seguinte formato:

```
dataPagamento;dataVencimento;valor;descricao;situacao
2024-03-01;2024-03-05;1000.50;Conta de energia;PAGO
2024-03-02;2024-03-10;500.00;Internet;PENDENTE
```

- ``: Data do pagamento no formato `YYYY-MM-DD`.
- ``: Data de vencimento da conta no formato `YYYY-MM-DD`.
- ``: Valor da conta (ponto decimal como separador).
- ``: Descrição da conta.
- ``: Status da conta (`PAGO` ou `PENDENTE`).

O upload do arquivo pode ser feito através do endpoint:

```bash
POST /api/v1/contas/import-csv
```

## Testes

Os testes unitários cobrem as camadas de serviço e os repositories. Para executá-los:

```bash
./mvnw test
```

Os testes são executados em um banco de dados em memória (H2), garantindo um ambiente isolado e independente para validação das funcionalidades da aplicação.

