package ru.yandex.practicum.filmorate.storegeTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({UserDbStorage.class, UserRowMapper.class, FriendsDbStorage.class})
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateUserAndSetNameFromLoginIfEmpty() {
        User user = new User();
        user.setEmail("test@test.ru");
        user.setLogin("login");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userDbStorage.create(user);

        assertThat(created.getId()).isPositive();
        assertThat(created.getName()).isEqualTo(created.getLogin());
    }

    @Test
    void shouldFindUserById() {
        jdbcTemplate.update("""
                    INSERT INTO users (id, email, login, name, birthday)
                    VALUES (1, 'a@a.ru', 'login', 'name', '2000-01-01')
                """);

        User user = userDbStorage.findById(1).orElse(null);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
    }
}