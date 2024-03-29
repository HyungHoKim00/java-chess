package chess.controller;

import chess.dao.ChessService;
import chess.domain.Board;
import chess.domain.BoardFactory;
import chess.domain.ChessGame;
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
import chess.util.GameCommand;
import chess.view.InputView;
import chess.view.OutputView;

public class ChessController {
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

    private void loadGame() {
        final String roomName = InputView.inputLoadRoomName();
        ChessService chessService = new ChessService();
        ChessGame chessGame = chessService.loadChessGame(roomName);
        Board board = chessGame.getBoard();
        OutputView.printGameState(new BoardStatusDto(board.getPieces(), chessGame.checkState()));

        play(chessGame, chessService, roomName);
    }

    private void startNewGame() {
        final String roomName = InputView.inputNewRoomName();
        ChessService chessService = new ChessService();
        Board board = new Board(BoardFactory.generateStartBoard());
        ChessGame chessGame = new ChessGame(board);
        chessService.initialize(board, chessGame.getCurrentTeam(), roomName);
        OutputView.printGameState(new BoardStatusDto(board.getPieces(), State.NORMAL));

        play(chessGame, chessService, roomName);
    }

    private GameCommand validateStartCommand() {
        try {
            return InputView.inputStartCommand();
        } catch (InvalidCommandException e) {
            OutputView.printErrorMessage(e.getMessage());
            return validateStartCommand();
        }
    }

    private void play(ChessGame chessGame, ChessService chessService, String roomName) {
        try {
            playTurns(chessGame, chessService, roomName);
        } catch (InvalidCommandException | ImpossibleMoveException e) {
            OutputView.printErrorMessage(e.getMessage());
            play(chessGame, chessService, roomName);
        }
    }

    private void playTurns(ChessGame chessGame, ChessService chessService, String roomName) {
        CommandDto commandDto = new CommandDto();
        State state = chessGame.checkState();
        Board board = chessGame.getBoard();
        while (state != State.CHECKMATE && (commandDto = InputView.inputCommand()).gameCommand() == GameCommand.MOVE) {
            playTurn(chessGame, chessService, commandDto.toMovementDomain(), roomName);
            state = chessGame.checkState();
        }
        printWinnerByStatus(board, commandDto.gameCommand());
        printWinnerByMate(chessGame, state);
        chessService.deleteChessGame(roomName);
    }

    private void playTurn(ChessGame chessGame, ChessService chessService, Movement movement, String roomName) {
        Board board = chessGame.getBoard();
        Piece piece = chessGame.movePiece(movement);
        chessService.update(movement, piece, chessGame.getCurrentTeam(), roomName);
        OutputView.printGameState(new BoardStatusDto(board.getPieces(), chessGame.checkState()));
    }

    private void printWinnerByStatus(Board board, GameCommand gameCommand) {
        if (gameCommand == GameCommand.STATUS) {
            OutputView.printPoint(Team.WHITE, board.calculateScore(Team.WHITE));
            OutputView.printPoint(Team.BLACK, board.calculateScore(Team.BLACK));
            OutputView.printWinner(board.findResultByScore());
        }
    }

    private void printWinnerByMate(ChessGame chessGame, State state) {
        if (state == State.CHECKMATE) {
            OutputView.printWinner(chessGame.getCurrentTeam().opponent());
        }
        if (state == State.STALEMATE) {
            OutputView.printWinner(GameResult.DRAW);
        }
    }
}
