package cz.muni.fi.pv168.winetasting.backend;

import org.junit.Before;
import org.junit.Test;

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

    private WineSampleDAO manager;

    @Before
    public void setUp() {
        manager = new WineSampleDAOImpl();
    }

    @Test
    public void createWineSample(){
        WineSample wineSample = new WineSample.Builder("Rizling")
                                                        .vintnerName("Tomovic", "Lukas")
                                                        .color(WineColor.WHITE)
                                                        .character(WineCharacter.VZH)
                                                        .year(2014)
                                                        .build();
        wineSample.setId((long) 1);

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
        wineSample1.setId((long)1);
        manager.createWineSample(wineSample1);

        WineSample wineSample2 = new WineSample.Builder("Chardonnay")
                                                        .vintnerName("Mrkvicka", "Jozef")
                                                        .color(WineColor.WHITE)
                                                        .character(WineCharacter.NZ)
                                                        .year(2013)
                                                        .build();
        wineSample2.setId((long)2);
        manager.createWineSample(wineSample2);

        WineSample wineSample3 = new WineSample.Builder("Alibernet")
                                                        .vintnerName("Bencik", "Peter")
                                                        .color(WineColor.RED)
                                                        .character(WineCharacter.KAB)
                                                        .year(2015)
                                                        .build();
        wineSample3.setId((long)3);
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
        expected.setId((long)1);
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
        wineSample1.setId((long)1);
        manager.createWineSample(wineSample1);

        WineSample wineSample2 = new WineSample.Builder("Chardonnay")
                                                        .vintnerName("Mrkvicka", "Jozef")
                                                        .color(WineColor.WHITE)
                                                        .character(WineCharacter.NZ)
                                                        .year(2013)
                                                        .build();
        wineSample2.setId((long)2);
        manager.createWineSample(wineSample2);

        WineSample wineSample3 = new WineSample.Builder("Alibernet")
                                                        .vintnerName("Bencik", "Peter")
                                                        .color(WineColor.RED)
                                                        .character(WineCharacter.KAB)
                                                        .year(2015)
                                                        .build();
        wineSample3.setId((long)3);
        manager.createWineSample(wineSample3);

        WineSample wineSample4 = new WineSample.Builder("Rizling")
                                                        .vintnerName("Tomovic", "Michal")
                                                        .color(WineColor.WHITE)
                                                        .character(WineCharacter.AKO)
                                                        .year(2010)
                                                        .build();
        wineSample1.setId((long)4);
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
        wineSample1.setId((long)1);
        manager.createWineSample(wineSample1);

        assertFalse(manager.findAllWineSamples().isEmpty());

        manager.deleteWineSample(wineSample1);
        WineSample actual = manager.findWineSampleById((long)1);

        assertThat("wine was not deleted", null, is(equalTo(actual)));
        assertTrue(manager.findAllWineSamples().isEmpty());
    }

    @Test
    public  void updateWineSample() {
        assertTrue(manager.findAllWineSamples().isEmpty());

        WineSample expected = new WineSample.Builder("Rizling")
                                                    .vintnerName("Tomovic", "Lukas")
                                                    .color(WineColor.WHITE)
                                                    .character(WineCharacter.VZH)
                                                    .year(2014)
                                                    .build();
        expected.setId((long)1);
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

    private static Comparator<WineSample> idComparator = new Comparator<WineSample>() {
        @Override
        public int compare(WineSample o1, WineSample o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };
}
