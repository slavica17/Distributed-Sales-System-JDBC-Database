package student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static final String username = "sa";
    private static final String password = "123";
    private static final String database = "SAB-projekat";
    private static final int port = 1433;
    private static final String server = "localhost";
    private static final String connectionUrl = "jdbc:sqlserver://" + server + ":" + port + ";databaseName=" + database + ";encrypt=true;trustServerCertificate=true";

    private DB() {
    }

    private static DB db = null;

    public static DB getInstance() {
        if (db == null) {
            db = new DB();
        }
        return db;
    }

    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(connectionUrl, username, password);
        conn.setAutoCommit(true);  
        return conn;
    }
}