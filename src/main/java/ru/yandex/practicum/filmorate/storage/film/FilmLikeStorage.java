package ru.yandex.practicum.filmorate.storage.film;

import java.util.Set;

public interface FilmLikeStorage {

    void addLike(int filmId, int userId);

    int removeLike(int filmId, int userId);

    Set<Integer> getLikes(int filmId);
}