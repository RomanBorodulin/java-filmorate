# 📽️Кинопоиск для своих
#### Социальная сеть, которая поможет выбрать кино на основе того, какие фильмы смотрят друзья и какие оценки они ставят.
___
### Инструкция по развёртыванию/использованию
1. Склонируйте репозиторий:
   `git clone https://github.com/RomanBorodulin/java-filmorate`
2. Перейдите в директорию проекта:
   `cd java-filmorate`
3. Соберите проект и запустите приложение с использованием Maven:
   `mvn clean install`
   `mvn spring-boot:run`
___
### Системные требования
* Java 11
* Apache Maven 3.6.0 или выше
___
### Cтек технологий                
* Java
* Spring Boot                               
* Maven
* Lombok
* PostgreSQL
* JDBC
___                                      
### API Reference
* PUT /users/{id}/friends/{friendId} — добавление в друзья

* DELETE /users/{id}/friends/{friendId} — удаление из друзей

* GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями

* GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем

* PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму

* DELETE /films/{id}/like/{userId} — пользователь удаляет лайк

* GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, вернутся первые 10

* GET /genres - получение списка всех жанров фильмов

* GET /genres/{id} - получение жанров по идентификатору

* GET /mpa - список рейтинга фильмов

* GET /mpa/{id} - рейтинг по идентификатору
___
### Диаграмма базы данных

<br>![ER-диаграмма базы данных](ER-diagram.png)
___
### Примеры запросов для основных операций

<details>
  <summary>Получить фильм с id=1</summary>

```sql
    SELECT *
    FROM films
    WHERE id = 1;
```

</details>  

<details>
  <summary>Удалить пользователя с id=1</summary>

```sql
    DELETE
    FROM users
    WHERE id = 1;
```

</details>

<details>
  <summary>Получить топ-10 фильмов по количеству лайков</summary>

```sql
    SELECT f.*,

    COUNT(l.user_id) AS count_likes

    FROM films AS f

    LEFT JOIN likes AS l ON f.id=l.film_id

    GROUP BY f.id

    ORDER BY count_likes DESC

    LIMIT 10;
```

</details>  

