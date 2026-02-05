package ru.yandex.practicum.filmorate.storegeTest;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenreDbStorage;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(FilmGenreDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmGenreDbStorageTest {

    private final FilmGenreDbStorage filmGenreDbStorage;

    @Test
    void testSaveAndGetGenres() {
        Film film = new Film();
        film.setId(1);
        film.setGenres(Set.of(new Genre(1, "Comedy"), new Genre(2, "Drama")));

        filmGenreDbStorage.saveFilmGenres(film);

        Set<Genre> genres = filmGenreDbStorage.getGenresForFilm(film.getId());
        assertThat(genres).extracting("id").containsExactlyInAnyOrder(1, 2);
    }
}