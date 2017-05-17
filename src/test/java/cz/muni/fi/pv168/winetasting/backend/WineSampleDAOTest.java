package cz.muni.fi.pv168.winetasting.backend;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by lukas on 3/15/16.
 */
public class WineSampleDAOTest {

    private WineSampleDAOImpl manager;
    private DataSource dataSource;

    private static DataSource prepareDataSource() throws SQLException {
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
        DBUtils.executeSqlScript(dataSource, WineTastingManager.class.getResource("/createTables.sql"));
        manager = new WineSampleDAOImpl(dataSource);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, WineTastingManager.class.getResource("/dropTables.sql"));
    }

    @Test
    public void createWineSample(){
        WineSample wineSample = new WineSample.Builder("Rizling")
                                                        .vintnerName("Tomovic", "Lukas")
                                                        .color(WineColor.WHITE)
                                                        .character(WineCharacter.VZH)
                                                        .year(2014)
                                                        .build();

        manager.createWineSample(wineSample);

        Long wineSampleId = wineSample.getId();

        assertThat("saved wine sample has null id", wineSample.getId(), is(not(equalTo(null))));

        WineSample res = manager.findWineSampleById(wineSampleId);

        assertThat("loaded wine differs from the saved one", res, is(equalTo(wineSample)));

        assertThat("loaded wine is the same instance", res, is(not(sameInstance(wineSample))));

        assertThat("loaded wine vintner first name differs from the saved one",
                    res.getVintnerFirstName(),
                    is(equalTo(wineSample.getVintnerFirstName())));

        assertThat("loaded wine vintner last name differs from the saved one",
                res.getVintnerLastName(),
                is(equalTo(wineSample.getVintnerLastName())));

        assertThat("loaded wine variety differs from the saved one",
                res.getVariety(),
                is(equalTo(wineSample.getVariety())));

        assertThat("loaded wine color differs from the saved one",
                res.getColor(),
                is(equalTo(wineSample.getColor())));

        assertThat("loaded wine character differs from the saved one",
                res.getCharacter(),
                is(equalTo(wineSample.getCharacter())));

        assertThat("loaded wine year differs from the saved one",
                res.getYear(),
                is(equalTo(wineSample.getYear())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNull() throws Exception {
        manager.createWineSample(null);
    }

    @Test
    public void getAllWineSamples() {
        assertTrue(manager.findAllWineSamples().isEmpty());

        WineSample wineSample1 = new WineSample.Builder("Rizling")
                                                        .vintnerName("Tomovic", "Lukas")
                                                        .color(WineColor.WHITE)
                                                        .character(WineCharacter.VZH)
                                                        .year(2014)
                                                        .build();
        manager.createWineSample(wineSample1);

        WineSample wineSample2 = new WineSample.Builder("Chardonnay")
                                                        .vintnerName("Mrkvicka", "Jozef")
                                                        .color(WineColor.WHITE)
                                                        .character(WineCharacter.NZ)
                                                        .year(2013)
                                                        .build();
        manager.createWineSample(wineSample2);

        WineSample wineSample3 = new WineSample.Builder("Alibernet")
                                                        .vintnerName("Bencik", "Peter")
                                                        .color(WineColor.RED)
                                                        .character(WineCharacter.KAB)
                                                        .year(2015)
                                                        .build();
        manager.createWineSample(wineSample3);

        List<WineSample> expected = Arrays.asList(wineSample1,
                                                  wineSample2,
                                                  wineSample3);

        List<WineSample> actual = manager.findAllWineSamples();

        Collections.sort(expected, idComparator);
        Collections.sort(actual, idComparator);

        assertEquals("saved and retrieved wines differ", expected, actual);
    }

    @Test
    public void findWineSample() {
        assertTrue(manager.findAllWineSamples().isEmpty());

        WineSample expected = new WineSample.Builder("Rizling")
                                                    .vintnerName("Tomovic", "Lukas")
                                                    .color(WineColor.WHITE)
                                                    .character(WineCharacter.VZH)
                                                    .year(2014)
                                                    .build();
        manager.createWineSample(expected);

        WineSample actual = manager.findWineSampleById((long)1);

        assertThat("expected and found wines differ", expected, is(equalTo(actual)));
    }

    @Test
    public void findWineSampleByVariety() {
        assertTrue(manager.findAllWineSamples().isEmpty());

        WineSample wineSample1 = new WineSample.Builder("Rizling")
                                                        .vintnerName("Tomovic", "Lukas")
                                                        .color(WineColor.WHITE)
                                                        .character(WineCharacter.VZH)
                                                        .year(2014)
                                                        .build();
        manager.createWineSample(wineSample1);

        WineSample wineSample2 = new WineSample.Builder("Chardonnay")
                                                        .vintnerName("Mrkvicka", "Jozef")
                                                        .color(WineColor.WHITE)
                                                        .character(WineCharacter.NZ)
                                                        .year(2013)
                                                        .build();
        manager.createWineSample(wineSample2);

        WineSample wineSample3 = new WineSample.Builder("Alibernet")
                                                        .vintnerName("Bencik", "Peter")
                                                        .color(WineColor.RED)
                                                        .character(WineCharacter.KAB)
                                                        .year(2015)
                                                        .build();
        manager.createWineSample(wineSample3);

        WineSample wineSample4 = new WineSample.Builder("Rizling")
                                                        .vintnerName("Tomovic", "Michal")
                                                        .color(WineColor.WHITE)
                                                        .character(WineCharacter.AKO)
                                                        .year(2010)
                                                        .build();
        manager.createWineSample(wineSample4);

        List<WineSample> expected = Arrays.asList(wineSample1,
                                                  wineSample4);

        List<WineSample> actual = manager.findWineSamplesByVariety("Rizling");

        Collections.sort(expected, idComparator);
        Collections.sort(actual, idComparator);

        assertEquals("saved and retrieved wines differ", expected, actual);
    }

    @Test
    public void deleteWineSample() {
        assertTrue(manager.findAllWineSamples().isEmpty());

        WineSample wineSample1 = new WineSample.Builder("Rizling")
                                                        .vintnerName("Tomovic", "Lukas")
                                                        .color(WineColor.WHITE)
                                                        .character(WineCharacter.VZH)
                                                        .year(2014)
                                                        .build();
        manager.createWineSample(wineSample1);

        assertFalse(manager.findAllWineSamples().isEmpty());

        manager.deleteWineSample(wineSample1);
        WineSample actual = manager.findWineSampleById((long)1);

        assertThat("wine was not deleted", null, is(equalTo(actual)));
        assertTrue(manager.findAllWineSamples().isEmpty());
    }

    @Test
    public void updateWineSample() {
        assertTrue(manager.findAllWineSamples().isEmpty());

        WineSample expected = new WineSample.Builder("Rizling")
                                                    .vintnerName("Tomovic", "Lukas")
                                                    .color(WineColor.WHITE)
                                                    .character(WineCharacter.VZH)
                                                    .year(2014)
                                                    .build();
        manager.createWineSample(expected);

        assertFalse(manager.findAllWineSamples().isEmpty());

        expected.setYear(2012);
        expected.setCharacter(WineCharacter.BV);
        expected.setVariety("Pinot Blanc");
        expected.setVintnerLastName("Vesely");
        expected.setVintnerFirstName("Michal");

        manager.updateWineSample(expected);
        WineSample actual = manager.findWineSampleById((long)1);

        assertThat("year of wine is not updated", expected.getYear(), is(equalTo(actual.getYear())));
        assertThat("character of wine is not updated", expected.getCharacter(), is(equalTo(actual.getCharacter())));
        assertThat("variety of wine is not updated", expected.getVariety(), is(equalTo(actual.getVariety())));
        assertThat("vintner first name is not updated", expected.getVintnerFirstName(),
                                                        is(equalTo(actual.getVintnerFirstName())));
        assertThat("vintner last name is not updated", expected.getVintnerLastName(),
                                                       is(equalTo(actual.getVintnerLastName())));
    }

    @Test
    public void findAllUnsessionedWines() {
        WineSample wineSample = new WineSample.Builder("Rizling")
                .vintnerName("Tomovic", "Lukas")
                .color(WineColor.WHITE)
                .character(WineCharacter.VZH)
                .year(2014)
                .build();

        manager.createWineSample(wineSample);

        WineSample wineSample2 = new WineSample.Builder("Chardonnay")
                .vintnerName("Peter", "Bencik")
                .color(WineColor.WHITE)
                .character(WineCharacter.VZH)
                .year(2013)
                .build();

        manager.createWineSample(wineSample2);

        WineTastingSession session = new WineTastingSession();
        LocalDate date = LocalDate.of(2016,02,10);
        session.setDate(date);
        session.setPlace("somewhere warm");

        WineTastingDAO tastingManagerDAO = new WineTastingDAOImpl(dataSource);

        tastingManagerDAO.createSession(session);

        WineTastingManager tastingManager = new WineTastingManagerImpl(dataSource);
        tastingManager.assignWineToSession(session,wineSample);

        List<WineSample> unsessionedSamples = manager.findAllUnsessionedWines();

        assertThat("Only one wine should be selected",unsessionedSamples.size(), is(equalTo(1)));
        assertThat("Wrong wine selected",unsessionedSamples.get(0),is(equalTo(wineSample2)));
    }

    private static Comparator<WineSample> idComparator = new Comparator<WineSample>() {
        @Override
        public int compare(WineSample o1, WineSample o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };
}
