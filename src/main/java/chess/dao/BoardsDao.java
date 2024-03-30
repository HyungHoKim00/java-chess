package chess.dao;

import chess.domain.Board;
import chess.domain.Movement;
import chess.domain.Position;
import chess.domain.piece.abstractPiece.Piece;
import chess.exception.DataAccessException;
import chess.util.PositionConverter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class BoardsDao {
    private final PiecesDao piecesDao;

    public BoardsDao() {
        this.piecesDao = new PiecesDao();
    }

    public void addAll(Board board, String roomName, Connection connection) {
        try {
            for (Entry<Position, Piece> square : board.getSquares().entrySet()) {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO boards (room_name, position, piece_id) VALUES (?, ?, ?)");

                statement.setString(1, roomName);
                statement.setString(2, PositionConverter.toNotation(square.getKey()));
                byte pieceId = piecesDao.findIdByPiece(square.getValue(), connection);
                statement.setByte(3, pieceId);

                statement.execute();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Board loadAll(String roomName, Connection connection) {
        try {
            final PreparedStatement statement = connection.prepareStatement(
                    "SELECT position, piece_id FROM boards WHERE room_name = ?");

            statement.setString(1, roomName);
            final ResultSet resultSet = statement.executeQuery();

            final Map<Position, Piece> loadedBoard = new HashMap<>();
            while (resultSet.next()) {
                Position position = PositionConverter.toPosition(
                        resultSet.getString("position"));
                Piece piece = piecesDao.findPieceById(
                        resultSet.getByte("piece_id"), connection);
                loadedBoard.put(position, piece);
            }
            return new Board(loadedBoard);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void update(Movement movement, Piece piece, String roomName, Connection connection) {
        deleteAttackedPiece(movement, roomName, connection);
        movePiece(movement, piece, roomName, connection);
    }

    private void deleteAttackedPiece(Movement movement, String roomName, Connection connection) {
        try {
            final PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM boards WHERE position = ? AND room_name = ?");
            statement.setString(1, PositionConverter.toNotation(movement.target()));
            statement.setString(2, roomName);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private void movePiece(Movement movement, Piece piece, String roomName, Connection connection) {
        try {
            final PreparedStatement statement = connection.prepareStatement("""
                    UPDATE boards SET position = ?, piece_id = ? 
                    WHERE position = ? AND room_name = ?
                    """);

            statement.setString(1, PositionConverter.toNotation(movement.target()));
            statement.setByte(2, piecesDao.findIdByPiece(piece, connection));
            statement.setString(3, PositionConverter.toNotation(movement.source()));
            statement.setString(4, roomName);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void delete(String roomName, Connection connection) {
        try {
            final PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM boards WHERE room_name = ?");
            statement.setString(1, roomName);

            statement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
