package db;

import java.sql.*;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/ngo_projects_db"; // your DB
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
