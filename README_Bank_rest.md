# Система управления банковскими картами
## Конфигурация

### Файл `.env` содержит переменные окружения

```bash
# Server
SERVER_PORT=8080

# PostgreSQL
POSTGRES_DB=bankcardsdb
POSTGRES_USER=user
POSTGRES_PASSWORD=password

# Spring Datasource
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/${POSTGRES_DB}
SPRING_DATASOURCE_USERNAME=${POSTGRES_USER}
SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
SPRING_JPA_HIBERNATE_DDL_AUTO=validate

# JWT
JWT_EXPIRATION=86400000
JWT_SECRET=SxQ8TM2epeSZJoQ9CydmBe9GRjxpBGGQGAB8YijZY4k=

# Encryption
ENCRYPTION_ALGORITHM=AES
ENCRYPTION_KEY=hV1o1JnXr4rgwlBpUZ/KL6mPEDZkwGT76jZcZGIIIoo=

# Admin
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin
ADMIN_ROLE=ADMIN
```

### Создание БД
При первом запуске БД создается с помощью Liquibase

По умолчанию создается пользователь с ролью администратора:
- Логин: `admin`
- Пароль: `admin`

## Запуск проекта

1. Сборка: `mvn clean package`
2. Запуск: `docker-compose up --build`

## Документация API

1. - Swagger UI: `http://localhost:8080/swagger-ui.html`
2. - OpenAPI JSON: `http://localhost:8080/api-docs`
