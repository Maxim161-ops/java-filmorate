package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@Service
@RequiredArgsConstructor
public class FilmLikeServiceImpl implements FilmLikeService {

    private final FilmLikeDbStorage filmLikeDbStorage;
    private final FilmStorage filmStorage;

    @Override
    public void addLike(int filmId, int userId) {
        // Проверяем, что фильм существует
        filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));

        filmLikeDbStorage.addLike(filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        // Проверяем, что фильм существует
        filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));

        filmLikeDbStorage.removeLike(filmId, userId);
    }
}
