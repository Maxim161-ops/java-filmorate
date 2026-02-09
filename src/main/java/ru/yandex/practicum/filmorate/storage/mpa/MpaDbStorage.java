package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAll() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Mpa(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
    }

    @Override
    public Optional<Mpa> findById(int id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        List<Mpa> result = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Mpa(
                        rs.getInt("id"),
                        rs.getString("name")
                ), id);
        return result.stream().findFirst();
    }
}
