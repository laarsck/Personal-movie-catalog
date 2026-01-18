package com.movie.catalog.controller;

import com.movie.catalog.entity.*;
import com.movie.catalog.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Контроллер для управления рецензиями - обработка CRUD запросов
 *
 * <p>Функциональность:
 * <ul>
 *   <li>Создание рецензий</li>
 *   <li>Редактирование рецензий</li>
 *   <li>Просмотр списка рецензий</li>
 *   <li>Просмотр рецензий для фильма</li>
 *   <li>Удаление рецензий</li>
 * </ul>
 * </p>
 *
 * <p><strong>Основные методы:</strong></p>
 * <ul>
 *   <li>{@link #listReviews(Model)} - отображение списка всех рецензий</li>
 *   <li>{@link #showCreateForm(Long, Model)} - отображение формы для создания новой рецензии</li>
 *   <li>{@link #createReview(Review, BindingResult, RedirectAttributes, Model)} - обработка формы для создания новой рецензии</li>
 *   <li>{@link #showEditForm(Long, Model, RedirectAttributes)} - отображение формы для редактирования существующей рецензии</li>
 *   <li>{@link #updateReview(Long, Review, BindingResult, RedirectAttributes, Model)} - обработка обновлений данных рецензии</li>
 *   <li>{@link #listReviewsForMovie(Long, Model, RedirectAttributes)} - отображение рецензии для фильма</li>
 *   <li>{@link #deleteReview(Long, RedirectAttributes)} - удаление рецензии</li>
 * </ul>
 *
 * @see ReviewService
 * @see MovieService
 * @see Review
 */

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final MovieService movieService;

    @Autowired // автоматическое внедрение зависимости с сервисом
    public ReviewController(ReviewService reviewService,
                            MovieService movieService) {
        this.reviewService = reviewService;
        this.movieService = movieService;
    }

    @GetMapping
    public String listReviews(Model model) {
        List<Review> reviews = reviewService.getAllReviews();
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewCount", reviews.size());
        return "reviews/list";
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) Long movieId, Model model) {
        Review review = new Review();

        if (movieId != null) {
            Movie movie = movieService.getMovieById(movieId).orElse(null);

            if (movie != null) {
                review.setMovie(movie);
            }
        }

        model.addAttribute("review", review);
        model.addAttribute("allMovies", movieService.getAllMovies());
        model.addAttribute("action", "create");
        return "reviews/form";
    }

    @PostMapping
    public String createReview(@Valid @ModelAttribute("review") Review review,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        if (result.hasErrors()) {
            model.addAttribute("allMovies", movieService.getAllMovies());
            model.addAttribute("action", "create");
            return "reviews/form";
        }

        try {
            reviewService.saveReview(review);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Рецензия для фильма «" + review.getMovie().getTitle() + "» добавлена");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reviews/new";
        }

        return "redirect:/reviews";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        Review review = reviewService.getReviewById(id).orElse(null);

        if (review == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Рецензия не найдена");
            return "redirect:/reviews";
        }

        model.addAttribute("review", review);
        model.addAttribute("allMovies", movieService.getAllMovies());
        model.addAttribute("action", "edit");
        return "reviews/form";
    }

    @PostMapping("/update/{id}")
    public String updateReview(@PathVariable("id") Long id,
                               @Valid @ModelAttribute("review") Review review,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allMovies", movieService.getAllMovies());
            model.addAttribute("action", "edit");
            review.setId(id);
            return "reviews/form";
        }

        try {
            reviewService.updateReview(id, review);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Рецензия для фильма «" + review.getMovie().getTitle() + "» обновлена");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reviews/edit/" + id;
        }

        return "redirect:/reviews";
    }

    @GetMapping("/delete/{id}")
    public String deleteReview(@PathVariable("id") Long id,
                               RedirectAttributes redirectAttributes) {

        try {
            Review review = reviewService.getReviewById(id).orElse(null);

            if (review != null) {
                reviewService.deleteReview(id);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Рецензия для фильма «" + review.getMovie().getTitle() + "» удалена");

            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Рецензия не найдена");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при удалении рецензии: " + e.getMessage());
        }

        return "redirect:/reviews";
    }

    @GetMapping("/movie/{movieId}")
    public String listReviewsForMovie(@PathVariable("movieId") Long movieId,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        Movie movie = movieService.getMovieById(movieId).orElse(null);

        if (movie == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Фильм не найден");
            return "redirect:/movies";
        }

        List<Review> reviews = reviewService.getReviewsByMovieId(movieId);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewCount", reviews.size());
        model.addAttribute("movie", movie);
        return "reviews/list";
    }
}