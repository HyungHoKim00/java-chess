package chess.dao;

import chess.domain.Board;
import chess.domain.ChessGame;
import chess.domain.Movement;
import chess.domain.piece.abstractPiece.Piece;
import chess.domain.piece.character.Team;
import chess.exception.DbException;
import java.sql.Connection;
import java.sql.SQLException;

public class ChessService {
    private final ConnectionGenerator connectionGenerator;
    private final PiecesDao piecesDao;
    private final ChessGameDao chessGameDao;

    public ChessService() {
        this(new ProductionConnectionGenerator(), new PiecesDao(), new ChessGameDao());
    }

    private ChessService(ConnectionGenerator connectionGenerator, PiecesDao piecesDao, ChessGameDao chessGameDao) {
        this.connectionGenerator = connectionGenerator;
        this.piecesDao = piecesDao;
        this.chessGameDao = chessGameDao;
    }

    public void initialize(Board board, Team team, String roomName) {
        Connection connection = null;
        try {
            connection = connectionGenerator.getConnection();
            connection.setAutoCommit(false);
            chessGameDao.add(team, roomName, connection);
            piecesDao.addAll(board, roomName, connection);
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
        } finally {
            close(connection);
        }
    }

    public ChessGame loadChessGame(String roomName) {
        Connection connection = null;
        try {
            connection = connectionGenerator.getConnection();
            connection.setAutoCommit(false);
            Team currentTeam = chessGameDao.findCurrentTeamByRoomName(roomName, connection);
            Board board = piecesDao.loadAll(roomName, connection);
            connection.commit();
            return new ChessGame(board, currentTeam);
        } catch (SQLException e) {
            rollback(connection);
        } finally {
            close(connection);
        }
        return null;
    }

    public void update(Movement movement, Piece piece, Team currentTeam, String roomName) {
        Connection connection = null;
        try {
            connection = connectionGenerator.getConnection();
            connection.setAutoCommit(false);
            chessGameDao.update(currentTeam, roomName, connection);
            piecesDao.update(movement, piece, roomName, connection);
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
        } finally {
            close(connection);
        }
    }

    public void deleteChessGame(String roomName) {
        Connection connection = null;
        try {
            connection = connectionGenerator.getConnection();
            connection.setAutoCommit(false);
            chessGameDao.delete(roomName, connection);
            piecesDao.delete(roomName, connection);
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
        } finally {
            close(connection);
        }
    }

    private void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DbException("DB 연결 오류");
            }
        }
    }

    private void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new DbException("DB 연결 오류");
            }
        }
        throw new DbException("DB 연결 오류");
    }
}
