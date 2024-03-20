package chess.domain.piece;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import chess.domain.Position;
import chess.domain.piece.character.Character;
import chess.domain.piece.character.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class KnightTest {
    @DisplayName("자신의 특징을 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"BLACK,BLACK_KNIGHT", "WHITE,WHITE_KNIGHT"})
    void findCharacter(Team team, Character character) {
        assertThat(new Knight(team, true).findCharacter())
                .isEqualTo(character);
    }

    @DisplayName("나이트는 날 일 자가 아닌 경우, 예외가 발생한다.")
    @Test
    void knightMoveOverDayHieroglyph() {
        assertThatThrownBy(() -> new Knight(Team.WHITE, true)
                .betweenPositions(Position.of(1, 1), Position.of(3, 3)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 위치로 움직일 수 없습니다.");
    }

    @DisplayName("두 위치 사이의 나이트가 갈 수 있는 위치들을 반환한다.")
    @Test
    void betweenPosition() {
        assertThat(new Knight(Team.WHITE, true)
                .betweenPositions(Position.of(3, 3), Position.of(2, 1)))
                .isEmpty();
    }
}