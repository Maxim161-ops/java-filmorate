package ru.yandex.practicum.filmorate.storegeTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(GenreDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;

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
        assertThat(genreOpt.get().getName()).isEqualTo("COMEDY");
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Genre> genreOpt = genreStorage.findById(999);
        assertThat(genreOpt).isEmpty();
    }
}
