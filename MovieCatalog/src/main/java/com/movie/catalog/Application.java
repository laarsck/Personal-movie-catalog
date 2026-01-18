package com.movie.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p><strong>Главный класс Spring Boot приложения для управления каталогом фильмов.</strong></p>
 *
 * <p>Точка входа в приложение, которая запускает Spring Boot и выполняет конфигурацию.</p>
 */

 @SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}