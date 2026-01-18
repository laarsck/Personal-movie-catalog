package com.movie.catalog.repository;

import com.movie.catalog.entity.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Репозиторий для работы с сущностью {@link WatchHistory} в базе данных.
 *
 * <p>Расширяет {@link JpaRepository}, предоставляя CRUD операции, а также методы для управления историей просмотров.</p>
 *
 * <p><strong>Основные методы:</strong></p>
 * <ul>
 *   <li>{@link #findByMovieId(Long)} - поиск записей по ID фильма</li>
 *   <li>{@link #findByMovieIdAndStatus(Long, String)} - поиск записей по ID фильма и статусу</li>
 *   <li>{@link #getWatchStatusStatistics()} - получение статистики по статусам просмотра</li>
 * </ul>
 *
 * @see WatchHistory
 * @see org.springframework.data.jpa.repository.JpaRepository
 */

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    List<WatchHistory> findByMovieId(Long movieId);

    Optional<WatchHistory> findByMovieIdAndStatus(Long movieId, String status);

    @Query("SELECT wh.status, COUNT(wh) FROM WatchHistory wh GROUP BY wh.status")
    List<Object[]> getWatchStatusStatistics();
}