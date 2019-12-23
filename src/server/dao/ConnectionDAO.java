package server.dao;

import main.ChatAnnProp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionDAO {
    //  Database credentials
    private static Connection dbConnection = null;

    /**
     * Connection to DB
     * @return singleton connection
     */
    public static Connection getDBConnection() {

        if(dbConnection == null) {
            Properties prop = ChatAnnProp.setProp();
            try {
                Class.forName(prop.getProperty("driver"));
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
            try {
                dbConnection = DriverManager.getConnection(prop.getProperty("url"), prop.getProperty("user"), prop.getProperty("pass"));
                return dbConnection;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return dbConnection;
    }
}
