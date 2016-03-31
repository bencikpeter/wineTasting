package cz.muni.fi.pv168.winetasting.backend;

import cz.muni.fi.pv168.winetasting.backend.Exceptions.ServiceFailureException;
import cz.muni.fi.pv168.winetasting.backend.Exceptions.ValidationException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by bencikpeter on 16.03.16.
 */
public class WineTastingDAOImpl implements WineTastingDAO {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    } // not sure if this method will be useful

    public WineTastingDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource(){
        if (dataSource == null){
            throw new IllegalStateException("DataSource is null");
        }
    }


    @Override
    public void createSession(WineTastingSession session) {
        checkDataSource();
        validate(session);

        if (session.getID() != null){
            throw new IllegalArgumentException("ID already set");
        }

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO WineTastingSession (place, date)" +
                                                                        "VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, session.getPlace());
            statement.setTimestamp(2, toSqlDate(session.getDateTime()));
            int addedRows = statement.executeUpdate();
            if (addedRows != 1){
                throw new ServiceFailureException("createSession: number of added rows is not one");
            }
            ResultSet keys = statement.getGeneratedKeys();
            session.setID(keys.getLong(1));

            //connection.commit();
            //no need to commit - connections by default are in auto commit mode

        } catch (SQLException ex){
            throw new ServiceFailureException("error when inserting session int db", ex);
        }
    }

    @Override
    public void updateSession(WineTastingSession session) {
        checkDataSource();
        validate(session);

        if (session.getID() == null){
            throw new IllegalArgumentException("Undefined session ID");
        }
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE WineTastingSession SET place = ?, date = ? " +
                                                                        "WHERE ID = ?")){
            statement.setString(1,session.getPlace());
            statement.setTimestamp(2,toSqlDate(session.getDateTime()));
            statement.setLong(3,session.getID());

            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0){
                throw new ServiceFailureException("session not found in db");
            } else if(updatedRows != 1){
                throw new ServiceFailureException("invalid number of updater rows");
            }

        } catch (SQLException ex){
            throw new ServiceFailureException("error updating session", ex);
        }

    }

    @Override
    public void deleteSession(WineTastingSession session) {
        if (session == null){
            throw new IllegalArgumentException("session is null");
        }
        if (session.getID() == null){
            throw new IllegalArgumentException("session id is null");
        }

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM WineTastingSession WHERE ID = ?")){

            statement.setLong(1,session.getID());

            int deletedRows = statement.executeUpdate();

            if (deletedRows == 0){
                throw new ServiceFailureException("entity not found in db");
            } else if (deletedRows !=1){
                throw new ServiceFailureException("more than one row deleted");
            }

        } catch (SQLException ex){
            throw new ServiceFailureException("error deleting session", ex);
        }

    }

    @Override
    public List<WineTastingSession> findSessionByDate(ZonedDateTime date) {
        //TODO implement findSession
        if (date == null) {
            throw new IllegalArgumentException("date is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT id, place, date FROM WineTastingSession WHERE date = ?")) {

            statement.setTimestamp(1, toSqlDate(date));
            ResultSet rs = statement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

        @Override
    public List<WineTastingSession> findAllSessions() {
        //TODO findAllSessions
        return null;
    }


    private static void validate(WineTastingSession session) {
        if (session == null) {
            throw new IllegalArgumentException("session is null");
        }
        if (session.getID() == null) {
            throw new ValidationException("ID is null");
        }
        if (session.getPlace() == null) {
            throw new ValidationException("place is null");
        }
        if (session.getDateTime() == null) {
            throw new ValidationException("DateTime is null");
        }
    }


    private Timestamp toSqlDate(ZonedDateTime zonedDateTime){ //TODO check and maybe change return type
        return new Timestamp(zonedDateTime.toInstant().getEpochSecond() * 1000L);
    }
}
