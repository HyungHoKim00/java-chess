package chess.dao;

import chess.exception.ConnectionException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionGenerator {
    public Connection getConnection() {
        return getConnection("production");
    }

    public Connection getConnection(String connectionType) {
        try {
            FileInputStream fis = new FileInputStream(
                    "C:\\Program Files\\intellijWorkspace\\intellijWorkspace\\java-chess\\src"
                            + "\\main\\resources\\" + connectionType + "_connection_context.yml");
            Properties properties = new Properties();
            properties.load(fis);
            return DriverManager.getConnection(
                    "jdbc:mysql://" + properties.get("server") + "/"
                            + properties.get("database") + properties.get("option"),
                    properties.get("username") + "",
                    properties.get("password") + ""
            );
        } catch (SQLException e) {
            System.err.println("DB 연결 오류:" + e.getMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.err.println("파일이 존재하지 않습니다: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("입출력 오류:" + e.getMessage());
            e.printStackTrace();
        }
        throw new ConnectionException();
    }
}
