package chess.exception;

public class ConnectionException extends RuntimeException {
    public ConnectionException() {
        this("DB 연결 오류");
    }

    public ConnectionException(String message) {
        super(message);
    }
}
