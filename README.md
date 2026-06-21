# Explore With Me — афиша событий

Приложение **Explore With Me** позволяет пользователям делиться информацией об интересных событиях, находить компанию для участия в них, комментировать и оценивать прошедшие мероприятия.

---

## Текущее состояние (Этап 2)

✅ **Модульный монолит** с выделенными библиотеками (`common-lib`, `user-lib`, `event-lib`, `request-lib`, `comment-lib`, `rating-lib`).  
✅ **Инфраструктура Spring Cloud** запущена и работает: Config Server, Eureka, API Gateway.  
✅ Все Postman-тесты успешно проходят через Gateway на порту `8080`.

В дальнейшем планируется постепенное выделение микросервисов с межсервисным взаимодействием через OpenFeign.

---

## Архитектура

Проект состоит из следующих модулей:

### Основные библиотеки (`core/`)
- **common-lib** — общие исключения, модель пользователя, базовые DTO.
- **user-lib** — управление пользователями и подписками.
- **event-lib** — события, категории, подборки, управляемые локации, модерация.
- **request-lib** — заявки на участие в событиях.
- **comment-lib** — комментарии к событиям.
- **rating-lib** — рейтинги и голосование за события.
- **main-service** — контроллеры и Spring Boot приложение, объединяющее все библиотеки.

### Инфраструктурные сервисы (`infra/`)
- **config-server** — Spring Cloud Config Server (порт `8888`).
- **discovery-server** — Eureka Server для обнаружения сервисов (порт `8761`).
- **gateway-server** — Spring Cloud Gateway как единая точка входа (порт `8080`).

### Сервис статистики (`stats-service/`)
- **stats-server** — сбор и просмотр статистики посещений (порт `9090`).
- **stats-client** — HTTP-клиент для взаимодействия с сервисом статистики.
- **stats-dto** — общие DTO для статистики.

Все сервисы регистрируются в Eureka и получают конфигурацию из Config Server.

---

## Внутреннее API

Взаимодействие между модулями `main-service` осуществляется напрямую через Java-интерфейсы (модульный монолит).  
Сервис статистики вызывается через `StatsClient` (RestTemplate).  
Переход на OpenFeign запланирован при выделении первого микросервиса.

---

## Внешний API

Полная спецификация API доступна в файлах:
- `ewm-main-service-spec.json`
- `ewm-stats-service-spec.json`

Краткий обзор эндпоинтов приведён ниже.

---

## Запуск (локально)

Предварительные требования:
- Docker и Docker Compose

### Быстрый старт через Docker Compose

```bash
mvn clean package -DskipTests
docker compose build --no-cache
docker compose up -d
```

Будут запущены контейнеры:
| Контейнер         | Порт       | Описание                     |
|-------------------|------------|------------------------------|
| `config-server`   | 8888       | Конфигурационный сервер       |
| `discovery-server`| 8761       | Eureka (обнаружение сервисов) |
| `gateway-server`  | 8080       | API-шлюз                     |
| `stats-db`        | 5433       | PostgreSQL для статистики     |
| `stats-server`    | 9090       | Сервис статистики             |
| `ewm-db`          | 5434       | PostgreSQL для основного сервиса |
| `ewm-service`     | внутренний | Основной сервис               |

После запуска все эндпоинты доступны через шлюз по адресу `http://localhost:8080`.

### Запуск без Docker (для разработки)

1. Убедитесь, что локально запущены два экземпляра PostgreSQL на портах `5433` и `5434`.
2. Выполните сборку:
   ```bash
   mvn clean package -DskipTests
   ```
3. Запустите приложения:
   - `infra/config-server/ConfigServerApplication`
   - `infra/discovery-server/DiscoveryServerApplication`
   - `infra/gateway-server/GatewayServerApplication`
   - `stats-service/stats-server/StatsServerApplication`
   - `core/main-service/MainServiceApplication`
     с соответствующими переменными окружения (см. `application.properties`).

---

## API (краткий обзор)

### Сервис статистики (через Gateway: `http://localhost:8080`)

| Метод | Путь      | Описание                       |
|-------|-----------|--------------------------------|
| POST  | `/hit`    | Сохранить информацию о запросе |
| GET   | `/stats`  | Получить статистику просмотров |

### Основной сервис (через Gateway: `http://localhost:8080`)

**Публичные эндпоинты**
- `GET /events` — поиск опубликованных событий
- `GET /events/{id}` — детальная информация о событии
- `GET /categories` — список категорий
- `GET /compilations` — подборки событий
- `GET /locations` — управляемые локации

**Приватные эндпоинты** (требуется ID пользователя)
- `POST /users/{userId}/events` — создать событие
- `GET /users/{userId}/events` — события пользователя
- `PATCH /users/{userId}/events/{eventId}` — редактирование
- `POST /users/{userId}/requests` — подать заявку
- `POST /users/{userId}/events/{eventId}/comments` — комментарий
- `PUT /users/{userId}/events/{eventId}/rating` — оценка
- `POST /users/{userId}/subscriptions` — подписка

**Административные эндпоинты**
- `GET /admin/events` — поиск событий (с фильтрами)
- `PATCH /admin/events/{eventId}` — модерация события
- `POST /admin/categories` — создать категорию
- `DELETE /admin/categories/{catId}` — удалить категорию
- `POST /admin/compilations` — создать подборку
- `DELETE /admin/compilations/{compId}` — удалить подборку
- `GET /admin/comments` — просмотр комментариев
- `PATCH /admin/comments/{commentId}` — модерировать комментарий
- `DELETE /admin/comments/{commentId}` — удалить комментарий
- `POST /admin/users` — создать пользователя
- `DELETE /admin/users/{userId}` — удалить пользователя
- `POST /admin/locations` — создать локацию
- `PATCH /admin/locations/{locationId}` — обновить локацию
- `DELETE /admin/locations/{locationId}` — удалить локацию

Полный список эндпоинтов и форматы запросов см. в файлах спецификации.

---

## Тестирование

В папке `postman` находятся три JSON-коллекции:

- `ewm-main-service.json` — тесты основного сервиса
- `ewm-stat-service.json` — тесты сервиса статистики
- `feature.json` — тесты дополнительной функциональности

Для запуска используйте [Newman](https://github.com/postmanlabs/newman):

```bash
newman run postman/ewm-main-service.json --delay-request 50
newman run postman/ewm-stat-service.json --delay-request 50
newman run postman/feature.json --delay-request 50
```

Все тесты успешно проходят локально и в CI (GitHub Actions).

---

## Технологии

| Компонент        | Технологии                                      |
|------------------|-------------------------------------------------|
| Язык             | Java 21                                         |
| Фреймворк        | Spring Boot 3.5.0, Spring Data JPA, Spring Cloud |
| База данных      | PostgreSQL 15 (основной сервис и статистика)     |
| Инфраструктура   | Spring Cloud Config, Eureka, Gateway, Docker     |
| Сборка           | Maven 3.9+                                      |
| Тестирование     | JUnit 5, Mockito, Postman (Newman)               |
| Прочее           | Lombok, MapStruct, Bean Validation, Actuator     |

---

## Дальнейшее развитие

В следующих итерациях:
- выделить `user-service` как первый микросервис с собственной БД и Feign-клиентами;
- повторить для остальных модулей;
- обеспечить отказоустойчивость с помощью Resilience4j.

---

## Автор

VanoStreyPracticum — в рамках обучения на платформе Яндекс Практикум.