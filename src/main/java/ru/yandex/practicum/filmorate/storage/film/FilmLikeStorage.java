package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface FilmLikeStorage {

    void addLike(int filmId, int userId);

    int removeLike(int filmId, int userId);

    Set<Integer> getLikes(int filmId);

    Map<Integer, Set<Long>> getLikesForFilms(Collection<Film> films);
}