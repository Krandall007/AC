package com.rockyrunstream.ac;

import com.rockyrunstream.ac.exception.BadRequest;
import com.rockyrunstream.ac.exception.OptimisticLockException;
import com.rockyrunstream.ac.service.ACFilter;
import com.rockyrunstream.ac.service.ACPlane;
import com.rockyrunstream.ac.service.ACService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static com.rockyrunstream.ac.service.ACPlane.Size.LARGE;
import static com.rockyrunstream.ac.service.ACPlane.Type.CARGO;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ACServiceTestConfiguration.class)
public class ACServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ACServiceTest.class);

    @Autowired
    private ACService service;

    @Test(expected = BadRequest.class)
    public void testValidationNoType() {
        final ACPlane plane = new ACPlane();
        plane.setSize(LARGE);
        service.push(plane);
    }

    @Test(expected = BadRequest.class)
    public void testValidationNoSize() {
        final ACPlane plane = new ACPlane();
        plane.setType(CARGO);
        service.push(plane);
    }

    @Test
    public void testCreate() {
        final ACPlane plane = new ACPlane();
        plane.setType(CARGO);
        plane.setSize(LARGE);
        final ACPlane created = service.push(plane);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertTrue(created.getTimestamp() > 0);
        assertEquals(CARGO, created.getType());
        assertEquals(LARGE, created.getSize());
        assertNotNull(created.getLabel());
    }

    @Test
    public void testMultiThreads() {
        final int threadNum = 10;
        final int iterationNum = 10;

        final CountDownLatch startSignal = new CountDownLatch(1);
        final CountDownLatch doneSignal = new CountDownLatch(threadNum);

        final AtomicInteger errorCounter = new AtomicInteger();
        final AtomicInteger optimisticLocCounter = new AtomicInteger();

        final int queueSizeBefore = service.list(new ACFilter()).getItems().size();

        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    startSignal.await();

                    int counter = iterationNum;
                    int createdCounter = 0;
                    while (counter > 0) {
                        if (createdCounter < iterationNum) {
                            final ACPlane plane = new ACPlane();
                            plane.setType(CARGO);
                            plane.setSize(LARGE);
                            service.push(plane);
                            createdCounter++;
                        }
                        service.list(new ACFilter());
                        try {
                            service.pop();
                            counter--;
                        } catch (OptimisticLockException e) {
                            log.info("OptimisticLockException");
                            optimisticLocCounter.incrementAndGet();
                        } catch (Exception e) {
                            log.error("Unexpected exception", e);
                            errorCounter.incrementAndGet();
                        }
                    }
                    log.debug("Counters {} : {}", counter, createdCounter);
                } catch (InterruptedException ex) {
                    log.error(ex.getMessage(), ex);
                } finally {
                    doneSignal.countDown();
                }

            }).start();
        }
        startSignal.countDown();

        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        log.info("Optimistic error counter {}", optimisticLocCounter.get());
        assertEquals(0, errorCounter.get());
        final int queueSizeAfter = service.list(new ACFilter()).getItems().size();
        assertEquals(queueSizeBefore, queueSizeAfter);
    }
}
