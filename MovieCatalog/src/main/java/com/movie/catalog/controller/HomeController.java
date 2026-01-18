package com.movie.catalog.controller;

import com.movie.catalog.entity.*;
import com.movie.catalog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/**
 * Контроллер для управления главной страницей
 *
 * <p>Функциональность:
 * <ul>
 *   <li>Поиск фильмов по названию</li>
 *   <li>Фильтрация фильмов по жанрам</li>
 *   <li>Отображение всех фильмов</li>
 *   <li>Сбор статистики просмотров</li>
 *   <li>Сбор статистики по жанрам</li>
 * </ul>
 * </p>
 *
 * <p>Метод {@link #home(String, String, Model)} обрабатывает get запросы и предоставляет функциональность фильтрации и поиска.</p>
 *
 * @see MovieService
 * @see WatchHistoryService
 * @see Movie
 */

@Controller
public class HomeController {

    private final MovieService movieService;
    private final WatchHistoryService watchHistoryService;

    @Autowired // автоматическое внедрение зависимостей с сервисами
    public HomeController(MovieService movieService,
                          WatchHistoryService watchHistoryService) {
        this.movieService = movieService;
        this.watchHistoryService = watchHistoryService;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String query,
                       @RequestParam(required = false) String genre,
                       Model model) {

        List<Movie> movies;

        if (query != null && !query.trim().isEmpty()) {
            movies = movieService.searchByTitle(query);
            model.addAttribute("searchQuery", query);

        } else if (genre != null && !genre.trim().isEmpty()) {
            String decodedGenre = genre.replace("+", " ");
            movies = movieService.searchByGenre(decodedGenre);
            model.addAttribute("selectedGenre", decodedGenre);

        } else {
            movies = movieService.getAllMovies();
        }

        Set<String> allGenres = new HashSet<>();
        for (Movie movie : movieService.getAllMovies()) {
            if (movie.getGenre() != null && !movie.getGenre().trim().isEmpty()) {
                String[] genreArray = movie.getGenre().split(",");

                for (String g : genreArray) {String trimmedGenre = g.trim();

                    if (!trimmedGenre.isEmpty()) {allGenres.add(trimmedGenre);}
                }
            }
        }

        List<Object[]> watchStats = watchHistoryService.getWatchStatusStatistics();

        long completedCount = 0;
        long watchingCount = 0;
        long plannedCount = 0;

        if (watchStats != null) {
            for (Object[] stat : watchStats) {String status = (String) stat[0];
                Long count = (Long) stat[1];

                switch (status) {
                    case "Просмотрено":
                        completedCount = count;
                        break;
                    case "Смотрю":
                        watchingCount = count;
                        break;
                    case "Запланировано":
                        plannedCount = count;
                        break;
                }
            }
        }

        model.addAttribute("movies", movies);
        model.addAttribute("movieCount", movies.size());
        model.addAttribute("allGenres", allGenres);

        model.addAttribute("completedCount", completedCount);
        model.addAttribute("watchingCount", watchingCount);
        model.addAttribute("plannedCount", plannedCount);

        return "index";
    }
}