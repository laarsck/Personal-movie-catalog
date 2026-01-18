package com.movie.catalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Сущность, представляющая рецензию на фильм.
 *
 * <p>Содержит оценку и комментарий пользователя к фильму, а также дату просмотра. Каждая рецензия связана с конкретным фильмом через отношение многие к одному.</p>
 *
 * <p>Основная сущность системы, содержащая информацию о фильме:
 * <ul>
 *    <li>Основные атрибуты фильма: оценка, отзыв, дата просмотра, ID фильма</li>
 *    <li>Связь с фильмами</li>
 * </ul>
 * </p>
 *
 * <p>Позволяет пользователю оставлять рецензии на добавленные фильмы.</p>
 * <p>Аннотации валидации обеспечивают проверку данных при создании и обновлении рецензий. Сущность связана с {@link Movie} через отношение многие к одному.</p>
 *
 * @see Movie
 */

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Оценка обязательна")
    @DecimalMin(value = "1.0", message = "Оценка должна быть не менее 1.0")
    @DecimalMax(value = "10.0", message = "Оценка должна быть не более 10.0")
    @Column(name = "rating", nullable = false)
    private Float rating;

    @Size(max = 1000, message = "Отзыв не должен превышать 1000 символов")
    @Column(name = "comment", length = 1000)
    private String comment;

    @NotNull(message = "Дата просмотра обязательна")
    @Column(name = "watch_date", nullable = false)
    private LocalDate watchDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    public Review(Float rating,
                  String comment,
                  LocalDate watchDate,
                  Movie movie) {
        this.rating = rating;
        this.comment = comment;
        this.watchDate = watchDate;
        this.movie = movie;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", rating=" + rating +
                ", comment='" + (comment != null ? comment.substring(0, Math.min(comment.length(), 50)) : "") + '\'' +
                ", watchDate=" + watchDate +
                ", movieId=" + (movie != null ? movie.getId() : null) +
                '}';
    }
}