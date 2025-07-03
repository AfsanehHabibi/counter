package ir.nofitech.counter.controller;

import ir.nofitech.counter.service.CounterService;
import ir.nofitech.counter.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Counter API", description = "API for counter and property management")
public class ApiController {

    private final CounterService counterService;
    private final PropertyService propertyService;

    @Autowired
    public ApiController(CounterService counterService, PropertyService propertyService) {
        this.counterService = counterService;
        this.propertyService = propertyService;
    }

    @Operation(
            summary = "Get next incrementing value",
            description = "Returns a sequentially incremented number starting from 1"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved next value"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/getNext")
    public ResponseEntity<Long> getNext() {
        return ResponseEntity.ok(counterService.getNext());
    }

    @Operation(
            summary = "Get property value by key",
            description = "Returns the value associated with the given key from properties file"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Value found and returned"),
            @ApiResponse(responseCode = "404", description = "Key not found")
    })
    @GetMapping("/getValue/{key}")
    public ResponseEntity<String> getValue(
            @Parameter(description = "Key to look up in properties", required = true, example = "server.port")
            @PathVariable String key) {
        String value = propertyService.getValue(key);
        if (value != null) {
            return ResponseEntity.ok(value);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Set key-value pair",
            description = "Adds or updates a key-value pair in the properties file"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Value set successfully"),
            @ApiResponse(responseCode = "500", description = "Failed to save properties")
    })
    @GetMapping("/setValue/{key}/{value}")
    public ResponseEntity<String> setValue(
            @Parameter(description = "Key to set", required = true, example = "timeout")
            @PathVariable String key,
            @Parameter(description = "Value to set", required = true, example = "30")
            @PathVariable String value) {
        propertyService.setValue(key, value);
        return ResponseEntity.ok("Value set successfully");
    }
}