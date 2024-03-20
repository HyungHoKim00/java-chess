package chess.domain.piece;

import chess.Calculator;
import chess.domain.Position;
import chess.domain.piece.character.Character;
import chess.domain.piece.character.Kind;
import chess.domain.piece.character.Team;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public static final int WHITE_NORMAL_MOVEMENT = 1;
    public static final int WHITE_START_MOVEMENT = 2;
    public static final int BLACK_NORMAL_MOVEMENT = -1;
    public static final int BLACK_START_MOVEMENT = -2;
    public static final int ATTACK_COLUMN_MOVEMENT = 1;

    public Pawn(Team team, boolean hasNotMoved) {
        super(team, hasNotMoved);
    }

    @Override
    public Character findCharacter() {
        return Character.findCharacter(team, Kind.PAWN);
    }

    @Override
    protected boolean isMovable(int rowDifference, int columnDifference) {
        if (columnDifference != 0) {
            return false;
        }

        if (team == Team.WHITE) {
            return rowDifference == WHITE_NORMAL_MOVEMENT || (hasNotMoved && rowDifference == WHITE_START_MOVEMENT);
        }
        return rowDifference == BLACK_NORMAL_MOVEMENT || (hasNotMoved && rowDifference == BLACK_START_MOVEMENT);
    }

    @Override
    protected List<Position> findBetweenPositions(Position position, int rowDifference, int columnDifference) {
        List<Position> positions = new ArrayList<>();
        if (Math.abs(rowDifference) == WHITE_START_MOVEMENT) {
            positions.add(position.move(Calculator.calculateSign(rowDifference), 0));
            return positions;
        }
        return new ArrayList<>();
    }

    @Override
    public List<Position> findBetweenPositionsWhenAttack(Position oldPosition, Position newPosition) {
        int rowDifference = oldPosition.calculateRowDifference(newPosition);
        int columnDifference = oldPosition.calculateColumnDifference(newPosition);
        validateAttackable(rowDifference, columnDifference);
        return findBetweenPositions(oldPosition, rowDifference, columnDifference);
    }

    private void validateAttackable(int rowDifference, int columnDifference) {
        if (isAttackable(rowDifference, columnDifference)) {
            return;
        }
        throw new IllegalArgumentException("해당 위치로 움직일 수 없습니다.");
    }

    private boolean isAttackable(int rowDifference, int columnDifference) {
        if (team == Team.WHITE) {
            return rowDifference == WHITE_NORMAL_MOVEMENT && Math.abs(columnDifference) == ATTACK_COLUMN_MOVEMENT;
        }
        return rowDifference == BLACK_NORMAL_MOVEMENT && Math.abs(columnDifference) == ATTACK_COLUMN_MOVEMENT;
    }

    @Override
    public Piece move() {
        if (hasNotMoved) {
            return new Pawn(team, false);
        }
        return this;
    }
}
