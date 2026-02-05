package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreDbStorage filmGenreDbStorage;
    private final FilmRowMapper filmRowMapper;
    private final FilmLikeDbStorage filmLikeDbStorage;

    @Override
    public Film create(Film film) {
        validateReleaseDate(film);

        // Если MPA не передан, ставим дефолтный (id = 1)
        if (film.getMpa() == null) {
            film.setMpa(new Mpa(1, null)); // name можно оставить null
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId()); // теперь гарантированно не null
            return ps;
        }, keyHolder);

        // Получаем сгенерированный ID и устанавливаем в объект
        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(generatedId);

        // Сохраняем жанры фильма (если есть)
        filmGenreDbStorage.saveFilmGenres(film);

        // Подгружаем лайки и жанры
        film.setLikes(filmLikeDbStorage.getLikes(generatedId));
        film.setGenres(filmGenreDbStorage.getGenresForFilm(generatedId));

        return film;
    }

    @Override
    public Film update(Film film) {
        validateReleaseDate(film);

        // Проверка существования фильма
        findById(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + film.getId() + " не найден"));

        if (film.getMpa() == null) {
            film.setMpa(new Mpa(1, null));
        }

        // Обновляем запись в БД
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        int rows = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        if (rows == 0) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }

        // Обновляем жанры фильма
        filmGenreDbStorage.saveFilmGenres(film);

        // Подгружаем лайки и жанры для корректного ответа
        film.setLikes(filmLikeDbStorage.getLikes(film.getId()));
        film.setGenres(filmGenreDbStorage.getGenresForFilm(film.getId()));

        return film;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.id";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);

        for (Film film : films) {
            if (film.getMpa() == null) {
                film.setMpa(new Mpa(1, null));
            }
            film.setGenres(filmGenreDbStorage.getGenresForFilm(film.getId()));
            film.setLikes(filmLikeDbStorage.getLikes(film.getId()));
        }

        return films;
    }

    @Override
    public Optional<Film> findById(long id) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.id " +
                "WHERE f.id = ?";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, id);
        if (films.isEmpty()) return Optional.empty();

        Film film = films.get(0);
        film.setGenres(filmGenreDbStorage.getGenresForFilm(film.getId()));
        film.setLikes(filmLikeDbStorage.getLikes(film.getId()));

        return Optional.of(film);
    }

    @Override
    public void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
    }
}
