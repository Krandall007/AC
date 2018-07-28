package com.rockyrunstream.ac;

import com.rockyrunstream.ac.service.ACFilter;
import com.rockyrunstream.ac.service.ACPlane;
import com.rockyrunstream.ac.service.ACService;
import com.rockyrunstream.ac.store.ACStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Random;

@Service
public class DataGenerator {

    private static final Logger log = LoggerFactory.getLogger(DataGenerator.class);

    private static final int QUEUE_SIZE = 10;

    @Autowired
    private ACService service;

    @Autowired
    private ACStore store;

    @PostConstruct
    public void generate() {
        if (CollectionUtils.isEmpty(store.list(new ACFilter()))) {
            log.info("DB is empty - generating test data");
        }
        final Random random = new Random();
        for (int i = 0; i < QUEUE_SIZE; i++) {
            final ACPlane plane = new ACPlane();
            plane.setType(ACPlane.Type.values()[random.nextInt(ACPlane.Type.values().length)]);
            plane.setSize(ACPlane.Size.values()[random.nextInt(ACPlane.Size.values().length)]);
            plane.setLabel(String.format("%s %s %d", plane.getSize(), plane.getType(), i));
            service.push(plane);
        }
        log.info("{} ACPlanes generated", QUEUE_SIZE);
    }
}
