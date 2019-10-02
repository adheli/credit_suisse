package credit.suisse.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author adheli.tavares
 */
public class HsqlDatabaseConnection implements DatabaseConnection {
    
    private static Connection connection = null;
    private static final Logger logger = Logger.getGlobal();
    private static final String USER = "SA";
    private static final String PASSWORD = "";
    private static final String URL = "jdbc:hsqldb:file:eventsdb;shutdown=true";
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                logger.info("Getting connection.");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException sqlExcept) {
            logger.severe(String.format("Couldn't create connection to database!\n%s", sqlExcept.getMessage()));
        }
        return connection;
    }

    @Override
    public void closeConnection() {
        try {
            logger.info("Closing connection.");
            getConnection().close();
        } catch (SQLException sqlExcept) {
            logger.severe(String.format("Couldn't close connection.\n%s", sqlExcept.getMessage()));
        }
    }
}
