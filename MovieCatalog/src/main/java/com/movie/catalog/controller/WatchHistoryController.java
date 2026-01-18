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

import java.time.LocalDate;
import java.util.List;

/**
 * Контроллер для управления историей просмотра - обработка CRUD запросов
 *
 * <p>Функциональность:
 * <ul>
 *   <li>Добавление фильмов в историю</li>
 *   <li>Изменение статуса фильма</li>
 *   <li>Быстрое добавление фильма</li>
 *   <li>Просмотр истории фильма</li>
 * </ul>
 * </p>
 *
 * <p><strong>Основные методы:</strong></p>
 * <ul>
 *   <li>{@link #listWatchHistory(Model)} - отображение списка всей истории просмотра</li>
 *   <li>{@link #showCreateForm(Long, Model)} - отображение формы для создания новой записи о просмотре</li>
 *   <li>{@link #createWatchHistory(WatchHistory, BindingResult, Long, RedirectAttributes, Model)} - обработка формы для создания новой записи о просмотре</li>
 *   <li>{@link #addToWatchHistory(Long, RedirectAttributes)} - добавление фильма по статусу запланировано</li>
 *   <li>{@link #showEditForm(Long, Model, RedirectAttributes)} - отображение формы для редактирования существующей записи о просмотре</li>
 *   <li>{@link #updateWatchHistory(Long, WatchHistory, BindingResult, Long, RedirectAttributes, Model) - обработка обновлений данных записи о просмотре</li>
 *   <li>{@link #deleteWatchHistory(Long, RedirectAttributes)} - удаление записи о просмотре из БД</li>
 *   <li>{@link #changeStatus(Long, String, RedirectAttributes)} - изменение статуса просмотра фильма</li>
 * </ul>
 *
 * @see WatchHistoryService
 * @see MovieService
 * @see WatchHistory
 */

@Controller
@RequestMapping("/watch-history")
public class WatchHistoryController {

    private final WatchHistoryService watchHistoryService;
    private final MovieService movieService;

    @Autowired // автоматическое внедрение зависимости с сервисом
    public WatchHistoryController(WatchHistoryService watchHistoryService,
                                  MovieService movieService) {
        this.watchHistoryService = watchHistoryService;
        this.movieService = movieService;
    }

    @GetMapping
    public String listWatchHistory(Model model) {
        List<WatchHistory> watchHistory = watchHistoryService.getAllWatchHistory();
        model.addAttribute("watchHistory", watchHistory);
        model.addAttribute("watchHistoryCount", watchHistory.size());

        List<Object[]> stats = watchHistoryService.getWatchStatusStatistics();
        model.addAttribute("watchStats", stats);

        return "watch-history/list";
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) Long movieId,
                                 Model model) {
        WatchHistory watchHistory = new WatchHistory();
        watchHistory.setAddedDate(LocalDate.now());

        if (movieId != null) {
            Movie movie = movieService.getMovieById(movieId).orElse(null);

            if (movie != null) {
                watchHistory.setMovie(movie);
            }
        }

        model.addAttribute("watchHistory", watchHistory);
        model.addAttribute("allMovies", movieService.getAllMovies());
        model.addAttribute("statusOptions", List.of("Запланировано", "Смотрю", "Просмотрено", "Брошено"));
        model.addAttribute("action", "create");
        return "watch-history/form";
    }

    @PostMapping
    public String createWatchHistory(@Valid @ModelAttribute("watchHistory") WatchHistory watchHistory,
                                     BindingResult result,
                                     @RequestParam("movie.id") Long movieId,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allMovies", movieService.getAllMovies());
            model.addAttribute("statusOptions", List.of("Запланировано", "Смотрю", "Просмотрено", "Брошено"));
            model.addAttribute("action", "create");
            return "watch-history/form";
        }

        try {
            List<WatchHistory> existingEntries = watchHistoryService.getWatchHistoryByMovieId(movieId);
            if (!existingEntries.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Этот фильм уже есть в истории просмотров");
                return "redirect:/watch-history/new?movieId=" + movieId;
            }

            Movie movie = movieService.getMovieById(movieId).orElseThrow(() -> new RuntimeException("Фильм не найден с ID: " + movieId));

            watchHistory.setMovie(movie);
            watchHistoryService.saveWatchHistory(watchHistory);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Фильм «" + movie.getTitle() + "» добавлен в историю просмотра");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/watch-history/new";
        }

        return "redirect:/watch-history";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        WatchHistory watchHistory = watchHistoryService.getWatchHistoryById(id).orElse(null);

        if (watchHistory == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Запись истории просмотра не найдена");
            return "redirect:/watch-history";
        }

        model.addAttribute("watchHistory", watchHistory);
        model.addAttribute("allMovies", movieService.getAllMovies());
        model.addAttribute("statusOptions", List.of("Запланировано", "Смотрю", "Просмотрено", "Брошено"));
        model.addAttribute("action", "edit");
        return "watch-history/form";
    }

    @PostMapping("/update/{id}")
    public String updateWatchHistory(@PathVariable("id") Long id,
                                     @Valid @ModelAttribute("watchHistory") WatchHistory watchHistory,
                                     BindingResult result,
                                     @RequestParam("movie.id") Long movieId,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allMovies", movieService.getAllMovies());
            model.addAttribute("statusOptions", List.of("Запланировано", "Смотрю", "Просмотрено", "Брошено"));
            model.addAttribute("action", "edit");
            watchHistory.setId(id);
            return "watch-history/form";
        }

        try {
            Movie movie = movieService.getMovieById(movieId).orElseThrow(() -> new RuntimeException("Фильм не найден с ID: " + movieId));

            List<WatchHistory> existingEntries = watchHistoryService.getWatchHistoryByMovieId(movieId);
            if (existingEntries.size() > 0) {
                boolean isDuplicate = existingEntries.stream().anyMatch(entry -> !entry.getId().equals(id));

                if (isDuplicate) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Этот фильм уже есть в другой записи истории просмотров");
                    return "redirect:/watch-history/edit/" + id;
                }
            }

            watchHistory.setMovie(movie);
            watchHistoryService.updateWatchHistory(id, watchHistory);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Запись истории просмотра для фильма «" + movie.getTitle() + "» обновлена");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/watch-history/edit/" + id;
        }

        return "redirect:/watch-history";
    }

    @GetMapping("/delete/{id}")
    public String deleteWatchHistory(@PathVariable("id") Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            WatchHistory watchHistory = watchHistoryService.getWatchHistoryById(id).orElse(null);

            if (watchHistory != null) {
                String movieTitle = watchHistory.getMovie() != null ? watchHistory.getMovie().getTitle() : "Неизвестный фильм";
                watchHistoryService.deleteWatchHistory(id);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Запись истории просмотра для фильма «" + movieTitle + "» успешно удалена");

            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Запись истории просмотра не найдена");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при удалении записи истории просмотра: " + e.getMessage());
        }

        return "redirect:/watch-history";
    }

    @GetMapping("/add/{movieId}")
    public String addToWatchHistory(@PathVariable("movieId") Long movieId,
                                    RedirectAttributes redirectAttributes) {
        try {
            Movie movie = movieService.getMovieById(movieId).orElseThrow(() -> new RuntimeException("Фильм не найден"));

            List<WatchHistory> existingEntries = watchHistoryService.getWatchHistoryByMovieId(movieId);

            if (!existingEntries.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Фильм «" + movie.getTitle() + "» уже есть в истории просмотров");
                return "redirect:/watch-history";
            }

            watchHistoryService.addMovieToWatchHistory(movie, "Запланировано");
            redirectAttributes.addFlashAttribute("successMessage",
                    "Фильм «" + movie.getTitle() + "» добавлен в список запланированных");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/watch-history";
    }

    @GetMapping("/change-status/{movieId}/{newStatus}")
    public String changeStatus(@PathVariable("movieId") Long movieId,
                               @PathVariable("newStatus") String newStatus,
                               RedirectAttributes redirectAttributes) {
        try {
            Movie movie = movieService.getMovieById(movieId).orElseThrow(() -> new RuntimeException("Фильм не найден"));

            watchHistoryService.changeWatchStatus(movieId, newStatus);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Статус фильма «" + movie.getTitle() + "» изменен на " + newStatus);

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/watch-history";
    }
}