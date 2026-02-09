package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    List<Mpa> getAll();                 // получить все MPA
    Optional<Mpa> getById(int id);      // получить MPA по id
}
