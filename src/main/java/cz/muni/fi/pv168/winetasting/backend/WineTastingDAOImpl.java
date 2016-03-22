package cz.muni.fi.pv168.winetasting.backend;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by bencikpeter on 16.03.16.
 */
public class WineTastingDAOImpl implements WineTastingDAO {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    private void chceckDataSource(){
        if (dataSource == null){
            throw new IllegalStateException("DataSource is null");
        }
    }


    @Override
    public void createSession(WineTastingSession session) {
        chceckDataSource();
        validate(session);

        if (session.getID() != null){
            throw new IllegalArgumentException("ID already set");
        }

        try(Connection connection = dataSource.getConnection()){
            PreparedStatement statement = connection.prepareStatement("INSERT INTO WineTastingSession (ID, place, date)" +
                                                                        "VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, session.getID());
            statement.setString(2, session.getPlace());
            statement.setDate(3, toSqlDate(session.getDateTime()));
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            session.setID(keys.getLong(1));

            //connection.commit();
            //no need to commit - connections by default are in auto commit mode

        } catch (SQLException ex){
            //TODO manage this exception
            //I was thinking - what about making a universal exception as in https://github.com/petradamek/PV168/
            //something like ServiceFailure exception
        }

    }

    @Override
    public void updateSession(WineTastingSession session) {

    }

    @Override
    public void deleteSession(WineTastingSession session) {

    }

    @Override
    public WineTastingSession findSessionByDate(ZonedDateTime date) {
        return null;
    }

    @Override
    public List<WineTastingSession> findAllSessions() {
        return null;
    }

    private static void validate(WineTastingSession session){} //TODO implement session validation

    private Date toSqlDate(ZonedDateTime zonedDateTime){ //TODO implement somehow that freaking conversion
        return null;
    }
}
