package ru.yandex.practicum.filmorate.storegeTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@Import({
        FilmDbStorage.class,
        FilmGenreDbStorage.class,
        FilmRowMapper.class
})
class FilmGenreDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private FilmLikeDbStorage filmLikeDbStorage;

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private FilmGenreDbStorage filmGenreDbStorage;

    @BeforeEach
    void setup() {
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM genres");
        jdbcTemplate.update("DELETE FROM mpa");

        jdbcTemplate.update(
                "MERGE INTO mpa (id, name) KEY(id) VALUES (1, 'G')"
        );

        jdbcTemplate.update(
                "MERGE INTO genres (id, name) KEY(id) VALUES (1, 'COMEDY')"
        );

        jdbcTemplate.update(
                "MERGE INTO genres (id, name) KEY(id) VALUES (2, 'DRAMA')"
        );
    }

    @Test
    void testSaveAndGetGenres() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));

        Film created = filmDbStorage.create(film);

        created.setGenres(Set.of(
                new Genre(1, "COMEDY"),
                new Genre(2, "DRAMA")
        ));

        filmGenreDbStorage.saveFilmGenres(created);
        Set<Genre> genres = filmGenreDbStorage.getGenresForFilm(created.getId());

        // Проверяем id
        assertThat(genres)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1, 2);

        // Проверяем названия
        assertThat(genres)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("COMEDY", "DRAMA");
    }
}