package com.movie.catalog.repository;

import com.movie.catalog.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностью {@link Movie} в базе данных.
 *
 * <p>Расширяет {@link JpaRepository}, предоставляя CRUD операции, а также специализированные методы для поиска фильмов.</p>
 *
 * <p>Основные методы:
 * <ul>
 *   <li>{@link #findByTitleContainingIgnoreCase(String)} - поиск по названию</li>
 *   <li>{@link #findByGenreContainingIgnoreCase(String)} - поиск по жанру</li>
 * </ul>
 * </p>
 *
 * @see Movie
 * @see org.springframework.data.jpa.repository.JpaRepository
 */

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByGenreContainingIgnoreCase(String genre);
}