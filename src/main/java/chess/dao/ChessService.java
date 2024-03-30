package chess.dao;

import chess.domain.Board;
import chess.domain.ChessGame;
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
        try (final Connection connection = connectionGenerator.getConnection()) {
            connection.setAutoCommit(false);
            try {
                gamesDao.add(team, roomName, connection);
                boardsDao.addAll(board, roomName, connection);
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
            connection.commit();
        } catch (SQLException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    public ChessGame loadChessGame(String roomName) {
        try (final Connection connection = connectionGenerator.getConnection()) {
            connection.setAutoCommit(false);
            Team currentTeam;
            Board board;
            try {
                currentTeam = gamesDao.findCurrentTeamByRoomName(roomName, connection);
                board = boardsDao.loadAll(roomName, connection);
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
            connection.commit();
            return new ChessGame(board, currentTeam);
        } catch (SQLException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    public void update(Movement movement, Piece piece, Team currentTeam, String roomName) {
        try (final Connection connection = connectionGenerator.getConnection()) {
            connection.setAutoCommit(false);
            try {
                gamesDao.update(currentTeam, roomName, connection);
                boardsDao.update(movement, piece, roomName, connection);
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
            connection.commit();
        } catch (SQLException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    public void deleteChessGame(String roomName) {
        try (final Connection connection = connectionGenerator.getConnection()) {
            connection.setAutoCommit(false);
            try {
                gamesDao.delete(roomName, connection);
                boardsDao.delete(roomName, connection);
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
            connection.commit();
        } catch (SQLException e) {
            throw new ConnectionException(e.getMessage());
        }
    }
}
