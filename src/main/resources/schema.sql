-- Таблицы справочников
CREATE TABLE IF NOT EXISTS mpa (
    id INT PRIMARY KEY,
    name VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    birthday DATE NOT NULL
);

-- Таблица фильмов
CREATE TABLE IF NOT EXISTS films (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    mpa_id INT NOT NULL,
    FOREIGN KEY (mpa_id) REFERENCES mpa(id)
);

-- Таблица связей фильмов и жанров
CREATE TABLE IF NOT EXISTS film_genres (
    film_id INT,
    genre_id INT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id)
);

-- Таблица лайков фильмов
CREATE TABLE IF NOT EXISTS film_likes (
    film_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Таблица дружбы
CREATE TABLE IF NOT EXISTS friends (
    user_id INT,
    friend_id INT,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);