package chess.controller;

import chess.domain.Board;
import chess.domain.BoardFactory;
import chess.domain.Game;
import chess.domain.GameResult;
import chess.domain.Movement;
import chess.domain.State;
import chess.domain.piece.abstractPiece.Piece;
import chess.domain.piece.character.Team;
import chess.dto.BoardStatusDto;
import chess.dto.CommandDto;
import chess.exception.ImpossibleMoveException;
import chess.exception.InvalidCommandException;
import chess.exception.InvalidGameRoomException;
import chess.service.ChessService;
import chess.util.GameCommand;
import chess.view.InputView;
import chess.view.OutputView;

public class ChessController {
    private final ChessService chessService;

    public ChessController(ChessService chessService) {
        this.chessService = chessService;
    }

    public void run() {
        try {
            switch (validateStartCommand()) {
                case START -> startNewGame();
                case LOAD -> loadGame();
            }
        } catch (InvalidGameRoomException e) {
            OutputView.printErrorMessage(e.getMessage());
            run();
        }
    }

    private void startNewGame() {
        String roomName = InputView.inputNewRoomName();
        Board board = new Board(BoardFactory.generateStartBoard());
        Game game = new Game(board, roomName);
        chessService.initialize(board, game.getCurrentTeam(), roomName);
        OutputView.printGameState(new BoardStatusDto(board.getSquares(), State.NORMAL));

        play(game);
    }

    private void loadGame() {
        String roomName = InputView.inputLoadRoomName();
        Game game = chessService.loadChessGame(roomName);
        Board board = game.getBoard();
        OutputView.printGameState(new BoardStatusDto(board.getSquares(), game.checkState()));

        play(game);
    }

    private GameCommand validateStartCommand() {
        try {
            return InputView.inputStartCommand();
        } catch (InvalidCommandException e) {
            OutputView.printErrorMessage(e.getMessage());
            return validateStartCommand();
        }
    }

    private void play(Game game) {
        try {
            playTurns(game);
        } catch (InvalidCommandException | ImpossibleMoveException e) {
            OutputView.printErrorMessage(e.getMessage());
            play(game);
        }
    }

    private void playTurns(Game game) {
        CommandDto commandDto;
        while ((commandDto = InputView.inputCommand()).gameCommand() != GameCommand.END) {
            doByCommand(game, commandDto);
            if (game.isMated()) {
                printWinnerByMate(game);
                chessService.deleteChessGame(game.getRoomName());
                break;
            }
        }
    }

    private void doByCommand(Game game, CommandDto commandDto) {
        if (commandDto.gameCommand() == GameCommand.MOVE) {
            playTurn(game, commandDto.toMovementDomain());
        }
        if (commandDto.gameCommand() == GameCommand.STATUS) {
            printWinnerByStatus(game.getBoard());
        }
    }

    private void playTurn(Game game, Movement movement) {
        Board board = game.getBoard();
        Piece piece = game.movePiece(movement);
        chessService.update(movement, piece, game.getCurrentTeam(), game.getRoomName());
        OutputView.printGameState(new BoardStatusDto(board.getSquares(), game.checkState()));
    }

    private void printWinnerByStatus(Board board) {
        OutputView.printPoint(Team.WHITE, board.calculateScore(Team.WHITE));
        OutputView.printPoint(Team.BLACK, board.calculateScore(Team.BLACK));
        OutputView.printWinner(board.findResultByScore());
    }

    private void printWinnerByMate(Game game) {
        State state = game.checkState();
        if (state == State.CHECKMATE) {
            OutputView.printWinner(game.getCurrentTeam().opponent());
        }
        if (state == State.STALEMATE) {
            OutputView.printWinner(GameResult.DRAW);
        }
    }
}
