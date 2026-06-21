package ru.practicum.main.managedlocation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.managedlocation.dto.ManagedLocationDto;
import ru.practicum.main.managedlocation.dto.NewManagedLocationDto;
import ru.practicum.main.managedlocation.dto.UpdateManagedLocationDto;
import ru.practicum.main.managedlocation.service.ManagedLocationService;

import java.util.List;

@RestController
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
public class AdminManagedLocationController {

    private final ManagedLocationService service;

    @PostMapping
    public ResponseEntity<ManagedLocationDto> create(@Valid @RequestBody NewManagedLocationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PatchMapping("/{locationId}")
    public ResponseEntity<ManagedLocationDto> update(@PathVariable Long locationId,
                                                     @RequestBody UpdateManagedLocationDto dto) {
        if (locationId <= 0) {
            throw new ValidationException("locationId must be positive");
        }
        return ResponseEntity.ok(service.update(locationId, dto));
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> delete(@PathVariable Long locationId) {
        if (locationId <= 0) {
            throw new ValidationException("locationId must be positive");
        }
        service.delete(locationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<ManagedLocationDto> getById(@PathVariable Long locationId) {
        if (locationId <= 0) {
            throw new ValidationException("locationId must be positive");
        }
        return ResponseEntity.ok(service.getById(locationId));
    }

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
}
