package chess.service;

import chess.dao.BoardsDao;
import chess.dao.ConnectionGenerator;
import chess.dao.GamesDao;
import chess.domain.Board;
import chess.domain.Game;
import chess.domain.Movement;
import chess.domain.piece.abstractPiece.Piece;
import chess.domain.piece.character.Team;
import chess.exception.ConnectionException;
import java.sql.Connection;
import java.sql.SQLException;

public class ChessService {
    private final ConnectionGenerator connectionGenerator;
    private final BoardsDao boardsDao;
    private final GamesDao gamesDao;

    public ChessService() {
        this(new ConnectionGenerator(), new BoardsDao(), new GamesDao());
    }

    private ChessService(
            ConnectionGenerator connectionGenerator,
            BoardsDao boardsDao,
            GamesDao gamesDao
    ) {
        this.connectionGenerator = connectionGenerator;
        this.boardsDao = boardsDao;
        this.gamesDao = gamesDao;
    }

    public void initialize(Board board, Team team, String roomName) {
        Connection connection = connectionGenerator.getConnection();
        try {
            connection.setAutoCommit(false);
            gamesDao.add(team, roomName, connection);
            boardsDao.addAll(board, roomName, connection);
            connection.commit();
        } catch (RuntimeException e) {
            rollback(connection);
            throw e;
        } catch (SQLException e) {
            rollback(connection);
            throw new ConnectionException(e.getMessage());
        } finally {
            close(connection);
        }
    }

    public Game loadChessGame(String roomName) {
        Connection connection = connectionGenerator.getConnection();
        try {
            connection.setAutoCommit(false);
            Team currentTeam = gamesDao.findCurrentTeamByRoomName(roomName, connection);
            Board board = boardsDao.loadAll(roomName, connection);
            connection.commit();
            return new Game(board, roomName, currentTeam);
        } catch (RuntimeException e) {
            rollback(connection);
            throw e;
        } catch (SQLException e) {
            rollback(connection);
            throw new ConnectionException(e.getMessage());
        } finally {
            close(connection);
        }
    }

    public void update(Movement movement, Piece piece, Team currentTeam, String roomName) {
        Connection connection = connectionGenerator.getConnection();
        try {
            connection.setAutoCommit(false);
            gamesDao.update(currentTeam, roomName, connection);
            boardsDao.update(movement, piece, roomName, connection);
            connection.commit();
        } catch (RuntimeException e) {
            rollback(connection);
            throw e;
        } catch (SQLException e) {
            rollback(connection);
            throw new ConnectionException(e.getMessage());
        } finally {
            close(connection);
        }
    }

    public void deleteChessGame(String roomName) {
        Connection connection = connectionGenerator.getConnection();
        try {
            connection.setAutoCommit(false);
            gamesDao.delete(roomName, connection);
            boardsDao.delete(roomName, connection);
            connection.commit();
        } catch (RuntimeException e) {
            rollback(connection);
            throw e;
        } catch (SQLException e) {
            rollback(connection);
            throw new ConnectionException(e.getMessage());
        } finally {
            close(connection);
        }
    }

    private void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new ConnectionException(e.getMessage());
            }
        }
    }

    private void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new ConnectionException(e.getMessage());
            }
        }
    }
}
