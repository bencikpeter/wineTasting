package cz.muni.fi.pv168.winetasting.backend;

import org.junit.Before;
import org.testng.annotations.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by bencikpeter on 16.03.16.
 */
public class WineTastingDAOTest {

    private WineTastingDAO wineTastingDAO;

    @Before
    public void setUp() {
        wineTastingDAO = new WineTastingDAOImpl();
    }

    @Test
    public void createWineTastingSession(){
        WineTastingSession session = new WineTastingSession();
        ZonedDateTime date = ZonedDateTime.of(2016,02,10,11,23,1,11,java.time.ZoneId.of("GMT+2"));
        session.setDateTime(date);
        session.setID(1);
        session.setPlace("somewhere warm");

        wineTastingDAO.createSession(session);

        WineTastingSession result = wineTastingDAO.findSessionByDate(date);

        assertThat("session loaded is different than stored",result,is(equalTo(session)));

        assertThat("loaded grave should not be the same instance",result,is(not(sameInstance(session))));

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void CreateWithNull(){
        wineTastingDAO.createSession(null);
    }

    @Test
    public void CreateWithWrongArguments(){
        WineTastingSession session = new WineTastingSession();
        ZonedDateTime date = ZonedDateTime.of(2016,02,11,11,23,1,11,java.time.ZoneId.of("GMT+2"));
        session.setDateTime(date);
        session.setID(2);
        session.setPlace("somewhere warm");

        try {
            session.setID(-1);
            wineTastingDAO.createSession(session);
            fail("should refuse negative ID");
        }catch (IllegalArgumentException exc){
            session.setID(2);
            //OK
        }

        try {
            WineTastingSession session2 = new WineTastingSession();
            ZonedDateTime date2 = ZonedDateTime.of(2016,03,11,11,23,1,11,java.time.ZoneId.of("GMT+2"));
            session2.setDateTime(date2);
            session2.setID(2);
            session2.setPlace("somewhere warm");

            wineTastingDAO.createSession(session);
            wineTastingDAO.createSession(session2);
            fail("should reject duplicate ID");
        } catch (IllegalArgumentException exc){
            //OK
        }

        try{
            session.setDateTime(null);
            wineTastingDAO.createSession(session);
            fail("non-present date undetected");
        } catch (IllegalArgumentException exc){
            //OK
        }
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
        ZonedDateTime date = ZonedDateTime.of(2016,02,10,11,23,1,11,java.time.ZoneId.of("GMT+2"));
        session.setDateTime(date);
        session.setID(5);
        session.setPlace("somewhere warm");

        WineTastingSession session2 = new WineTastingSession();
        ZonedDateTime date2 = ZonedDateTime.of(2016,03,10,11,23,1,11,java.time.ZoneId.of("GMT+2"));
        session2.setDateTime(date2);
        session2.setID(6);
        session2.setPlace("somewhere warm");

        WineTastingSession session3 = new WineTastingSession();
        ZonedDateTime date3 = ZonedDateTime.of(2016,04,10,11,23,1,11,java.time.ZoneId.of("GMT+2"));
        session3.setDateTime(date3);
        session3.setID(7);
        session3.setPlace("somewhere warm");

        wineTastingDAO.createSession(session);
        wineTastingDAO.createSession(session2);
        wineTastingDAO.createSession(session3);

        List<WineTastingSession> expected = new ArrayList<>();
        expected.add(session);
        expected.add(session2);
        expected.add(session3);

        List<WineTastingSession> result = wineTastingDAO.findAllSessions();

        Collections.sort(expected,comparator);
        Collections.sort(result,comparator);

        assertEquals("expected and retrieved lists are different",expected,result);
    }

    private static Comparator<WineTastingSession> comparator = new Comparator<WineTastingSession>() {
        @Override
        public int compare(WineTastingSession o1, WineTastingSession o2) {
            return Integer.compare(o1.getID(),o2.getID());
        }
    };

}
