package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmLikeDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public void addLike(long filmId, long userId) {
        jdbcTemplate.update(
                "INSERT INTO likes (film_id, user_id) VALUES (?, ?)",
                filmId, userId
        );
    }

    public void removeLike(long filmId, long userId) {
        jdbcTemplate.update(
                "DELETE FROM likes WHERE film_id = ? AND user_id = ?",
                filmId, userId
        );
    }

    public Set<Long> getLikes(long filmId) {
        return new HashSet<>(
                jdbcTemplate.query(
                        "SELECT user_id FROM likes WHERE film_id = ?",
                        (rs, rowNum) -> rs.getLong("user_id"),
                        filmId
                )
        );
    }
}
