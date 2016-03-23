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
        WineSample  wineSample= new WineSample(1,
                                               "Lukas",
                                               "Tomovic",
                                               "Rizling",
                                               WineColor.WHITE,
                                               WineCharacter.VZH,
                                               2014);

        manager.createWineSample(wineSample);

        Integer wineSampleId = wineSample.getId();

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

        WineSample wineSample1 = new WineSample(1,
                                                "Lukas",
                                                "Tomovic",
                                                "Rizling",
                                                WineColor.WHITE,
                                                WineCharacter.VZH,
                                                2014);

        WineSample wineSample2 = new WineSample(2,
                                                "Jozef",
                                                "Mrkvicka",
                                                "Chardonnay",
                                                WineColor.WHITE,
                                                WineCharacter.NZ,
                                                2013);

        WineSample wineSample3 = new WineSample(3,
                                                "Peter",
                                                "Bencik",
                                                "Alibernet",
                                                WineColor.RED,
                                                WineCharacter.KAB,
                                                2015);

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

        WineSample  expected = new WineSample(1,
                                              "Lukas",
                                              "Tomovic",
                                              "Rizling",
                                              WineColor.WHITE,
                                              WineCharacter.VZH,
                                              2014);

        WineSample actual = manager.findWineSampleById(1);

        assertThat("expected and found wines differ", expected, is(equalTo(actual)));

    }

    @Test
    public void findWineSampleByVariety() {
        assertTrue(manager.findAllWineSamples().isEmpty());

        WineSample  expected1 = new WineSample(1,
                "Lukas",
                "Tomovic",
                "Rizling",
                WineColor.WHITE,
                WineCharacter.VZH,
                2014);

        WineSample  expected2 = new WineSample(1,
                "Lukas",
                "Tomovic",
                "Rizling",
                WineColor.WHITE,
                WineCharacter.VZH,
                2014);
        //TODO

    }

    private static Comparator<WineSample> idComparator = new Comparator<WineSample>() {
        @Override
        public int compare(WineSample o1, WineSample o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };
}
