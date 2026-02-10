package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.HashSet;
import java.util.Set;


@Repository
@RequiredArgsConstructor
public class FilmLikeDbStorage {

    private final JdbcTemplate jdbc;

    public void addLike(int filmId, int userId) {
        jdbc.update(
                "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)",
                filmId, userId
        );
    }

    public void removeLike(int filmId, int userId) {
        int rows = jdbc.update(
                "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?",
                filmId, userId
        );
        if (rows == 0) {
            throw new NotFoundException("Лайк не найден");
        }
    }

    public Set<Integer> getLikes(int filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbc.query(
                sql,
                (rs, rowNum) -> rs.getInt("user_id"),
                filmId
        ));
    }
}
