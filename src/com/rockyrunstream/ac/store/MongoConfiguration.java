package com.rockyrunstream.ac.store;

import com.mongodb.MongoClient;
import com.rockyrunstream.ac.service.ACPlane;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Configuration
@Profile("mongo")
public class MongoConfiguration {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private MongoDbFactory dbFactory;

    @PostConstruct
    public void init() {
        datastore().ensureIndexes();
    }

    @Bean
    public Morphia morphia() {
        final Set<Class> mappedClasses = new HashSet<>();
        mappedClasses.add(ACPlane.class);
        return new Morphia(mappedClasses);
    }

    @Bean
    public Datastore datastore() {
        return morphia().createDatastore(mongoClient, dbFactory.getDb().getName());
    }
}
