package ru.yandex.practicum.filmorate.storegeTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmLikeDbStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({FilmDbStorage.class, FilmRowMapper.class, FilmGenreDbStorage.class, FilmLikeDbStorage.class})
class FilmDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private FilmGenreDbStorage filmGenreDbStorage;

    @Autowired
    private FilmLikeDbStorage filmLikeDbStorage;

    @BeforeEach
    void setup() {
        // Очищаем таблицы перед каждым тестом
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM genres");
        jdbcTemplate.update("DELETE FROM mpa");
        jdbcTemplate.update("DELETE FROM users");

        jdbcTemplate.update("MERGE INTO mpa (id, name) KEY(id) VALUES (1, 'G')");
        jdbcTemplate.update("MERGE INTO genres (id, name) KEY(id) VALUES (1, 'Comedy')");
        jdbcTemplate.update("MERGE INTO users (id, email, login, name, birthday) KEY(id) " +
                "VALUES (1, 'a@a.com', 'login', 'User', '2000-01-01')");
    }

    @Test
    void shouldCreateFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));

        Film created = filmDbStorage.create(film);

        assertThat(created.getId()).isPositive();
    }

    @Test
    void shouldAddGenreToFilm() {
        Film film = new Film();
        film.setName("FilmWithGenre");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));

        Film created = filmDbStorage.create(film);

        created.setGenres(Set.of(new Genre(1, "Comedy")));
        filmGenreDbStorage.saveFilmGenres(created);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_genres WHERE film_id = ? AND genre_id = ?",
                Integer.class, created.getId(), 1
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldAddLikeToFilm() {
        Film film = new Film();
        film.setName("FilmWithLike");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(90);
        film.setMpa(new Mpa(1, "G"));

        Film created = filmDbStorage.create(film);

        // Добавляем лайк
        filmLikeDbStorage.addLike(created.getId(), 1);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?",
                Integer.class, created.getId(), 1);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testSaveAndGetGenres() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));

        Film created = filmDbStorage.create(film);
        created.setGenres(Set.of(new Genre(1, "Comedy")));
        filmGenreDbStorage.saveFilmGenres(created);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_genres WHERE film_id = ? AND genre_id = ?",
                Integer.class, created.getId(), 1
        );
        assertThat(count).isEqualTo(1);
    }
}
