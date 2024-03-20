package chess.domain.piece;

import static org.assertj.core.api.Assertions.assertThat;

import chess.domain.Position;
import chess.domain.piece.character.Character;
import chess.domain.piece.character.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class KnightTest {
    @DisplayName("자신의 특징을 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"BLACK,BLACK_KNIGHT", "WHITE,WHITE_KNIGHT"})
    void findCharacter(Team team, Character character) {
        assertThat(new Knight(Position.of(1, 1), team).findCharacter())
                .isEqualTo(character);
    }
}