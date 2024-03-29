package credit.suisse.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import credit.suisse.pojo.EventResult;

public class EventResultService {

    private static Connection connection = null;
    private static final Logger logger = Logger.getGlobal();

    private static Connection getConnection() {
        try {
            if (EventResultService.connection == null || EventResultService.connection.isClosed()) {
                EventResultService.logger.info("Getting connection.");
                connection = DriverManager.getConnection("jdbc:hsqldb:file:eventsdb;shutdown=true", "SA", "");
            }
        } catch (SQLException sqlExcept) {
            EventResultService.logger.severe(String.format("Couldn't create connection to database!\n%s", sqlExcept.getMessage()));
        }
        return connection;
    }

    public void createTable() {
        try {
            String sql = "CREATE TABLE EVENT(" +
                    "ID_PK INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                    "ID VARCHAR(250) NOT NULL, " +
                    "DURATION SMALLINT NOT NULL, " +
                    "TYPE VARCHAR(250), " +
                    "HOST VARCHAR(250), " +
                    "ALERT BOOLEAN NOT NULL)";

            EventResultService.getConnection().prepareStatement(sql).executeUpdate();

            EventResultService.logger.info("Event table created successfully!");
        } catch (SQLException sqlExcept) {
            EventResultService.logger.severe(String.format("Couldn't create Event table!\n%s", sqlExcept.getMessage()));
        } finally {
			this.closeConnection();
		}
    }

    public void cleanTable() {
        try {
            String sql = "DELETE FROM EVENT";

            EventResultService.getConnection().prepareStatement(sql).executeUpdate();
            EventResultService.logger.info("All data from table Event was erased!");
        } catch (SQLException sqlExcept) {
            EventResultService.logger.severe(String.format("Couldn't delete data from Event table!\n%s", sqlExcept.getMessage()));
        } finally {
			this.closeConnection();
		}
    }

    public void saveEventResult(EventResult result) {
        try {
            String sql = "INSERT INTO EVENT(ID, DURATION, TYPE, HOST, ALERT) VALUES (?,?,?,?,?)";
            PreparedStatement stmt = EventResultService.getConnection().prepareStatement(sql);
            stmt.setString(1, result.getId());
            stmt.setInt(2, result.getDuration());
            stmt.setString(3, result.getType());
            stmt.setString(4, result.getHost());
            stmt.setBoolean(5, result.getAlert());

            stmt.execute();
            stmt.close();

            EventResultService.logger.info(String.format("Event %s saved!", result.getId()));
        } catch (SQLException sqlExcept) {
            EventResultService.logger.severe(String.format("Couldn't insert event %s!\n%s", result.getId(), sqlExcept.getMessage()));
        } finally {
			this.closeConnection();
		}
    }

    public List<EventResult> listAllEvents() {
        List<EventResult> results = new ArrayList<EventResult>();
        try {
            EventResultService.logger.info("Selecting all items from Event.");

            String sql = "SELECT * FROM EVENT";
            ResultSet resultSet = EventResultService.getConnection().prepareStatement(sql).executeQuery();

            while (resultSet.next()) {
                EventResult result = new EventResult(resultSet.getString("ID"), resultSet.getInt("DURATION"), resultSet.getBoolean("ALERT"));
                result.setType(resultSet.getString("TYPE"));
                result.setHost(resultSet.getString("HOST"));

                results.add(result);
            }
        } catch (SQLException sqlExcept) {
            EventResultService.logger.severe(String.format("Couldn't read values from table Event.\n%s", sqlExcept.getMessage()));
        } finally {
        	this.closeConnection();
		}

        return results;
    }

    public void closeConnection() {
        try {
            EventResultService.getConnection().close();
        } catch (SQLException sqlExcept) {
            EventResultService.logger.severe(String.format("Couldn't close connection.\n%s", sqlExcept.getMessage()));
        }
    }
}
