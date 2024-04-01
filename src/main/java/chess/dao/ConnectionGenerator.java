package chess.dao;

import chess.dto.resource.DbUrlDto;
import chess.dto.resource.ResourceDto;
import chess.exception.ConnectionException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class ConnectionGenerator {
    public Connection getConnection() {
        return getConnection("production");
    }

    public Connection getConnection(String connectionType) {
        try {
            FileInputStream fis = new FileInputStream(
                    "src/main/resources/" + connectionType + ".yml");
            Yaml yaml = new Yaml(new Constructor(ResourceDto.class, new LoaderOptions()));
            ResourceDto resourceDto = yaml.load(fis);
            DbUrlDto dbUrlDto = resourceDto.getDb();
            return DriverManager.getConnection(
                    "jdbc:mysql://" + dbUrlDto.getServer() + "/" + dbUrlDto.getDatabase()
                            + dbUrlDto.getOption(),
                    dbUrlDto.getUsername(),
                    dbUrlDto.getPassword()
            );
        } catch (SQLException e) {
            System.err.println("DB 연결 오류:" + e.getMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.err.println("파일이 존재하지 않습니다: " + e.getMessage());
            e.printStackTrace();
        }
        throw new ConnectionException();
    }
}
