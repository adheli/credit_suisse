package credit.suisse.db;

import java.sql.Connection;

/**
 * @author adheli.tavares
 */
public interface DatabaseConnection {

    void closeConnection();
    Connection getConnection();
}
