package ru.yandex.practicum.filmorate.exception;

public class GenreNotFoundException extends NotFoundException {
    public GenreNotFoundException(int id) {
        super("Жанр с id=" + id + " не найден");
    }
}
