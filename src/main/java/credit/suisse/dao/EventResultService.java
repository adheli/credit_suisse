package credit.suisse.dao;

import credit.suisse.pojo.EventResult;

import java.sql.*;

public class EventResultService {

    private Connection connection = null;

    private Connection getConnection() {
        if (this.connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:hsqldb:file:eventsdb;shutdown=true", "SA", "");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE EVENT(" +
                "ID_PK INTEGER PRIMARY KEY, " +
                "ID VARCHAR(2) NOT NULL, " +
                "DURATION SMALLINT NOT NULL, " +
                "TYPE VARCHAR(2), " +
                "HOST VARCHAR(2), " +
                "ALERT BOOLEAN NOT NULL)";

        this.getConnection().prepareStatement(sql).executeUpdate();
    }

    public boolean isTableCreated() throws SQLException {
        DatabaseMetaData md = this.getConnection().getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);
        while (rs.next()) {
            if (rs.getString(3).toLowerCase().equals("event")) {
                return true;
            }
        }

        return false;
    }

    public void saveEventResult(EventResult result) throws SQLException {
        if (!this.isTableCreated()) {
            this.createTable();
        }

        String sql = "INSERT INTO EVENT(ID, DURATION, TYPE, HOST, ALERT) VALUES (?,?,?,?)";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setString(1, result.getId());
        stmt.setInt(2, result.getDuration());
        stmt.setString(3, result.getType());
        stmt.setString(4, result.getHost());
        stmt.setBoolean(3, result.getAlert());

        stmt.execute();
        stmt.close();

        System.out.println("Event " + result.getId() + " saved!");

        getConnection().close();
    }

    public void listAllEvents() throws SQLException {
        String sql = "SELECT * FROM EVENT";
        ResultSet resultSet = this.getConnection().prepareStatement(sql).executeQuery();

        while (resultSet.next()) {
            EventResult result = new EventResult(resultSet.getString("ID"), resultSet.getInt("DURATION"), resultSet.getBoolean("ALERT"));
            result.setType(resultSet.getString("TYPE"));
            result.setHost(resultSet.getString("HOST"));

            System.out.println(result);
        }

        this.getConnection().close();
    }

    public static void main(String[] args) throws SQLException {
        EventResultService service = new EventResultService();

        try {
            EventResult result = new EventResult("adheli", 3, false);
            result.setHost("adheli");
            result.setType("adheli");
            service.saveEventResult(result);
            service.listAllEvents();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            service.getConnection().close();
        }
    }
}
