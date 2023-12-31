# java-shareit
Template repository for Shareit project.

### **_Бэкенд для сервиса шеринга (от англ. share — «делиться») вещей._**
Сервис решает проблему связанную с необходимостью приобретения вещей для временного использования. Вместо того, чтобы покупать новую вещь, пользователи могут найти ее на сервисе и взять в аренду на определенное время. Это позволяет экономить деньги и ресурсы, а также уменьшает нагрузку на окружающую среду.

Функционал сервиса позволяет бронировать вещь на определенные даты и закрывает к ней доступ на время бронирования от других желающих. Если нужной вещи на сервисе нет, пользователи могут оставлять запросы, по которым можно добавлять новые вещи для шеринга. Это обеспечивает удобство и гибкость для пользователей и помогает им находить нужные вещи для временного использования. Так же реализована возможность оставлять отзывы после того как пользователь воспользовался вещью.

Микросервисная архитектура
Приложение состоит из 2 сервисов:

Gateway. Принимает запросы от пользователей. Распределяет нагрузку, выполняет первичную проверку и направляет запросы дальше в основной сервис
Server. Серверная часть приложения. Получает запросы, выполняет операции, отправляет данные клиенту

# Спринт №13

### **_Реализация модели данных_**

Cтруктура по фичам — весь код для работы с определённой сущностью в одном пакете.
Всего четыре пакета — item, booking, request и user.
В каждом из этих пакетов свои контроллеры, сервисы, репозитории.

### **_Создание DTO-объектов и мапперов_**

Разделение объектов, которые хранятся в базе данных и которые возвращаются пользователям.
Для реализация отдельная версия каждого класса, с которой будут работать пользователи
— DTO (Data Transfer Object).
Mapper-классы — помогают преобразовывать объекты модели в DTO-объекты и обратно.

### **_Разработка контроллеров_**

Основные сценарии, которые поддерживает приложение:
* Добавление новой вещи. Происходит по эндпойнту POST /items. На вход поступает объект ItemDto.
  userId в заголовке X-Sharer-User-Id — это идентификатор пользователя, который добавляет вещь.
  Именно этот пользователь — владелец вещи. Идентификатор владельца поступает на вход в каждом из
  запросов, рассмотренных далее.
* Редактирование вещи. Эндпойнт PATCH /items/{itemId}. Изменить можно название, описание и статус
  доступа к аренде. Редактировать вещь может только её владелец.
* Просмотр информации о конкретной вещи по её идентификатору. Эндпойнт GET /items/{itemId}.
  Информацию о вещи может просмотреть любой пользователь.
* Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой.
  Эндпойнт GET /items.
* Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст,
  и система ищет вещи, содержащие этот текст в названии или описании.
  Происходит по эндпойнту /items/search?text={text}, в text передаётся текст для поиска.


# Спринт №14

### **_Создание базы данных_**

В ней по одной таблице для каждой из основных сущностей, а также таблица, где хранятся отзывы.
SQL-код для создания всех таблиц хранится в файле resources/schema.sql — Spring Boot выполняет
содержащийся в нём скрипт на старте проекта, все конструкции в этом файле поддерживают
множественное выполнение.

![img.png](schema.png)

### **_Реализация функции бронирования_**

Основные сценарии, которые поддерживает приложение:
* Добавление нового запроса на бронирование. Запрос может быть создан любым пользователем,
  а затем подтверждён владельцем вещи. Эндпоинт — POST /bookings.
  После создания запрос находится в статусе WAITING — «ожидает подтверждения».
* Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
  Затем статус бронирования становится либо APPROVED, либо REJECTED.
  Эндпоинт — PATCH /bookings/{bookingId}?approved={approved},
  параметр approved может принимать значения true или false.
* Получение данных о конкретном бронировании (включая его статус).
  Может быть выполнено либо автором бронирования, либо владельцем вещи,
  к которой относится бронирование. Эндпоинт — GET /bookings/{bookingId}.
* Получение списка всех бронирований текущего пользователя.
  Эндпоинт — GET /bookings?state={state}. Параметр state необязательный и
  по умолчанию равен ALL (англ. «все»). Также он может принимать значения
  CURRENT (англ. «текущие»), PAST (англ. «завершённые»), FUTURE (англ. «будущие»),
  WAITING (англ. «ожидающие подтверждения»), REJECTED (англ. «отклонённые»).
  Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
* Получение списка бронирований для всех вещей текущего пользователя.
  Эндпоинт — GET /bookings/owner?state={state}. Этот запрос имеет смысл для владельца
  хотя бы одной вещи. Работа параметра state аналогична его работе в предыдущем сценарии.

### **_Добавление отзывов_**

* Отзыв можно добавить по эндпоинту POST /items/{itemId}/comment.
* Отзывы можно увидеть по двум эндпоинтам — по GET /items/{itemId}
  для одной конкретной вещи и по GET /items для всех вещей данного пользователя.


# Спринт №15

### **_Добавление запроса вещи_**

Пользователь создаёт такой запрос, когда не может найти нужную вещь,
воспользовавшись поиском, но при этом надеется, что у кого-то она всё же имеется.
Другие пользователи могут просматривать подобные запросы и,
если у них есть описанная вещь и они готовы предоставить её в аренду,
добавлять нужную вещь в ответ на запрос.

* POST /requests — добавить новый запрос вещи.
  Основная часть запроса — текст запроса, где пользователь описывает, какая именно вещь ему нужна.
* GET /requests — получить список своих запросов вместе с данными об ответах на них.
  Для каждого запроса указывается описание, дата и время создания и список ответов в формате:
  id вещи, название, id владельца. Так в дальнейшем, используя указанные id вещей,
  можно получить подробную информацию о каждой вещи.
  Запросы возвращаются в отсортированном порядке от более новых к более старым.
* GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями.
  С помощью этого эндпоинта пользователи могут просматривать существующие запросы,
  на которые они могли бы ответить. Запросы сортируются по дате создания: от более новых к более старым.
  Результаты возвращаются постранично. Для этого нужно передать два параметра:
  from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения.
* GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах
  на него в том же формате, что и в эндпоинте GET /requests.
  Посмотреть данные об отдельном запросе может любой пользователь.

### **_Добавление опции ответа на запрос_**
Добавлена возможность при создании вещи указать id запроса, в ответ на который создаётся нужная вещь.
Добавлено поле requestId в тело запроса POST /items.
Сохраняется возможность добавить вещь и без указания requestId.


### **_Добавление пагинации к существующим эндпоинтам_**

Добавлена пагинация в эндпоинты GET /items, GET /items/search, GET /bookings и GET /bookings/owner.
Параметры такие же, как и для эндпоинта на получение запросов вещей:
номер первой записи и желаемое количество элементов для отображения.

### **_Добавление тестов_**

Написаны тесты, проверяющие реализацию на соответствие требованиям. 


# Спринт №16

### **_Приложение ShareIt разбито на два — shareIt-server и shareIt-gateway._**

* Приложение shareIt-server содержит всю основную логику.
* Приложение shareIt-gateway содержит валидацию входных данных.
* Каждое из приложений запускается как самостоятельное Java-приложение, а их общение происходит через REST.

### **_Настроен запуск ShareIt через Docker._**

* Приложения shareIt-server, shareIt-gateway и база данных PostgreSQL запускаются 
в отдельном Docker-контейнере каждый. Их взаимодействие настроено через Docker Compose.

