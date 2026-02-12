package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface FilmGenreStorage {

    void saveFilmGenres(Film film);

    Set<Genre> getGenresForFilm(int filmId);

    Map<Integer, Set<Genre>> getGenresForFilms(Collection<Film> films);


}
