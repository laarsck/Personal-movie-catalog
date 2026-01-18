package com.movie.catalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Сущность, представляющая запись в истории просмотров.
 *
 * <p>Основная сущность системы, содержащая информацию о просмотре фильма:
 * <ul>
 *   <li>Основные атрибуты фильма: статус, дата добавления, дата завершения просмотра, ID фильма</li>
 *   <li>Связь с фильмами</li>
 * </ul>
 * </p>
 *
 * <p>Позволяет пользователю отслеживать прогресс просмотра фильмов и собирать статистику по просмотрам.</p>
 * <p>Аннотации валидации обеспечивают проверку данных при создании и обновлении статуса просмотра фильма. Сущность связана с {@link Movie} через отношение многие к одному.</p>
 *
 * @see Movie
 */

@Entity
@Table(name = "watch_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Статус обязателен")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @NotNull(message = "Дата добавления обязательна")
    @Column(name = "added_date", nullable = false)
    private LocalDate addedDate;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    public WatchHistory(String status,
                        LocalDate addedDate,
                        Movie movie) {
        this.status = status;
        this.addedDate = addedDate;
        this.movie = movie;
    }

    @Override
    public String toString() {
        return "WatchHistory{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", addedDate=" + addedDate +
                ", completedDate=" + completedDate +
                ", movieId=" + (movie != null ? movie.getId() : null) +
                '}';
    }
}