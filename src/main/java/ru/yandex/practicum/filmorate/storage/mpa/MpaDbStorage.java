package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Mpa> mpaRowMapper = new RowMapper<>() {
        @Override
        public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Mpa(
                    rs.getInt("id"),
                    rs.getString("name")
            );
        }
    };

    @Override
    public Collection<Mpa> findAll() {
        String sql = "SELECT id, name FROM mpa ORDER BY id";
        return jdbcTemplate.query(sql, mpaRowMapper);
    }

    @Override
    public Mpa findById(int id) {
        String sql = "SELECT id, name FROM mpa WHERE id = ?";
        return jdbcTemplate.query(sql, mpaRowMapper, id)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("Рейтинг с id=" + id + " не найден"));
    }
}
