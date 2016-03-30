package cz.muni.fi.pv168.winetasting.backend;

import cz.muni.fi.pv168.winetasting.backend.Exceptions.ServiceFailureException;
import cz.muni.fi.pv168.winetasting.backend.Exceptions.ValidationException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by lukas on 3/15/16.
 */
public class WineSampleDAOImpl implements WineSampleDAO {

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

        wineSample.setId(resultSet.getLong("id"));
        wineSample.setVintnerFirstName(resultSet.getString("vintnerFirstName"));
        wineSample.setVintnerLastName(resultSet.getString("vintnerLastName"));
        wineSample.setVariety(resultSet.getString("variety"));
        wineSample.setColor(stringToColor(resultSet.getString("color")));
        wineSample.setCharacter(stringToWineCharacter(resultSet.getString("character")));
        wineSample.setYear(resultSet.getInt("year"));

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
                                "character," +
                                "year) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, wineSample.getVintnerFirstName());
            statement.setString(2, wineSample.getVintnerLastName());
            statement.setString(3, wineSample.getVariety());
            statement.setString(4, colorToString(wineSample.getColor()));
            statement.setString(5, wineCharacterToString(wineSample.getCharacter()));
            statement.setInt(6, wineSample.getYear());

            int addRows = statement.executeUpdate();
            if (addRows != 1) {
                throw new ServiceFailureException("createWineSample: number of added rows is not one");
            }
            ResultSet keys = statement.getGeneratedKeys();
            wineSample.setId(keys.getLong(1));

        }catch (SQLException ex) {
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
                            "character = ?," +
                            "year = ?")) {

            statement.setString(1, wineSample.getVintnerFirstName());
            statement.setString(2, wineSample.getVintnerLastName());
            statement.setString(3, wineSample.getVariety());
            statement.setString(4, colorToString(wineSample.getColor()));
            statement.setString(5, wineCharacterToString(wineSample.getCharacter()));
            statement.setInt(6, wineSample.getYear());

            int updatedRows = statement.executeUpdate();
            if (updatedRows != 1) {
                throw new ServiceFailureException("updateWineSample: number of updated rows is not one");
            }

        }catch (SQLException ex) {
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
            statement.setLong(1, wineSample.getId());

            int deletedRows = statement.executeUpdate();

            if (deletedRows == 0) {
                throw new ServiceFailureException("wineSample not found in db");
            } else if (deletedRows != 1) {
                throw new ServiceFailureException("more than one row deleted");
            }

        } catch (SQLException ex) {
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
                    "SELECT vintnerFirstName," +
                            "vintnerLastName," +
                            "variety," +
                            "color," +
                            "character," +
                            "year FROM WineSample WHERE id = ?")) {
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
            throw new ServiceFailureException("error retrieving WineSample with id " + id, ex);
        }
    }

    @Override
    public List<WineSample> findAllWineSamples() {
        checkDataSource();

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT vintnerFirstName," +
                            "vintnerLastName," +
                            "variety," +
                            "color," +
                            "character," +
                            "year FROM WineSample")) {
            ResultSet resultSet = statement.executeQuery();

            List<WineSample> wineSamples = new ArrayList<>();
            while (resultSet.next()) {
                wineSamples.add(resultSetToWineSample(resultSet));
            }
            return wineSamples;
        } catch (SQLException ex) {
            throw new ServiceFailureException("error retrieving all wineSamples from db", ex);
        }
    }

    @Override
    public List<WineSample> findWineSamplesByVariety(String variety) {
        checkDataSource();

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT vintnerFirstName," +
                            "vintnerLastName," +
                            "variety," +
                            "color," +
                            "character," +
                            "year FROM WineSample WHERE variety = ?")) {
            statement.setString(1, variety);
            ResultSet resultSet = statement.executeQuery();

            List<WineSample> wineSamples = new ArrayList<>();
            while (resultSet.next()) {
                wineSamples.add(resultSetToWineSample(resultSet));
            }
            return wineSamples;
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error retrieving wineSamples by variety " + variety, ex);
        }
    }
}
