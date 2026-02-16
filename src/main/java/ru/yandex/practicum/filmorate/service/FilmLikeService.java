package ru.yandex.practicum.filmorate.service;

public interface FilmLikeService {

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);
}