package cz.muni.fi.pv168.winetasting.backend;

import cz.muni.fi.pv168.winetasting.backend.Exceptions.ServiceFailureException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 3/27/16.
 */
public class WineTastingManagerImpl implements WineTastingManager{

    private DataSource dataSource;

    public WineTastingManagerImpl() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public WineTastingManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource(){
        if (dataSource == null){
            throw new IllegalStateException("DataSource is null");
        }
    }

    @Override
    public WinesLayout generateWinesLayout(WineTastingSession session) {
        checkDataSource();
        List wines = findAllWinesInSession(session);
        return new WinesLayout(wines);

    }

    @Override
    public void assignWineToSession(WineTastingSession session, WineSample sample) {
        checkDataSource();

        if (session == null || sample == null) {
            throw new IllegalArgumentException("at least one parameter is null");
        }

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO WineTasting (sessionId, sampleId)" +
                        "VALUES (?,?)")){
            statement.setLong(1, session.getID());
            statement.setLong(2, sample.getId());

            int addedRows = statement.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("assignWineToSession: number of added rows is not one");
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("error assigning wine to session",ex);
        }

    }

    @Override
    public void assignRatingToWine(WineSample sample, WineTastingSession session, int rating) {
        checkDataSource();
        if (rating < 0) {
            throw new IllegalArgumentException("rating is below zero");
        }
        if (rating > 100) {
            throw new IllegalArgumentException("rating is higher than 100");
        }
        if (session == null || sample == null) {
            throw new IllegalArgumentException("at least one parameter is null");
        }

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE WineTasting SET rating = ?" +
                                                                        "WHERE sessionId = ? AND sampleId = ?")) {
            statement.setInt(1, rating);
            statement.setLong(2, session.getID());
            statement.setLong(3, sample.getId());

            int updatedRows = statement.executeUpdate();
            if (updatedRows != 1) {
                throw new ServiceFailureException("number of updated rows is not one");
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("error assigning rating to wine", ex);
        }
    }

    @Override
    public WineTastingSession findSessionWithWine(WineSample sample) {
        checkDataSource();
        if (sample == null) {
            throw new IllegalArgumentException("sample is null");
        }

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT sessionId FROM WineTasting " +
                                                                        "WHERE sampleId = ?")) {
            statement.setLong(1, sample.getId());
            ResultSet resultSet = statement.executeQuery();
            Long sessionId = null;
            if (resultSet.next()) {
                sessionId = resultSet.getLong("sessionId");
                if (resultSet.next()) {
                    throw new ServiceFailureException("retrieved more than one row from WineTasting");
                }
            }
            WineTastingDAO manager = new WineTastingDAOImpl(dataSource);
            WineTastingSession session = manager.findSessionById(sessionId);
            return session;
        } catch (SQLException ex) {
            throw new ServiceFailureException("error finding session with specific wine", ex);
        }
    }

    @Override
    public List<WineSample> findAllWinesInSession(WineTastingSession session) {
        checkDataSource();
        if (session == null) {
            throw new IllegalArgumentException("session is null");
        }

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT  sampleId FROM WineTasting WHERE sessionId = ?")) {
            statement.setLong(1, session.getID());
            ResultSet resultSet = statement.executeQuery();
            List<WineSample> wines = new ArrayList<>();
            while (resultSet.next()) {
                WineSampleDAO manager = new WineSampleDAOImpl(dataSource);
                WineSample sample = manager.findWineSampleById(resultSet.getLong("sampleId"));
                if (sample != null) {
                    wines.add(sample);
                }
            }
            return wines;
        } catch (SQLException ex) {
            throw new ServiceFailureException("error finding all wines in specific session", ex);
        }
    }
}
