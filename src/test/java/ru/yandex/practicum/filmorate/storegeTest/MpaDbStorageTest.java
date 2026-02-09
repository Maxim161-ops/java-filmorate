package ru.yandex.practicum.filmorate.storegeTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Import(MpaDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

    private final MpaDbStorage mpaStorage;

    @Test
    void testFindAllMpa() {
        Collection<Mpa> allMpa = mpaStorage.findAll();
        assertThat(allMpa).isNotEmpty();
        assertThat(allMpa).extracting("id").contains(1, 2, 3, 4, 5);
    }

    @Test
    void testFindById() {
        Mpa mpa = mpaStorage.findById(1)
                .orElseThrow(() -> new RuntimeException("MPA с id=1 не найден"));
        assertThat(mpa).isNotNull();
        assertThat(mpa.getName()).isEqualTo("G");
    }

    @Test
    void testFindByIdNotFound() {
        assertThatThrownBy(() -> mpaStorage.findById(999))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("не найден");
    }
}
