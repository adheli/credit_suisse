package credit.suisse.dao.impl;

import credit.suisse.dao.EventResultService;
import credit.suisse.pojo.EventConstants;
import credit.suisse.pojo.EventResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EventResultServiceImpl implements EventResultService {

    private static Connection connection = null;
    private static final Logger logger = Logger.getGlobal();
    private static final String USER = "SA";
    private static final String PASSWORD = "";
    private static final String URL = "jdbc:hsqldb:file:eventsdb;shutdown=true";

    private static Connection getConnection() {
        try {
            if (EventResultServiceImpl.connection == null || EventResultServiceImpl.connection.isClosed()) {
                EventResultServiceImpl.logger.info("Getting connection.");
                connection = DriverManager.getConnection(EventResultServiceImpl.URL, EventResultServiceImpl.USER, EventResultServiceImpl.PASSWORD);
            }
        } catch (SQLException sqlExcept) {
            EventResultServiceImpl.logger.severe(String.format("Couldn't create connection to database!\n%s", sqlExcept.getMessage()));
        }
        return connection;
    }

    @Override
    public void createTable() {
        try {
            String sql = "CREATE TABLE EVENT(" +
                    "ID_PK INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                    "ID VARCHAR(250) NOT NULL, " +
                    "DURATION SMALLINT NOT NULL, " +
                    "TYPE VARCHAR(250), " +
                    "HOST VARCHAR(250), " +
                    "ALERT BOOLEAN NOT NULL)";

            EventResultServiceImpl.getConnection().prepareStatement(sql).executeUpdate();

            EventResultServiceImpl.logger.info("Event table created successfully!");
        } catch (SQLException sqlExcept) {
            EventResultServiceImpl.logger.severe(String.format("Couldn't create Event table!\n%s", sqlExcept.getMessage()));
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public void cleanTable() {
        try {
            String sql = "DELETE FROM EVENT";

            EventResultServiceImpl.getConnection().prepareStatement(sql).executeUpdate();
            EventResultServiceImpl.logger.info("All data from table Event was erased!");
        } catch (SQLException sqlExcept) {
            EventResultServiceImpl.logger.severe(String.format("Couldn't delete data from Event table!\n%s", sqlExcept.getMessage()));
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public void saveEventResult(EventResult result) {
        try {
            String sql = "INSERT INTO EVENT(ID, DURATION, TYPE, HOST, ALERT) VALUES (?,?,?,?,?)";
            PreparedStatement stmt = EventResultServiceImpl.getConnection().prepareStatement(sql);
            stmt.setString(EventConstants.ID.getIndex(), result.getId());
            stmt.setInt(EventConstants.DURATION.getIndex(), result.getDuration());
            stmt.setString(EventConstants.TYPE.getIndex(), result.getType());
            stmt.setString(EventConstants.HOST.getIndex(), result.getHost());
            stmt.setBoolean(EventConstants.ALERT.getIndex(), result.getAlert());

            stmt.execute();
            stmt.close();

            EventResultServiceImpl.logger.info(String.format("Event %s saved!", result.getId()));
        } catch (SQLException sqlExcept) {
            EventResultServiceImpl.logger.severe(String.format("Couldn't insert event %s!\n%s", result.getId(), sqlExcept.getMessage()));
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public List<EventResult> listAllEvents() {
        List<EventResult> results = new ArrayList<>();
        try {
            EventResultServiceImpl.logger.info("Selecting all items from Event.");

            String sql = "SELECT * FROM EVENT";
            ResultSet resultSet = EventResultServiceImpl.getConnection().prepareStatement(sql).executeQuery();

            while (resultSet.next()) {
                EventResult result = new EventResult(resultSet.getString(EventConstants.ID.getValue()),
                        resultSet.getInt(EventConstants.DURATION.getValue()),
                        resultSet.getBoolean(EventConstants.ALERT.getValue()));
                result.setType(resultSet.getString(EventConstants.TYPE.getValue()));
                result.setHost(resultSet.getString(EventConstants.HOST.getValue()));

                results.add(result);
            }
        } catch (SQLException sqlExcept) {
            EventResultServiceImpl.logger.severe(String.format("Couldn't read values from table Event.\n%s", sqlExcept.getMessage()));
        } finally {
            this.closeConnection();
        }

        return results;
    }

    private void closeConnection() {
        try {
            EventResultServiceImpl.getConnection().close();
        } catch (SQLException sqlExcept) {
            EventResultServiceImpl.logger.severe(String.format("Couldn't close connection.\n%s", sqlExcept.getMessage()));
        }
    }
}
