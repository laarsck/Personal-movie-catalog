package com.movie.catalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

/**
 * Сущность, представляющая фильм в каталоге.
 *
 * <p>Основная сущность системы, содержащая информацию о фильме:
 * <ul>
 *   <li>Основные атрибуты фильма: название, год выпуска, описание, рейтинг, длительность, жанры</li>
 *   <li>Связи с рецензиями и историей просмотров</li>
 * </ul>
 * </p>
 *
 * <p>Аннотации валидации обеспечивают проверку данных при создании и обновлении фильмов. Сущность связана с {@link Review} и {@link WatchHistory} через отношения один ко многим.</p>
 *
 * @see Review
 * @see WatchHistory
 * @see jakarta.validation.constraints
 */

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название фильма обязательно")
    @Size(min = 1, max = 200, message = "Название должно быть от 1 до 200 символов")
    private String title;

    @NotNull(message = "Год выпуска обязателен")
    @Min(value = 1800, message = "Год выпуска должен быть не раньше 1800")
    @Max(value = 2026, message = "Год выпуска должен быть не позже 2026")
    private Integer releaseYear;

    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    @DecimalMin(value = "1.0", message = "Рейтинг должен быть не менее 1.0")
    @DecimalMax(value = "10.0", message = "Рейтинг должен быть не более 10.0")
    private Float rating;

    @Min(value = 1, message = "Длительность должна быть не менее 1 минуты")
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Size(max = 100, message = "Жанр не должен превышать 100 символов")
    @Column(name = "genre", length = 100)
    private String genre;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WatchHistory> watchHistories = new ArrayList<>();

    public Movie(String title,
                 Integer releaseYear,
                 String description,
                 Float rating) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.description = description;
        this.rating = rating;
    }

    public Movie(String title,
                 Integer releaseYear,
                 String description,
                 Float rating,
                 Integer durationMinutes,
                 String genre) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.description = description;
        this.rating = rating;
        this.durationMinutes = durationMinutes;
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseYear=" + releaseYear +
                ", description='" + (description != null ? description.substring(0, Math.min(description.length(), 50)) : "") + '\'' +
                ", rating=" + rating +
                ", durationMinutes=" + durationMinutes +
                ", genre='" + genre + '\'' +
                '}';
    }
}