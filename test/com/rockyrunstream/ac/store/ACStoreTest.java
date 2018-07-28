package com.rockyrunstream.ac.store;

import com.rockyrunstream.ac.ACServiceTestConfiguration;
import com.rockyrunstream.ac.exception.OptimisticLockException;
import com.rockyrunstream.ac.service.ACFilter;
import com.rockyrunstream.ac.service.ACPlane;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.rockyrunstream.ac.service.ACPlane.Size.*;
import static com.rockyrunstream.ac.service.ACPlane.Type.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ACServiceTestConfiguration.class)
public class ACStoreTest {

    @Autowired
    private ACStore store;

    @Before
    public void setup() {
        store.drop();
    }

    @Test
    public void testCreate() {
        final ACPlane plane = new ACPlane();
        plane.setTimestamp(111L);
        plane.setType(CARGO);
        plane.setSize(LARGE);
        plane.setLabel("Big cargo plane");

        final ACPlane created = store.create(plane);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(111L, created.getTimestamp());
        assertEquals(CARGO, created.getType());
        assertEquals(LARGE, created.getSize());
        assertEquals("Big cargo plane", created.getLabel());

        final ACFilter filter = new ACFilter();
        filter.setLimit(51);

        final List<ACPlane> result = store.list(filter);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testRemove() {
        final ACPlane plane = new ACPlane();
        plane.setTimestamp(111L);
        plane.setType(VIP);
        plane.setSize(SMALL);
        plane.setLabel("Small VIP plane");

        store.create(plane);
        final ACPlane removed = store.remove(plane.getId());
        assertNotNull(removed);
        assertNotNull(removed.getId());
        assertEquals(111L, removed.getTimestamp());
        assertEquals(VIP, removed.getType());
        assertEquals(SMALL, removed.getSize());
        assertEquals("Small VIP plane", removed.getLabel());

        final ACFilter filter = new ACFilter();
        filter.setLimit(51);

        final List<ACPlane> result = store.list(filter);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testList() {
        addPlane(VIP, SMALL, 100);
        addPlane(CARGO, LARGE, 200);
        addPlane(PASSENGER, LARGE, 400);
        addPlane(EMERGENCY, SMALL, 300);

        final ACFilter filter = new ACFilter();
        filter.setLimit(51);

        final List<ACPlane> result = store.list(filter);
        assertNotNull(result);
        assertEquals(4, result.size());

        final ACPlane top = result.get(0);
        assertNotNull(top.getId());
        assertEquals(300L, top.getTimestamp());
        assertEquals(EMERGENCY, top.getType());
        assertEquals(SMALL, top.getSize());
        assertEquals("SMALL EMERGENCY 300", result.get(0).getLabel());
        assertEquals("SMALL VIP 100", result.get(1).getLabel());
        assertEquals("LARGE PASSENGER 400", result.get(2).getLabel());
        assertEquals("LARGE CARGO 200", result.get(3).getLabel());
    }

    private ACPlane addPlane(ACPlane.Type type, ACPlane.Size size, long timestamp) {
        final ACPlane plane = new ACPlane();
        plane.setTimestamp(timestamp);
        plane.setType(type);
        plane.setSize(size);
        plane.setLabel(size + " " + type + " " + timestamp);
        return store.create(plane);
    }


    @Test
    public void testSortByType() {
        addPlane(VIP, SMALL, 100);
        addPlane(CARGO, SMALL, 100);
        addPlane(PASSENGER, SMALL, 100);
        addPlane(EMERGENCY, SMALL, 100);

        final ACFilter filter = new ACFilter();
        filter.setLimit(51);

        final List<ACPlane> result = store.list(filter);
        assertNotNull(result);
        assertEquals(4, result.size());

        assertEquals("SMALL EMERGENCY 100", result.get(0).getLabel());
        assertEquals("SMALL VIP 100", result.get(1).getLabel());
        assertEquals("SMALL PASSENGER 100", result.get(2).getLabel());
        assertEquals("SMALL CARGO 100", result.get(3).getLabel());
    }

    @Test
    public void testSortBySize() {
        addPlane(PASSENGER, SMALL, 100);
        addPlane(PASSENGER, LARGE, 100);
        addPlane(PASSENGER, SMALL, 100);
        addPlane(PASSENGER, LARGE, 100);

        final ACFilter filter = new ACFilter();
        filter.setLimit(51);

        final List<ACPlane> result = store.list(filter);
        assertNotNull(result);
        assertEquals(4, result.size());

        assertEquals("LARGE PASSENGER 100", result.get(0).getLabel());
        assertEquals("LARGE PASSENGER 100", result.get(1).getLabel());
        assertEquals("SMALL PASSENGER 100", result.get(2).getLabel());
        assertEquals("SMALL PASSENGER 100", result.get(3).getLabel());
    }

    @Test
    public void testSortByTimestamp() {
        addPlane(PASSENGER, SMALL, 100);
        addPlane(PASSENGER, SMALL, 400);
        addPlane(PASSENGER, SMALL, 300);
        addPlane(PASSENGER, SMALL, 200);

        final ACFilter filter = new ACFilter();
        filter.setLimit(51);

        final List<ACPlane> result = store.list(filter);
        assertNotNull(result);
        assertEquals(4, result.size());

        assertEquals("SMALL PASSENGER 100", result.get(0).getLabel());
        assertEquals("SMALL PASSENGER 200", result.get(1).getLabel());
        assertEquals("SMALL PASSENGER 300", result.get(2).getLabel());
        assertEquals("SMALL PASSENGER 400", result.get(3).getLabel());
    }


    @Test
    public void testFilterByType() {
        addPlane(VIP, SMALL, 100);
        addPlane(CARGO, LARGE, 200);
        addPlane(PASSENGER, LARGE, 400);
        addPlane(EMERGENCY, SMALL, 300);

        final ACFilter filter = new ACFilter();
        filter.setLimit(51);
        filter.setType(CARGO);

        final List<ACPlane> result = store.list(filter);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("LARGE CARGO 200", result.get(0).getLabel());
    }

    @Test
    public void testFilterBySize() {
        addPlane(VIP, SMALL, 100);
        addPlane(CARGO, LARGE, 200);
        addPlane(PASSENGER, LARGE, 400);
        addPlane(EMERGENCY, SMALL, 300);

        final ACFilter filter = new ACFilter();
        filter.setLimit(51);
        filter.setSize(LARGE);

        final List<ACPlane> result = store.list(filter);
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("LARGE PASSENGER 400", result.get(0).getLabel());
        assertEquals("LARGE CARGO 200", result.get(1).getLabel());
    }

    @Test
    public void testPaging() {
        addPlane(VIP, SMALL, 100);
        addPlane(CARGO, LARGE, 200);
        addPlane(PASSENGER, LARGE, 400);
        addPlane(EMERGENCY, SMALL, 300);

        final ACFilter filter = new ACFilter();
        filter.setLimit(2);

        final List<ACPlane> firstPage = store.list(filter);
        assertNotNull(firstPage);
        assertEquals(2, firstPage.size());

        assertEquals("SMALL EMERGENCY 300", firstPage.get(0).getLabel());
        assertEquals("SMALL VIP 100", firstPage.get(1).getLabel());

        filter.setLimit(3);
        filter.setLastId(firstPage.get(1).getId());

        addPlane(EMERGENCY, SMALL, 500);
        final List<ACPlane> secondPage = store.list(filter);
        assertNotNull(secondPage);
        assertEquals(2, secondPage.size());
        assertEquals("LARGE PASSENGER 400", secondPage.get(0).getLabel());
        assertEquals("LARGE CARGO 200", secondPage.get(1).getLabel());
    }

    @Test(expected = OptimisticLockException.class)
    public void testRemoveRemoved() {
        final ACPlane testPlane = addPlane(VIP, SMALL, 100);
        store.remove(testPlane.getId());
        store.remove(testPlane.getId());
    }

    @Test
    public void testSearch() {
        addPlane(VIP, SMALL, 100);
        addPlane(CARGO, LARGE, 100);
        addPlane(PASSENGER, SMALL, 100);
        addPlane(EMERGENCY, LARGE, 100);

        final ACFilter filter = new ACFilter();
        filter.setSearch("large");
        filter.setLimit(51);

        final List<ACPlane> result = store.list(filter);
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("LARGE EMERGENCY 100", result.get(0).getLabel());
        assertEquals("LARGE CARGO 100", result.get(1).getLabel());
    }

    @Test
    public void testPriority() {
        for (int i = 0; i < 100; i++) {
            final ACPlane plane = new ACPlane();
            plane.setType(EMERGENCY);
            plane.setSize(SMALL);
            plane.setTimestamp(1000L);
            plane.setLabel(String.format("%s %s %d", plane.getSize(), plane.getType(), i));
            store.create(plane);
        }

        final ACFilter filter = new ACFilter();
        filter.setLimit(50);
        final List<ACPlane> page1 = store.list(filter);
        assertNotNull(page1);
        for (int i = 0; i < page1.size(); i++) {
            final ACPlane plane = page1.get(i);
            assertEquals("SMALL EMERGENCY " + i, plane.getLabel());
        }

        filter.setLastId(page1.get(49).getId());

        final List<ACPlane> page2 = store.list(filter);
        for (int i = 0; i < page2.size(); i++) {
            final ACPlane plane = page2.get(i);
            assertEquals("SMALL EMERGENCY " + (i + 50), plane.getLabel());
        }

    }


}