USE chess;

drop table pieces;
drop table chess_game;

CREATE TABLE pieces
(
    id        INT UNSIGNED NOT NULL AUTO_INCREMENT,
    room_name VARCHAR(16)  NOT NULL,
    position  CHAR(2)      NOT NULL,
    team      CHAR(5)      NOT NULL,
    kind      VARCHAR(6)   NOT NULL,
    is_moved  BOOLEAN      NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE chess_game
(
    id           INT UNSIGNED NOT NULL AUTO_INCREMENT,
    room_name    VARCHAR(16)  NOT NULL,
    current_team CHAR(5)      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (room_name)
);
