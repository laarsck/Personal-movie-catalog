package com.movie.catalog.repository;

import com.movie.catalog.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностью {@link Review} в базе данных.
 *
 * <p>Расширяет {@link JpaRepository}, предоставляя CRUD операции, а также метод поиск рецензий по ID фильма для доступа к рецензиям.</p>
 *
 * <p>Основной метод:
 * <ul>
 *   <li>{@link #findByMovieId(Long)} - получение всех рецензий для указанного фильма</li>
 * </ul>
 * </p>
 *
 * @see Review
 * @see org.springframework.data.jpa.repository.JpaRepository
 */

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMovieId(Long movieId);
}