package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        validateReleaseDate(film.getReleaseDate());

        film.setId(nextId++);
        films.put(film.getId(), film);

        log.info("Добавлен фильм: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм не найден: id={}", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Фильм не найден");
        }

        validateReleaseDate(film.getReleaseDate());
        films.put(film.getId(), film);

        log.info("Обновлён фильм: {}", film);
        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен запрос на получение всех фильмов. Количество: {}", films.size());
        return films.values();
    }

    private void validateReleaseDate(LocalDate date) {
        if (date.isBefore(CINEMA_BIRTHDAY)) {
            log.error("Некорректная дата релиза: {}", date);
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (date.isAfter(LocalDate.now())) {
            log.error("Некорректная дата релиза (в будущем): {}", date);
            throw new ValidationException("Дата релиза не может быть в будущем");
        }
    }
}
