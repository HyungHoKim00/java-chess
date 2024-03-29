package chess.dao;

import chess.domain.Board;
import chess.domain.Movement;
import chess.domain.Position;
import chess.domain.piece.abstractPiece.Piece;
import chess.domain.piece.character.Kind;
import chess.domain.piece.character.Team;
import chess.exception.DataAccessException;
import chess.exception.InvalidGameRoomException;
import chess.util.PositionConverter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class PiecesDao {
    private static final String TABLE_NAME = "pieces";

    public void addAll(Board board, String roomName, Connection connection) {
        try {
            for (Entry<Position, Piece> piece : board.getPieces().entrySet()) {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO " + TABLE_NAME
                                + "(room_name, position, team, kind, is_moved)"
                                + " VALUES (?, ?, ?, ?, ?)");

                statement.setString(1, roomName);
                statement.setString(2, PositionConverter.toNotation(piece.getKey()));
                statement.setString(3, piece.getValue().team().name());
                statement.setString(4, piece.getValue().kind().name());
                statement.setBoolean(5, piece.getValue().isMoved());

                statement.execute();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Board loadAll(String roomName, Connection connection) {
        try {
            final PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM " + TABLE_NAME + " WHERE room_name = ?");

            statement.setString(1, roomName);
            final ResultSet resultSet = statement.executeQuery();

            final Map<Position, Piece> loadedBoard = new HashMap<>();
            try {
                while (resultSet.next()) {
                    getPiece(resultSet, loadedBoard);
                }
            } catch (SQLException e) {
                throw new InvalidGameRoomException("존재하지 않는 방 이름입니다.");
            }
            return new Board(loadedBoard);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private void getPiece(ResultSet resultSet, Map<Position, Piece> loadedBoard) {
        try {
            Position position
                    = PositionConverter.toPosition(resultSet.getString("position"));
            Team team = Team.valueOf(resultSet.getString("team"));
            Kind kind = Kind.valueOf(resultSet.getString("kind"));
            boolean isMoved = resultSet.getBoolean("is_moved");
            Piece piece = kind.createPiece(team, isMoved);

            loadedBoard.put(position, piece);
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
                    "DELETE FROM " + TABLE_NAME + " WHERE position = ? AND room_name = ?");
            statement.setString(1, PositionConverter.toNotation(movement.target()));
            statement.setString(2, roomName);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private void movePiece(Movement movement, Piece piece, String roomName, Connection connection) {
        try {
            final PreparedStatement statement = connection.prepareStatement(
                    "UPDATE " + TABLE_NAME
                            + " SET position = ?, is_moved = ?"
                            + " WHERE position = ? AND room_name = ?");

            statement.setString(1, PositionConverter.toNotation(movement.target()));
            statement.setBoolean(2, piece.isMoved());
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
                    "DELETE FROM " + TABLE_NAME + " WHERE room_name = ?");
            statement.setString(1, roomName);

            statement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
