package chess.dao;

import chess.domain.piece.abstractPiece.Piece;
import chess.domain.piece.character.Kind;
import chess.domain.piece.character.Team;
import chess.exception.DataAccessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PiecesDao {
    public byte findIdByPiece(Piece piece, Connection connection) {
        try {
            final PreparedStatement statement = connection.prepareStatement(
                    "SELECT id FROM pieces WHERE team = ? AND kind = ? AND is_moved = ?");

            statement.setString(1, piece.team().name());
            statement.setString(2, piece.kind().name());
            statement.setBoolean(3, piece.isMoved());
            final ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                createPiece(piece, connection);
            }
            return resultSet.getByte("id");
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private void createPiece(Piece piece, Connection connection) {
        try {
            final PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO pieces (team, kind, is_moved) 
                    WHERE team = ? AND kind = ? AND is_moved = ?
                    """);

            statement.setString(1, piece.team().name());
            statement.setString(2, piece.kind().name());
            statement.setBoolean(3, piece.isMoved());
            statement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Piece findPieceById(byte id, Connection connection) {
        try {
            final PreparedStatement statement = connection.prepareStatement(
                    "SELECT team, kind, is_moved FROM pieces WHERE id = ?");

            statement.setByte(1, id);
            final ResultSet resultSet = statement.executeQuery();

            Team team = Team.valueOf(resultSet.getString("team"));
            Kind kind = Kind.valueOf(resultSet.getString("kind"));
            boolean isMoved = resultSet.getBoolean("is_moved");
            return kind.createPiece(team, isMoved);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
