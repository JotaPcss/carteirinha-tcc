# Carteirinha Universitária Digital

Sistema de carteirinha universitária digital com QR Code para o Centro Universitário La Salle (UniLaSalle).  
Backend: **Spring Boot 3.3.5 + Java 21**. Frontend: **React + TypeScript + Vite** (servido como build estático pelo Spring Boot).

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|------------|--------------|
| Java (JDK) | 21 |
| Maven      | 3.9+ |
| MySQL      | 8.0+ |
| Node.js    | 18+ (apenas para rebuild do frontend) |

---

## 1. Banco de dados

```sql
-- Execute no MySQL como root:
SOURCE scripts/schema.sql;
```

O script cria o banco `carteirinha_db`, as tabelas e insere dados iniciais:
- **Administrador:** `admin@unilasalle.br` / `admin123`
- **Estudantes de teste:** matrícula `20240001` a `20240008` / senha `123456`

---

## 2. Configuração

Edite `main/resources/application.properties` (ou use variáveis de ambiente):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/carteirinha_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=SUA_SENHA_AQUI

# Diretório onde as fotos enviadas ficam armazenadas
app.upload.dir=uploads/fotos

# Segredos JWT e AES (use valores únicos em produção)
jwt.secret=4f7vK9Xz8WqLmN2bVp9JkRt5YxCwMz1aPqRsTvUxYzA=
aes.secret.key=uH9fK5zNxY8mQpW2vD6rT4gL7sB1vN3mK9xP4vR8tZ0=
```

> **Segurança:** em produção, mova `jwt.secret` e `aes.secret.key` para variáveis de ambiente e **não** os deixe no repositório.

---

## 3. Compilar e executar

```bash
# Dentro do diretório do projeto:
mvn clean package -DskipTests

java -jar target/carteirinha-digital-0.0.1-SNAPSHOT.jar
```

A aplicação sobe em **http://localhost:8080**.

---

## 4. (Opcional) Rebuild do frontend

O build já está incluído em `main/resources/static/`. Para recompilar:

```bash
cd ../carteirinha-frontend   # ou onde estiver o projeto React
npm install
npm run build
cp -r dist/* ../TCC/main/resources/static/
```

---

## 5. Fluxo de uso

```
Estudante                          Admin
────────────────────────────────────────────────
1. /cadastro  → cria conta
2. /login     → obtém JWT
3. Dashboard  → envia foto
                                4. /login (admin@…)
                                5. Dashboard admin → aprova foto
6. Dashboard  → QR Code gerado automaticamente
7. QR Code    → validação pública em /api/validacao/verificar?token=…
```

---

## 6. Endpoints principais

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/api/estudantes/cadastro` | Público | Cadastra novo estudante |
| POST | `/api/auth/login` | Público | Login (retorna JWT) |
| GET  | `/api/estudantes/me` | ESTUDANTE | Perfil do estudante |
| POST | `/api/estudantes/upload-foto` | ESTUDANTE | Upload de foto (max 5 MB, JPG/PNG/WEBP) |
| GET  | `/api/estudantes/carteirinha/qrcode` | ESTUDANTE | Gera token de QR Code |
| GET  | `/api/admin/me` | ADMIN | Perfil do admin |
| GET  | `/api/admin/estudantes/pendentes` | ADMIN | Lista fotos pendentes de aprovação |
| PUT  | `/api/admin/estudantes/{id}/aprovar-foto?aprovado=true` | ADMIN | Aprova ou reprova foto |
| GET  | `/api/validacao/verificar?token=…` | Público | Valida QR Code |

---

## 7. Arquitetura

```
carteirinha-digital/
├── main/
│   ├── java/com/unilasalle/carteirinha/digital/
│   │   ├── config/          # SecurityConfig, JwtAuthenticationFilter, WebMvcConfig, DataInitializer
│   │   ├── controller/      # AuthController, EstudanteController, AdminController, ValidacaoController
│   │   ├── dto/             # EstudanteCadastroDTO, EstudanteResponseDTO, LoginRequestDTO, LoginResponseDTO
│   │   ├── entity/          # Estudante, Administrador, Carteirinha, Curso, ValidacaoLog, enums
│   │   ├── repository/      # Spring Data JPA repositories
│   │   ├── security/        # JwtService
│   │   ├── service/         # AuthService, EstudanteService, CarteirinhaService, UploadService
│   │   └── util/            # AesEncryptionService
│   └── resources/
│       ├── application.properties
│       └── static/          # Build do frontend React (index.html + assets)
├── scripts/
│   └── schema.sql           # DDL + dados iniciais
└── pom.xml
```
