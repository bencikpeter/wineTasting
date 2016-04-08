package cz.muni.fi.pv168.winetasting.backend;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import cz.muni.fi.pv168.winetasting.backend.Exceptions.ValidationException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


import static org.junit.Assert.assertThat;

/**
 * Created by bencikpeter on 16.03.16.
 */
public class WineTastingDAOTest {

    private WineTastingDAO tastingManager;
    private DataSource dataSource;

    private static DataSource prepareDataSource(){
        EmbeddedDataSource dataSource = new EmbeddedDataSource();
        // we will use in memory database
        dataSource.setDatabaseName("memory:winesample-test");
        // database is created automatically if it does not exist yet
        dataSource.setCreateDatabase("create");
        return dataSource;
    }

    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, WineTastingManager.class.getResource("createTables.sql"));
        tastingManager = new WineTastingDAOImpl(dataSource);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, WineTastingManager.class.getResource("dropTables.sql"));
    }

    @Test
    public void createWineTastingSession() throws SQLException {
        WineTastingSession session = new WineTastingSession();
        //ZonedDateTime date = ZonedDateTime.of(2016,02,10,11,23,1,11,java.time.ZoneId.of("GMT+2"));
        LocalDate date = LocalDate.of(2016,02,10);
        session.setDate(date);
        //session.setID(new Long(1));
        session.setPlace("somewhere warm");

        tastingManager.createSession(session);
        session.setID(new Long(1));
        List<WineTastingSession> result = tastingManager.findSessionByDate(date);

        assertThat("session loaded is different than stored",result.get(0),is(equalTo(session)));

        assertThat("loaded grave should not be the same instance",result.get(0),is(not(sameInstance(session))));

    }

    @Test(expected = IllegalArgumentException.class)
    public void CreateWithNull(){
        tastingManager.createSession(null);
    }

    @Test(expected = ValidationException.class)
    public void CreateWithWrongArguments(){
        WineTastingSession session = new WineTastingSession();

        session.setPlace("somewhere warm");
        session.setDate(null);

        tastingManager.createSession(session);
        fail("non-present date undetected");
}



    @Test
    public void updateSession(){
        //TODO
    }
    @Test
    public void deleteSession(){
        //TODO
    }

    @Test
    public void findAllSessions(){
        WineTastingSession session = new WineTastingSession();
        LocalDate date = LocalDate.of(2016,02,10);
        session.setDate(date);
        //session.setID(5);
        session.setPlace("somewhere warm");

        WineTastingSession session2 = new WineTastingSession();
        LocalDate date2 = LocalDate.of(2016,02,11);
        session2.setDate(date2);
        //session2.setID(6);
        session2.setPlace("somewhere warm");

        WineTastingSession session3 = new WineTastingSession();
        LocalDate date3 = LocalDate.of(2016,02,12);
        session3.setDate(date3);
        //session3.setID(7);
        session3.setPlace("somewhere warm");

        tastingManager.createSession(session);
        tastingManager.createSession(session2);
        tastingManager.createSession(session3);

        List<WineTastingSession> expected = new ArrayList<>();
        expected.add(session);
        expected.add(session2);
        expected.add(session3);

        List<WineTastingSession> result = tastingManager.findAllSessions();

        Collections.sort(expected,comparator);
        Collections.sort(result,comparator);

        assertEquals("expected and retrieved lists are different",expected,result);
    }

    private static Comparator<WineTastingSession> comparator = new Comparator<WineTastingSession>() {
        @Override
        public int compare(WineTastingSession o1, WineTastingSession o2) {
            return Long.compare(o1.getID(),o2.getID());
        }
    };

}
