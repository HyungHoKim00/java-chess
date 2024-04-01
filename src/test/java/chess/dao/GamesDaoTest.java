package chess.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import chess.domain.piece.character.Team;
import chess.exception.InvalidGameRoomException;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GamesDaoTest {
    private static final String ROOM_NAME = "roomName";
    private GamesDao gamesDao;
    private Connection connection;

    @BeforeEach
    void setUp() {
        try {
            ConnectionGenerator connectionGenerator = new ConnectionGenerator();
            connection = connectionGenerator.getConnection("test");
            connection.setAutoCommit(false);
            gamesDao = new GamesDao();
        } catch (SQLException ignored) {
        }
    }

    @AfterEach
    void tearDown() {
        try {
            connection.rollback();
            connection.close();
        } catch (SQLException ignored) {
        }
    }

    @DisplayName("필드 추가")
    @Test
    void add() {
        assertThatCode(() -> gamesDao.add(Team.WHITE, ROOM_NAME, connection))
                .doesNotThrowAnyException();
    }

    @DisplayName("입력된 방 이름이 16자를 넘어가면 예외가 발생한다.")
    @Test
    void invalidAddIfNameOver16() {
        String name = "soLongRoomNameInput";

        assertThatThrownBy(() -> gamesDao.add(Team.WHITE, name, connection))
                .isInstanceOf(InvalidGameRoomException.class)
                .hasMessage("방 이름은 16자 이하로 입력해 주세요.");
    }

    @DisplayName("입력된 방 이름이 중복되면 예외가 발생한다.")
    @Test
    void invalidAddIfNameDuplicated() {
        gamesDao.add(Team.WHITE, ROOM_NAME, connection);

        assertThatThrownBy(() -> gamesDao.add(Team.WHITE, ROOM_NAME, connection))
                .isInstanceOf(InvalidGameRoomException.class)
                .hasMessage("중복된 이름이 존재합니다.");
    }

    @DisplayName("방 이름으로 현재 팀을 찾는다.")
    @Test
    void findCurrentTeamByRoomName() {
        gamesDao.add(Team.WHITE, ROOM_NAME, connection);

        Team currentTeam = gamesDao.findCurrentTeamByRoomName(ROOM_NAME, connection);

        assertThat(currentTeam).isEqualTo(Team.WHITE);
    }

    @DisplayName("방 이름이 존재하지 않으면, 예외가 발생한다.")
    @Test
    void findCurrentTeamByInvalidRoomName() {
        String name = "noname";

        gamesDao.add(Team.WHITE, ROOM_NAME, connection);

        assertThatThrownBy(() -> gamesDao.findCurrentTeamByRoomName(name, connection))
                .isInstanceOf(InvalidGameRoomException.class)
                .hasMessage("존재하지 않는 방 이름입니다.");
    }

    @DisplayName("입력된 팀으로 입력된 방이름의 현재 팀을 바꾼다.")
    @Test
    void update() {
        gamesDao.add(Team.WHITE, ROOM_NAME, connection);
        gamesDao.update(Team.BLACK, ROOM_NAME, connection);
        Team currentTeam = gamesDao.findCurrentTeamByRoomName(ROOM_NAME, connection);

        assertThat(currentTeam)
                .isEqualTo(Team.BLACK);
    }

    @DisplayName("입력된 방 이름을 삭제한다.")
    @Test
    void delete() {
        gamesDao.add(Team.WHITE, ROOM_NAME, connection);
        gamesDao.delete(ROOM_NAME, connection);

        assertThatThrownBy(() -> gamesDao.findCurrentTeamByRoomName(ROOM_NAME, connection))
                .isInstanceOf(InvalidGameRoomException.class)
                .hasMessage("존재하지 않는 방 이름입니다.");
    }
}
