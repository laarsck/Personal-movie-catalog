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

/**
 * Контроллер для управления фильмами - обработка CRUD запросов
 *
 * <p>Функциональность:
 * <ul>
 *   <li>Создание новых фильмов</li>
 *   <li>Редактирование существующих фильмов</li>
 *   <li>Просмотр детальной информации о фильме</li>
 *   <li>Удаление фильмов</li>
 * </ul>
 * </p>
 *
 * <p><strong>Основные методы:</strong></p>
 * <ul>
 *   <li>{@link #showCreateForm(Model)} - отображение формы создания фильма</li>
 *   <li>{@link #createMovie(Movie, BindingResult, RedirectAttributes, Model)} - обработка создания фильма</li>
 *   <li>{@link #showEditForm(Long, Model, RedirectAttributes)} - отображение формы редактирования</li>
 *   <li>{@link #updateMovie(Long, Movie, BindingResult, RedirectAttributes, Model)} - обработка обновления фильма</li>
 *   <li>{@link #viewMovie(Long, Model, RedirectAttributes)} - просмотр деталей фильма</li>
 *   <li>{@link #deleteMovie(Long, RedirectAttributes)} - удаление фильма</li>
 * </ul>
 *
 * @see MovieService
 * @see Movie
 * @see jakarta.validation.Valid
 * @see org.springframework.validation.BindingResult
 * @see org.springframework.web.servlet.mvc.support.RedirectAttributes
 */

@Controller
@RequestMapping("/movies")
public class MovieController {

    @Autowired // автоматическое внедрение зависимости с сервисом
    private MovieService movieService;

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("action", "create");
        return "movies/form";
    }

    @PostMapping
    public String createMovie(@Valid @ModelAttribute("movie") Movie movie,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        if (result.hasErrors()) {
            model.addAttribute("action", "create");
            return "movies/form";
        }

        try {
            movieService.saveMovie(movie);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Фильм «" + movie.getTitle() + "» добавлен");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/movies/new";
        }

        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        Movie movie = movieService.getMovieById(id).orElse(null);

        if (movie == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Фильм не найден");
            return "redirect:/";
        }

        model.addAttribute("movie", movie);
        model.addAttribute("action", "edit");
        return "movies/form";
    }

    @PostMapping("/update/{id}")
    public String updateMovie(@PathVariable Long id,
                              @Valid @ModelAttribute("movie") Movie movie,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        if (result.hasErrors()) {
            model.addAttribute("action", "edit");
            return "movies/form";
        }

        try {
            movieService.updateMovie(id, movie);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Фильм «" + movie.getTitle() + "» обновлен");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/movies/edit/" + id;
        }

        return "redirect:/";
    }

    @GetMapping("/view/{id}")
    public String viewMovie(@PathVariable Long id, Model model,
                            RedirectAttributes redirectAttributes) {
        Movie movie = movieService.getMovieById(id).orElse(null);

        if (movie == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Фильм не найден");
            return "redirect:/";
        }

        model.addAttribute("movie", movie);
        return "movies/view";
    }

    @GetMapping("/delete/{id}")
    public String deleteMovie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Movie movie = movieService.getMovieById(id).orElse(null);

            if (movie != null) {
                movieService.deleteMovie(id);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Фильм «" + movie.getTitle() + "» удален");

            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Фильм не найден");
            }

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/";
    }
}