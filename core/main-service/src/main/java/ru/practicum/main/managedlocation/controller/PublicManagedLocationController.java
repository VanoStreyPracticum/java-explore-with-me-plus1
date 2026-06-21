package ru.practicum.main.managedlocation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.managedlocation.dto.ManagedLocationDto;
import ru.practicum.main.managedlocation.service.ManagedLocationService;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class PublicManagedLocationController {

    private final ManagedLocationService service;

    @GetMapping
    public ResponseEntity<List<ManagedLocationDto>> getAll(@RequestParam(defaultValue = "0") int from,
                                                           @RequestParam(defaultValue = "10") int size) {
        if (size <= 0) {
            throw new ValidationException("size must be positive");
        }
        if (from < 0) {
            throw new ValidationException("from must be >= 0");
        }
        return ResponseEntity.ok(service.getAll(from, size));
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<ManagedLocationDto> getById(@PathVariable Long locationId) {
        if (locationId <= 0) {
            throw new ValidationException("locationId must be positive");
        }
        return ResponseEntity.ok(service.getById(locationId));
    }
}
