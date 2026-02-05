package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmGenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public void saveFilmGenres(Film film) {
        // удаление старых жанров
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        Set<Genre> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) return;

        // добавляем новые
        for (Genre genre : genres) {
            jdbcTemplate.update(
                    "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                    film.getId(),
                    genre.getId() // используем id из объекта, а не ordinal
            );
        }
    }

    public Set<Genre> getGenresForFilm(Long filmId) {

        return new LinkedHashSet<>(jdbcTemplate.query(
                "SELECT g.id, g.name " +
                        "FROM film_genres fg " +
                        "JOIN genres g ON fg.genre_id = g.id " +
                        "WHERE fg.film_id = ? " +
                        "ORDER BY g.id",
                (rs, rowNum) -> new Genre(
                        rs.getInt("id"),
                        rs.getString("name")
                ),
                filmId
        ));
    }
}