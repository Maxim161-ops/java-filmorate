package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmServiceImpl(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    @Override
    public Film create(Film film) {
        Film created = filmStorage.create(film);
        log.info("Создан фильм: id={}, name={}", created.getId(), created.getName());
        return created;
    }

    @Override
    public Film update(Film film) {
        Film existingFilm = filmStorage.findById(film.getId());

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
        log.debug("Запрос фильма с id={}", id);
        return filmStorage.findById(id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = findById(filmId);
        userService.findById(userId); // проверка существования пользователя

        boolean added = film.getLikes().add(userId);

        if (added) {
            log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        } else {
            log.info("Пользователь {} уже ставил лайк фильму {}", userId, filmId);
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
    public Collection<Film> getPopular(int count) {
        log.debug("Запрошен список популярных фильмов, count={}", count);

        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }
}
