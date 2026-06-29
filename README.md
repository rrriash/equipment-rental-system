# Система аренды учебного оборудования

Три независимых Spring Boot приложения в одном Maven-проекте.

## Порты сервисов

| Сервис            | Порт |
|-------------------|------|
| user-service      | 8081 |
| equipment-service | 8082 |
| booking-service   | 8083 |

## Сборка

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home \
  mvn clean package -DskipTests
```

## Запуск (три отдельных терминала)

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home \
  java -jar user-service/target/user-service-1.0.0.jar

JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home \
  java -jar equipment-service/target/equipment-service-1.0.0.jar

JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home \
  java -jar booking-service/target/booking-service-1.0.0.jar
```

---

## user-service — примеры запросов (порт 8081)

**Создать пользователя**
```bash
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Иван Иванов","email":"ivan@uni.ru","role":"STUDENT"}'
```

**Получить список пользователей**
```bash
curl http://localhost:8081/users
```

**Заблокировать пользователя**
```bash
curl -X PATCH http://localhost:8081/users/1/block
```

---

## equipment-service — примеры запросов (порт 8082)

**Создать категорию**
```bash
curl -X POST http://localhost:8082/equipment-categories \
  -H "Content-Type: application/json" \
  -d '{"name":"Компьютерная техника","description":"Ноутбуки и проекторы"}'
```

**Добавить оборудование**
```bash
curl -X POST http://localhost:8082/equipment \
  -H "Content-Type: application/json" \
  -d '{"name":"Ноутбук Dell","type":"LAPTOP","description":"14 дюймов","inventoryNumber":"INV-001","categoryId":1}'
```

**Получить доступное оборудование**
```bash
curl http://localhost:8082/equipment/available
```

---

## booking-service — примеры запросов (порт 8083)

**Создать бронирование**
```bash
curl -X POST http://localhost:8083/bookings \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"equipmentId":1,"startDate":"2026-07-01","endDate":"2026-07-05"}'
```

**Подтвердить бронирование**
```bash
curl -X PATCH http://localhost:8083/bookings/1/approve
```

**Получить уведомления по бронированию**
```bash
curl http://localhost:8083/bookings/1/notifications
```

---

---

## Этап 4 — Межсервисное взаимодействие

### Что реализовано

- Синхронное взаимодействие booking-service с user-service (проверка существования и статуса пользователя)
- Синхронное взаимодействие booking-service с equipment-service (проверка статуса оборудования, обновление статуса при approve / issue / return)
- Основной сценарий создания бронирования: валидация → проверка пользователя → проверка оборудования → создание заявки
- Изменение статуса оборудования: BOOKED при approve, ISSUED при issue, AVAILABLE при return
- Mock-отправка уведомлений: статус SENT, вывод в лог `[MOCK NOTIFICATION]`
- Обработка corner cases с конкретными кодами ошибок
- Логирование времени выполнения POST /bookings (SLA)

### Порядок запуска

1. `user-service` (порт 8081)
2. `equipment-service` (порт 8082)
3. `booking-service` (порт 8083) — зависит от первых двух

> Целевое требование SLA: сценарий POST /bookings должен успешно выполняться при доступности user-service и equipment-service. Время выполнения логируется: `POST /bookings completed in N ms`.

### Полный сценарий (curl)

**1. Создать пользователя**
```bash
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Иван Иванов","email":"ivan@uni.ru","role":"STUDENT"}'
```

**2. Создать категорию**
```bash
curl -X POST http://localhost:8082/equipment-categories \
  -H "Content-Type: application/json" \
  -d '{"name":"Компьютерная техника","description":"Ноутбуки и проекторы"}'
```

**3. Создать оборудование**
```bash
curl -X POST http://localhost:8082/equipment \
  -H "Content-Type: application/json" \
  -d '{"name":"Ноутбук Dell","type":"LAPTOP","description":"14 дюймов","inventoryNumber":"INV-001","categoryId":1}'
```

**4. Создать бронирование** _(booking-service проверяет user и equipment)_
```bash
curl -X POST http://localhost:8083/bookings \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"equipmentId":1,"startDate":"2026-07-01","endDate":"2026-07-05"}'
```

**5. Подтвердить бронирование** _(оборудование → BOOKED, создаётся уведомление)_
```bash
curl -X PATCH http://localhost:8083/bookings/1/approve
```

**6. Выдать оборудование** _(оборудование → ISSUED)_
```bash
curl -X PATCH http://localhost:8083/bookings/1/issue
```

**7. Вернуть оборудование** _(оборудование → AVAILABLE)_
```bash
curl -X PATCH http://localhost:8083/bookings/1/return
```

**8. Получить уведомления по бронированию**
```bash
curl http://localhost:8083/bookings/1/notifications
```

### Corner cases

| Ситуация | HTTP | Код ошибки |
|----------|------|-----------|
| Пользователь не найден | 404 | `USER_NOT_FOUND` |
| Пользователь заблокирован | 403 | `USER_BLOCKED` |
| Оборудование не найдено | 404 | `EQUIPMENT_NOT_FOUND` |
| Оборудование не AVAILABLE | 409 | `EQUIPMENT_NOT_AVAILABLE` |
| startDate >= endDate | 400 | `INVALID_BOOKING_PERIOD` |
| Пересечение дат | 409 | `BOOKING_PERIOD_CONFLICT` |
| Лимит 3 активных бронирования | 409 | `TOO_MANY_ACTIVE_BOOKINGS` |
| user-service / equipment-service недоступен | 503 | `SERVICE_UNAVAILABLE` |

---

## Формат ошибок

Все сервисы возвращают единый формат:

```json
{
  "code": "EQUIPMENT_NOT_FOUND",
  "message": "Оборудование не найдено",
  "details": "Оборудование с id = 99 не существует"
}
```

| Статус | Код                              | Когда                               |
|--------|----------------------------------|-------------------------------------|
| 400    | `VALIDATION_ERROR`               | Не прошла валидация полей           |
| 404    | `USER_NOT_FOUND`                 | Пользователь не найден              |
| 404    | `EQUIPMENT_NOT_FOUND`            | Оборудование не найдено             |
| 404    | `BOOKING_NOT_FOUND`              | Бронирование не найдено             |
| 409    | `EMAIL_ALREADY_EXISTS`           | Email уже занят                     |
| 409    | `INVENTORY_NUMBER_ALREADY_EXISTS`| Инвентарный номер уже занят         |
| 409    | `BOOKING_CONFLICT`               | Пересечение дат или лимит           |
| 409    | `INVALID_BOOKING_OPERATION`      | Недопустимый переход статуса        |
| 500    | `INTERNAL_ERROR`                 | Внутренняя ошибка                   |
