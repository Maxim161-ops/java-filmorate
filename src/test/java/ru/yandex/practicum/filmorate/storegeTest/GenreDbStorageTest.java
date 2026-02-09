package ru.yandex.practicum.filmorate.storegeTest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GenreDbStorageTest {

    @Autowired
    private GenreDbStorage genreStorage;

    @Test
    void testFindAllGenres() {
        List<Genre> genres = genreStorage.findAll();

        assertThat(genres).isNotNull();
        assertThat(genres).isNotEmpty();
    }

    @Test
    void testFindById() {
        Optional<Genre> genreOpt = genreStorage.findById(1);

        assertThat(genreOpt).isPresent();
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Genre> genreOpt = genreStorage.findById(999);

        assertThat(genreOpt).isEmpty();
    }
}
