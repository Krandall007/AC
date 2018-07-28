package com.rockyrunstream.ac.store;

import com.rockyrunstream.ac.exception.OptimisticLockException;
import com.rockyrunstream.ac.service.ACFilter;
import com.rockyrunstream.ac.service.ACPlane;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * In memory implementation
 */
@Service
@Profile("memory")
public class InMemoryStore implements ACStore {

    private TreeSet<ACPlane> queue;
    private Map<ObjectId, ACPlane> idIndex;

    private ReadWriteLock transactionLock;

    public InMemoryStore() {
        transactionLock = new ReentrantReadWriteLock();
    }

    @PostConstruct
    public void drop() {
        this.queue = new TreeSet<>(new PlaneComparator());
        this.idIndex = new HashMap<>();
    }

    public List<ACPlane> list(ACFilter filter) {
        return readOperation(() -> {

            final Set<ACPlane> workingSet;
            // 1. Apply pagination
            final ACPlane tail = getTail(filter);
            if (tail == null) {
                workingSet = queue;
            } else {
                workingSet = queue.tailSet(tail, false);
            }

            final Iterator<ACPlane> iterator = workingSet.iterator();

            // 3. Scroll + filter
            final List<ACPlane> result = new ArrayList<>(filter.getLimit());
            while (iterator.hasNext() && result.size() < filter.getLimit()) {
                final ACPlane plane = iterator.next();
                boolean matches = filter.getType() == null || filter.getType() == plane.getType();
                matches &= filter.getSize() == null || filter.getSize() == plane.getSize();
                matches &= StringUtils.isBlank(filter.getSearch()) || StringUtils.containsIgnoreCase(plane.getLabel(), filter.getSearch());
                if (matches) {
                    result.add(clonePlane(plane));
                }
            }
            return result;
        });
    }

    private ACPlane getTail(ACFilter filter) {
        if (filter.getLastId() == null) {
            return null;
        }
        final ACPlane plane = idIndex.get(filter.getLastId());
        if (plane == null) {
            filter.setLastId(null);
        }
        return plane;
    }

    public ACPlane create(ACPlane plane) {
        plane.setId(ObjectId.get());
        return writeOperation(()-> {
            final ACPlane clone = clonePlane(plane);
            queue.add(clone);
            idIndex.put(clone.getId(), clone);
            return clonePlane(clone);
        });
    }

    public ACPlane remove(ObjectId id) {
        return writeOperation(()-> {
            final ACPlane stored = idIndex.remove(id);
            if (stored == null) {
                throw new OptimisticLockException(String.format("Plane with %s not found", id));
            }
            queue.remove(stored);
            return stored;
        });
    }

    private <T> T readOperation(Supplier<T> callable) {
        return lockOperation(callable, transactionLock.readLock());
    }

    private <T> T writeOperation(Supplier<T> callable) {
        return lockOperation(callable, transactionLock.writeLock());
    }

    private <T> T lockOperation(Supplier<T> callable, Lock lock) {
        lock.lock();
        try {
            return callable.get();
        } finally {
            lock.unlock();
        }
    }

    private ACPlane clonePlane(ACPlane original) {
        final ACPlane clone = new ACPlane();
        return copyPlane(original, clone);
    }

    private ACPlane copyPlane(ACPlane source, ACPlane target)  {
        target.setId(source.getId());
        target.setTimestamp(source.getTimestamp());
        target.setSize(source.getSize());
        target.setType(source.getType());
        target.setLabel(source.getLabel());
        return target;
    }

    private class PlaneComparator implements Comparator<ACPlane> {

        @Override
        public int compare(ACPlane o1, ACPlane o2) {
            int result = o1.getType().compareTo(o2.getType());
            if (result != 0) {
                return result;
            }
            result = o1.getSize().compareTo(o2.getSize());
            if (result != 0) {
                return result;
            }
            result = Long.compare(o1.getTimestamp(), o2.getTimestamp());
            if (result != 0) {
                return result;
            }
            return o1.getId().compareTo(o2.getId());
        }
    }

}
