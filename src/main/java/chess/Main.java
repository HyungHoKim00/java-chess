package chess;

import chess.controller.ChessController;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        ChessController chessController = new ChessController();
        chessController.run();
    }
}
