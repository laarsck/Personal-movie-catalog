# Movie Catalog

Movie Catalog — веб-приложение для управления персональным каталогом фильмов, разработанное на Spring Boot Web MVC. Приложение позволяет пользователю управлять фильмами, оставлять рецензии, отслеживать историю просмотров и получать статистику по просмотру.

<img width="1914" height="941" alt="image" src="https://github.com/user-attachments/assets/04f47592-e095-4586-8e1d-1dfb332f969a" />

## Функциональность
- CRUD операции: создание, чтение, обновление, удаление фильмов
- Поиск: поиск фильмов по названию и жанру
- Фильтрация: фильтрация по жанрам
- Детальная информация: полное описание, рейтинг, длительность, год выпуска

## Структура проекта
```
movie-catalog/
├── src/main/java/com/movie/catalog/
│   ├── Application.java              # главный класс
│   ├── controller/
│   │   ├── HomeController.java       # контроллер главной страницы
│   │   ├── MovieController.java      # контроллер фильмов
│   │   ├── ReviewController.java     # контроллер рецензий
│   │   └── WatchHistoryController.java # контроллер истории просмотра
│   ├── entity/
│   │   ├── Movie.java               # сущность фильма
│   │   ├── Review.java              # сущность рецензии
│   │   └── WatchHistory.java        # сущность истории просмотра
│   ├── repository/                  # репозитории для работы с БД
│   │   ├── MovieRepository.java
│   │   ├── ReviewRepository.java
│   │   └── WatchHistoryRepository.java
│   └── service/                     # бизнес-логика
│       ├── MovieService.java
│       ├── ReviewService.java
│       └── WatchHistoryService.java
├── src/main/resources/
│   ├── static/                      # стили приложения
│   ├── templates/
│   │   ├── index.html              # главная страница
│   │   ├── movies/                 # шаблоны для фильмов
│   │   ├── reviews/                # шаблоны для рецензий
│   │   └── watch-history/          # шаблоны для истории просмотров
│   └── application.properties       # Конфигурация приложения
└── pom.xml
```
