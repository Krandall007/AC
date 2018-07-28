package com.rockyrunstream.ac.web;

import com.rockyrunstream.ac.service.ACFilter;
import com.rockyrunstream.ac.service.ACPlane;
import com.rockyrunstream.ac.service.ACService;
import com.rockyrunstream.ac.service.ListResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class ACController {

    @Autowired
    private ACService service;

    @RequestMapping(value = "/", method = GET)
    public ListResponse<ACPlane> list(@RequestParam(required = false) String search,
                                      @RequestParam(required = false) ObjectId lastId,
                                      @RequestParam(required = false) Integer limit,
                                      @RequestParam(required = false) ACPlane.Type type,
                                      @RequestParam(required = false) ACPlane.Size size) {
        final ACFilter filter = new ACFilter();
        filter.setSearch(search);
        filter.setLastId(lastId);
        if (limit !=null) filter.setLimit(limit);
        filter.setType(type);
        filter.setSize(size);
        return service.list(filter);
    }

    @RequestMapping(value = "/", method = PUT)
    public ACPlane push(@RequestBody ACPlane plane) {
        return service.push(plane);
    }

    @RequestMapping(value = "/", method = DELETE)
    public ACPlane pop() {
        return service.pop();
    }

}
