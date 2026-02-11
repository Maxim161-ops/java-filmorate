package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final FilmGenreDbStorage filmGenreDbStorage;
    private final FilmLikeDbStorage filmLikeDbStorage;

    @Override
    public Film create(Film film) {
        validateReleaseDate(film);
        checkAndSetMpa(film, true);
        checkAndSetGenres(film);

        Film createdFilm = filmStorage.create(film);

        // Сохраняем жанры
        filmGenreDbStorage.saveFilmGenres(createdFilm);

        // Подтягиваем актуальные жанры из БД
        createdFilm.setGenres(filmGenreDbStorage.getGenresForFilm(createdFilm.getId()));

        createdFilm.setLikes(filmLikeDbStorage.getLikes(createdFilm.getId()));

        log.info("Создан фильм: id={}, name={}", createdFilm.getId(), createdFilm.getName());
        return createdFilm;
    }

    @Override
    public Film update(Film film) {
        validateReleaseDate(film);
        Film existingFilm = getFilmOrThrow(film.getId());

        checkAndSetMpa(film, false);
        checkAndSetGenres(film);

        Film updatedFilm = filmStorage.update(film);

        // Обновляем жанры
        filmGenreDbStorage.saveFilmGenres(updatedFilm);
        updatedFilm.setGenres(filmGenreDbStorage.getGenresForFilm(updatedFilm.getId()));

        log.info("Обновлён фильм: id={}, name={}", updatedFilm.getId(), updatedFilm.getName());
        return updatedFilm;
    }

    @Override
    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();
        log.debug("Получен список всех фильмов, count={}", films.size());
        return films;
    }

    @Override
    public Film findById(int id) {
        Film film = filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        log.debug("Найден фильм: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    // вспомогательные методы
    private void validateReleaseDate(Film film) {
        LocalDate firstFilmDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(firstFilmDate)) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
    }

    private void checkAndSetMpa(Film film, boolean isCreate) {
        if (film.getMpa() == null) {
            if (isCreate) {
                // При создании, если MPA не указан — ставим MPA с id=1
                film.setMpa(
                        mpaStorage.findById(1)
                                .orElseThrow(() -> new NotFoundException("MPA с id=1 не найден"))
                );
            }
            // При обновлении, если MPA отсутствует — ничего не делаем
        } else {
            // Если MPA указан, проверяем его существование
            int mpaId = film.getMpa().getId();
            film.setMpa(
                    mpaStorage.findById(mpaId)
                            .orElseThrow(() -> new NotFoundException("MPA с id=" + mpaId + " не найден"))
            );
        }
    }

    private void checkAndSetGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Genre> validatedGenres = new HashSet<>();
            for (Genre genre : film.getGenres()) {
                Genre validGenre = genreStorage.findById(genre.getId())
                        .orElseThrow(() -> new NotFoundException("Жанр с id=" + genre.getId() + " не найден"));
                validatedGenres.add(validGenre);
            }
            film.setGenres(validatedGenres); // убираем дубликаты
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmStorage.findPopularFilms(count);
    }

    private Film getFilmOrThrow(int filmId) {
        return filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));
    }
}