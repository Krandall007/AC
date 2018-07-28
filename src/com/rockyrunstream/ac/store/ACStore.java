package com.rockyrunstream.ac.store;

import com.rockyrunstream.ac.service.ACFilter;
import com.rockyrunstream.ac.service.ACPlane;
import org.bson.types.ObjectId;

import java.util.List;

public interface ACStore {

    void drop();
    List<ACPlane> list(ACFilter filter);
    ACPlane create(ACPlane plane);
    ACPlane remove(ObjectId id);

}
