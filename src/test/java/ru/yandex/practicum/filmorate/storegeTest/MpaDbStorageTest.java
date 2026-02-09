package ru.yandex.practicum.filmorate.storegeTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MpaDbStorageTest {

    @Autowired
    private MpaDbStorage mpaStorage;

    @Test
    void testFindById() {
        Optional<Mpa> mpaOpt = mpaStorage.findById(1);
        assertThat(mpaOpt).isPresent();
        assertThat(mpaOpt.get().getName()).isEqualTo("G");
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Mpa> mpaOpt = mpaStorage.findById(999);
        assertThat(mpaOpt).isEmpty();
    }

    @Test
    void testFindAllMpa() {
        List<Mpa> allMpa = mpaStorage.findAll();
        assertThat(allMpa).isNotEmpty();
    }
}
