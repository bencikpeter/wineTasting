package cz.muni.fi.pv168.winetasting.backend;

import cz.muni.fi.pv168.winetasting.backend.Exceptions.ServiceFailureException;
import cz.muni.fi.pv168.winetasting.backend.Exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by lukas on 3/15/16.
 */
public class WineSampleDAOImpl implements WineSampleDAO {

    final static Logger log = LoggerFactory.getLogger(WineSampleDAOImpl.class);

    private DataSource dataSource;

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is null");
        }
    }

    private static void validate(WineSample wineSample) {
        if (wineSample == null) {
            throw new IllegalArgumentException("wineSample is null");
        }
        if (wineSample.getColor() == null) {
            throw new ValidationException("color is null");
        }
        if (wineSample.getVariety() == null) {
            throw new ValidationException("variety is null");
        }
        if (wineSample.getVintnerFirstName() == null) {
            throw new ValidationException("vintner first name is null");
        }
        if (wineSample.getVintnerLastName() == null) {
            throw new ValidationException("vintner last name is null");
        }
        if (wineSample.getYear() < 0) {
            throw new ValidationException("year is below zero");
        }
        if (wineSample.getYear() > Calendar.getInstance().get(Calendar.YEAR)) {
            throw new ValidationException("year is in the future");
        }
    }

    private static String colorToString(WineColor color) {
        return color == null ? null : color.name();
    }

    private static WineColor stringToColor(String color) {
        return color == null ? null : WineColor.valueOf(color);
    }

    private static String wineCharacterToString(WineCharacter wineCharacter) {
        return wineCharacter == null ? null : wineCharacter.name();
    }

    private static WineCharacter stringToWineCharacter(String wineCharacter) {
        return wineCharacter == null ? null : WineCharacter.valueOf(wineCharacter);
    }

    private WineSample resultSetToWineSample(ResultSet resultSet) throws SQLException {
        WineSample wineSample = new WineSample();

        wineSample.setId(resultSet.getLong("ID"));
        wineSample.setVintnerFirstName(resultSet.getString("vintnerFirstName"));
        wineSample.setVintnerLastName(resultSet.getString("vintnerLastName"));
        wineSample.setVariety(resultSet.getString("variety"));
        wineSample.setColor(stringToColor(resultSet.getString("color")));
        wineSample.setCharacter(stringToWineCharacter(resultSet.getString("character_")));
        wineSample.setYear(resultSet.getInt("year_"));

        return wineSample;
    }

    public WineSampleDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createWineSample(WineSample wineSample) {
        checkDataSource();
        validate(wineSample);

        if (wineSample.getId() != null) {
            throw new IllegalArgumentException("id already exists");
        }

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO WineSample(vintnerFirstName," +
                                "vintnerLastName," +
                                "variety," +
                                "color," +
                                "character_," +
                                "year_) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            log.debug("Creatring wine sample {}",wineSample);

            statement.setString(1, wineSample.getVintnerFirstName());
            statement.setString(2, wineSample.getVintnerLastName());
            statement.setString(3, wineSample.getVariety());
            statement.setString(4, colorToString(wineSample.getColor()));
            statement.setString(5, wineCharacterToString(wineSample.getCharacter()));
            statement.setInt(6, wineSample.getYear());

            int addRows = statement.executeUpdate();
            DBUtils.checkUpdatesCount(addRows, wineSample, true);
            Long id = DBUtils.getId(statement.getGeneratedKeys());
            wineSample.setId(id);
        }catch (SQLException ex) {
            log.error("Error when creating wineSample",ex,wineSample);
            throw new ServiceFailureException("error inserting wineSample into db", ex);
        }
    }

    @Override
    public void updateWineSample(WineSample wineSample) {
        checkDataSource();
        validate(wineSample);

        if (wineSample.getId() == null) {
            throw new IllegalArgumentException("undefined wineSample id");
        }
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE WineSample SET vintnerFirstName = ?," +
                            "vintnerLastName = ?," +
                            "variety = ?," +
                            "color = ?," +
                            "character_ = ?," +
                            "year_ = ? " +
                            "WHERE id = ?")) {
            log.debug("Updating wine sample {}",wineSample);

            statement.setString(1, wineSample.getVintnerFirstName());
            statement.setString(2, wineSample.getVintnerLastName());
            statement.setString(3, wineSample.getVariety());
            statement.setString(4, colorToString(wineSample.getColor()));
            statement.setString(5, wineCharacterToString(wineSample.getCharacter()));
            statement.setInt(6, wineSample.getYear());
            statement.setLong(7, wineSample.getId());

            int updatedRows = statement.executeUpdate();
            DBUtils.checkUpdatesCount(updatedRows, wineSample, false);

        }catch (SQLException ex) {
            log.error("Error when updating wineSample",ex,wineSample);
            throw new ServiceFailureException("error updating wineSample", ex);
        }
    }

    @Override
    public void deleteWineSample(WineSample wineSample) {
        checkDataSource();
        validate(wineSample);

        if (wineSample.getId() == null) {
            throw new IllegalArgumentException("wineSample id is null");
        }
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM WineSample WHERE id = ?")) {
            log.debug("Deleting wine sample {}",wineSample);
            statement.setLong(1, wineSample.getId());

            int deletedRows = statement.executeUpdate();
            DBUtils.checkUpdatesCount(deletedRows, wineSample, false);

        } catch (SQLException ex) {
            log.error("Error when deleting wine sample",ex,wineSample);
            throw new ServiceFailureException("error deleting wineSample", ex);
        }
    }

    @Override
    public WineSample findWineSampleById(Long id) {
        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT id," +
                            "vintnerFirstName," +
                            "vintnerLastName," +
                            "variety," +
                            "color," +
                            "character_," +
                            "year_ FROM WineSample WHERE id = ?")) {
            log.debug("Finding wine sample by ID {}",id);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                WineSample wineSample = resultSetToWineSample(resultSet);

                if (resultSet.next()) {
                    throw new ServiceFailureException("More entities with the same id found");
                }

                return wineSample;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            log.error("Error when find wine sample by id",ex,id);
            throw new ServiceFailureException("error retrieving WineSample with id " + id, ex);
        }
    }

    @Override
    public List<WineSample> findAllWineSamples() {
        checkDataSource();

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT id," +
                            "vintnerFirstName," +
                            "vintnerLastName," +
                            "variety," +
                            "color," +
                            "character_," +
                            "year_ FROM WineSample")) {
            log.debug("Finding all wine samples");
            ResultSet resultSet = statement.executeQuery();

            List<WineSample> wineSamples = new ArrayList<>();
            while (resultSet.next()) {
                wineSamples.add(resultSetToWineSample(resultSet));
            }
            return wineSamples;
        } catch (SQLException ex) {
            log.error("Error when finding all wine samples",ex);
            throw new ServiceFailureException("error retrieving all wineSamples from db", ex);
        }
    }

    @Override
    public List<WineSample> findWineSamplesByVariety(String variety) {
        checkDataSource();

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT id," +
                            "vintnerFirstName," +
                            "vintnerLastName," +
                            "variety," +
                            "color," +
                            "character_," +
                            "year_ FROM WineSample WHERE variety = ?")) {
            log.debug("Finding wine samples by variety {}",variety);
            statement.setString(1, variety);
            ResultSet resultSet = statement.executeQuery();

            List<WineSample> wineSamples = new ArrayList<>();
            while (resultSet.next()) {
                wineSamples.add(resultSetToWineSample(resultSet));
            }
            return wineSamples;
        } catch (SQLException ex) {
            log.error("Error when finding wine samples by variety",ex,variety);
            throw new ServiceFailureException("Error retrieving wineSamples by variety " + variety, ex);
        }
    }

    @Override
    public List<WineSample> findAllUnsessionedWines(){
        checkDataSource();

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT WineSample.id as id," +
                            "vintnerFirstName," +
                            "vintnerLastName," +
                            "variety," +
                            "color," +
                            "character_," +
                            "year_ FROM WineSample INNER JOIN WineTasting " +
                            "ON WineSample.id = WineTasting.sampleID")) {
            log.debug("Finding all unsessioned wines");
            List<WineSample> allWineSamples = findAllWineSamples();
            ResultSet resultSet = statement.executeQuery();

            List<WineSample> sessionedWineSamples = new ArrayList<>();
            while (resultSet.next()) {
                sessionedWineSamples.add(resultSetToWineSample(resultSet));
            }

            allWineSamples.removeAll(sessionedWineSamples);
            return allWineSamples;
        } catch (SQLException ex) {
            log.error("Error when finding all usessioned wines",ex);
            throw new ServiceFailureException("Error retrieving unsessioned wine samples", ex);
        }
    }
}
