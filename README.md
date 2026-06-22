# Explore With Me — афиша событий с рекомендательной системой

Приложение **Explore With Me** позволяет пользователям делиться информацией об интересных событиях, находить компанию для участия в них, комментировать и оценивать прошедшие мероприятия. На третьем этапе добавлена рекомендательная система на основе совместной фильтрации и косинусного сходства.

---

## Текущее состояние (Этап 3)

✅ **Рекомендательная система** реализована:
- Микросервисы **Collector**, **Aggregator**, **Analyzer** работают с Apache Kafka и gRPC.
- Collector принимает действия пользователей (просмотр, регистрация, лайк) по gRPC и отправляет в Kafka.
- Aggregator рассчитывает сходство мероприятий по косинусному сходству и публикует в топик `stats.events-similarity.v1`.
- Analyzer сохраняет данные в PostgreSQL и предоставляет gRPC API для получения рекомендаций и рейтинга мероприятий.
- В основной сервис добавлены эндпоинты:
    - `GET /events/recommendations` — рекомендации для пользователя.
    - `PUT /events/{eventId}/like` — лайк мероприятия.
    - При получении опубликованного события отправляется просмотр через Collector, а рейтинг запрашивается из Analyzer.
- Взаимодействие через gRPC-клиенты в модуле `stats-client`, обнаружение адресов через Eureka.

✅ **Инфраструктура**:
- Apache Kafka + Zookeeper + Schema Registry в Docker Compose.
- Все микросервисы (collector, aggregator, analyzer) регистрируются в Eureka и получают конфигурацию из Config Server.
- Основной сервис (main-service) и сервис статистики (stats-server) продолжают работать через Gateway.

✅ **Все Postman-тесты** (`ewm-stat-service`, `ewm-main-service`, `feature`) проходят успешно (за исключением одного ассерта, связанного с переходом с `views` на `rating`, который будет адаптирован).

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

### Сервис статистики и рекомендаций (`stats-service/`)
- **stats-server** — сбор и просмотр статистики посещений (порт `9090`).
- **stats-client** — HTTP и gRPC клиенты для взаимодействия с сервисами.
- **stats-dto** — общие DTO для статистики.
- **stats-avro-schemas** — Avro-схемы для Kafka.
- **stats-grpc-proto** — gRPC-схемы.
- **collector** — gRPC-сервер, принимающий действия пользователей и отправляющий в Kafka.
- **aggregator** — рассчитывает сходство мероприятий и отправляет результаты в Kafka.
- **analyzer** — хранит данные в БД и предоставляет gRPC API для рекомендаций и рейтинга.

---

## Внутреннее API

- **Collector**: `CollectUserAction (UserActionProto) returns (Empty)` — вызывается из `main-service` через gRPC при просмотрах, регистрациях, лайках.
- **Analyzer**:
    - `GetRecommendationsForUser (UserPredictionsRequestProto) returns (stream RecommendedEventProto)` — возвращает рекомендованные мероприятия для пользователя.
    - `GetSimilarEvents (SimilarEventsRequestProto) returns (stream RecommendedEventProto)` — возвращает похожие мероприятия.
    - `GetInteractionsCount (InteractionsCountRequestProto) returns (stream RecommendedEventProto)` — возвращает сумму весов взаимодействий для мероприятия.

---

## Внешний API

Полная спецификация API доступна в файлах:
- `ewm-main-service-spec.json`
- `ewm-stats-service-spec.json`

Все эндпоинты доступны через Gateway на порту `8080`.

Новые эндпоинты:
- `GET /events/recommendations?size=N` (заголовок `X-EWM-USER-ID`) — возвращает рекомендованные события.
- `PUT /events/{eventId}/like` (заголовок `X-EWM-USER-ID`) — ставит лайк событию.

---

## Запуск (локально)

### Через Docker Compose

```bash
mvn clean package -DskipTests
docker compose build --no-cache
docker compose up -d
```

Будут запущены все контейнеры, включая Kafka, Zookeeper, Schema Registry, микросервисы рекомендаций.

После запуска проверьте, что все сервисы healthy:
```bash
docker compose ps
```

Затем выполните тесты:
```bash
newman run postman/ewm-stat-service.json --delay-request 50 -r cli
newman run postman/ewm-main-service.json --delay-request 50 -r cli
newman run postman/feature.json --delay-request 50 -r cli
```

---

## Технологии

| Компонент        | Технологии                                      |
|------------------|-------------------------------------------------|
| Язык             | Java 21                                         |
| Фреймворк        | Spring Boot 3.5.0, Spring Data JPA, Spring Cloud |
| База данных      | PostgreSQL 15                                   |
| Микросервисы     | Spring Cloud Config, Eureka, Gateway, gRPC      |
| Очередь сообщений| Apache Kafka, Avro, Schema Registry             |
| Сборка           | Maven 3.9+                                      |
| Тестирование     | JUnit 5, Mockito, Postman (Newman)               |
| Прочее           | Lombok, MapStruct, Bean Validation, Actuator    |

---

## Автор

VanoStreyPracticum — в рамках обучения на платформе Яндекс Практикум.