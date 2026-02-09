package ru.yandex.practicum.filmorate.storegeTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(GenreDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;

    @Autowired
    private MpaStorage mpaStorage;

    @Test
    void testFindAllGenres() {
        Collection<Genre> genres = genreStorage.findAll();
        assertThat(genres).isNotEmpty();
        assertThat(genres).extracting("id").contains(1, 2, 3, 4, 5, 6);
    }

    @Test
    void testFindById() {
        Optional<Genre> genreOpt = genreStorage.findById(1);
        assertThat(genreOpt).isPresent();
        assertThat(genreOpt.get().getName()).isEqualTo("Комедия");
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Mpa> mpaOpt = mpaStorage.findById(999);
        assertThat(mpaOpt).isEmpty();
    }
}
