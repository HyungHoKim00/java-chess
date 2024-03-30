package chess.dao;

import chess.domain.piece.character.Team;
import chess.exception.DataAccessException;
import chess.exception.InvalidGameRoomException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GamesDao {

    public void add(Team currentTeam, String roomName, Connection connection) {
        try {
            validateRoomName(roomName, connection);
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO games(room_name, current_team) VALUES (?, ?)");

            statement.setString(1, roomName);
            statement.setString(2, currentTeam.name());
            statement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Team findCurrentTeamByRoomName(String roomName, Connection connection) {
        try {
            final ResultSet resultSet = findCurrentTeamDataByRoomName(roomName, connection);
            boolean exists = resultSet.next();
            if (!exists) {
                throw new InvalidGameRoomException("존재하지 않는 방 이름입니다.");
            }
            return Team.valueOf(resultSet.getString("current_team"));
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void validateRoomName(String roomName, Connection connection) {
        try {
            if (roomName.length() > 16) {
                throw new InvalidGameRoomException("방 이름은 16자 이하로 입력해 주세요.");
            }
            final ResultSet resultSet = findCurrentTeamDataByRoomName(roomName, connection);
            if (resultSet.next()) {
                throw new InvalidGameRoomException("중복된 이름이 존재합니다.");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private static ResultSet findCurrentTeamDataByRoomName(String roomName, Connection connection)
            throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(
                "SELECT current_team FROM games WHERE room_name = ?");

        statement.setString(1, roomName);
        return statement.executeQuery();
    }

    public void update(Team currentTeam, String roomName, Connection connection) {
        try {
            final PreparedStatement statement = connection.prepareStatement(
                    "UPDATE games SET current_team = ? WHERE room_name = ?");

            statement.setString(1, currentTeam.name());
            statement.setString(2, roomName);

            statement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void delete(String roomName, Connection connection) {
        try {
            final PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM games WHERE room_name = ?");
            statement.setString(1, roomName);

            statement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
