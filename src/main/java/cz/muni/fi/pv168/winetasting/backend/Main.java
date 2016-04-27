package cz.muni.fi.pv168.winetasting.backend;

import cz.muni.fi.pv168.winetasting.backend.Exceptions.WineException;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;


/**
 * Created by lukas on 4/17/16.
 */
public class Main {

    final static Logger log = LoggerFactory.getLogger(Main.class);

    public static DataSource createMemoryDatabase() throws SQLException {
        EmbeddedDataSource dataSource = new EmbeddedDataSource();
        // in memory database
        dataSource.setDatabaseName("memory:winesample-web");
        // database is created automatically if it does not exist yet
        dataSource.setCreateDatabase("create");
        DBUtils.executeSqlScript(dataSource, WineTastingManager.class.getResource("/createTables.sql"));
        //populate db with tables and data
        return dataSource;
    }

    public static void main(String[] args) throws WineException {

        log.info("zaciname");
        DataSource dataSource = null;
        try {
            dataSource = createMemoryDatabase();
        } catch (SQLException ex) {
            throw new WineException("error creating database", ex);
           // TODO throw new custom exception, delete BookException
        }
        WineSampleDAO wineManager = new WineSampleDAOImpl(dataSource);

        List<WineSample> allWines = wineManager.findAllWineSamples();
        System.out.println("allBooks = " + allWines);

    }

}