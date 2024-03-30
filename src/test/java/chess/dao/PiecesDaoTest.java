package chess.dao;

import static org.assertj.core.api.Assertions.assertThat;

import chess.domain.piece.WhitePawn;
import chess.domain.piece.abstractPiece.Piece;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PiecesDaoTest {
    private PiecesDao piecesDao;
    private static Connection connection;


    @BeforeAll
    static void openConnection() {
        try {
            ConnectionGenerator connectionGenerator = new ConnectionGenerator();
            connection = connectionGenerator.getConnection("test");
            connection.setAutoCommit(false);
        } catch (SQLException ignored) {
        }
    }

    @AfterAll
    static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        piecesDao = new PiecesDao();
    }

    @AfterEach
    void tearDown() {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    @DisplayName("기물로 id를 찾는다.")
    @Test
    void findIdByPiece() {
        Piece piece = new WhitePawn(true);

        assertThat(piecesDao.findIdByPiece(piece, connection)).isEqualTo((byte) 1);
    }

    @DisplayName("id로 기물을 찾는다.")
    @Test
    void findPieceById() {
        byte id = 1;

        assertThat(piecesDao.findPieceById(id, connection)).isEqualTo(new WhitePawn(true));
    }
}
