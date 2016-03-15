package cz.muni.fi.pv168.winetasting.backend;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author Petr Adamek, Martin Kuba
 */
public class GraveManagerImplTest {

    private GraveManagerImpl manager;

    @Before
    public void setUp() throws SQLException {
        manager = new GraveManagerImpl();
    }

    @Test
    public void createGrave() {
        Grave grave = newGrave(12, 13, 6, "Nice grave");
        manager.createGrave(grave);

        Long graveId = grave.getId();
        //newer type of assertions
        assertThat("saved grave has null id", grave.getId(), is(not(equalTo(null))));
        //old type of assertions
        assertNotNull("saved grave has null id", graveId);

        Grave result = manager.getGrave(graveId);
        //loaded instance should be equal to the saved one
        assertThat("loaded grave differs from the saved one", result, is(equalTo(grave)));
        //but it should be another instance
        assertThat("loaded grave is the same instance", result, is(not(sameInstance(grave))));
        //Grave.equals() method may be broken, check properties' values
        assertDeepEquals(grave, result);
    }

    // expected exception - JUnit 4 style
    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNull() throws Exception {
        manager.createGrave(null);
    }

    //expected exception - JUnit 3 style
    @Test
    public void testCreateWithNullOldStyle() throws Exception {
        try {
            manager.createGrave(null);
            fail("expected IllegalArgumentException  for null argument");
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }

    @Test
    public void createGraveWithWrongValues() {
        Grave grave = newGrave(12, 13, 6, "Nice grave");
        grave.setId(1L);
        try {
            manager.createGrave(grave);
            fail("should refuse assigned id");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        grave = newGrave(-1, 13, 6, "Nice grave");
        try {
            manager.createGrave(grave);
            fail("negative column number not detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        grave = newGrave(1, -1, 6, "Nice grave");
        try {
            manager.createGrave(grave);
            fail("negative row not detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        grave = newGrave(1, 1, -1, "Nice grave");
        try {
            manager.createGrave(grave);
            fail("negative capacity not detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        grave = newGrave(1, 1, 0, "Nice grave");
        try {
            manager.createGrave(grave);
            fail("zero capacity not detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }

    }


    @Test
    public void getAllGraves() {

        assertTrue(manager.findAllGraves().isEmpty());

        Grave g1 = newGrave(23, 44, 5, "Grave 1");
        Grave g2 = newGrave(12, 4, 1, "Grave 2");

        manager.createGrave(g1);
        manager.createGrave(g2);

        List<Grave> expected = Arrays.asList(g1, g2);
        List<Grave> actual = manager.findAllGraves();

        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);

        assertEquals("saved and retrieved graves differ", expected, actual);
        assertDeepEquals(expected, actual);
    }



    @Test
    public void updateGrave() {
        Grave grave = newGrave(12, 13, 6, "Nice grave");
        Grave g2 = newGrave(18, 19, 100, "Another record");
        manager.createGrave(grave);
        manager.createGrave(g2);
        Long graveId = grave.getId();

        //change column value to 0
        grave.setColumn(666);
        manager.updateGrave(grave);
        //load from database
        grave = manager.getGrave(graveId);
        //new style assertions
        assertThat("column was not changed", grave.getColumn(), is(equalTo(666)));
        assertThat("row was changed when changing column", grave.getRow(), is(equalTo(13)));
        assertThat("capacity was changed when changing column", grave.getCapacity(), is(equalTo(6)));
        assertThat("note was changed when changing column", grave.getNote(), is(equalTo("Nice grave")));

        //change row value to 0
        grave.setRow(0);
        manager.updateGrave(grave);
        //load from database
        grave = manager.getGrave(graveId);
        //old style assertions
        assertEquals(666, grave.getColumn());
        assertEquals(0, grave.getRow());
        assertEquals(6, grave.getCapacity());
        assertEquals("Nice grave", grave.getNote());

        grave.setCapacity(1);
        manager.updateGrave(grave);
        grave = manager.getGrave(graveId);
        assertEquals(666, grave.getColumn());
        assertEquals(0, grave.getRow());
        assertEquals(1, grave.getCapacity());
        assertEquals("Nice grave", grave.getNote());

        grave.setNote("Another grave");
        manager.updateGrave(grave);
        grave = manager.getGrave(graveId);
        assertEquals(666, grave.getColumn());
        assertEquals(0, grave.getRow());
        assertEquals(1, grave.getCapacity());
        assertEquals("Another grave", grave.getNote());

        grave.setNote(null);
        manager.updateGrave(grave);
        grave = manager.getGrave(graveId);
        assertEquals(666, grave.getColumn());
        assertEquals(0, grave.getRow());
        assertEquals(1, grave.getCapacity());
        assertNull(grave.getNote());

        // Check if updates didn't affected other records
        assertDeepEquals(g2, manager.getGrave(g2.getId()));
    }

    @Test
    public void updateGraveWithWrongAttributes() {

        Grave grave = newGrave(12, 13, 6, "Nice grave");
        manager.createGrave(grave);
        Long graveId = grave.getId();

        try {
            manager.updateGrave(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            grave = manager.getGrave(graveId);
            grave.setId(null);
            manager.updateGrave(grave);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            grave = manager.getGrave(graveId);
            grave.setId(graveId - 1);
            manager.updateGrave(grave);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            grave = manager.getGrave(graveId);
            grave.setColumn(-1);
            manager.updateGrave(grave);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            grave = manager.getGrave(graveId);
            grave.setRow(-1);
            manager.updateGrave(grave);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            grave = manager.getGrave(graveId);
            grave.setCapacity(0);
            manager.updateGrave(grave);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            grave = manager.getGrave(graveId);
            grave.setCapacity(-1);
            manager.updateGrave(grave);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }

    @Test
    public void deleteGrave() {

        Grave g1 = newGrave(12, 13, 6, "Nice grave");
        Grave g2 = newGrave(18, 19, 100, "Another record");
        manager.createGrave(g1);
        manager.createGrave(g2);

        assertNotNull(manager.getGrave(g1.getId()));
        assertNotNull(manager.getGrave(g2.getId()));

        manager.deleteGrave(g1);

        assertNull(manager.getGrave(g1.getId()));
        assertNotNull(manager.getGrave(g2.getId()));

    }

    @Test
    public void deleteGraveWithWrongAttributes() {

        Grave grave = newGrave(12, 13, 6, "Nice grave");

        try {
            manager.deleteGrave(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            grave.setId(null);
            manager.deleteGrave(grave);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            grave.setId(1L);
            manager.deleteGrave(grave);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

    }

    private static Grave newGrave(int column, int row, int capacity, String note) {
        Grave grave = new Grave();
        grave.setColumn(column);
        grave.setRow(row);
        grave.setCapacity(capacity);
        grave.setNote(note);
        return grave;
    }

    private void assertDeepEquals(List<Grave> expectedList, List<Grave> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Grave expected = expectedList.get(i);
            Grave actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEquals(Grave expected, Grave actual) {
        assertEquals("id value is not equal",expected.getId(), actual.getId());
        assertEquals("column value is not equal",expected.getColumn(), actual.getColumn());
        assertEquals("row value is not equal",expected.getRow(), actual.getRow());
        assertEquals("capacity value is not equal",expected.getCapacity(), actual.getCapacity());
        assertEquals("note value is not equal",expected.getNote(), actual.getNote());
    }

    private static Comparator<Grave> idComparator = new Comparator<Grave>() {
        @Override
        public int compare(Grave o1, Grave o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

}
