package ir.nofitech.counter.controller;

import ir.nofitech.counter.service.CounterService;
import ir.nofitech.counter.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final CounterService counterService;
    private final PropertyService propertyService;

    @Autowired
    public ApiController(CounterService counterService, PropertyService propertyService) {
        this.counterService = counterService;
        this.propertyService = propertyService;
    }

    /**
     * API 1: Get next incrementing value
     * GET /api/getNext
     */
    @GetMapping("/getNext")
    public ResponseEntity<Long> getNext() {
        return ResponseEntity.ok(counterService.getNext());
    }

    /**
     * API 2: Get value by key from properties file
     * GET /api/getValue/{key}
     */
    @GetMapping("/getValue/{key}")
    public ResponseEntity<String> getValue(@PathVariable String key) {
        String value = propertyService.getValue(key);
        if (value != null) {
            return ResponseEntity.ok(value);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * API 3: Set key-value pair in properties file
     * GET /api/setValue/{key}/{value}
     */
    @GetMapping("/setValue/{key}/{value}")
    public ResponseEntity<String> setValue(
            @PathVariable String key,
            @PathVariable String value) {
        propertyService.setValue(key, value);
        return ResponseEntity.ok("Value set successfully");
    }
}