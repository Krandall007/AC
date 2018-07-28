package com.rockyrunstream.ac.service;

import com.rockyrunstream.ac.exception.BadRequest;
import com.rockyrunstream.ac.exception.OptimisticLockException;
import com.rockyrunstream.ac.exception.QueueIsEmpty;
import com.rockyrunstream.ac.store.ACStore;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Air Controller Service
 */
@Service
public class ACService {

    private static final Logger log = LoggerFactory.getLogger(ACService.class);

    private static final int MAX_PAGE_SIZE = 50;

    private static final int MAX_TRY = 10;

    @Autowired
    private ACStore store;

    @Autowired
    private Validator validator;

    public ListResponse<ACPlane> list(ACFilter filter) {
        log.debug("list {}", filter);

        if (filter.getLimit() < 1 || filter.getLimit() > MAX_PAGE_SIZE) {
            filter.setLimit(MAX_PAGE_SIZE);
        }
        filter.setLimit(filter.getLimit() + 1);
        final List<ACPlane> items = store.list(filter);
        final boolean hasMore = items.size() == filter.getLimit();
        if (hasMore) {
            items.remove(items.size() - 1);
        }
        filter.setLimit(filter.getLimit() - 1);

        final ListResponse<ACPlane> response = new ListResponse<>();
        response.setItems(items);
        response.setHasMore(hasMore);
        response.setFilter(filter);
        log.debug("result: {}", response);
        return response;

    }

    public ACPlane push(ACPlane plane) {
        log.debug("push: {}", plane);
        plane.setTimestamp(System.currentTimeMillis());
        if (plane.getLabel() == null) {
            plane.setLabel(plane.getSize() + " " + plane.getType() + " " + plane.getTimestamp());
        }
        final Set<ConstraintViolation<ACPlane>> validationResult = validator.validate(plane);
        if (CollectionUtils.isNotEmpty(validationResult)) {
            throw new BadRequest(new ConstraintViolationException(validationResult));
        }
        final ACPlane result = store.create(plane);
        log.debug("created: {}", result);
        return result;
    }

    public ACPlane pop() {
        log.debug("pop");
        return optimisticOperation(() -> {
            final ACFilter request = new ACFilter();
            request.setLimit(1);

            final List<ACPlane> items = store.list(request);
            if (CollectionUtils.isEmpty(items)) {
                throw new QueueIsEmpty("Queue is empty");
            }

            final ACPlane head = items.get(0);
            store.remove(head.getId());

            log.debug("pop result {}", head);
            return head;
        });
    }

    private <T> T optimisticOperation(Supplier<T> callable) {
        for (int i = 1; i < MAX_TRY; i++) {
            try {
                return callable.get();
            } catch (OptimisticLockException e) {
                log.debug("Attempt {} failed", i);
            }
        }
        //Give it last try
        return callable.get();
    }

}
