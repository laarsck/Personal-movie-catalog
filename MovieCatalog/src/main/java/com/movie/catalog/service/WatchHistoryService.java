package com.movie.catalog.service;

import com.movie.catalog.entity.*;
import com.movie.catalog.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/**
 * Сервис для управления историей просмотра.
 *
 * <p>Предоставляет набор операций для управления рецензиями:</p>
 * <ul>
 *   <li>Перевод статусов через {@link #translateStatusToRussian(String)} и {@link #translateStatusToEnglish(String)}</li>
 *   <li>Статистические отчеты через {@link #getWatchStatusStatistics()}</li>
 * </ul>
 *
 * <p><strong>Основные методы:</strong></p>
 * <ul>
 *   <li>{@link #getAllWatchHistory()} - получает весь список истории просмотра</li>
 *   <li>{@link #getWatchHistoryById(Long)} - получает запись об истории просмотра по её ID</li>
 *   <li>{@link #getWatchHistoryByMovieId(Long)} - получает все записи истории просмотра для фильма</li>
 *   <li>{@link #saveWatchHistory(WatchHistory)} - сохраняет новую запись истории просмотра</li>
 *   <li>{@link #updateWatchHistory(Long, WatchHistory)} - обновляет данные о существующей записи истории просмотра</li>
 *   <li>{@link #deleteWatchHistory(Long)} - удаляет запись истории просмотра по её ID</li>
 *   <li>{@link #addMovieToWatchHistory(Movie, String)} - быстро добавляет фильм в историю просмотра со статусом запланировано</li>
 *   <li>{@link #changeWatchStatus(Long, String)} - изменяет статус просмотра для фильма</li>
 *   <li>{@link #getWatchStatusStatistics()} - статистика по статусам</li>
 * </ul>
 *
 * @see WatchHistoryRepository
 * @see MovieService
 * @see WatchHistory
 * @see org.springframework.transaction.annotation.Transactional
 */

@Service
@Transactional
public class WatchHistoryService {

    private final WatchHistoryRepository watchHistoryRepository;
    private final MovieService movieService;

    @Autowired // автоматическое внедрение зависимости с сервисом
    public WatchHistoryService(WatchHistoryRepository watchHistoryRepository,
                               MovieService movieService) {
        this.watchHistoryRepository = watchHistoryRepository;
        this.movieService = movieService;
    }

    public List<WatchHistory> getAllWatchHistory() {
        List<WatchHistory> history = watchHistoryRepository.findAll();
        history.forEach(this::translateStatusToRussian);
        return history;
    }

    public Optional<WatchHistory> getWatchHistoryById(Long id) {
        return watchHistoryRepository.findById(id)
                .map(this::translateStatusToRussian);
    }

    public List<WatchHistory> getWatchHistoryByMovieId(Long movieId) {
        List<WatchHistory> history = watchHistoryRepository.findByMovieId(movieId);
        history.forEach(this::translateStatusToRussian);
        return history;
    }

    public WatchHistory saveWatchHistory(WatchHistory watchHistory) {
        watchHistory.setStatus(translateStatusToEnglish(watchHistory.getStatus()));
        return watchHistoryRepository.save(watchHistory);
    }

    public WatchHistory updateWatchHistory(Long id,
                                           WatchHistory watchHistoryDetails) {
        WatchHistory watchHistory = watchHistoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Запись истории просмотра не найдена с ID: " + id));

        watchHistoryDetails.setStatus(translateStatusToEnglish(watchHistoryDetails.getStatus()));

        watchHistory.setStatus(watchHistoryDetails.getStatus());
        watchHistory.setAddedDate(watchHistoryDetails.getAddedDate());
        watchHistory.setCompletedDate(watchHistoryDetails.getCompletedDate());

        if (watchHistoryDetails.getMovie() != null) {
            watchHistory.setMovie(watchHistoryDetails.getMovie());
        }

        return watchHistoryRepository.save(watchHistory);
    }

    public void deleteWatchHistory(Long id) { watchHistoryRepository.deleteById(id); }

    public WatchHistory addMovieToWatchHistory(Movie movie,
                                               String status) {

        WatchHistory watchHistory = new WatchHistory();
        watchHistory.setMovie(movie);
        watchHistory.setStatus(translateStatusToEnglish(status));
        watchHistory.setAddedDate(LocalDate.now());

        return watchHistoryRepository.save(watchHistory);
    }

    public WatchHistory changeWatchStatus(Long movieId, String newStatus) {
        WatchHistory watchHistory = watchHistoryRepository.findByMovieIdAndStatus(movieId, "watching").orElseThrow(() -> new RuntimeException("Фильм не найден в истории просмотра"));

        watchHistory.setStatus(translateStatusToEnglish(newStatus));

        if ("completed".equals(newStatus)) {
            watchHistory.setCompletedDate(LocalDate.now());
        }
        return watchHistoryRepository.save(watchHistory);
    }

    public List<Object[]> getWatchStatusStatistics() { return watchHistoryRepository.getWatchStatusStatistics(); }

    private WatchHistory translateStatusToRussian(WatchHistory watchHistory) {
        if (watchHistory != null) {
            String russianStatus = translateStatusToRussian(watchHistory.getStatus());
            watchHistory.setStatus(russianStatus);
        }
        return watchHistory;
    }

    private String translateStatusToRussian(String englishStatus) {
        if (englishStatus == null) return null;

        return switch (englishStatus.toLowerCase()) {
            case "planned" -> "Запланировано";
            case "watching" -> "Смотрю";
            case "completed" -> "Просмотрено";
            case "dropped" -> "Брошено";
            default -> englishStatus;
        };
    }

    private String translateStatusToEnglish(String russianStatus) {
        if (russianStatus == null) return null;

        return switch (russianStatus.toLowerCase()) {
            case "запланировано" -> "planned";
            case "смотрю" -> "watching";
            case "просмотрено" -> "completed";
            case "брошено" -> "dropped";
            default -> russianStatus;
        };
    }
}