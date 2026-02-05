package ru.yandex.practicum.filmorate.storegeTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.user.FriendsDbStorage;


import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(FriendsDbStorage.class)
class FriendsDbStorageTest {

    @Autowired
    private FriendsDbStorage friendsDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldAddAndRemoveFriend() {
        jdbcTemplate.update("""
            INSERT INTO users (id, email, login, name, birthday)
            VALUES 
            (1, 'a@a.ru', 'a', 'a', '2000-01-01'),
            (2, 'b@b.ru', 'b', 'b', '2000-01-01')
        """);

        friendsDbStorage.addFriend(1, 2);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friends WHERE user_id = 1 AND friend_id = 2",
                Integer.class
        );

        assertThat(count).isEqualTo(1);

        friendsDbStorage.removeFriend(1, 2);

        count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friends WHERE user_id = 1 AND friend_id = 2",
                Integer.class
        );

        assertThat(count).isZero();
    }
}
