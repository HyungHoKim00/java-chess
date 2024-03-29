package chess.dao;

import chess.domain.Board;
import chess.domain.ChessGame;
import chess.domain.Movement;
import chess.domain.piece.abstractPiece.Piece;
import chess.domain.piece.character.Team;
import chess.exception.ConnectionException;
import chess.exception.DataAccessException;
import chess.exception.InvalidGameRoomException;
import java.sql.Connection;
import java.sql.SQLException;

public class ChessService {
    private final ConnectionGenerator connectionGenerator;
    private final PiecesDao piecesDao;
    private final ChessGameDao chessGameDao;

    public ChessService() {
        this(new ProductionConnectionGenerator(), new PiecesDao(), new ChessGameDao());
    }

    private ChessService(
            ConnectionGenerator connectionGenerator,
            PiecesDao piecesDao,
            ChessGameDao chessGameDao
    ) {
        this.connectionGenerator = connectionGenerator;
        this.piecesDao = piecesDao;
        this.chessGameDao = chessGameDao;
    }

    public void initialize(Board board, Team team, String roomName) {
        try (final Connection connection = connectionGenerator.getConnection()) {
            connection.setAutoCommit(false);
            try {
                chessGameDao.add(team, roomName, connection);
                piecesDao.addAll(board, roomName, connection);
            } catch (InvalidGameRoomException | DataAccessException e) {
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
                currentTeam = chessGameDao.findCurrentTeamByRoomName(roomName, connection);
                board = piecesDao.loadAll(roomName, connection);
            } catch (InvalidGameRoomException | DataAccessException e) {
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
                chessGameDao.update(currentTeam, roomName, connection);
                piecesDao.update(movement, piece, roomName, connection);
            } catch (DataAccessException e) {
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
                chessGameDao.delete(roomName, connection);
                piecesDao.delete(roomName, connection);
            } catch (DataAccessException e) {
                connection.rollback();
                throw e;
            }
            connection.commit();
        } catch (SQLException e) {
            throw new ConnectionException(e.getMessage());
        }
    }
}
