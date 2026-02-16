package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmLikeDbStorage implements FilmLikeStorage {

    private final JdbcTemplate jdbc;

    @Override
    public void addLike(int filmId, int userId) {
        jdbc.update(
                "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)",
                filmId, userId
        );
    }

    @Override
    public int removeLike(int filmId, int userId) {
        return jdbc.update(
                "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?",
                filmId, userId
        );
    }

    @Override
    public Set<Integer> getLikes(int filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbc.query(sql,
                (rs, rowNum) -> rs.getInt("user_id"),
                filmId
        ));
    }

    @Override
    public Map<Integer, Set<Long>> getLikesForFilms(Collection<Film> films) {

        if (films.isEmpty()) {
            return new HashMap<>();
        }

        List<Integer> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        String sql = """
            SELECT film_id, user_id
            FROM film_likes
            WHERE film_id IN (%s)
            """.formatted(
                filmIds.stream()
                        .map(id -> "?")
                        .collect(Collectors.joining(","))
        );

        Map<Integer, Set<Long>> result = new HashMap<>();

        jdbc.query(sql, filmIds.toArray(), rs -> {
            int filmId = rs.getInt("film_id");
            long userId = rs.getLong("user_id");

            result.computeIfAbsent(filmId, k -> new HashSet<>())
                    .add(userId);
        });

        return result;
    }
}

