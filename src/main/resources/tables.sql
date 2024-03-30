USE chess;

CREATE TABLE games
(
    id           BIGINT      NOT NULL AUTO_INCREMENT,
    room_name    VARCHAR(16) NOT NULL,
    current_team CHAR(5)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (room_name)
);

CREATE TABLE boards
(
    id        BIGINT      NOT NULL AUTO_INCREMENT,
    room_name VARCHAR(16) NOT NULL,
    position  CHAR(2)     NOT NULL,
    piece_id  TINYINT     NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE pieces
(
    id       TINYINT    NOT NULL AUTO_INCREMENT,
    team     CHAR(5)    NOT NULL,
    kind     VARCHAR(6) NOT NULL,
    is_moved BOOLEAN    NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO pieces (team, kind, is_moved)
VALUES ("WHITE", "PAWN", true),
       ("WHITE", "PAWN", false),
       ("WHITE", "KNIGHT", true),
       ("WHITE", "KNIGHT", false),
       ("WHITE", "BISHOP", true),
       ("WHITE", "BISHOP", false),
       ("WHITE", "ROOK", true),
       ("WHITE", "ROOK", false),
       ("WHITE", "QUEEN", true),
       ("WHITE", "QUEEN", false),
       ("WHITE", "KING", true),
       ("WHITE", "KING", false),
       ("BLACK", "PAWN", true),
       ("BLACK", "PAWN", false),
       ("BLACK", "KNIGHT", true),
       ("BLACK", "KNIGHT", false),
       ("BLACK", "BISHOP", true),
       ("BLACK", "BISHOP", false),
       ("BLACK", "ROOK", true),
       ("BLACK", "ROOK", false),
       ("BLACK", "QUEEN", true),
       ("BLACK", "QUEEN", false),
       ("BLACK", "KING", true),
       ("BLACK", "KING", false)
