package com.felix.demo.common;

import com.felix.demo.common.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class GenericCrudController {

    @Autowired
    private GenericCrudService genericCrudService;

    @GetMapping("/{resource}")
    public PageResult list(
            @PathVariable String resource,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int size,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String fields) {

        if (page == 0 && size == 0) {
            return genericCrudService.list(resource, filter, sort, fields);
        }
        return genericCrudService.page(resource, page, size, filter, sort, fields);
    }

    @GetMapping("/{resource}/{id}")
    public Map<String, Object> get(
            @PathVariable String resource,
            @PathVariable String id) {
        return genericCrudService.get(resource, id);
    }

    @PostMapping("/{resource}")
    public int create(
            @PathVariable String resource,
            @RequestBody Map<String, Object> body) {
        return genericCrudService.create(resource, body);
    }

    @RequestMapping(value = "/{resource}/{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public int update(
            @PathVariable String resource,
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        return genericCrudService.update(resource, id, body);
    }

    @DeleteMapping("/{resource}/{id}")
    public int delete(
            @PathVariable String resource,
            @PathVariable String id) {
        return genericCrudService.delete(resource, id);
    }
}

