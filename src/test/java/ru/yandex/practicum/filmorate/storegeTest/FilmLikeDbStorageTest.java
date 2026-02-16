package ru.yandex.practicum.filmorate.storegeTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.film.FilmLikeDbStorage;


import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(FilmLikeDbStorage.class)
class FilmLikeDbStorageTest {

    @Autowired
    private FilmLikeDbStorage filmLikeDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Test
    void shouldAddLike() {
        jdbcTemplate.update("""
                    INSERT INTO users (id, email, login, name, birthday)
                    VALUES (1, 'a@a.ru', 'a', 'a', '2000-01-01')
                """);

        jdbcTemplate.update("""
                    INSERT INTO films (id, name, description, release_date, duration, mpa_id)
                    VALUES (1, 'film', 'desc', '2020-01-01', 100, 1)
                """);

        filmLikeDbStorage.addLike(1, 1);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE film_id = 1 AND user_id = 1",
                Integer.class
        );

        assertThat(count).isEqualTo(1);
    }
}
