package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmLikeService;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmLikeController {

    private final FilmLikeService filmLikeService;

    @DeleteMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable int filmId, @PathVariable int userId) {
        filmLikeService.removeLike(filmId, userId);
        return ResponseEntity.noContent().build(); // 204
    }

    @PutMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable int filmId, @PathVariable int userId) {
        filmLikeService.addLike(filmId, userId);
        return ResponseEntity.ok().build(); // 200
    }
}