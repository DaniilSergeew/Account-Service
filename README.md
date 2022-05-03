# О проекте
REST API для учета сотрудников компании
## Технологии
- Java
- Spring Framework
- Spring Security
- Hibernate
- H2 Database
## Преимущества
- Защищенное соединение по HTTPS протоколу
- Разграничение прав пользователей: администратора, бухгалтера, аудитора безопасности и иного работника
- Блокировка пользователя поле пяти неудачных попыток входа
- Журналирование наиболее важных событый, связанных с безопасностью
- Возможность вести бухгалтерский учет
### Конечные точки и уровни доступа
|endponit                       | Anonymous | User |Accountant|Administrator|Auditor|
|:--------:                     |:--------: |:----:|:--------:|:-----------:|:-----:|
|```POST api/auth/signup```     | +         | +    |+         | +           |+      |
|```POST api/auth/changepass``` |           | +    |+         | +           |  -    |       
|```GET api/empl/payment```     | -         | +    |+         | -           |  -    |   
|```POST api/acct/payments```   | -         |  -   |+         | -           |  -    |
|```PUT api/acct/payments```    | -         |  -   |+         | -           |  -    |
|```GET api/admin/user```       | -         |  -   |-         |        +    |  -    |
|```DELETE api/admin/user```    | -         |  -   |-         |        +    |  -    |
|```PUT api/admin/user/role```  | -         |  -   |-         |        +    |  -    |
|```PUT api/admin/user/access```| -         |  -   |-         |        +    |  -    |
|```GET api/security/events```  | -         |  -   |-         |-            |+      |
# Примеры
## Регистрация пользователя
### ```POST api/auth/signup``` request

Request body:
```json
{
   "name": "Admin",
   "lastname": "Admin",
   "email": "admin@acme.com",
   "password": "secret"
}
```
Response body:
```json
{
    "id": 1,
    "name": "Admin",
    "lastname": "Admin",
    "email": "admin@acme.com",
    "roles": [
        "ROLE_ADMINISTRATOR"
    ]
}
```

**Пользователь, зарегистрировавшийся первым, автоматически назначается администратором**

## Назначение роли
### ```PUT api/admin/user/role``` request с авторизацией администратора

Request body:
```json
{
   "user": "accountant@acme.com",
   "role": "ACCOUNTANT",
   "operation": "GRANT"
}
```
Response body:
```json
{
    "id": 3,
    "name": "accountant",
    "lastname": "accountant",
    "email": "accountant@acme.com",
    "roles": [
        "ROLE_ACCOUNTANT",
        "ROLE_USER"
    ]
}

```
**Для удаление роли используйте операцию REMOVE**

## Загрузка платежей
### ```POST api/acct/payments``` request с авторизацией бухгалтера

Request body:
```json
[
    {
        "employee": "user@acme.com",
        "period": "01-2021",
        "salary": 123457
    },
    {
        "employee": "user@acme.com",
        "period": "02-2021",
        "salary": 123456
    },
    {
        "employee": "user@acme.com",
        "period": "03-2021",
        "salary": 123456
    }
]

```
Response body:
```json
{
    "status": "Added successfully!"
}
```
**Период должен быть уникальным**

## Просмотр платежа
### ```GET api/empl/payment?period=01-2021``` request с авторизацией пользователя

Response body:
```json
{
   "name": "User",
   "lastname": "User",
   "period": "January-2021",
   "salary": "123456 dollar(s) 56 cent(s)"
}
```


## Просмотр платежей
### ```GET api/empl/payment``` request с авторизацией пользователя

Response body:
```json
[
    {
       "name": "User",
       "lastname": "User",
       "period": "March-2021",
       "salary": "1234 dollar(s) 56 cent(s)"
    },
    {
       "name": "User",
       "lastname": "User",
       "period": "February-2021",
       "salary": "1234 dollar(s) 56 cent(s)"
    },
    {
       "name": "User",
       "lastname": "User",
       "period": "January-2021",
       "salary": "1234 dollar(s) 56 cent(s)"
    }
]
```
## Блокировка/разблокировка пользователя
### ```PUT api/admin/user/access``` request с авторизацией администратора

Request body:
```json
{
   "user": "user@acme.com",
   "operation": "LOCK" //UNLOCK для разблокировки
}

```
Response body:
```json
{
    "status": "User user@acme.com locked!"
}
```
**Администратора заблокировать невозможно**

## Просмотр журнала
### ```GET api/auth/signup``` request с авторизацией аудитора

Response body:
```json
[
{
  "id" : 1,
  "date" : "<date>",
  "action" : "CREATE_USER",
  "subject" : "Anonymous", \\ A User is not defined, fill with Anonymous
  "object" : "johndoe@acme.com",
  "path" : "/api/auth/signup"
}, {
  "id" : 6,
  "date" : "<date>",
  "action" : "LOGIN_FAILED",
  "subject" : "maxmustermann@acme.com",
  "object" : "/api/empl/payment", \\ the endpoint where the event occurred
  "path" : "/api/empl/payment"
}, {
  "id" : 9,
  "date" : "<date>",
  "action" : "GRANT_ROLE",
  "subject" : "johndoe@acme.com",
  "object" : "Grant role ACCOUNTANT to petrpetrov@acme.com",
  "path" : "/api/admin/user/role"
}, {
  "id" : 10,
  "date" : "<date>",
  "action" : "REMOVE_ROLE",
  "subject" : "johndoe@acme.com",
  "object" : "Remove role ACCOUNTANT from petrpetrov@acme.com",
  "path" : "/api/admin/user/role"
}, {
  "id" : 11,
  "date" : "<date>",
  "action" : "DELETE_USER",
  "subject" : "johndoe@acme.com",
  "object" : "petrpetrov@acme.com",
  "path" : "/api/admin/user"
}, {
  "id" : 12,
  "date" : "<date>",
  "action" : "CHANGE_PASSWORD",
  "subject" : "johndoe@acme.com",
  "object" : "johndoe@acme.com",
  "path" : "/api/auth/changepass"
}, {
  "id" : 16,
  "date" : "<date>",
  "action" : "ACCESS_DENIED",
  "subject" : "johndoe@acme.com",
  "object" : "/api/acct/payments", \\ the endpoint where the event occurred
  "path" : "/api/acct/payments"
}, {
  "id" : 25,
  "date" : "<date>",
  "action" : "BRUTE_FORCE",
  "subject" : "maxmustermann@acme.com",
  "object" : "/api/empl/payment", \\ the endpoint where the event occurred
  "path" : "/api/empl/payment"
}, {
  "id" : 26,
  "date" : "<date>",
  "action" : "LOCK_USER",
  "subject" : "maxmustermann@acme.com",
  "object" : "Lock user maxmustermann@acme.com",
  "path" : "/api/empl/payment" \\ the endpoint where the lock occurred
}, {
  "id" : 27,
  "date" : "<date>",
  "action" : "UNLOCK_USER",
  "subject" : "johndoe@acme.com",
  "object" : "Unlock user maxmustermann@acme.com",
  "path" : "/api/admin/user/access"
}
]
```

### Список журналируемых событий
|Описание                       | Событие   | 
|:--------:                     |:--------: |
|Пользователь успешно зарегистрирован| CREATE_USER         |
|Пользователь успешно изменил пароль|CHANGE_PASSWORD|      
|Пользователь пытается получить доступ к ресурсу без прав доступа|ACCESS_DENIED|  
|Неудачная аутентификация|LOGIN_FAILED|
|Роль предоставляется пользователю|GRANT_ROLE| 
|Роль была отозвана|REMOVE_ROLE| 
|Администратор заблокировал пользователя|LOCK_USER| 
|Администратор разблокировал пользователя|UNLOCK_USER| 
|Администратор удалил пользователя|DELETE_USER| 
|Пользователь был заблокирован по подозрению в атаке методом перебора|BRUTE_FORCE| 
