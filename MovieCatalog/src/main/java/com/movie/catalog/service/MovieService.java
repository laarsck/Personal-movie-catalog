package com.movie.catalog.service;

import com.movie.catalog.entity.*;
import com.movie.catalog.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Сервис для управления фильмами.
 *
 * <p>Предоставляет набор операций для управления фильмами:
 * <ul>
 *   <li>CRUD операции через {@link #saveMovie(Movie)}, {@link #updateMovie(Long, Movie)}, {@link #deleteMovie(Long)}</li>
 *   <li>Поиск фильмов через {@link #searchByTitle(String)} и {@link #searchByGenre(String)}</li>
 *   <li>Получение данных через {@link #getAllMovies()} и {@link #getMovieById(Long)}</li>
 * </ul>
 * </p>
 *
 * <p><strong>Основные методы:</strong></p>
 * <ul>
 *   <li>{@link #updateMovie(Long, Movie)} - обновляет информацию о существующем фильме</li>
 *   <li>{@link #saveMovie(Movie)} - сохраняет новый фильм в БД</li>
 *   <li>{@link #getAllMovies()} - получает список всех фильмов из БД</li>
 *   <li>{@link #getMovieById(Long)} - получает фильм по его ID</li>
 *   <li>{@link #deleteMovie(Long)} - удаляет фильм по его Id</li>
 *   <li>{@link #searchByTitle(String)} - выполняет поиск фильмов по названию</li>
 *   <li>{@link #searchByGenre(String)} - выполнет поиск фильмов по жанру</li>
 * </ul>
 *
 * @see MovieRepository
 * @see Movie
 * @see org.springframework.transaction.annotation.Transactional
 */

@Service
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired // автоматическое внедрение зависимости с сервисом
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie updateMovie(Long id, Movie movieDetails) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Фильм не найден с ID: " + id));

        movie.setTitle(movieDetails.getTitle());
        movie.setReleaseYear(movieDetails.getReleaseYear());
        movie.setDescription(movieDetails.getDescription());
        movie.setRating(movieDetails.getRating());
        movie.setDurationMinutes(movieDetails.getDurationMinutes());
        movie.setGenre(movieDetails.getGenre());

        return movieRepository.save(movie);
    }

    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    public List<Movie> searchByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Movie> searchByGenre(String genre) {
        return movieRepository.findByGenreContainingIgnoreCase(genre);
    }
}