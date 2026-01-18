package com.movie.catalog.service;

import com.movie.catalog.entity.*;
import com.movie.catalog.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Сервис для управления рецензиями.
 *
 * <p>Предоставляет набор операций для управления рецензиями:</p>
 * <ul>
 *   <li>Управление рецензиями через {@link #saveReview(Review)}, {@link #updateReview(Long, Review)}, {@link #deleteReview(Long)}</li>
 *   <li>Фильтрация рецензий по фильмам через {@link #getReviewsByMovieId(Long)}</li>
 *   <li>Получение данных через {@link #getAllReviews()} и {@link #getReviewById(Long)}</li>
 * </ul>
 *
 * <p><strong>Основные методы:</strong></p>
 * <ul>
 *   <li>{@link #getAllReviews()} - получает список всех рецензий</li>
 *   <li>{@link #getReviewById(Long)} - получает рецензию по её ID</li>
 *   <li>{@link #getReviewsByMovieId(Long)} - получает рецензии по ID фильма</li>
 *   <li>{@link #saveReview(Review)} - сохраняет рецензии в БД</li>
 *   <li>{@link #updateReview(Long, Review)} - обновляет информацию о существующей рецензии</li>
 *   <li>{@link #deleteReview(Long)} - удаляет рецензию по её ID</li>
 * </ul>
 *
 * @see ReviewRepository
 * @see Review
 * @see org.springframework.transaction.annotation.Transactional
 */

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired // автоматическое внедрение зависимости с сервисом
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public List<Review> getReviewsByMovieId(Long movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public Review updateReview(Long id, Review reviewDetails) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Рецензия не найден с ID: " + id));

        review.setRating(reviewDetails.getRating());
        review.setComment(reviewDetails.getComment());
        review.setWatchDate(reviewDetails.getWatchDate());

        return reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}