package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmServiceImpl(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    @Override
    public Film create(Film film) {
        validateReleaseDate(film); // Проверка даты перед созданием
        Film created = filmStorage.create(film);
        log.info("Создан фильм: id={}, name={}", created.getId(), created.getName());
        return created;
    }

    @Override
    public Film update(Film film) {
        Film existing = filmStorage.findById(film.getId())
                .orElseThrow(() -> new NotFoundException(
                        "Фильм с id=" + film.getId() + " не найден"
                ));

        Film updated = filmStorage.update(film);
        log.info("Обновлён фильм: id={}, name={}", updated.getId(), updated.getName());
        return updated;
    }

    @Override
    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();
        log.debug("Получен список фильмов, count={}", films.size());
        return films;
    }

    @Override
    public Film findById(int id) {
        return filmStorage.findById(id).orElseThrow(() ->
                        new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = findById(filmId);
        userService.findById(userId); // проверка существования пользователя
        boolean added = film.getLikes().add(userId);
        if (added) {
            log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = findById(filmId);
        userService.findById(userId);

        boolean removed = film.getLikes().remove(userId);
        if (removed) {
            log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
        } else {
            log.info("Лайка пользователя {} у фильма {} не было", userId, filmId);
        }
    }

    @Override
    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10")@Positive(
            message = "Количество фильмов должно быть больше 0")
            int count) {
        log.debug("Запрошен список популярных фильмов, count={}", count);
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException(
                    "Дата релиза не может быть раньше 28.12.1895"
            );
        }
    }
}
// я поошибке ее уже смержил
// не знаю правильно ли я исправил