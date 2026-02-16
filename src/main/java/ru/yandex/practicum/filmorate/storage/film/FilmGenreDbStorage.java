package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveFilmGenres(Film film) {
        jdbcTemplate.update(
                "DELETE FROM film_genres WHERE film_id = ?",
                film.getId()
        );

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(
                    "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                    film.getId(),
                    genre.getId()
            );
        }
    }

    @Override
    public Set<Genre> getGenresForFilm(int filmId) {
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

    @Override
    public Map<Integer, Set<Genre>> getGenresForFilms(Collection<Film> films) {

        if (films.isEmpty()) {
            return new HashMap<>();
        }

        List<Integer> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        String sql = """
        SELECT fg.film_id, g.id, g.name
        FROM film_genres fg
        JOIN genres g ON fg.genre_id = g.id
        WHERE fg.film_id IN (%s)
        """.formatted(
                filmIds.stream()
                        .map(id -> "?")
                        .collect(Collectors.joining(","))
        );

        Map<Integer, Set<Genre>> result = new HashMap<>();

        jdbcTemplate.query(sql, filmIds.toArray(), rs -> {
            int filmId = rs.getInt("film_id");

            Genre genre = new Genre(
                    rs.getInt("id"),
                    rs.getString("name")
            );

            result.computeIfAbsent(filmId, k -> new HashSet<>())
                    .add(genre);
        });

        return result;
    }
}